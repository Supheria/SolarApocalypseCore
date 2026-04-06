package com.supheria.solar_apocalypse_core.transforms.fluid;

import com.supheria.solar_apocalypse_core.BlockTransform;
import com.supheria.solar_apocalypse_core.block.FallingSnowBlock;
import com.supheria.solar_apocalypse_core.init.SolarModBlocks;
import com.supheria.solar_apocalypse_core.network.SolarModVariables;
import com.supheria.solar_apocalypse_core.world.SolarStage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 第六阶段（坍缺）永恒降雪：积雪会继续堆积、压缩，并在失去支撑后以可见下落过程坠落。
 */
public class SnowMelt {

    public static final BlockTransform TRANSFORM = SnowMelt::transform;

    private static final int MAX_STACKED_SNOW_BLOCKS = 4;
    private static final int MAX_SNOW_LAYERS = 8;

    private static void transform(LevelAccessor world, double x, double y, double z) {
        SolarStage currentPhase = SolarModVariables.MapVariables.get(world).getCurrentStage();
        if (currentPhase != SolarStage.STAGE_6) {
            return;
        }

        BlockPos startPos = BlockPos.containing(x, y, z);
        BlockPos topPos = findTopSnowColumnPos(world, startPos);
        if (topPos == null) {
            return;
        }

        BlockState topState = world.getBlockState(topPos);
        if (topState.getBlock() instanceof SnowLayerBlock) {
            growOrCompressSnowLayer(world, topPos, topState);
            return;
        }

        if ((topState.is(Blocks.SNOW_BLOCK) || isValidSnowSurface(topState)) && canContinueRising(world, topPos)) {
            BlockPos snowPos = topPos.above();
            if (world.getBlockState(snowPos).isAir()) {
                BlockState newSnow = snowLayerState(1);
                world.setBlock(snowPos, newSnow, 3);
                if (world instanceof ServerLevel serverLevel) {
                    triggerVisibleFall(serverLevel, snowPos, newSnow);
                }
            }
        }
    }

    public static boolean triggerVisibleFall(ServerLevel level, BlockPos pos, BlockState state) {
        if (!isStage6(level)) {
            return false;
        }

        int mass = getSnowMass(state);
        if (mass <= 0 || state.is(SolarModBlocks.FALLING_SNOW.get())) {
            return false;
        }

        boolean shouldFall = state.getBlock() instanceof SnowLayerBlock
                ? !canSnowSurviveAt(level, pos)
                : state.is(Blocks.SNOW_BLOCK) && FallingBlock.isFree(level.getBlockState(pos.below()));
        if (!shouldFall) {
            return false;
        }

        BlockState fallingState = SolarModBlocks.FALLING_SNOW.get().defaultBlockState().setValue(FallingSnowBlock.MASS, mass);
        FallingBlockEntity fallingEntity = FallingBlockEntity.fall(level, pos, fallingState);
        fallingEntity.dropItem = false;
        return true;
    }

    public static void landFallingSnow(ServerLevel level, BlockPos pos, BlockState fallingState, BlockState replacedState) {
        int totalMass = getSnowMass(fallingState) + getSnowMass(replacedState);
        if (totalMass <= 0) {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            return;
        }

        if (level.getBlockState(pos).is(SolarModBlocks.FALLING_SNOW.get())) {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        }

        depositSnowMass(level, pos, totalMass);
    }

    public static boolean canSnowSurviveAt(LevelReader world, BlockPos pos) {
        BlockState belowState = world.getBlockState(pos.below());
        if (belowState.is(BlockTags.SNOW_LAYER_CANNOT_SURVIVE_ON)) {
            return false;
        }
        if (belowState.is(BlockTags.SNOW_LAYER_CAN_SURVIVE_ON)) {
            return true;
        }
        return Block.isFaceFull(belowState.getCollisionShape(world, pos.below()), Direction.UP)
                || belowState.is(Blocks.SNOW) && belowState.getValue(SnowLayerBlock.LAYERS) == MAX_SNOW_LAYERS;
    }

    public static int getSnowMass(BlockState state) {
        if (state.getBlock() instanceof SnowLayerBlock) {
            return state.getValue(SnowLayerBlock.LAYERS);
        }
        if (state.is(Blocks.SNOW_BLOCK)) {
            return MAX_SNOW_LAYERS;
        }
        if (state.is(SolarModBlocks.FALLING_SNOW.get())) {
            return state.getValue(FallingSnowBlock.MASS);
        }
        return 0;
    }

