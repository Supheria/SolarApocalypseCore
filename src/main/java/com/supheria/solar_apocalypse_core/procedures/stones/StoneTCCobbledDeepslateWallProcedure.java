package com.supheria.solar_apocalypse_core.procedures.stones;
import com.supheria.solar_apocalypse_core.config.solar.StageHeightConfig;

import com.supheria.solar_apocalypse_core.network.SapModVariables;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;

public class StoneTCCobbledDeepslateWallProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		int stage = (int) SapModVariables.MapVariables.get(world).SolarFlare;
		if (stage >= 2 && stage < 6) {
			if (world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_overworld")))
					&& (world.canSeeSkyFromBelowWater(BlockPos.containing(x, y + 1, z))
					|| ((world.getBlockState(BlockPos.containing(x, y + 1, z))).getBlock() == Blocks.LAVA)
					|| ((world.getBlockState(BlockPos.containing(x, y - 1, z))).getBlock() == Blocks.LAVA)
					|| ((world.getBlockState(BlockPos.containing(x + 1, y, z))).getBlock() == Blocks.LAVA)
					|| ((world.getBlockState(BlockPos.containing(x - 1, y, z))).getBlock() == Blocks.LAVA)
					|| ((world.getBlockState(BlockPos.containing(x, y, z + 1))).getBlock() == Blocks.LAVA)
					|| ((world.getBlockState(BlockPos.containing(x, y, z - 1))).getBlock() == Blocks.LAVA))
					&& y >= Math.min(Math.min(StageHeightConfig.getSafeHeight(2), StageHeightConfig.getSafeHeight(3)),
							Math.min(StageHeightConfig.getSafeHeight(4), StageHeightConfig.getSafeHeight(5)))
					&& Mth.nextDouble(RandomSource.create(), 0, (world.dayTime() / 24000) + 5) <= (world.dayTime() / 16000)) {
				world.setBlock(BlockPos.containing(x, y, z), Blocks.COBBLED_DEEPSLATE_WALL.defaultBlockState(), 3);
			}
		}
		if (stage == 3) {
			if (world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_overworld")))
					&& y >= StageHeightConfig.getSafeHeight(2)
					&& Mth.nextDouble(RandomSource.create(), 0, (world.dayTime() / 24000) + 5) <= (world.dayTime() / 16000)) {
				world.setBlock(BlockPos.containing(x, y, z), Blocks.COBBLED_DEEPSLATE_WALL.defaultBlockState(), 3);
			}
		}
		if (stage >= 4 && stage < 6) {
			if (world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_overworld")))
					&& y >= StageHeightConfig.getSafeHeight(4)) {
				world.setBlock(BlockPos.containing(x, y, z), Blocks.COBBLED_DEEPSLATE_WALL.defaultBlockState(), 3);
			}
		}
	}
}

