package com.supheria.solar_apocalypse_core.procedures;

import com.supheria.solar_apocalypse_core.config.solar.SolarStageConfig;
import com.supheria.solar_apocalypse_core.network.SapModVariables;
import com.supheria.solar_apocalypse_core.world.SolarPhase;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

/**
 * 第六阶段（坍缺）的永恒降雪和积雪堆积系统
 * 在地表逐步积累雪层，最大深度可配置
 */
public class EternalSnowProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		// 仅在第六阶段执行
		SolarPhase currentPhase = SapModVariables.MapVariables.get(world).getCurrentPhase();
		if (currentPhase != SolarPhase.COLLAPSE) {
			return;
		}

		BlockPos pos = BlockPos.containing(x, y, z);
		BlockState blockState = world.getBlockState(pos);
		Block block = blockState.getBlock();

		// 检查是否为有效的积雪表面（固体方块）
		if (!isValidSnowSurface(block)) {
			return;
		}

		// 在上方放置雪层
		BlockPos posAbove = pos.above();
		BlockState blockAbove = world.getBlockState(posAbove);

		// 如果上方是空气，放置一层新的雪
		if (blockAbove.getBlock() == Blocks.AIR) {
			BlockState newSnowLayer = Blocks.SNOW.defaultBlockState()
					.setValue(SnowLayerBlock.LAYERS, 1);
			world.setBlock(posAbove, newSnowLayer, 3);
		}
		// 如果上方已经是雪层，尝试增加层数
		else if (blockAbove.getBlock() instanceof SnowLayerBlock) {
			int currentLayers = blockAbove.getValue(SnowLayerBlock.LAYERS);
			int maxSnowLayer = SolarStageConfig.SOLAR_STAGE_VALUES.collapseMaxSnowLayer.get();

			if (currentLayers < maxSnowLayer) {
				// 增加雪层数量
				BlockState thickerSnow = blockAbove.setValue(SnowLayerBlock.LAYERS, currentLayers + 1);
				world.setBlock(posAbove, thickerSnow, 3);
			} else if (currentLayers >= maxSnowLayer) {
				// 已达到最大层数，在上方放置新的雪层
				BlockPos posAboveAbove = posAbove.above();
				if (world.getBlockState(posAboveAbove).getBlock() == Blocks.AIR) {
					BlockState newSnowLayer = Blocks.SNOW.defaultBlockState()
							.setValue(SnowLayerBlock.LAYERS, 1);
					world.setBlock(posAboveAbove, newSnowLayer, 3);
				}
			}
		}
	}

	/**
	 * 检查一个方块是否是有效的积雪表面
	 * 只有固体、不透明的方块才能作为积雪的支撑
	 */
	private static boolean isValidSnowSurface(Block block) {
		// 排除：岩浆、水、空气等不能支撑雪的方块
		return !(block == Blocks.AIR ||
				block == Blocks.WATER ||
				block == Blocks.LAVA ||
				block == Blocks.GLASS ||
				block == Blocks.GLASS_PANE ||
				block == Blocks.BEDROCK ||
				block == Blocks.SNOW ||
				block == Blocks.SNOW_BLOCK);
	}
}
