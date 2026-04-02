package com.supheria.solar_apocalypse_core.procedures;
import com.supheria.solar_apocalypse_core.config.solar.StageHeightConfig;

import com.supheria.solar_apocalypse_core.init.SapModBlocks;
import com.supheria.solar_apocalypse_core.network.SapModVariables;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;

public class WitheredLeavesBlockFProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		int stage = (int) SapModVariables.MapVariables.get(world).SolarFlare;
		if (!((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == SapModBlocks.WITHERED_LEAVES.get())
				&& stage >= 1 && stage < 6) {
			if (world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_overworld")))
					&& !(SapModVariables.MapVariables.get(world).TodayTime > 12566 && SapModVariables.MapVariables.get(world).TodayTime < 23450)
					&& (world.canSeeSkyFromBelowWater(BlockPos.containing(x, y + 1, z)) || (world.getBlockState(BlockPos.containing(x, y+1, z))).getBlock() == SapModBlocks.WITHERED_LEAVES.get())
					&& !world.getLevelData().isRaining()
					&& world.dayTime() >= 48000
					&& Mth.nextDouble(RandomSource.create(), 0, 10) <= (world.dayTime() / 24000) + 3) {
				world.setBlock(BlockPos.containing(x, y, z), SapModBlocks.WITHERED_LEAVES.get().defaultBlockState(), 3);
			}
		}
		if (!((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == SapModBlocks.WITHERED_LEAVES.get())
				&& stage >= 2 && stage < 6) {
			if (world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_overworld")))
					&& !(SapModVariables.MapVariables.get(world).TodayTime > 12566 && SapModVariables.MapVariables.get(world).TodayTime < 23450)
					&& !world.getLevelData().isRaining()
					&& world.dayTime() >= 240000
					&& y >= StageHeightConfig.getSafeHeight(2)
					&& Mth.nextDouble(RandomSource.create(), 0, 10) <= ((world.dayTime() / 24000) / 3) + 5) {
				world.setBlock(BlockPos.containing(x, y, z), SapModBlocks.WITHERED_LEAVES.get().defaultBlockState(), 3);
			}
		}
		if (stage >= 2 && stage < 6) {
			if (world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_overworld")))
					&& !(SapModVariables.MapVariables.get(world).TodayTime > 12566 && SapModVariables.MapVariables.get(world).TodayTime < 23450)
					&& (world.canSeeSkyFromBelowWater(BlockPos.containing(x, y + 1, z)) || (world.getBlockState(BlockPos.containing(x, y+1, z))).getBlock() == SapModBlocks.WITHERED_LEAVES.get())
					&& !world.getLevelData().isRaining()
					&& ((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == SapModBlocks.WITHERED_LEAVES.get())
					&& Mth.nextDouble(RandomSource.create(), 0, 10) <= ((world.dayTime() / 24000) / 3) + 2) {
				world.setBlock(BlockPos.containing(x, y + 1, z), Blocks.FIRE.defaultBlockState(), 3);
			}
		}
		if (stage == 2) {
			if (world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_overworld")))
					&& !(SapModVariables.MapVariables.get(world).TodayTime > 12566 && SapModVariables.MapVariables.get(world).TodayTime < 23450)
					&& !world.getLevelData().isRaining()
					&& world.dayTime() >= 240000
					&& ((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == SapModBlocks.WITHERED_LEAVES.get()
					&& y >= 63)
					&& Mth.nextDouble(RandomSource.create(), 0, 15) <= ((world.dayTime() / 24000) / 4) + 3) {
				world.setBlock(BlockPos.containing(x, y, z), Blocks.FIRE.defaultBlockState(), 3);
			}
		}
		if (stage >= 3 && stage < 6
				&& (world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == SapModBlocks.WITHERED_LEAVES.get()) {
			if (world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_overworld")))) {
				world.setBlock(BlockPos.containing(x, y, z), Blocks.FIRE.defaultBlockState(), 3);
				if ((world.getBlockState(BlockPos.containing(x + 1, y, z))).is(BlockTags.LEAVES)) {
					world.setBlock(BlockPos.containing(x + 1, y, z), Blocks.FIRE.defaultBlockState(), 3);
				}
				if ((world.getBlockState(BlockPos.containing(x - 1, y, z))).is(BlockTags.LEAVES)) {
					world.setBlock(BlockPos.containing(x - 1, y, z), Blocks.FIRE.defaultBlockState(), 3);
				}
				if ((world.getBlockState(BlockPos.containing(x, y, z + 1))).is(BlockTags.LEAVES)) {
					world.setBlock(BlockPos.containing(x, y, z + 1), Blocks.FIRE.defaultBlockState(), 3);
				}
				if ((world.getBlockState(BlockPos.containing(x, y, z - 1))).is(BlockTags.LEAVES)) {
					world.setBlock(BlockPos.containing(x, y, z - 1), Blocks.FIRE.defaultBlockState(), 3);
				}
			}
		}
		if (stage >= 3 && stage < 6
				&& world.dayTime() >= 360000
				&& !((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == SapModBlocks.WITHERED_LEAVES.get())) {
			if (world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_overworld")))
					&& y >= 63) {
				world.setBlock(BlockPos.containing(x, y, z), Blocks.FIRE.defaultBlockState(), 3);
				if ((world.getBlockState(BlockPos.containing(x + 1, y, z))).is(BlockTags.LEAVES)) {
					world.setBlock(BlockPos.containing(x + 1, y, z), Blocks.FIRE.defaultBlockState(), 3);
				}
				if ((world.getBlockState(BlockPos.containing(x - 1, y, z))).is(BlockTags.LEAVES)) {
					world.setBlock(BlockPos.containing(x - 1, y, z), Blocks.FIRE.defaultBlockState(), 3);
				}
				if ((world.getBlockState(BlockPos.containing(x, y, z + 1))).is(BlockTags.LEAVES)) {
					world.setBlock(BlockPos.containing(x, y, z + 1), Blocks.FIRE.defaultBlockState(), 3);
				}
				if ((world.getBlockState(BlockPos.containing(x, y, z - 1))).is(BlockTags.LEAVES)) {
					world.setBlock(BlockPos.containing(x, y, z - 1), Blocks.FIRE.defaultBlockState(), 3);
				}
			}
		}
		if (stage >= 3 && stage < 6
				&& world.dayTime() >= 360000
				&& !((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == SapModBlocks.WITHERED_LEAVES.get())) {
			if (world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_overworld")))
					&& y < 63 && y >= 8) {
				if (Mth.nextDouble(RandomSource.create(), 0, 2) <= 1){
					world.setBlock(BlockPos.containing(x, y, z), SapModBlocks.WITHERED_LEAVES.get().defaultBlockState(), 3);
				} else {
					world.setBlock(BlockPos.containing(x, y, z), Blocks.FIRE.defaultBlockState(), 3);
					if ((world.getBlockState(BlockPos.containing(x + 1, y, z))).is(BlockTags.LEAVES)) {
						world.setBlock(BlockPos.containing(x + 1, y, z), Blocks.FIRE.defaultBlockState(), 3);
					}
					if ((world.getBlockState(BlockPos.containing(x - 1, y, z))).is(BlockTags.LEAVES)) {
						world.setBlock(BlockPos.containing(x - 1, y, z), Blocks.FIRE.defaultBlockState(), 3);
					}
					if ((world.getBlockState(BlockPos.containing(x, y, z + 1))).is(BlockTags.LEAVES)) {
						world.setBlock(BlockPos.containing(x, y, z + 1), Blocks.FIRE.defaultBlockState(), 3);
					}
					if ((world.getBlockState(BlockPos.containing(x, y, z - 1))).is(BlockTags.LEAVES)) {
						world.setBlock(BlockPos.containing(x, y, z - 1), Blocks.FIRE.defaultBlockState(), 3);
					}
				}
			}
		}
		if (stage == 4) {
			if (world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_overworld")))
					&& y >= 64) {
				if (Mth.nextDouble(RandomSource.create(), 0, 2) <= 1){
					world.setBlock(BlockPos.containing(x, y, z), Blocks.FIRE.defaultBlockState(), 3);
					if ((world.getBlockState(BlockPos.containing(x + 1, y, z))).is(BlockTags.LEAVES)) {
						world.setBlock(BlockPos.containing(x + 1, y, z), Blocks.FIRE.defaultBlockState(), 3);
					}
					if ((world.getBlockState(BlockPos.containing(x - 1, y, z))).is(BlockTags.LEAVES)) {
						world.setBlock(BlockPos.containing(x - 1, y, z), Blocks.FIRE.defaultBlockState(), 3);
					}
					if ((world.getBlockState(BlockPos.containing(x, y, z + 1))).is(BlockTags.LEAVES)) {
						world.setBlock(BlockPos.containing(x, y, z + 1), Blocks.FIRE.defaultBlockState(), 3);
					}
					if ((world.getBlockState(BlockPos.containing(x, y, z - 1))).is(BlockTags.LEAVES)) {
						world.setBlock(BlockPos.containing(x, y, z - 1), Blocks.FIRE.defaultBlockState(), 3);
					}
					if ((world.getBlockState(BlockPos.containing(x + 1, y, z + 1))).is(BlockTags.LEAVES)) {
						world.setBlock(BlockPos.containing(x + 1, y, z + 1), Blocks.FIRE.defaultBlockState(), 3);
					}
					if ((world.getBlockState(BlockPos.containing(x - 1, y, z + 1))).is(BlockTags.LEAVES)) {
						world.setBlock(BlockPos.containing(x - 1, y, z + 1), Blocks.FIRE.defaultBlockState(), 3);
					}
					if ((world.getBlockState(BlockPos.containing(x + 1, y, z - 1))).is(BlockTags.LEAVES)) {
						world.setBlock(BlockPos.containing(x + 1, y, z - 1), Blocks.FIRE.defaultBlockState(), 3);
					}
					if ((world.getBlockState(BlockPos.containing(x - 1, y, z - 1))).is(BlockTags.LEAVES)) {
						world.setBlock(BlockPos.containing(x - 1, y, z - 1), Blocks.FIRE.defaultBlockState(), 3);
					}
				} else {
					world.setBlock(BlockPos.containing(x, y, z), Blocks.AIR.defaultBlockState(), 3);
					if ((world.getBlockState(BlockPos.containing(x + 1, y, z))).is(BlockTags.LEAVES)) {
						world.setBlock(BlockPos.containing(x + 1, y, z), SapModBlocks.DUST.get().defaultBlockState(), 3);
					}
					if ((world.getBlockState(BlockPos.containing(x - 1, y, z))).is(BlockTags.LEAVES)) {
						world.setBlock(BlockPos.containing(x - 1, y, z), SapModBlocks.DUST.get().defaultBlockState(), 3);
					}
					if ((world.getBlockState(BlockPos.containing(x, y, z + 1))).is(BlockTags.LEAVES)) {
						world.setBlock(BlockPos.containing(x, y, z + 1), SapModBlocks.DUST.get().defaultBlockState(), 3);
					}
					if ((world.getBlockState(BlockPos.containing(x, y, z - 1))).is(BlockTags.LEAVES)) {
						world.setBlock(BlockPos.containing(x, y, z - 1), SapModBlocks.DUST.get().defaultBlockState(), 3);
					}
					if ((world.getBlockState(BlockPos.containing(x + 1, y, z + 1))).is(BlockTags.LEAVES)) {
						world.setBlock(BlockPos.containing(x + 1, y, z + 1), SapModBlocks.DUST.get().defaultBlockState(), 3);
					}
					if ((world.getBlockState(BlockPos.containing(x - 1, y, z + 1))).is(BlockTags.LEAVES)) {
						world.setBlock(BlockPos.containing(x - 1, y, z + 1), SapModBlocks.DUST.get().defaultBlockState(), 3);
					}
					if ((world.getBlockState(BlockPos.containing(x + 1, y, z - 1))).is(BlockTags.LEAVES)) {
						world.setBlock(BlockPos.containing(x + 1, y, z - 1), SapModBlocks.DUST.get().defaultBlockState(), 3);
					}
					if ((world.getBlockState(BlockPos.containing(x - 1, y, z - 1))).is(BlockTags.LEAVES)) {
						world.setBlock(BlockPos.containing(x - 1, y, z - 1), SapModBlocks.DUST.get().defaultBlockState(), 3);
					}
				}
			}
		}
		if (world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_overworld")))
				&& stage == 5
				&& y >= 8) {
				if (Mth.nextDouble(RandomSource.create(), 0, 2) <= 1){
					world.setBlock(BlockPos.containing(x, y, z), Blocks.FIRE.defaultBlockState(), 3);
					if ((world.getBlockState(BlockPos.containing(x + 1, y, z))).is(BlockTags.LEAVES)) {
						world.setBlock(BlockPos.containing(x + 1, y, z), Blocks.FIRE.defaultBlockState(), 3);
					}
					if ((world.getBlockState(BlockPos.containing(x - 1, y, z))).is(BlockTags.LEAVES)) {
						world.setBlock(BlockPos.containing(x - 1, y, z), Blocks.FIRE.defaultBlockState(), 3);
					}
					if ((world.getBlockState(BlockPos.containing(x, y, z + 1))).is(BlockTags.LEAVES)) {
						world.setBlock(BlockPos.containing(x, y, z + 1), Blocks.FIRE.defaultBlockState(), 3);
					}
					if ((world.getBlockState(BlockPos.containing(x, y, z - 1))).is(BlockTags.LEAVES)) {
						world.setBlock(BlockPos.containing(x, y, z - 1), Blocks.FIRE.defaultBlockState(), 3);
					}
					if ((world.getBlockState(BlockPos.containing(x + 1, y, z + 1))).is(BlockTags.LEAVES)) {
						world.setBlock(BlockPos.containing(x + 1, y, z + 1), Blocks.FIRE.defaultBlockState(), 3);
					}
					if ((world.getBlockState(BlockPos.containing(x - 1, y, z + 1))).is(BlockTags.LEAVES)) {
						world.setBlock(BlockPos.containing(x - 1, y, z + 1), Blocks.FIRE.defaultBlockState(), 3);
					}
					if ((world.getBlockState(BlockPos.containing(x + 1, y, z - 1))).is(BlockTags.LEAVES)) {
						world.setBlock(BlockPos.containing(x + 1, y, z - 1), Blocks.FIRE.defaultBlockState(), 3);
					}
					if ((world.getBlockState(BlockPos.containing(x - 1, y, z - 1))).is(BlockTags.LEAVES)) {
						world.setBlock(BlockPos.containing(x - 1, y, z - 1), Blocks.FIRE.defaultBlockState(), 3);
					}
					if ((world.getBlockState(BlockPos.containing(x + 1, y - 1, z))).is(BlockTags.LEAVES)) {
						world.setBlock(BlockPos.containing(x + 1, y - 1, z), Blocks.FIRE.defaultBlockState(), 3);
					}
					if ((world.getBlockState(BlockPos.containing(x - 1, y - 1, z))).is(BlockTags.LEAVES)) {
						world.setBlock(BlockPos.containing(x - 1, y - 1, z), Blocks.FIRE.defaultBlockState(), 3);
					}
					if ((world.getBlockState(BlockPos.containing(x, y - 1, z + 1))).is(BlockTags.LEAVES)) {
						world.setBlock(BlockPos.containing(x, y - 1, z + 1), Blocks.FIRE.defaultBlockState(), 3);
					}
					if ((world.getBlockState(BlockPos.containing(x, y - 1, z - 1))).is(BlockTags.LEAVES)) {
						world.setBlock(BlockPos.containing(x, y - 1, z - 1), Blocks.FIRE.defaultBlockState(), 3);
					}
					if ((world.getBlockState(BlockPos.containing(x + 1, y - 1, z + 1))).is(BlockTags.LEAVES)) {
						world.setBlock(BlockPos.containing(x + 1, y - 1, z + 1), Blocks.FIRE.defaultBlockState(), 3);
					}
					if ((world.getBlockState(BlockPos.containing(x - 1, y - 1, z + 1))).is(BlockTags.LEAVES)) {
						world.setBlock(BlockPos.containing(x - 1, y - 1, z + 1), Blocks.FIRE.defaultBlockState(), 3);
					}
					if ((world.getBlockState(BlockPos.containing(x + 1, y - 1, z - 1))).is(BlockTags.LEAVES)) {
						world.setBlock(BlockPos.containing(x + 1, y - 1, z - 1), Blocks.FIRE.defaultBlockState(), 3);
					}
					if ((world.getBlockState(BlockPos.containing(x - 1, y - 1, z - 1))).is(BlockTags.LEAVES)) {
						world.setBlock(BlockPos.containing(x - 1, y - 1, z - 1), Blocks.FIRE.defaultBlockState(), 3);
					}
					if ((world.getBlockState(BlockPos.containing(x, y - 1, z))).is(BlockTags.LEAVES)) {
						world.setBlock(BlockPos.containing(x, y - 1, z), Blocks.FIRE.defaultBlockState(), 3);
					}
			} else {
					world.setBlock(BlockPos.containing(x, y, z), Blocks.AIR.defaultBlockState(), 3);
					if ((world.getBlockState(BlockPos.containing(x + 1, y, z))).is(BlockTags.LEAVES)) {
						world.setBlock(BlockPos.containing(x + 1, y, z), Blocks.AIR.defaultBlockState(), 3);
					}
					if ((world.getBlockState(BlockPos.containing(x - 1, y, z))).is(BlockTags.LEAVES)) {
						world.setBlock(BlockPos.containing(x - 1, y, z), Blocks.AIR.defaultBlockState(), 3);
					}
					if ((world.getBlockState(BlockPos.containing(x, y, z + 1))).is(BlockTags.LEAVES)) {
						world.setBlock(BlockPos.containing(x, y, z + 1), Blocks.AIR.defaultBlockState(), 3);
					}
					if ((world.getBlockState(BlockPos.containing(x, y, z - 1))).is(BlockTags.LEAVES)) {
						world.setBlock(BlockPos.containing(x, y, z - 1), Blocks.AIR.defaultBlockState(), 3);
					}
					if ((world.getBlockState(BlockPos.containing(x + 1, y, z + 1))).is(BlockTags.LEAVES)) {
						world.setBlock(BlockPos.containing(x + 1, y, z + 1), Blocks.AIR.defaultBlockState(), 3);
					}
					if ((world.getBlockState(BlockPos.containing(x - 1, y, z + 1))).is(BlockTags.LEAVES)) {
						world.setBlock(BlockPos.containing(x - 1, y, z + 1), Blocks.AIR.defaultBlockState(), 3);
					}
					if ((world.getBlockState(BlockPos.containing(x + 1, y, z - 1))).is(BlockTags.LEAVES)) {
						world.setBlock(BlockPos.containing(x + 1, y, z - 1), Blocks.AIR.defaultBlockState(), 3);
					}
					if ((world.getBlockState(BlockPos.containing(x - 1, y, z - 1))).is(BlockTags.LEAVES)) {
						world.setBlock(BlockPos.containing(x - 1, y, z - 1), Blocks.AIR.defaultBlockState(), 3);
					}
					if ((world.getBlockState(BlockPos.containing(x + 1, y - 1, z))).is(BlockTags.LEAVES)) {
						world.setBlock(BlockPos.containing(x + 1, y - 1, z), Blocks.AIR.defaultBlockState(), 3);
					}
					if ((world.getBlockState(BlockPos.containing(x - 1, y - 1, z))).is(BlockTags.LEAVES)) {
						world.setBlock(BlockPos.containing(x - 1, y - 1, z), Blocks.AIR.defaultBlockState(), 3);
					}
					if ((world.getBlockState(BlockPos.containing(x, y - 1, z + 1))).is(BlockTags.LEAVES)) {
						world.setBlock(BlockPos.containing(x, y - 1, z + 1), Blocks.AIR.defaultBlockState(), 3);
					}
					if ((world.getBlockState(BlockPos.containing(x, y - 1, z - 1))).is(BlockTags.LEAVES)) {
						world.setBlock(BlockPos.containing(x, y - 1, z - 1), Blocks.AIR.defaultBlockState(), 3);
					}
					if ((world.getBlockState(BlockPos.containing(x + 1, y - 1, z + 1))).is(BlockTags.LEAVES)) {
						world.setBlock(BlockPos.containing(x + 1, y - 1, z + 1), Blocks.AIR.defaultBlockState(), 3);
					}
					if ((world.getBlockState(BlockPos.containing(x - 1, y - 1, z + 1))).is(BlockTags.LEAVES)) {
						world.setBlock(BlockPos.containing(x - 1, y - 1, z + 1), Blocks.AIR.defaultBlockState(), 3);
					}
					if ((world.getBlockState(BlockPos.containing(x + 1, y - 1, z - 1))).is(BlockTags.LEAVES)) {
						world.setBlock(BlockPos.containing(x + 1, y - 1, z - 1), Blocks.AIR.defaultBlockState(), 3);
					}
					if ((world.getBlockState(BlockPos.containing(x - 1, y - 1, z - 1))).is(BlockTags.LEAVES)) {
						world.setBlock(BlockPos.containing(x - 1, y - 1, z - 1), Blocks.AIR.defaultBlockState(), 3);
					}
					if ((world.getBlockState(BlockPos.containing(x, y - 1, z))).is(BlockTags.LEAVES)) {
						world.setBlock(BlockPos.containing(x, y - 1, z), Blocks.AIR.defaultBlockState(), 3);
					}
				}
		}
	}
}
