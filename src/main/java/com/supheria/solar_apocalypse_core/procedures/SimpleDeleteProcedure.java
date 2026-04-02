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

public class SimpleDeleteProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		int stage = (int) SapModVariables.MapVariables.get(world).SolarFlare;
		if (world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_overworld")))
				&& stage >= 1 && stage < 6
				&& world.canSeeSkyFromBelowWater(BlockPos.containing(x, y + 1, z))
				&& Mth.nextDouble(RandomSource.create(), 0, (world.dayTime() / 24000) + 1) <= (world.dayTime() / 24000)) {
			world.setBlock(BlockPos.containing(x, y, z), Blocks.AIR.defaultBlockState(), 3);
		}
		if (world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_overworld")))
				&& stage == 2
				&& !(SapModVariables.MapVariables.get(world).TodayTime > 12566 && SapModVariables.MapVariables.get(world).TodayTime < 23450)
				&& y >= StageHeightConfig.getSafeHeight(2)
				&& Mth.nextDouble(RandomSource.create(), 0, (world.dayTime() / 24000) + 1) <= (world.dayTime() / 24000)) {
			world.setBlock(BlockPos.containing(x, y, z), Blocks.AIR.defaultBlockState(), 3);
		} else if (world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_overworld")))
				&& stage == 3
				&& y >= StageHeightConfig.getSafeHeight(2)) {
			world.setBlock(BlockPos.containing(x, y, z), Blocks.AIR.defaultBlockState(), 3);
		}else if (world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_overworld")))
				&& stage == 4
				&& y >= StageHeightConfig.getSafeHeight(4)) {
			world.setBlock(BlockPos.containing(x, y, z), Blocks.AIR.defaultBlockState(), 3);
		}
		if (world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_overworld")))
				&& stage == 5
				&& y >= StageHeightConfig.getSafeHeight(5)) {
			world.setBlock(BlockPos.containing(x, y, z), Blocks.AIR.defaultBlockState(), 3);
		}
	}
}
