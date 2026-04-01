package com.supheria.solar_apocalypse_core.procedures;

import com.supheria.solar_apocalypse_core.init.SapModBlocks;
import com.supheria.solar_apocalypse_core.init.SapModTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;
import net.minecraft.tags.TagKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.BlockPos;

import com.supheria.solar_apocalypse_core.network.SapModVariables;

public class GrassBlockTCDirtProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		if (SapModVariables.MapVariables.get(world).SolarFlare >= 1 && SapModVariables.MapVariables.get(world).SolarFlare < 6) {
			if (world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_overworld")))
					&& !(SapModVariables.MapVariables.get(world).TodayTime > 12566 && SapModVariables.MapVariables.get(world).TodayTime < 23450) //해가 떠있을때
					&& world.canSeeSkyFromBelowWater(BlockPos.containing(x, y + 1, z)) //블럭이 하늘이 보일때
					&& !world.getLevelData().isRaining() //비가 내리지 않을때
					&& world.dayTime() >= 1000 //게임시간이 1000틱하고 같거나 클때
					&& Mth.nextDouble(RandomSource.create(), 0, 10) <= world.dayTime() / 24000 + 1) {
				world.setBlock(BlockPos.containing(x, y, z), Blocks.DIRT.defaultBlockState(), 3);
			}
		}
		if (SapModVariables.MapVariables.get(world).SolarFlare >= 2 && SapModVariables.MapVariables.get(world).SolarFlare < 6) {
			if (world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_overworld")))
					&& world.canSeeSkyFromBelowWater(BlockPos.containing(x, y + 1, z))) {
				world.setBlock(BlockPos.containing(x, y, z), Blocks.COARSE_DIRT.defaultBlockState(), 3);
			}
		}
		if (SapModVariables.MapVariables.get(world).SolarFlare == 3) {
			if (world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_overworld")))
					&& y >= 63) {
				world.setBlock(BlockPos.containing(x, y, z), Blocks.SAND.defaultBlockState(), 3);
				if ((world.getBlockState(BlockPos.containing(x + 1, y, z))).is(SapModTags.Blocks.MOIST_DIRT)) {
					world.setBlock(BlockPos.containing(x + 1, y, z), Blocks.SAND.defaultBlockState(), 3);
				}
				if ((world.getBlockState(BlockPos.containing(x - 1, y, z))).is(SapModTags.Blocks.MOIST_DIRT)) {
					world.setBlock(BlockPos.containing(x - 1, y, z), Blocks.SAND.defaultBlockState(), 3);
				}
				if ((world.getBlockState(BlockPos.containing(x, y, z + 1))).is(SapModTags.Blocks.MOIST_DIRT)) {
					world.setBlock(BlockPos.containing(x, y, z + 1), Blocks.SAND.defaultBlockState(), 3);
				}
				if ((world.getBlockState(BlockPos.containing(x, y, z - 1))).is(SapModTags.Blocks.MOIST_DIRT)) {
					world.setBlock(BlockPos.containing(x, y, z - 1), Blocks.SAND.defaultBlockState(), 3);
				}
			}
		}
		if (SapModVariables.MapVariables.get(world).SolarFlare == 4) {
			if (world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_overworld")))
					&& y >= 8) {
				world.setBlock(BlockPos.containing(x, y, z), SapModBlocks.DUST.get().defaultBlockState(), 3);
				if ((world.getBlockState(BlockPos.containing(x + 1, y, z))).is(SapModTags.Blocks.MOIST_DIRT)) {
					world.setBlock(BlockPos.containing(x + 1, y, z), SapModBlocks.DUST.get().defaultBlockState(), 3);
				}
				if ((world.getBlockState(BlockPos.containing(x - 1, y, z))).is(SapModTags.Blocks.MOIST_DIRT)) {
					world.setBlock(BlockPos.containing(x - 1, y, z), SapModBlocks.DUST.get().defaultBlockState(), 3);
				}
				if ((world.getBlockState(BlockPos.containing(x, y, z + 1))).is(SapModTags.Blocks.MOIST_DIRT)) {
					world.setBlock(BlockPos.containing(x, y, z + 1), SapModBlocks.DUST.get().defaultBlockState(), 3);
				}
				if ((world.getBlockState(BlockPos.containing(x, y, z - 1))).is(SapModTags.Blocks.MOIST_DIRT)) {
					world.setBlock(BlockPos.containing(x, y, z - 1), SapModBlocks.DUST.get().defaultBlockState(), 3);
				}
				if ((world.getBlockState(BlockPos.containing(x + 1, y, z + 1))).is(SapModTags.Blocks.MOIST_DIRT)) {
					world.setBlock(BlockPos.containing(x + 1, y, z + 1), SapModBlocks.DUST.get().defaultBlockState(), 3);
				}
				if ((world.getBlockState(BlockPos.containing(x - 1, y, z + 1))).is(SapModTags.Blocks.MOIST_DIRT)) {
					world.setBlock(BlockPos.containing(x - 1, y, z + 1), SapModBlocks.DUST.get().defaultBlockState(), 3);
				}
				if ((world.getBlockState(BlockPos.containing(x + 1, y, z - 1))).is(SapModTags.Blocks.MOIST_DIRT)) {
					world.setBlock(BlockPos.containing(x + 1, y, z - 1), SapModBlocks.DUST.get().defaultBlockState(), 3);
				}
				if ((world.getBlockState(BlockPos.containing(x - 1, y, z - 1))).is(SapModTags.Blocks.MOIST_DIRT)) {
					world.setBlock(BlockPos.containing(x - 1, y, z - 1), SapModBlocks.DUST.get().defaultBlockState(), 3);
				}
			}
		}
		if (world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_overworld")))
				&& SapModVariables.MapVariables.get(world).SolarFlare == 5
				&& y >= 8) {
			world.setBlock(BlockPos.containing(x, y, z), Blocks.AIR.defaultBlockState(), 3);
			if ((world.getBlockState(BlockPos.containing(x + 1, y, z))).is(BlockTags.DIRT)) {
				world.setBlock(BlockPos.containing(x + 1, y, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getBlockState(BlockPos.containing(x - 1, y, z))).is(BlockTags.DIRT)) {
				world.setBlock(BlockPos.containing(x - 1, y, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getBlockState(BlockPos.containing(x, y, z + 1))).is(BlockTags.DIRT)) {
				world.setBlock(BlockPos.containing(x, y, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getBlockState(BlockPos.containing(x, y, z - 1))).is(BlockTags.DIRT)) {
				world.setBlock(BlockPos.containing(x, y, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getBlockState(BlockPos.containing(x + 1, y, z + 1))).is(BlockTags.DIRT)) {
				world.setBlock(BlockPos.containing(x + 1, y, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getBlockState(BlockPos.containing(x - 1, y, z + 1))).is(BlockTags.DIRT)) {
				world.setBlock(BlockPos.containing(x - 1, y, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getBlockState(BlockPos.containing(x + 1, y, z - 1))).is(BlockTags.DIRT)) {
				world.setBlock(BlockPos.containing(x + 1, y, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getBlockState(BlockPos.containing(x - 1, y, z - 1))).is(BlockTags.DIRT)) {
				world.setBlock(BlockPos.containing(x - 1, y, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getBlockState(BlockPos.containing(x + 1, y - 1, z))).is(BlockTags.DIRT)) {
				world.setBlock(BlockPos.containing(x + 1, y - 1, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getBlockState(BlockPos.containing(x - 1, y - 1, z))).is(BlockTags.DIRT)) {
				world.setBlock(BlockPos.containing(x - 1, y - 1, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getBlockState(BlockPos.containing(x, y - 1, z + 1))).is(BlockTags.DIRT)) {
				world.setBlock(BlockPos.containing(x, y - 1, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getBlockState(BlockPos.containing(x, y - 1, z - 1))).is(BlockTags.DIRT)) {
				world.setBlock(BlockPos.containing(x, y - 1, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getBlockState(BlockPos.containing(x + 1, y - 1, z + 1))).is(BlockTags.DIRT)) {
				world.setBlock(BlockPos.containing(x + 1, y - 1, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getBlockState(BlockPos.containing(x - 1, y - 1, z + 1))).is(BlockTags.DIRT)) {
				world.setBlock(BlockPos.containing(x - 1, y - 1, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getBlockState(BlockPos.containing(x + 1, y - 1, z - 1))).is(BlockTags.DIRT)) {
				world.setBlock(BlockPos.containing(x + 1, y - 1, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getBlockState(BlockPos.containing(x - 1, y - 1, z - 1))).is(BlockTags.DIRT)) {
				world.setBlock(BlockPos.containing(x - 1, y - 1, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getBlockState(BlockPos.containing(x, y - 1, z))).is(BlockTags.DIRT)) {
				world.setBlock(BlockPos.containing(x, y - 1, z), Blocks.AIR.defaultBlockState(), 3);
			}
		}
	}
}
