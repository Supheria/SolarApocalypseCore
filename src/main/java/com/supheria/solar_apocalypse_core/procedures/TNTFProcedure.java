package com.supheria.solar_apocalypse_core.procedures;

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
		if (SapModVariables.MapVariables.get(world).SolarFlare >= 1 && SapModVariables.MapVariables.get(world).SolarFlare < 6) {
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
		if (SapModVariables.MapVariables.get(world).SolarFlare == 2) {
			if (world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_overworld")))
					&& !(SapModVariables.MapVariables.get(world).TodayTime > 12566 && SapModVariables.MapVariables.get(world).TodayTime < 23450)
					&& world.canSeeSkyFromBelowWater(BlockPos.containing(x, y + 1, z))
					&& !world.getLevelData().isRaining()
					&& world.dayTime() >= 216000
					&& (world.getBlockState(BlockPos.containing(x, y+1, z))).getBlock() == Blocks.AIR
					&& Mth.nextDouble(RandomSource.create(), 0, 15) <= ((world.dayTime() / 24000) / 4) + 3) {
				world.setBlock(BlockPos.containing(x, y+1, z), Blocks.FIRE.defaultBlockState(), 3);
			}
		} else if (SapModVariables.MapVariables.get(world).SolarFlare == 3) {
			if (world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_overworld")))
					&& y >= 63
					&& (world.getBlockState(BlockPos.containing(x, y+1, z))).getBlock() == Blocks.AIR) {
				world.setBlock(BlockPos.containing(x, y+1, z), Blocks.FIRE.defaultBlockState(), 3);
			}
		}
		if (SapModVariables.MapVariables.get(world).SolarFlare >= 4 && SapModVariables.MapVariables.get(world).SolarFlare < 6) {
			if (world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_overworld")))
					&& y >= 8
					&& (world.getBlockState(BlockPos.containing(x, y+1, z))).getBlock() == Blocks.AIR) {
				world.setBlock(BlockPos.containing(x, y+1, z), Blocks.FIRE.defaultBlockState(), 3);
			}
		}
	}
}