    private static void depositSnowMass(ServerLevel level, BlockPos startPos, int totalMass) {
        BlockPos depositPos = findDepositPos(level, startPos);
        if (depositPos == null) {
            return;
        }

        int remainingMass = totalMass;
        BlockPos currentPos = depositPos;
        while (remainingMass > 0) {
            BlockState currentState = level.getBlockState(currentPos);
            if (currentState.getBlock() instanceof SnowLayerBlock) {
                int currentLayers = currentState.getValue(SnowLayerBlock.LAYERS);
                int addedLayers = Math.min(remainingMass, MAX_SNOW_LAYERS - currentLayers);
                int totalLayers = currentLayers + addedLayers;
                level.setBlock(currentPos, snowLayerState(totalLayers), 3);
                remainingMass -= addedLayers;
                if (remainingMass <= 0) {
                    return;
                }
                if (totalLayers < MAX_SNOW_LAYERS || !canCompressAt(level, currentPos)) {
                    return;
                }
                level.setBlock(currentPos, Blocks.SNOW_BLOCK.defaultBlockState(), 3);
                currentPos = currentPos.above();
                continue;
            }

            if (currentState.is(Blocks.SNOW_BLOCK)) {
                currentPos = currentPos.above();
                continue;
            }

            if (!currentState.isAir()) {
                return;
            }

            if (remainingMass >= MAX_SNOW_LAYERS && canCompressAt(level, currentPos)) {
                level.setBlock(currentPos, Blocks.SNOW_BLOCK.defaultBlockState(), 3);
                remainingMass -= MAX_SNOW_LAYERS;
                currentPos = currentPos.above();
                continue;
            }

            level.setBlock(currentPos, snowLayerState(Math.min(remainingMass, MAX_SNOW_LAYERS)), 3);
            return;
        }
    }

    private static BlockPos findDepositPos(ServerLevel level, BlockPos startPos) {
        BlockPos currentPos = startPos;
        while (currentPos.getY() > level.getMinBuildHeight()) {
            if (canSnowSurviveAt(level, currentPos)) {
                return currentPos;
            }

            BlockPos belowPos = currentPos.below();
            BlockState belowState = level.getBlockState(belowPos);
            if (belowState.isAir()) {
                currentPos = belowPos;
                continue;
            }

            if (belowState.getBlock() instanceof SnowLayerBlock && belowState.getValue(SnowLayerBlock.LAYERS) < MAX_SNOW_LAYERS) {
                return belowPos;
            }
            return null;
        }
        return null;
    }

    private static void growOrCompressSnowLayer(LevelAccessor world, BlockPos snowPos, BlockState snowState) {
        int currentLayers = snowState.getValue(SnowLayerBlock.LAYERS);
        if (currentLayers < MAX_SNOW_LAYERS) {
            BlockState grownSnow = snowState.setValue(SnowLayerBlock.LAYERS, currentLayers + 1);
            world.setBlock(snowPos, grownSnow, 3);
            if (world instanceof ServerLevel serverLevel) {
                triggerVisibleFall(serverLevel, snowPos, grownSnow);
            }
            return;
        }

        if (!canCompressAt(world, snowPos)) {
            return;
        }

        BlockState snowBlock = Blocks.SNOW_BLOCK.defaultBlockState();
        world.setBlock(snowPos, snowBlock, 3);
        if (world instanceof ServerLevel serverLevel) {
            triggerVisibleFall(serverLevel, snowPos, snowBlock);
        }

        if (canContinueRising(world, snowPos)) {
            BlockPos nextSnowPos = snowPos.above();
            if (world.getBlockState(nextSnowPos).isAir()) {
                BlockState newSnow = snowLayerState(1);
                world.setBlock(nextSnowPos, newSnow, 3);
                if (world instanceof ServerLevel serverLevel) {
                    triggerVisibleFall(serverLevel, nextSnowPos, newSnow);
                }
            }
        }
    }

    private static BlockPos findTopSnowColumnPos(LevelAccessor world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        if (!(state.getBlock() instanceof SnowLayerBlock) && !state.is(Blocks.SNOW_BLOCK) && !isValidSnowSurface(state)) {
            return null;
        }

        BlockPos currentPos = pos;
        while (isSnowColumnBlock(world.getBlockState(currentPos.above()))) {
            currentPos = currentPos.above();
        }
        return currentPos;
    }

    private static boolean isSnowColumnBlock(BlockState state) {
        return state.getBlock() instanceof SnowLayerBlock || state.is(Blocks.SNOW_BLOCK);
    }

    private static boolean canContinueRising(LevelAccessor world, BlockPos topPos) {
        BlockState topState = world.getBlockState(topPos);
        return !topState.is(Blocks.SNOW_BLOCK) || countStackedSnowBlocks(world, topPos) < MAX_STACKED_SNOW_BLOCKS;
    }

    private static boolean canCompressAt(LevelAccessor world, BlockPos pos) {
        return countStackedSnowBlocks(world, pos.below()) < MAX_STACKED_SNOW_BLOCKS;
    }

    private static int countStackedSnowBlocks(LevelAccessor world, BlockPos fromPos) {
        int count = 0;
        BlockPos currentPos = fromPos;
        while (world.getBlockState(currentPos).is(Blocks.SNOW_BLOCK)) {
            count++;
            currentPos = currentPos.below();
        }
        return count;
    }

    private static BlockState snowLayerState(int layers) {
        return Blocks.SNOW.defaultBlockState().setValue(SnowLayerBlock.LAYERS, layers);
    }

    private static boolean isValidSnowSurface(BlockState state) {
        Block block = state.getBlock();
        return !(block == Blocks.AIR
                || block == Blocks.WATER
                || block == Blocks.LAVA
                || block == Blocks.GLASS
                || block == Blocks.GLASS_PANE
                || block == Blocks.BEDROCK
                || block == Blocks.SNOW
                || block == Blocks.SNOW_BLOCK);
    }

    private static boolean isStage6(LevelAccessor world) {
        return SolarModVariables.MapVariables.get(world).getCurrentStage() == SolarStage.STAGE_6;
    }

    private SnowMelt() {}
}
