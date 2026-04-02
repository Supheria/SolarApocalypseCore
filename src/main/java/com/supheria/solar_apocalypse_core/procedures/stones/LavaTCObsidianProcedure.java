package com.supheria.solar_apocalypse_core.procedures.stones;

import com.supheria.solar_apocalypse_core.config.solar.SolarStageConfig;
import com.supheria.solar_apocalypse_core.network.SapModVariables;
import com.supheria.solar_apocalypse_core.world.SolarPhase;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;

/**
 * 第六阶段（坍缩）中岩浆逐步转换为黑曜石
 * 模拟自然冷却过程：相邻有水时加速转换
 */
public class LavaTCObsidianProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		// 仅在第六阶段执行
		SolarPhase currentPhase = SapModVariables.MapVariables.get(world).getCurrentPhase();
		if (currentPhase != SolarPhase.COLLAPSE) {
			return;
		}

		BlockPos pos = BlockPos.containing(x, y, z);

		// 检查是否在主世界
		if (!world.getBiome(pos).is(TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_overworld")))) {
			return;
		}

		// 检查是否有相邻水块（冷却效应）
		boolean hasAdjacentWater = hasAdjacentWater(world, x, y, z);

		// 如果有相邻水块或随机概率满足，转换为黑曜石
		double probability = hasAdjacentWater ? 0.8 : 0.3; // 有水时概率80%，无水时30%
		if (Mth.nextDouble(RandomSource.create(), 0, 1) <= probability) {
			world.setBlock(pos, Blocks.OBSIDIAN.defaultBlockState(), 3);
		}
	}

	/**
	 * 检查相邻方块中是否有水
	 */
	private static boolean hasAdjacentWater(LevelAccessor world, double x, double y, double z) {
		return isWater(world, x + 1, y, z) ||
				isWater(world, x - 1, y, z) ||
				isWater(world, x, y + 1, z) ||
				isWater(world, x, y - 1, z) ||
				isWater(world, x, y, z + 1) ||
				isWater(world, x, y, z - 1);
	}

	/**
	 * 检查指定位置是否为水方块
	 */
	private static boolean isWater(LevelAccessor world, double x, double y, double z) {
		// 检查流体状态中是否有水
		return world.getFluidState(BlockPos.containing(x, y, z)).is(FluidTags.WATER);
	}
}
