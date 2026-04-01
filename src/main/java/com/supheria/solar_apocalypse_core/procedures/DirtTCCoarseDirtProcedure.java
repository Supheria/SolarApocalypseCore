package com.supheria.solar_apocalypse_core.procedures;

import com.supheria.solar_apocalypse_core.init.SapModBlocks;
import com.supheria.solar_apocalypse_core.init.SapModTags;
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

public class DirtTCCoarseDirtProcedure {
    public static void execute(LevelAccessor world, double x, double y, double z) {
        if (world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_overworld")))
                && SapModVariables.MapVariables.get(world).SolarFlare >= 1 && SapModVariables.MapVariables.get(world).SolarFlare < 6
                && !(SapModVariables.MapVariables.get(world).TodayTime > 12566 && SapModVariables.MapVariables.get(world).TodayTime < 23450)
                && world.canSeeSkyFromBelowWater(BlockPos.containing(x, y + 1, z)) && world.dayTime() >= 24000
                && !world.getLevelData().isRaining()
                && Mth.nextDouble(RandomSource.create(), 0, 10) <= world.dayTime() / 24000 + 1) {
            world.setBlock(BlockPos.containing(x, y, z), Blocks.COARSE_DIRT.defaultBlockState(), 3);
        }
        if (world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_overworld")))
                && SapModVariables.MapVariables.get(world).SolarFlare == 2
                && world.canSeeSkyFromBelowWater(BlockPos.containing(x, y + 1, z))
                && world.dayTime() >= 168000) {
            world.setBlock(BlockPos.containing(x, y, z), Blocks.SAND.defaultBlockState(), 3);
        }else if (world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_overworld")))
                && SapModVariables.MapVariables.get(world).SolarFlare == 3
                && y >= 63) {
            world.setBlock(BlockPos.containing(x, y, z), SapModBlocks.DUST.get().defaultBlockState(), 3);
            if ((world.getBlockState(BlockPos.containing(x + 1, y, z))).is(SapModTags.Blocks.DIRT)) {
                world.setBlock(BlockPos.containing(x + 1, y, z), SapModBlocks.DUST.get().defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 1, y, z))).is(SapModTags.Blocks.DIRT)) {
                world.setBlock(BlockPos.containing(x - 1, y, z), SapModBlocks.DUST.get().defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x, y, z + 1))).is(SapModTags.Blocks.DIRT)) {
                world.setBlock(BlockPos.containing(x, y, z + 1), SapModBlocks.DUST.get().defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x, y, z - 1))).is(SapModTags.Blocks.DIRT)) {
                world.setBlock(BlockPos.containing(x, y, z - 1), SapModBlocks.DUST.get().defaultBlockState(), 3);
            }
        }else if (world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_overworld")))
                && SapModVariables.MapVariables.get(world).SolarFlare >= 4 && SapModVariables.MapVariables.get(world).SolarFlare < 6
                && y >= 32) {
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
