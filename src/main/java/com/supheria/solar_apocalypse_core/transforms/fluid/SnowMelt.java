package com.supheria.solar_apocalypse_core.transforms.fluid;

import com.supheria.solar_apocalypse_core.BlockTransform;
import com.supheria.solar_apocalypse_core.config.solar.SolarStageConfig;
import com.supheria.solar_apocalypse_core.network.SapModVariables;
import com.supheria.solar_apocalypse_core.world.SolarStageHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 第六阶段（坍缺）永恒降雪：在地表逐步堆积雪层。
 */
public class SnowMelt {

    public static final BlockTransform TRANSFORM = SnowMelt::transform;

    private static void transform(LevelAccessor world, double x, double y, double z) {
        int currentPhase = SapModVariables.MapVariables.get(world).getCurrentStage();
        if (currentPhase != SolarStageHelper.STAGE_6) return;

        BlockPos pos = BlockPos.containing(x, y, z);
        BlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();

        if (!isValidSnowSurface(block)) return;

        BlockPos posAbove = pos.above();
        BlockState blockAbove = world.getBlockState(posAbove);

        if (blockAbove.getBlock() == Blocks.AIR) {
            BlockState newSnowLayer = Blocks.SNOW.defaultBlockState()
                    .setValue(SnowLayerBlock.LAYERS, 1);
            world.setBlock(posAbove, newSnowLayer, 3);
        } else if (blockAbove.getBlock() instanceof SnowLayerBlock) {
            int currentLayers = blockAbove.getValue(SnowLayerBlock.LAYERS);
            int maxSnowLayer = SolarStageConfig.SOLAR_STAGE_VALUES.collapseMaxSnowLayer.get();

            if (currentLayers < maxSnowLayer) {
                BlockState thickerSnow = blockAbove.setValue(SnowLayerBlock.LAYERS, currentLayers + 1);
                world.setBlock(posAbove, thickerSnow, 3);
            } else {
                BlockPos posAboveAbove = posAbove.above();
                if (world.getBlockState(posAboveAbove).getBlock() == Blocks.AIR) {
                    BlockState newSnowLayer = Blocks.SNOW.defaultBlockState()
                            .setValue(SnowLayerBlock.LAYERS, 1);
                    world.setBlock(posAboveAbove, newSnowLayer, 3);
                }
            }
        }
    }

    private static boolean isValidSnowSurface(Block block) {
        return !(block == Blocks.AIR
                || block == Blocks.WATER
                || block == Blocks.LAVA
                || block == Blocks.GLASS
                || block == Blocks.GLASS_PANE
                || block == Blocks.BEDROCK
                || block == Blocks.SNOW
                || block == Blocks.SNOW_BLOCK);
    }

    private SnowMelt() {}
}
