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

public class FlowerPotFProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		if (world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_overworld")))
				&& SapModVariables.MapVariables.get(world).SolarFlare >= 1 && SapModVariables.MapVariables.get(world).SolarFlare < 6
				&& world.canSeeSkyFromBelowWater(BlockPos.containing(x, y + 1, z))
				&& !((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == Blocks.FLOWER_POT)
				&& Mth.nextDouble(RandomSource.create(), 0, (world.dayTime() / 24000) + 1) <= (world.dayTime() / 24000)) {
			world.setBlock(BlockPos.containing(x, y, z), Blocks.FLOWER_POT.defaultBlockState(), 3);
		}
		if (world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_overworld")))
				&& SapModVariables.MapVariables.get(world).SolarFlare == 2
				&& !(SapModVariables.MapVariables.get(world).TodayTime > 12566 && SapModVariables.MapVariables.get(world).TodayTime < 23450)
				&& y >= 63
				&& !((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == Blocks.FLOWER_POT)
				&& Mth.nextDouble(RandomSource.create(), 0, (world.dayTime() / 24000) + 1) <= (world.dayTime() / 24000)) {
			world.setBlock(BlockPos.containing(x, y, z), Blocks.FLOWER_POT.defaultBlockState(), 3);
		} else if (world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_overworld")))
				&& SapModVariables.MapVariables.get(world).SolarFlare == 3
				&& y >= 63
				&& !((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == Blocks.FLOWER_POT)) {
			world.setBlock(BlockPos.containing(x, y, z), Blocks.FLOWER_POT.defaultBlockState(), 3);
		}else if (world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_overworld")))
				&& SapModVariables.MapVariables.get(world).SolarFlare == 4
				&& y >= 32
				&& !((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == Blocks.FLOWER_POT)) {
			world.setBlock(BlockPos.containing(x, y, z), Blocks.FLOWER_POT.defaultBlockState(), 3);
		}
		if (world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_overworld")))
				&& SapModVariables.MapVariables.get(world).SolarFlare == 5
				&& y >= 8
				&& !((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == Blocks.FLOWER_POT)) {
			world.setBlock(BlockPos.containing(x, y, z), Blocks.FLOWER_POT.defaultBlockState(), 3);
		}
	}
}
