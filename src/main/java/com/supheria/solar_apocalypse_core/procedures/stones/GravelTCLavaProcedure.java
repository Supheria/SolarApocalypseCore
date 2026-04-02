package com.supheria.solar_apocalypse_core.procedures.stones;
import com.supheria.solar_apocalypse_core.config.solar.StageHeightConfig;

import com.supheria.solar_apocalypse_core.config.solar.SolarStageConfig;
import com.supheria.solar_apocalypse_core.network.SapModVariables;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;

public class GravelTCLavaProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		int stage = (int) SapModVariables.MapVariables.get(world).SolarFlare;

		// 阶段 4-5 的原有逻辑
		if (stage >= 4 && stage < 6) {
			// 使用较低的安全高度（stage 5的-16），既适用于stage 4也适用于stage 5
			int minSafeHeight = Math.min(StageHeightConfig.getSafeHeight(4), StageHeightConfig.getSafeHeight(5));
			if (world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_overworld")))
					&& (world.canSeeSkyFromBelowWater(BlockPos.containing(x, y + 1, z))
					|| ((world.getBlockState(BlockPos.containing(x, y + 1, z))).getBlock() == Blocks.LAVA)
					|| ((world.getBlockState(BlockPos.containing(x, y - 1, z))).getBlock() == Blocks.LAVA)
					|| ((world.getBlockState(BlockPos.containing(x + 1, y, z))).getBlock() == Blocks.LAVA)
					|| ((world.getBlockState(BlockPos.containing(x - 1, y, z))).getBlock() == Blocks.LAVA)
					|| ((world.getBlockState(BlockPos.containing(x, y, z + 1))).getBlock() == Blocks.LAVA)
					|| ((world.getBlockState(BlockPos.containing(x, y, z - 1))).getBlock() == Blocks.LAVA))
					&& y >= minSafeHeight
					&& Mth.nextDouble(RandomSource.create(), 0, 2) <= 1) {
				world.setBlock(BlockPos.containing(x, y, z), Blocks.LAVA.defaultBlockState(), 3);
			}
		}

		// 阶段 5 的原有逻辑
		if (stage == 5) {
			if (world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_overworld")))
					&& y >= StageHeightConfig.getExtraDamageHeight(stage)) {
				world.setBlock(BlockPos.containing(x, y, z), Blocks.LAVA.defaultBlockState(), 3);
			}
		}

		// 第六阶段：加速所有方块向岩浆转换（无高度限制）
		if (stage == 6) {
			if (world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_overworld")))) {
				// 使用配置的转换速率倍数
				int transformRate = SolarStageConfig.SOLAR_STAGE_VALUES.collapseBlockTransformRate.get();
				double probability = Math.min(1.0, (double) transformRate / 2.0); // 转换速率倍数越高，概率越高

				if (Mth.nextDouble(RandomSource.create(), 0, 1) <= probability) {
					world.setBlock(BlockPos.containing(x, y, z), Blocks.LAVA.defaultBlockState(), 3);
				}
			}
		}
	}
}

