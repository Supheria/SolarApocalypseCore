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

public class MossyDeleteProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		if (SapModVariables.MapVariables.get(world).SolarFlare >= 1 && SapModVariables.MapVariables.get(world).SolarFlare < 6) {
			if (world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_overworld")))
					&& !(SapModVariables.MapVariables.get(world).TodayTime > 12566 && SapModVariables.MapVariables.get(world).TodayTime < 23450)
					&& world.canSeeSkyFromBelowWater(BlockPos.containing(x, y + 1, z))
					&& !world.getLevelData().isRaining()
					&& world.dayTime() >= 48000
					&& Mth.nextDouble(RandomSource.create(), 0, (world.dayTime() / 24000) + 1) <= (world.dayTime() / 24000)) {
				if ((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == Blocks.MOSSY_COBBLESTONE) {
					world.setBlock(BlockPos.containing(x, y, z), Blocks.COBBLESTONE.defaultBlockState(), 3);
				}
				if ((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == Blocks.MOSSY_COBBLESTONE_SLAB) {
					world.setBlock(BlockPos.containing(x, y, z), Blocks.COBBLESTONE_SLAB.defaultBlockState(), 3);
				}
				if ((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == Blocks.MOSSY_COBBLESTONE_STAIRS) {
					world.setBlock(BlockPos.containing(x, y, z), Blocks.COBBLESTONE_STAIRS.defaultBlockState(), 3);
				}
				if ((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == Blocks.MOSSY_COBBLESTONE_WALL) {
					world.setBlock(BlockPos.containing(x, y, z), Blocks.COBBLESTONE_WALL.defaultBlockState(), 3);
				}
				if ((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == Blocks.MOSSY_STONE_BRICKS) {
					world.setBlock(BlockPos.containing(x, y, z), Blocks.STONE_BRICKS.defaultBlockState(), 3);
				}
				if ((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == Blocks.MOSSY_STONE_BRICK_SLAB) {
					world.setBlock(BlockPos.containing(x, y, z), Blocks.STONE_BRICK_SLAB.defaultBlockState(), 3);
				}
				if ((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == Blocks.MOSSY_STONE_BRICK_STAIRS) {
					world.setBlock(BlockPos.containing(x, y, z), Blocks.STONE_BRICK_STAIRS.defaultBlockState(), 3);
				}
				if ((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == Blocks.MOSSY_STONE_BRICK_WALL) {
					world.setBlock(BlockPos.containing(x, y, z), Blocks.STONE_BRICK_WALL.defaultBlockState(), 3);
				}
			}
		}
		if (world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_overworld")))
				&& SapModVariables.MapVariables.get(world).SolarFlare == 2
				&& !(SapModVariables.MapVariables.get(world).TodayTime > 12566 && SapModVariables.MapVariables.get(world).TodayTime < 23450)
				&& y >= 63
				&& Mth.nextDouble(RandomSource.create(), 0, (world.dayTime() / 24000) + 1) <= (world.dayTime() / 24000)) {
			if ((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == Blocks.MOSSY_COBBLESTONE) {
				world.setBlock(BlockPos.containing(x, y, z), Blocks.COBBLESTONE.defaultBlockState(), 3);
			}
			if ((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == Blocks.MOSSY_COBBLESTONE_SLAB) {
				world.setBlock(BlockPos.containing(x, y, z), Blocks.COBBLESTONE_SLAB.defaultBlockState(), 3);
			}
			if ((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == Blocks.MOSSY_COBBLESTONE_STAIRS) {
				world.setBlock(BlockPos.containing(x, y, z), Blocks.COBBLESTONE_STAIRS.defaultBlockState(), 3);
			}
			if ((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == Blocks.MOSSY_COBBLESTONE_WALL) {
				world.setBlock(BlockPos.containing(x, y, z), Blocks.COBBLESTONE_WALL.defaultBlockState(), 3);
			}
			if ((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == Blocks.MOSSY_STONE_BRICKS) {
				world.setBlock(BlockPos.containing(x, y, z), Blocks.STONE_BRICKS.defaultBlockState(), 3);
			}
			if ((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == Blocks.MOSSY_STONE_BRICK_SLAB) {
				world.setBlock(BlockPos.containing(x, y, z), Blocks.STONE_BRICK_SLAB.defaultBlockState(), 3);
			}
			if ((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == Blocks.MOSSY_STONE_BRICK_STAIRS) {
				world.setBlock(BlockPos.containing(x, y, z), Blocks.STONE_BRICK_STAIRS.defaultBlockState(), 3);
			}
			if ((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == Blocks.MOSSY_STONE_BRICK_WALL) {
				world.setBlock(BlockPos.containing(x, y, z), Blocks.STONE_BRICK_WALL.defaultBlockState(), 3);
			}
		}
		else if (world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_overworld")))
				&& SapModVariables.MapVariables.get(world).SolarFlare == 3
				&& y >= 63) {
			if ((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == Blocks.MOSSY_COBBLESTONE) {
				world.setBlock(BlockPos.containing(x, y, z), Blocks.COBBLESTONE.defaultBlockState(), 3);
			}
			if ((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == Blocks.MOSSY_COBBLESTONE_SLAB) {
				world.setBlock(BlockPos.containing(x, y, z), Blocks.COBBLESTONE_SLAB.defaultBlockState(), 3);
			}
			if ((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == Blocks.MOSSY_COBBLESTONE_STAIRS) {
				world.setBlock(BlockPos.containing(x, y, z), Blocks.COBBLESTONE_STAIRS.defaultBlockState(), 3);
			}
			if ((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == Blocks.MOSSY_COBBLESTONE_WALL) {
				world.setBlock(BlockPos.containing(x, y, z), Blocks.COBBLESTONE_WALL.defaultBlockState(), 3);
			}
			if ((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == Blocks.MOSSY_STONE_BRICKS) {
				world.setBlock(BlockPos.containing(x, y, z), Blocks.STONE_BRICKS.defaultBlockState(), 3);
			}
			if ((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == Blocks.MOSSY_STONE_BRICK_SLAB) {
				world.setBlock(BlockPos.containing(x, y, z), Blocks.STONE_BRICK_SLAB.defaultBlockState(), 3);
			}
			if ((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == Blocks.MOSSY_STONE_BRICK_STAIRS) {
				world.setBlock(BlockPos.containing(x, y, z), Blocks.STONE_BRICK_STAIRS.defaultBlockState(), 3);
			}
			if ((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == Blocks.MOSSY_STONE_BRICK_WALL) {
				world.setBlock(BlockPos.containing(x, y, z), Blocks.STONE_BRICK_WALL.defaultBlockState(), 3);
			}
		}
		else if (world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_overworld")))
				&& SapModVariables.MapVariables.get(world).SolarFlare == 4
				&& y >= 32) {
			if ((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == Blocks.MOSSY_COBBLESTONE) {
				world.setBlock(BlockPos.containing(x, y, z), Blocks.COBBLESTONE.defaultBlockState(), 3);
			}
			if ((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == Blocks.MOSSY_COBBLESTONE_SLAB) {
				world.setBlock(BlockPos.containing(x, y, z), Blocks.COBBLESTONE_SLAB.defaultBlockState(), 3);
			}
			if ((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == Blocks.MOSSY_COBBLESTONE_STAIRS) {
				world.setBlock(BlockPos.containing(x, y, z), Blocks.COBBLESTONE_STAIRS.defaultBlockState(), 3);
			}
			if ((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == Blocks.MOSSY_COBBLESTONE_WALL) {
				world.setBlock(BlockPos.containing(x, y, z), Blocks.COBBLESTONE_WALL.defaultBlockState(), 3);
			}
			if ((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == Blocks.MOSSY_STONE_BRICKS) {
				world.setBlock(BlockPos.containing(x, y, z), Blocks.STONE_BRICKS.defaultBlockState(), 3);
			}
			if ((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == Blocks.MOSSY_STONE_BRICK_SLAB) {
				world.setBlock(BlockPos.containing(x, y, z), Blocks.STONE_BRICK_SLAB.defaultBlockState(), 3);
			}
			if ((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == Blocks.MOSSY_STONE_BRICK_STAIRS) {
				world.setBlock(BlockPos.containing(x, y, z), Blocks.STONE_BRICK_STAIRS.defaultBlockState(), 3);
			}
			if ((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == Blocks.MOSSY_STONE_BRICK_WALL) {
				world.setBlock(BlockPos.containing(x, y, z), Blocks.STONE_BRICK_WALL.defaultBlockState(), 3);
			}
		}
		else if (world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_overworld")))
				&& SapModVariables.MapVariables.get(world).SolarFlare == 5
				&& y >= 8) {
			if ((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == Blocks.MOSSY_COBBLESTONE) {
				world.setBlock(BlockPos.containing(x, y, z), Blocks.COBBLESTONE.defaultBlockState(), 3);
			}
			if ((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == Blocks.MOSSY_COBBLESTONE_SLAB) {
				world.setBlock(BlockPos.containing(x, y, z), Blocks.COBBLESTONE_SLAB.defaultBlockState(), 3);
			}
			if ((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == Blocks.MOSSY_COBBLESTONE_STAIRS) {
				world.setBlock(BlockPos.containing(x, y, z), Blocks.COBBLESTONE_STAIRS.defaultBlockState(), 3);
			}
			if ((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == Blocks.MOSSY_COBBLESTONE_WALL) {
				world.setBlock(BlockPos.containing(x, y, z), Blocks.COBBLESTONE_WALL.defaultBlockState(), 3);
			}
			if ((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == Blocks.MOSSY_STONE_BRICKS) {
				world.setBlock(BlockPos.containing(x, y, z), Blocks.STONE_BRICKS.defaultBlockState(), 3);
			}
			if ((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == Blocks.MOSSY_STONE_BRICK_SLAB) {
				world.setBlock(BlockPos.containing(x, y, z), Blocks.STONE_BRICK_SLAB.defaultBlockState(), 3);
			}
			if ((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == Blocks.MOSSY_STONE_BRICK_STAIRS) {
				world.setBlock(BlockPos.containing(x, y, z), Blocks.STONE_BRICK_STAIRS.defaultBlockState(), 3);
			}
			if ((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == Blocks.MOSSY_STONE_BRICK_WALL) {
				world.setBlock(BlockPos.containing(x, y, z), Blocks.STONE_BRICK_WALL.defaultBlockState(), 3);
			}
		}
	}
}
