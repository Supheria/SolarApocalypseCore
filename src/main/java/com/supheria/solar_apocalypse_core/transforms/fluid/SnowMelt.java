package com.supheria.solar_apocalypse_core.transforms.fluid;

import com.supheria.solar_apocalypse_core.BlockTransform;
import com.supheria.solar_apocalypse_core.config.solar.SolarStageConfig;
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
import org.jetbrains.annotations.Nullable;

/**
 * 第六阶段（坍缺）永恒降雪：积雪会继续堆积、压缩，并在失去支撑后以可见下落过程坠落。
 */
public class SnowMelt {

    public static final BlockTransform TRANSFORM = SnowMelt::transform;
    public static final BlockTransform FALL_CHECK_TRANSFORM = SnowMelt::checkUnsupportedSnow;

    private static final int MAX_SNOW_LAYERS = 8;

    private static void transform(LevelAccessor world, double x, double y, double z) {
        processBudgeted(world, BlockPos.containing(x, y, z), SolarStageConfig.getCollapseSnowStepBudget());
    }

    private static void checkUnsupportedSnow(LevelAccessor world, double x, double y, double z) {
        if (world instanceof ServerLevel serverLevel) {
            BlockPos pos = BlockPos.containing(x, y, z);
            triggerVisibleFallAnyStage(serverLevel, pos, world.getBlockState(pos));
        }
    }

    public static void processBudgeted(LevelAccessor world, BlockPos startPos, int stepBudget) {
        if (!isStage6(world)) {
            return;
        }

        int remainingSteps = Math.max(1, stepBudget);
        BlockPos topPos = findTopSnowColumnPos(world, startPos);
        if (topPos == null) {
            return;
        }

        BlockState topState = world.getBlockState(topPos);
        if (topState.getBlock() instanceof SnowLayerBlock) {
            growOrCompressSnowLayer(world, topPos, topState, remainingSteps);
            return;
        }

        if ((topState.is(Blocks.SNOW_BLOCK) || isValidSnowSurface(topState)) && canContinueRising(world, topPos)) {
            BlockPos snowPos = topPos.above();
            if (remainingSteps > 0 && world.getBlockState(snowPos).isAir()) {
                placeSnowLayer(world, snowPos, 1);
            }
        }
    }

    public static boolean triggerVisibleFall(ServerLevel level, BlockPos pos, BlockState state) {
        if (!isStage6(level)) {
            return false;
        }
        return triggerVisibleFallAnyStage(level, pos, state);
    }

    public static boolean triggerVisibleFallAnyStage(ServerLevel level, BlockPos pos, BlockState state) {
        if (!(state.getBlock() instanceof SnowLayerBlock) && !state.is(Blocks.SNOW_BLOCK)) {
            return false;
        }

        boolean shouldFall = state.getBlock() instanceof SnowLayerBlock
                ? !canSnowSurviveAt(level, pos)
                : state.is(Blocks.SNOW_BLOCK) && FallingBlock.isFree(level.getBlockState(pos.below()));
        if (!shouldFall) {
            return false;
        }

        FallingBlockEntity fallingEntity = FallingBlockEntity.fall(level, pos, state);
        fallingEntity.dropItem = false;
        return true;
    }

    public static boolean canSnowSurviveAt(LevelReader world, BlockPos pos) {
        BlockState belowState = world.getBlockState(pos.below());
        if (belowState.is(Blocks.ICE) || belowState.is(Blocks.PACKED_ICE) || belowState.is(Blocks.BLUE_ICE) || belowState.is(Blocks.FROSTED_ICE)) {
            return true;
        }
        if (belowState.is(BlockTags.SNOW_LAYER_CANNOT_SURVIVE_ON)) {
            return false;
        }
        if (belowState.is(BlockTags.SNOW_LAYER_CAN_SURVIVE_ON)) {
            return true;
        }
        return Block.isFaceFull(belowState.getCollisionShape(world, pos.below()), Direction.UP)
                || belowState.is(Blocks.SNOW) && belowState.getValue(SnowLayerBlock.LAYERS) == MAX_SNOW_LAYERS;
    }


    private static void growOrCompressSnowLayer(LevelAccessor world, BlockPos snowPos, BlockState snowState, int remainingSteps) {
        int currentLayers = snowState.getValue(SnowLayerBlock.LAYERS);
        if (currentLayers < MAX_SNOW_LAYERS && remainingSteps > 0) {
            placeSnowLayer(world, snowPos, currentLayers + 1);
            return;
        }

        if (!canCompressAt(world, snowPos) || remainingSteps <= 0) {
            return;
        }

        placeSnowBlock(world, snowPos);
        int remainingAfterCompress = remainingSteps - 1;
        if (remainingAfterCompress <= 0 || !canContinueRising(world, snowPos)) {
            return;
        }

        BlockPos nextSnowPos = snowPos.above();
        if (world.getBlockState(nextSnowPos).isAir()) {
            placeSnowLayer(world, nextSnowPos, 1);
        }
    }

    private static void placeSnowLayer(LevelAccessor world, BlockPos pos, int layers) {
        BlockState newSnow = snowLayerState(layers);
        world.setBlock(pos, newSnow, 3);
        if (world instanceof ServerLevel serverLevel) {
            triggerVisibleFall(serverLevel, pos, newSnow);
        }
    }

    private static void placeSnowBlock(LevelAccessor world, BlockPos pos) {
        BlockState snowBlock = Blocks.SNOW_BLOCK.defaultBlockState();
        world.setBlock(pos, snowBlock, 3);
        if (world instanceof ServerLevel serverLevel) {
            triggerVisibleFall(serverLevel, pos, snowBlock);
        }
    }

    private static @Nullable BlockPos findTopSnowColumnPos(LevelAccessor world, BlockPos pos) {
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
        return true;
    }

    private static boolean canCompressAt(LevelAccessor world, BlockPos pos) {
        return true;
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
        return SolarModVariables.MapVariables.get(world).getSolarStage() == SolarStage.STAGE_6;
    }

    private SnowMelt() {}
}
