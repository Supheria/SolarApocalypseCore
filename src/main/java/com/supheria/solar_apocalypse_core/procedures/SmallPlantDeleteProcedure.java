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

public class SmallPlantDeleteProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		int stage = (int) SapModVariables.MapVariables.get(world).SolarFlare;
		if (world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_overworld")))
				&& stage >= 1 && stage < 6
				&& !(SapModVariables.MapVariables.get(world).TodayTime > 12566 && SapModVariables.MapVariables.get(world).TodayTime < 23450)
				&& world.canSeeSkyFromBelowWater(BlockPos.containing(x, y + 1, z))
				&& !world.getLevelData().isRaining()
				&& Mth.nextDouble(RandomSource.create(), 0, (world.dayTime() / 24000) + 1) <= (world.dayTime() / 24000)) {
			world.setBlock(BlockPos.containing(x, y, z), Blocks.AIR.defaultBlockState(), 3);
		}
		if (world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_overworld")))
				&& stage == 2
				&& world.canSeeSkyFromBelowWater(BlockPos.containing(x, y + 1, z))
				&& !world.getLevelData().isRaining()
				&& Mth.nextDouble(RandomSource.create(), 0, (world.dayTime() / 24000) + 1) <= (world.dayTime() / 24000)) {
			world.setBlock(BlockPos.containing(x, y, z), Blocks.AIR.defaultBlockState(), 3);
		}
		if (world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_overworld")))
				&& SapModVariables.MapVariables.get(world).SolarFlare == 3
				&& y >= 63) {
			world.setBlock(BlockPos.containing(x, y, z), Blocks.AIR.defaultBlockState(), 3);
		}
		if (world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_overworld")))
				&& SapModVariables.MapVariables.get(world).SolarFlare >= 4 && SapModVariables.MapVariables.get(world).SolarFlare < 6
				&& y >= 8) {
			world.setBlock(BlockPos.containing(x, y, z), Blocks.AIR.defaultBlockState(), 3);
		}
	}
}
