package com.supheria.solar_apocalypse_core.procedures;
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

public class TNTFProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		int stage = (int) SapModVariables.MapVariables.get(world).SolarFlare;
		if (stage >= 1 && stage < 6) {
			if (world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_overworld")))
					&& !(SapModVariables.MapVariables.get(world).TodayTime > 12566 && SapModVariables.MapVariables.get(world).TodayTime < 23450)
					&& world.canSeeSkyFromBelowWater(BlockPos.containing(x, y + 1, z))
					&& !world.getLevelData().isRaining()
					&& world.dayTime() >= 48000
					&& (world.getBlockState(BlockPos.containing(x, y+1, z))).getBlock() == Blocks.AIR
					&& Mth.nextDouble(RandomSource.create(), 0, 10) <= ((world.dayTime() / 24000) / 2) + 2) {
				world.setBlock(BlockPos.containing(x, y+1, z), Blocks.FIRE.defaultBlockState(), 3);
			}
		}
		if (stage == 2) {
			if (world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_overworld")))
					&& !(SapModVariables.MapVariables.get(world).TodayTime > 12566 && SapModVariables.MapVariables.get(world).TodayTime < 23450)
					&& world.canSeeSkyFromBelowWater(BlockPos.containing(x, y + 1, z))
					&& !world.getLevelData().isRaining()
					&& world.dayTime() >= 216000
					&& (world.getBlockState(BlockPos.containing(x, y+1, z))).getBlock() == Blocks.AIR
					&& Mth.nextDouble(RandomSource.create(), 0, 15) <= ((world.dayTime() / 24000) / 4) + 3) {
				world.setBlock(BlockPos.containing(x, y+1, z), Blocks.FIRE.defaultBlockState(), 3);
			}
		} else if (stage == 3) {
			if (world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_overworld")))
					&& y >= StageHeightConfig.getSafeHeight(2)
					&& (world.getBlockState(BlockPos.containing(x, y+1, z))).getBlock() == Blocks.AIR) {
				world.setBlock(BlockPos.containing(x, y+1, z), Blocks.FIRE.defaultBlockState(), 3);
			}
		}
		if (stage >= 4 && stage < 6) {
			if (world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_overworld")))
					&& y >= 8
					&& (world.getBlockState(BlockPos.containing(x, y+1, z))).getBlock() == Blocks.AIR) {
				world.setBlock(BlockPos.containing(x, y+1, z), Blocks.FIRE.defaultBlockState(), 3);
			}
		}
	}
}
