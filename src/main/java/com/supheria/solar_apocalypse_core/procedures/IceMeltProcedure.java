package com.supheria.solar_apocalypse_core.procedures;
import com.supheria.solar_apocalypse_core.config.solar.StageHeightConfig;

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

public class IceMeltProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		int stage = (int) SapModVariables.MapVariables.get(world).SolarFlare;
		if (!((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == Blocks.LAVA)) {
			if (world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_overworld")))
					&& !(SapModVariables.MapVariables.get(world).TodayTime > 12566 && SapModVariables.MapVariables.get(world).TodayTime < 23450)) {
				if ((((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == Blocks.ICE) || ((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == Blocks.FROSTED_ICE))) {
					if (Mth.nextDouble(RandomSource.create(), 0, 10) <= (world.dayTime() / 24000) / 1.5
							&& stage >= 1 && stage < 6) {
						world.setBlock(BlockPos.containing(x, y, z), Blocks.WATER.defaultBlockState(), 3);
					}
					if (Mth.nextDouble(RandomSource.create(), 0, 10) <= (world.dayTime() / 24000) / 1.5
							&& stage == 2
							&& y >= StageHeightConfig.getSafeHeight(2)) {
						world.setBlock(BlockPos.containing(x, y, z), Blocks.AIR.defaultBlockState(), 3);
					} else if (Mth.nextDouble(RandomSource.create(), 0, 10) <= (world.dayTime() / 24000) / 1.5
							&& stage == 2
							&& y < 63 && y >= 8) {
						world.setBlock(BlockPos.containing(x, y, z), Blocks.WATER.defaultBlockState(), 3);
					} else if (stage >= 3 && stage < 6
							&& y >= StageHeightConfig.getSafeHeight(3)) {
						world.setBlock(BlockPos.containing(x, y, z), Blocks.AIR.defaultBlockState(), 3);
						if (((world.getBlockState(BlockPos.containing(x + 1, y, z))).getBlock() == Blocks.ICE) || ((world.getBlockState(BlockPos.containing(x + 1, y, z))).getBlock() == Blocks.FROSTED_ICE)) {
							world.setBlock(BlockPos.containing(x + 1, y, z), Blocks.AIR.defaultBlockState(), 3);
						}
						if (((world.getBlockState(BlockPos.containing(x - 1, y, z))).getBlock() == Blocks.ICE) || ((world.getBlockState(BlockPos.containing(x - 1, y, z))).getBlock() == Blocks.FROSTED_ICE)) {
							world.setBlock(BlockPos.containing(x - 1, y, z), Blocks.AIR.defaultBlockState(), 3);
						}
						if (((world.getBlockState(BlockPos.containing(x, y, z + 1))).getBlock() == Blocks.ICE) || ((world.getBlockState(BlockPos.containing(x, y, z + 1))).getBlock() == Blocks.FROSTED_ICE)) {
							world.setBlock(BlockPos.containing(x, y, z + 1), Blocks.AIR.defaultBlockState(), 3);
						}
						if (((world.getBlockState(BlockPos.containing(x, y, z - 1))).getBlock() == Blocks.ICE) || ((world.getBlockState(BlockPos.containing(x, y, z - 1))).getBlock() == Blocks.FROSTED_ICE)) {
							world.setBlock(BlockPos.containing(x, y, z - 1), Blocks.AIR.defaultBlockState(), 3);
						}
					}
				}
				if ((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == Blocks.PACKED_ICE) {
					if (Mth.nextDouble(RandomSource.create(), 0, 15) <= (world.dayTime() / 24000) / 1.5
							&& stage == 2) {
						world.setBlock(BlockPos.containing(x, y, z), Blocks.ICE.defaultBlockState(), 3);
					} else if (stage == 3
							&& y >= StageHeightConfig.getSafeHeight(2)) {
						world.setBlock(BlockPos.containing(x, y, z), Blocks.WATER.defaultBlockState(), 3);
						if ((world.getBlockState(BlockPos.containing(x + 1, y, z))).getBlock() == Blocks.PACKED_ICE) {
							world.setBlock(BlockPos.containing(x + 1, y, z), Blocks.WATER.defaultBlockState(), 3);
						}
						if ((world.getBlockState(BlockPos.containing(x - 1, y, z))).getBlock() == Blocks.PACKED_ICE) {
							world.setBlock(BlockPos.containing(x - 1, y, z), Blocks.WATER.defaultBlockState(), 3);
						}
						if ((world.getBlockState(BlockPos.containing(x, y, z + 1))).getBlock() == Blocks.PACKED_ICE) {
							world.setBlock(BlockPos.containing(x, y, z + 1), Blocks.WATER.defaultBlockState(), 3);
						}
						if ((world.getBlockState(BlockPos.containing(x, y, z - 1))).getBlock() == Blocks.PACKED_ICE) {
							world.setBlock(BlockPos.containing(x, y, z - 1), Blocks.WATER.defaultBlockState(), 3);
						}
					} else if (stage == 3
							&& y < StageHeightConfig.getSafeHeight(2) && y >= StageHeightConfig.getSafeHeight(4)) {
						world.setBlock(BlockPos.containing(x, y, z), Blocks.ICE.defaultBlockState(), 3);
						if ((world.getBlockState(BlockPos.containing(x + 1, y, z))).getBlock() == Blocks.PACKED_ICE) {
							world.setBlock(BlockPos.containing(x + 1, y, z), Blocks.ICE.defaultBlockState(), 3);
						}
						if ((world.getBlockState(BlockPos.containing(x - 1, y, z))).getBlock() == Blocks.PACKED_ICE) {
							world.setBlock(BlockPos.containing(x - 1, y, z), Blocks.ICE.defaultBlockState(), 3);
						}
						if ((world.getBlockState(BlockPos.containing(x, y, z + 1))).getBlock() == Blocks.PACKED_ICE) {
							world.setBlock(BlockPos.containing(x, y, z + 1), Blocks.ICE.defaultBlockState(), 3);
						}
						if ((world.getBlockState(BlockPos.containing(x, y, z - 1))).getBlock() == Blocks.PACKED_ICE) {
							world.setBlock(BlockPos.containing(x, y, z - 1), Blocks.ICE.defaultBlockState(), 3);
						}
					}else if (SapModVariables.MapVariables.get(world).SolarFlare >= 4 && SapModVariables.MapVariables.get(world).SolarFlare < 6
							&& y >= 8) {
						world.setBlock(BlockPos.containing(x, y, z), Blocks.AIR.defaultBlockState(), 3);
						if ((world.getBlockState(BlockPos.containing(x + 1, y, z))).getBlock() == Blocks.PACKED_ICE) {
							world.setBlock(BlockPos.containing(x + 1, y, z), Blocks.AIR.defaultBlockState(), 3);
						}
						if ((world.getBlockState(BlockPos.containing(x - 1, y, z))).getBlock() == Blocks.PACKED_ICE) {
							world.setBlock(BlockPos.containing(x - 1, y, z), Blocks.AIR.defaultBlockState(), 3);
						}
						if ((world.getBlockState(BlockPos.containing(x, y, z + 1))).getBlock() == Blocks.PACKED_ICE) {
							world.setBlock(BlockPos.containing(x, y, z + 1), Blocks.AIR.defaultBlockState(), 3);
						}
						if ((world.getBlockState(BlockPos.containing(x, y, z - 1))).getBlock() == Blocks.PACKED_ICE) {
							world.setBlock(BlockPos.containing(x, y, z - 1), Blocks.AIR.defaultBlockState(), 3);
						}
						if ((world.getBlockState(BlockPos.containing(x + 1, y, z + 1))).getBlock() == Blocks.PACKED_ICE) {
							world.setBlock(BlockPos.containing(x + 1, y, z + 1), Blocks.AIR.defaultBlockState(), 3);
						}
						if ((world.getBlockState(BlockPos.containing(x - 1, y, z + 1))).getBlock() == Blocks.PACKED_ICE) {
							world.setBlock(BlockPos.containing(x - 1, y, z + 1), Blocks.AIR.defaultBlockState(), 3);
						}
						if ((world.getBlockState(BlockPos.containing(x + 1, y, z - 1))).getBlock() == Blocks.PACKED_ICE) {
							world.setBlock(BlockPos.containing(x + 1, y, z - 1), Blocks.AIR.defaultBlockState(), 3);
						}
						if ((world.getBlockState(BlockPos.containing(x - 1, y, z - 1))).getBlock() == Blocks.PACKED_ICE) {
							world.setBlock(BlockPos.containing(x - 1, y, z - 1), Blocks.AIR.defaultBlockState(), 3);
						}
					}
				}
				if ((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == Blocks.BLUE_ICE) {
					if (SapModVariables.MapVariables.get(world).SolarFlare == 3
							&& y >= 8) {
						world.setBlock(BlockPos.containing(x, y, z), Blocks.PACKED_ICE.defaultBlockState(), 3);
						if ((world.getBlockState(BlockPos.containing(x + 1, y, z))).getBlock() == Blocks.BLUE_ICE) {
							world.setBlock(BlockPos.containing(x + 1, y, z), Blocks.PACKED_ICE.defaultBlockState(), 3);
						}
						if ((world.getBlockState(BlockPos.containing(x - 1, y, z))).getBlock() == Blocks.BLUE_ICE) {
							world.setBlock(BlockPos.containing(x - 1, y, z), Blocks.PACKED_ICE.defaultBlockState(), 3);
						}
						if ((world.getBlockState(BlockPos.containing(x, y, z + 1))).getBlock() == Blocks.BLUE_ICE) {
							world.setBlock(BlockPos.containing(x, y, z + 1), Blocks.PACKED_ICE.defaultBlockState(), 3);
						}
						if ((world.getBlockState(BlockPos.containing(x, y, z - 1))).getBlock() == Blocks.BLUE_ICE) {
							world.setBlock(BlockPos.containing(x, y, z - 1), Blocks.PACKED_ICE.defaultBlockState(), 3);
						}
					} else if (stage >= 4 && stage < 6
							&& y >= 63) {
						world.setBlock(BlockPos.containing(x, y, z), Blocks.ICE.defaultBlockState(), 3);
						if ((world.getBlockState(BlockPos.containing(x + 1, y, z))).getBlock() == Blocks.BLUE_ICE) {
							world.setBlock(BlockPos.containing(x + 1, y, z), Blocks.PACKED_ICE.defaultBlockState(), 3);
						}
						if ((world.getBlockState(BlockPos.containing(x - 1, y, z))).getBlock() == Blocks.BLUE_ICE) {
							world.setBlock(BlockPos.containing(x - 1, y, z), Blocks.PACKED_ICE.defaultBlockState(), 3);
						}
						if ((world.getBlockState(BlockPos.containing(x, y, z + 1))).getBlock() == Blocks.BLUE_ICE) {
							world.setBlock(BlockPos.containing(x, y, z + 1), Blocks.PACKED_ICE.defaultBlockState(), 3);
						}
						if ((world.getBlockState(BlockPos.containing(x, y, z - 1))).getBlock() == Blocks.BLUE_ICE) {
							world.setBlock(BlockPos.containing(x, y, z - 1), Blocks.PACKED_ICE.defaultBlockState(), 3);
						}
						if ((world.getBlockState(BlockPos.containing(x + 1, y, z + 1))).getBlock() == Blocks.BLUE_ICE) {
							world.setBlock(BlockPos.containing(x + 1, y, z + 1), Blocks.PACKED_ICE.defaultBlockState(), 3);
						}
						if ((world.getBlockState(BlockPos.containing(x - 1, y, z + 1))).getBlock() == Blocks.BLUE_ICE) {
							world.setBlock(BlockPos.containing(x - 1, y, z + 1), Blocks.PACKED_ICE.defaultBlockState(), 3);
						}
						if ((world.getBlockState(BlockPos.containing(x + 1, y, z - 1))).getBlock() == Blocks.BLUE_ICE) {
							world.setBlock(BlockPos.containing(x + 1, y, z - 1), Blocks.PACKED_ICE.defaultBlockState(), 3);
						}
						if ((world.getBlockState(BlockPos.containing(x - 1, y, z - 1))).getBlock() == Blocks.BLUE_ICE) {
							world.setBlock(BlockPos.containing(x - 1, y, z - 1), Blocks.PACKED_ICE.defaultBlockState(), 3);
						}
					} else if (stage >= 4 && stage < 6
							&& y >= StageHeightConfig.getSafeHeight(4)) {
						world.setBlock(BlockPos.containing(x, y, z), Blocks.AIR.defaultBlockState(), 3);
						if ((world.getBlockState(BlockPos.containing(x + 1, y, z))).getBlock() == Blocks.BLUE_ICE) {
							world.setBlock(BlockPos.containing(x + 1, y, z), Blocks.AIR.defaultBlockState(), 3);
						}
						if ((world.getBlockState(BlockPos.containing(x - 1, y, z))).getBlock() == Blocks.BLUE_ICE) {
							world.setBlock(BlockPos.containing(x - 1, y, z), Blocks.AIR.defaultBlockState(), 3);
						}
						if ((world.getBlockState(BlockPos.containing(x, y, z + 1))).getBlock() == Blocks.BLUE_ICE) {
							world.setBlock(BlockPos.containing(x, y, z + 1), Blocks.AIR.defaultBlockState(), 3);
						}
						if ((world.getBlockState(BlockPos.containing(x, y, z - 1))).getBlock() == Blocks.BLUE_ICE) {
							world.setBlock(BlockPos.containing(x, y, z - 1), Blocks.AIR.defaultBlockState(), 3);
						}
						if ((world.getBlockState(BlockPos.containing(x + 1, y, z + 1))).getBlock() == Blocks.BLUE_ICE) {
							world.setBlock(BlockPos.containing(x + 1, y, z + 1), Blocks.AIR.defaultBlockState(), 3);
						}
						if ((world.getBlockState(BlockPos.containing(x - 1, y, z + 1))).getBlock() == Blocks.BLUE_ICE) {
							world.setBlock(BlockPos.containing(x - 1, y, z + 1), Blocks.AIR.defaultBlockState(), 3);
						}
						if ((world.getBlockState(BlockPos.containing(x + 1, y, z - 1))).getBlock() == Blocks.BLUE_ICE) {
							world.setBlock(BlockPos.containing(x + 1, y, z - 1), Blocks.AIR.defaultBlockState(), 3);
						}
						if ((world.getBlockState(BlockPos.containing(x - 1, y, z - 1))).getBlock() == Blocks.BLUE_ICE) {
							world.setBlock(BlockPos.containing(x - 1, y, z - 1), Blocks.AIR.defaultBlockState(), 3);
						}
					}
				}
			}
			if (world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_overworld")))
					&& SapModVariables.MapVariables.get(world).SolarFlare == 5
					&& y >= 8) {
				world.setBlock(BlockPos.containing(x, y, z), Blocks.AIR.defaultBlockState(), 3);
				if ((world.getBlockState(BlockPos.containing(x + 1, y, z))).is(BlockTags.ICE)) {
					world.setBlock(BlockPos.containing(x + 1, y, z), Blocks.AIR.defaultBlockState(), 3);
				}
				if ((world.getBlockState(BlockPos.containing(x - 1, y, z))).is(BlockTags.ICE)) {
					world.setBlock(BlockPos.containing(x - 1, y, z), Blocks.AIR.defaultBlockState(), 3);
				}
				if ((world.getBlockState(BlockPos.containing(x, y, z + 1))).is(BlockTags.ICE)) {
					world.setBlock(BlockPos.containing(x, y, z + 1), Blocks.AIR.defaultBlockState(), 3);
				}
				if ((world.getBlockState(BlockPos.containing(x, y, z - 1))).is(BlockTags.ICE)) {
					world.setBlock(BlockPos.containing(x, y, z - 1), Blocks.AIR.defaultBlockState(), 3);
				}
				if ((world.getBlockState(BlockPos.containing(x + 1, y, z + 1))).is(BlockTags.ICE)) {
					world.setBlock(BlockPos.containing(x + 1, y, z + 1), Blocks.AIR.defaultBlockState(), 3);
				}
				if ((world.getBlockState(BlockPos.containing(x - 1, y, z + 1))).is(BlockTags.ICE)) {
					world.setBlock(BlockPos.containing(x - 1, y, z + 1), Blocks.AIR.defaultBlockState(), 3);
				}
				if ((world.getBlockState(BlockPos.containing(x + 1, y, z - 1))).is(BlockTags.ICE)) {
					world.setBlock(BlockPos.containing(x + 1, y, z - 1), Blocks.AIR.defaultBlockState(), 3);
				}
				if ((world.getBlockState(BlockPos.containing(x - 1, y, z - 1))).is(BlockTags.ICE)) {
					world.setBlock(BlockPos.containing(x - 1, y, z - 1), Blocks.AIR.defaultBlockState(), 3);
				}
				if ((world.getBlockState(BlockPos.containing(x + 1, y - 1, z))).is(BlockTags.ICE)) {
					world.setBlock(BlockPos.containing(x + 1, y - 1, z), Blocks.AIR.defaultBlockState(), 3);
				}
				if ((world.getBlockState(BlockPos.containing(x - 1, y - 1, z))).is(BlockTags.ICE)) {
					world.setBlock(BlockPos.containing(x - 1, y - 1, z), Blocks.AIR.defaultBlockState(), 3);
				}
				if ((world.getBlockState(BlockPos.containing(x, y - 1, z + 1))).is(BlockTags.ICE)) {
					world.setBlock(BlockPos.containing(x, y - 1, z + 1), Blocks.AIR.defaultBlockState(), 3);
				}
				if ((world.getBlockState(BlockPos.containing(x, y - 1, z - 1))).is(BlockTags.ICE)) {
					world.setBlock(BlockPos.containing(x, y - 1, z - 1), Blocks.AIR.defaultBlockState(), 3);
				}
				if ((world.getBlockState(BlockPos.containing(x + 1, y - 1, z + 1))).is(BlockTags.ICE)) {
					world.setBlock(BlockPos.containing(x + 1, y - 1, z + 1), Blocks.AIR.defaultBlockState(), 3);
				}
				if ((world.getBlockState(BlockPos.containing(x - 1, y - 1, z + 1))).is(BlockTags.ICE)) {
					world.setBlock(BlockPos.containing(x - 1, y - 1, z + 1), Blocks.AIR.defaultBlockState(), 3);
				}
				if ((world.getBlockState(BlockPos.containing(x + 1, y - 1, z - 1))).is(BlockTags.ICE)) {
					world.setBlock(BlockPos.containing(x + 1, y - 1, z - 1), Blocks.AIR.defaultBlockState(), 3);
				}
				if ((world.getBlockState(BlockPos.containing(x - 1, y - 1, z - 1))).is(BlockTags.ICE)) {
					world.setBlock(BlockPos.containing(x - 1, y - 1, z - 1), Blocks.AIR.defaultBlockState(), 3);
				}
				if ((world.getBlockState(BlockPos.containing(x, y - 1, z))).is(BlockTags.ICE)) {
					world.setBlock(BlockPos.containing(x, y - 1, z), Blocks.AIR.defaultBlockState(), 3);
				}
			}
		}
	}
}
