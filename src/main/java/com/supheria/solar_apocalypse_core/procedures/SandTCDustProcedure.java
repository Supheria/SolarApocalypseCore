package com.supheria.solar_apocalypse_core.procedures;

import com.supheria.solar_apocalypse_core.network.SapModVariables;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;
import net.minecraft.core.BlockPos;

import com.supheria.solar_apocalypse_core.init.SapModBlocks;

public class SandTCDustProcedure {
    public static void execute(LevelAccessor world, double x, double y, double z) {
        if (world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_overworld")))
                && SapModVariables.MapVariables.get(world).SolarFlare >= 2 && SapModVariables.MapVariables.get(world).SolarFlare < 6
                && !(SapModVariables.MapVariables.get(world).TodayTime > 12566 && SapModVariables.MapVariables.get(world).TodayTime < 23450)
                && world.canSeeSkyFromBelowWater(BlockPos.containing(x, y + 1, z))
                && !((world.getBlockState(BlockPos.containing(x, y + 1, z))).getBlock() == Blocks.WATER)
                && !world.getLevelData().isRaining()
                && Mth.nextInt(RandomSource.create(), 1, 10) == 1) {
            world.setBlock(BlockPos.containing(x, y, z), SapModBlocks.DUST.get().defaultBlockState(), 3);
        }
        if (world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_overworld")))
                && SapModVariables.MapVariables.get(world).SolarFlare == 3
                && !(SapModVariables.MapVariables.get(world).TodayTime > 12566 && SapModVariables.MapVariables.get(world).TodayTime < 23450)
                && !((world.getBlockState(BlockPos.containing(x, y + 1, z))).getBlock() == Blocks.WATER)
                && y >= 63) {
            world.setBlock(BlockPos.containing(x, y, z), Blocks.AIR.defaultBlockState(), 3);
            if ((world.getBlockState(BlockPos.containing(x + 1, y, z))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 1, y, z), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 1, y, z))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 1, y, z), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x, y, z + 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x, y, z + 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x, y, z - 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x, y, z - 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 1, y, z + 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 1, y, z + 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 1, y, z + 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 1, y, z + 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 1, y, z - 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 1, y, z - 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 1, y, z - 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 1, y, z - 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 1, y - 1, z))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 1, y - 1, z), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 1, y - 1, z))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 1, y - 1, z), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x, y - 1, z + 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x, y - 1, z + 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x, y - 1, z - 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x, y - 1, z - 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 1, y - 1, z + 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 1, y - 1, z + 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 1, y - 1, z + 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 1, y - 1, z + 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 1, y - 1, z - 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 1, y - 1, z - 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 1, y - 1, z - 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 1, y - 1, z - 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x, y - 1, z))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x, y - 1, z), Blocks.AIR.defaultBlockState(), 3);
            }
        }
        if (world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_overworld")))
                && SapModVariables.MapVariables.get(world).SolarFlare == 4
                && y >= 32) {
            world.setBlock(BlockPos.containing(x, y, z), Blocks.AIR.defaultBlockState(), 3);
            if ((world.getBlockState(BlockPos.containing(x + 1, y, z))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 1, y, z), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 2, y, z))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 2, y, z), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 1, y, z))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 1, y, z), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 2, y, z))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 2, y, z), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x, y, z + 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x, y, z + 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 1, y, z + 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 1, y, z + 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 2, y, z + 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 2, y, z + 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 1, y, z + 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 1, y, z + 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 2, y, z + 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 2, y, z + 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x, y, z + 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x, y, z + 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 1, y, z + 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 1, y, z + 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 2, y, z + 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 2, y, z + 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 1, y, z + 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 1, y, z + 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 2, y, z + 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 2, y, z + 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x, y, z - 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x, y, z - 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 1, y, z - 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 1, y, z - 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 2, y, z - 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 2, y, z - 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 1, y, z - 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 1, y, z - 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 2, y, z - 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 2, y, z - 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x, y, z + 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x, y, z + 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 1, y, z - 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 1, y, z - 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 2, y, z - 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 2, y, z - 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 1, y, z - 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 1, y, z - 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 2, y, z - 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 2, y, z - 2), Blocks.AIR.defaultBlockState(), 3);
            }
            //5x1
            if ((world.getBlockState(BlockPos.containing(x, y - 1, z))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x, y - 1, z), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 1, y - 1, z))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 1, y - 1, z), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 2, y - 1, z))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 2, y - 1, z), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 1, y - 1, z))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 1, y - 1, z), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 2, y - 1, z))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 2, y - 1, z), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x, y - 1, z + 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x, y - 1, z + 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 1, y - 1, z + 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 1, y - 1, z + 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 2, y - 1, z + 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 2, y - 1, z + 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 1, y - 1, z + 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 1, y - 1, z + 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 2, y - 1, z + 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 2, y - 1, z + 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x, y - 1, z + 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x, y - 1, z + 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 1, y - 1, z + 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 1, y - 1, z + 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 2, y - 1, z + 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 2, y - 1, z + 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 1, y - 1, z + 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 1, y - 1, z + 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 2, y - 1, z + 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 2, y - 1, z + 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x, y - 1, z - 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x, y - 1, z - 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 1, y - 1, z - 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 1, y - 1, z - 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 2, y - 1, z - 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 2, y - 1, z - 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 1, y - 1, z - 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 1, y - 1, z - 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 2, y - 1, z - 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 2, y - 1, z - 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x, y - 1, z + 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x, y - 1, z + 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 1, y - 1, z - 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 1, y - 1, z - 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 2, y - 1, z - 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 2, y - 1, z - 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 1, y - 1, z - 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 1, y - 1, z - 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 2, y - 1, z - 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 2, y - 1, z - 2), Blocks.AIR.defaultBlockState(), 3);
            }
            //5x2
            if ((world.getBlockState(BlockPos.containing(x, y - 2, z))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x, y - 2, z), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 1, y - 2, z))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 1, y - 2, z), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 2, y - 2, z))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 2, y - 2, z), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 1, y - 2, z))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 1, y - 2, z), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 2, y - 2, z))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 2, y - 2, z), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x, y - 2, z + 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x, y - 2, z + 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 1, y - 2, z + 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 1, y - 2, z + 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 2, y - 2, z + 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 2, y - 2, z + 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 1, y - 2, z + 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 1, y - 2, z + 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 2, y - 2, z + 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 2, y - 2, z + 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x, y - 2, z + 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x, y - 2, z + 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 1, y - 2, z + 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 1, y - 2, z + 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 2, y - 2, z + 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 2, y - 2, z + 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 1, y - 2, z + 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 1, y - 2, z + 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 2, y - 2, z + 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 2, y - 2, z + 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x, y - 2, z - 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x, y - 2, z - 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 1, y - 2, z - 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 1, y - 2, z - 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 2, y - 2, z - 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 2, y - 2, z - 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 1, y - 2, z - 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 1, y - 2, z - 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 2, y - 2, z - 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 2, y - 2, z - 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x, y - 2, z + 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x, y - 2, z + 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 1, y - 2, z - 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 1, y - 2, z - 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 2, y - 2, z - 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 2, y - 2, z - 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 1, y - 2, z - 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 1, y - 2, z - 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 2, y - 2, z - 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 2, y - 2, z - 2), Blocks.AIR.defaultBlockState(), 3);
            }
            //5x3
            if ((world.getBlockState(BlockPos.containing(x, y - 3, z))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x, y - 3, z), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 1, y - 3, z))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 1, y - 3, z), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 2, y - 3, z))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 2, y - 3, z), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 1, y - 3, z))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 1, y - 3, z), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 2, y - 3, z))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 2, y - 3, z), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x, y - 3, z + 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x, y - 3, z + 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 1, y - 3, z + 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 1, y - 3, z + 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 2, y - 3, z + 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 2, y - 3, z + 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 1, y - 3, z + 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 1, y - 3, z + 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 2, y - 3, z + 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 2, y - 3, z + 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x, y - 3, z + 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x, y - 3, z + 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 1, y - 3, z + 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 1, y - 3, z + 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 2, y - 3, z + 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 2, y - 3, z + 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 1, y - 3, z + 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 1, y - 3, z + 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 2, y - 3, z + 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 2, y - 3, z + 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x, y - 3, z - 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x, y - 3, z - 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 1, y - 3, z - 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 1, y - 3, z - 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 2, y - 3, z - 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 2, y - 3, z - 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 1, y - 3, z - 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 1, y - 3, z - 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 2, y - 3, z - 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 2, y - 3, z - 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x, y - 3, z + 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x, y - 3, z + 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 1, y - 3, z - 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 1, y - 3, z - 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 2, y - 3, z - 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 2, y - 3, z - 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 1, y - 3, z - 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 1, y - 3, z - 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 2, y - 3, z - 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 2, y - 3, z - 2), Blocks.AIR.defaultBlockState(), 3);
            }
            //5x4
            if ((world.getBlockState(BlockPos.containing(x, y - 4, z))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x, y - 4, z), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 1, y - 4, z))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 1, y - 4, z), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 2, y - 4, z))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 2, y - 4, z), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 1, y - 4, z))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 1, y - 4, z), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 2, y - 4, z))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 2, y - 4, z), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x, y - 4, z + 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x, y - 4, z + 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 1, y - 4, z + 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 1, y - 4, z + 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 2, y - 4, z + 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 2, y - 4, z + 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 1, y - 4, z + 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 1, y - 4, z + 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 2, y - 4, z + 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 2, y - 4, z + 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x, y - 4, z + 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x, y - 4, z + 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 1, y - 4, z + 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 1, y - 4, z + 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 2, y - 4, z + 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 2, y - 4, z + 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 1, y - 4, z + 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 1, y - 4, z + 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 2, y - 4, z + 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 2, y - 4, z + 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x, y - 4, z - 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x, y - 4, z - 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 1, y - 4, z - 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 1, y - 4, z - 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 2, y - 4, z - 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 2, y - 4, z - 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 1, y - 4, z - 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 1, y - 4, z - 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 2, y - 4, z - 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 2, y - 4, z - 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x, y - 4, z + 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x, y - 4, z + 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 1, y - 4, z - 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 1, y - 4, z - 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 2, y - 4, z - 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 2, y - 4, z - 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 1, y - 4, z - 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 1, y - 4, z - 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 2, y - 4, z - 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 2, y - 4, z - 2), Blocks.AIR.defaultBlockState(), 3);
            }
            //5x5
        }
        if (world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_overworld")))
                && SapModVariables.MapVariables.get(world).SolarFlare == 5
                && y >= 8) {
            world.setBlock(BlockPos.containing(x, y, z), Blocks.AIR.defaultBlockState(), 3);
            if ((world.getBlockState(BlockPos.containing(x + 1, y, z))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 1, y, z), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 2, y, z))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 2, y, z), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 1, y, z))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 1, y, z), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 2, y, z))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 2, y, z), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x, y, z + 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x, y, z + 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 1, y, z + 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 1, y, z + 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 2, y, z + 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 2, y, z + 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 1, y, z + 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 1, y, z + 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 2, y, z + 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 2, y, z + 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x, y, z + 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x, y, z + 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 1, y, z + 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 1, y, z + 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 2, y, z + 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 2, y, z + 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 1, y, z + 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 1, y, z + 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 2, y, z + 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 2, y, z + 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x, y, z - 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x, y, z - 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 1, y, z - 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 1, y, z - 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 2, y, z - 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 2, y, z - 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 1, y, z - 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 1, y, z - 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 2, y, z - 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 2, y, z - 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x, y, z + 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x, y, z + 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 1, y, z - 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 1, y, z - 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 2, y, z - 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 2, y, z - 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 1, y, z - 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 1, y, z - 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 2, y, z - 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 2, y, z - 2), Blocks.AIR.defaultBlockState(), 3);
            }
            //5x1
            if ((world.getBlockState(BlockPos.containing(x, y - 1, z))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x, y - 1, z), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 1, y - 1, z))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 1, y - 1, z), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 2, y - 1, z))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 2, y - 1, z), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 1, y - 1, z))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 1, y - 1, z), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 2, y - 1, z))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 2, y - 1, z), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x, y - 1, z + 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x, y - 1, z + 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 1, y - 1, z + 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 1, y - 1, z + 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 2, y - 1, z + 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 2, y - 1, z + 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 1, y - 1, z + 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 1, y - 1, z + 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 2, y - 1, z + 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 2, y - 1, z + 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x, y - 1, z + 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x, y - 1, z + 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 1, y - 1, z + 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 1, y - 1, z + 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 2, y - 1, z + 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 2, y - 1, z + 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 1, y - 1, z + 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 1, y - 1, z + 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 2, y - 1, z + 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 2, y - 1, z + 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x, y - 1, z - 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x, y - 1, z - 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 1, y - 1, z - 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 1, y - 1, z - 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 2, y - 1, z - 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 2, y - 1, z - 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 1, y - 1, z - 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 1, y - 1, z - 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 2, y - 1, z - 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 2, y - 1, z - 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x, y - 1, z + 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x, y - 1, z + 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 1, y - 1, z - 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 1, y - 1, z - 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 2, y - 1, z - 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 2, y - 1, z - 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 1, y - 1, z - 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 1, y - 1, z - 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 2, y - 1, z - 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 2, y - 1, z - 2), Blocks.AIR.defaultBlockState(), 3);
            }
            //5x2
            if ((world.getBlockState(BlockPos.containing(x, y - 2, z))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x, y - 2, z), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 1, y - 2, z))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 1, y - 2, z), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 2, y - 2, z))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 2, y - 2, z), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 1, y - 2, z))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 1, y - 2, z), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 2, y - 2, z))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 2, y - 2, z), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x, y - 2, z + 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x, y - 2, z + 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 1, y - 2, z + 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 1, y - 2, z + 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 2, y - 2, z + 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 2, y - 2, z + 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 1, y - 2, z + 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 1, y - 2, z + 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 2, y - 2, z + 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 2, y - 2, z + 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x, y - 2, z + 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x, y - 2, z + 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 1, y - 2, z + 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 1, y - 2, z + 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 2, y - 2, z + 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 2, y - 2, z + 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 1, y - 2, z + 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 1, y - 2, z + 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 2, y - 2, z + 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 2, y - 2, z + 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x, y - 2, z - 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x, y - 2, z - 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 1, y - 2, z - 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 1, y - 2, z - 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 2, y - 2, z - 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 2, y - 2, z - 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 1, y - 2, z - 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 1, y - 2, z - 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 2, y - 2, z - 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 2, y - 2, z - 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x, y - 2, z + 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x, y - 2, z + 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 1, y - 2, z - 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 1, y - 2, z - 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 2, y - 2, z - 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 2, y - 2, z - 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 1, y - 2, z - 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 1, y - 2, z - 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 2, y - 2, z - 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 2, y - 2, z - 2), Blocks.AIR.defaultBlockState(), 3);
            }
            //5x3
            if ((world.getBlockState(BlockPos.containing(x, y - 3, z))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x, y - 3, z), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 1, y - 3, z))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 1, y - 3, z), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 2, y - 3, z))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 2, y - 3, z), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 1, y - 3, z))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 1, y - 3, z), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 2, y - 3, z))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 2, y - 3, z), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x, y - 3, z + 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x, y - 3, z + 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 1, y - 3, z + 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 1, y - 3, z + 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 2, y - 3, z + 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 2, y - 3, z + 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 1, y - 3, z + 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 1, y - 3, z + 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 2, y - 3, z + 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 2, y - 3, z + 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x, y - 3, z + 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x, y - 3, z + 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 1, y - 3, z + 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 1, y - 3, z + 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 2, y - 3, z + 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 2, y - 3, z + 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 1, y - 3, z + 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 1, y - 3, z + 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 2, y - 3, z + 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 2, y - 3, z + 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x, y - 3, z - 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x, y - 3, z - 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 1, y - 3, z - 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 1, y - 3, z - 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 2, y - 3, z - 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 2, y - 3, z - 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 1, y - 3, z - 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 1, y - 3, z - 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 2, y - 3, z - 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 2, y - 3, z - 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x, y - 3, z + 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x, y - 3, z + 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 1, y - 3, z - 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 1, y - 3, z - 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 2, y - 3, z - 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 2, y - 3, z - 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 1, y - 3, z - 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 1, y - 3, z - 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 2, y - 3, z - 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 2, y - 3, z - 2), Blocks.AIR.defaultBlockState(), 3);
            }
            //5x4
            if ((world.getBlockState(BlockPos.containing(x, y - 4, z))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x, y - 4, z), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 1, y - 4, z))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 1, y - 4, z), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 2, y - 4, z))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 2, y - 4, z), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 1, y - 4, z))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 1, y - 4, z), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 2, y - 4, z))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 2, y - 4, z), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x, y - 4, z + 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x, y - 4, z + 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 1, y - 4, z + 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 1, y - 4, z + 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 2, y - 4, z + 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 2, y - 4, z + 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 1, y - 4, z + 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 1, y - 4, z + 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 2, y - 4, z + 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 2, y - 4, z + 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x, y - 4, z + 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x, y - 4, z + 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 1, y - 4, z + 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 1, y - 4, z + 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 2, y - 4, z + 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 2, y - 4, z + 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 1, y - 4, z + 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 1, y - 4, z + 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 2, y - 4, z + 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 2, y - 4, z + 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x, y - 4, z - 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x, y - 4, z - 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 1, y - 4, z - 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 1, y - 4, z - 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 2, y - 4, z - 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 2, y - 4, z - 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 1, y - 4, z - 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 1, y - 4, z - 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 2, y - 4, z - 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 2, y - 4, z - 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x, y - 4, z + 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x, y - 4, z + 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 1, y - 4, z - 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 1, y - 4, z - 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 2, y - 4, z - 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 2, y - 4, z - 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 1, y - 4, z - 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 1, y - 4, z - 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 2, y - 4, z - 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 2, y - 4, z - 2), Blocks.AIR.defaultBlockState(), 3);
            }
            //5x5
            if ((world.getBlockState(BlockPos.containing(x, y + 1, z))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x, y + 1, z), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 1, y + 1, z))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 1, y + 1, z), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 2, y + 1, z))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 2, y + 1, z), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 1, y + 1, z))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 1, y + 1, z), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 2, y + 1, z))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 2, y + 1, z), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x, y + 1, z + 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x, y + 1, z + 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 1, y + 1, z + 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 1, y + 1, z + 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 2, y + 1, z + 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 2, y + 1, z + 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 1, y + 1, z + 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 1, y + 1, z + 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 2, y + 1, z + 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 2, y + 1, z + 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x, y + 1, z + 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x, y + 1, z + 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 1, y + 1, z + 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 1, y + 1, z + 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 2, y + 1, z + 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 2, y + 1, z + 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 1, y + 1, z + 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 1, y + 1, z + 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 2, y + 1, z + 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 2, y + 1, z + 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x, y + 1, z - 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x, y + 1, z - 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 1, y + 1, z - 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 1, y + 1, z - 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 2, y + 1, z - 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 2, y + 1, z - 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 1, y + 1, z - 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 1, y + 1, z - 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 2, y + 1, z - 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 2, y + 1, z - 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x, y + 1, z + 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x, y + 1, z + 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 1, y + 1, z - 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 1, y + 1, z - 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 2, y + 1, z - 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 2, y + 1, z - 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 1, y + 1, z - 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 1, y + 1, z - 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 2, y + 1, z - 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 2, y + 1, z - 2), Blocks.AIR.defaultBlockState(), 3);
            }
            //5x6
            if ((world.getBlockState(BlockPos.containing(x, y + 2, z))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x, y + 2, z), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 1, y + 2, z))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 1, y + 2, z), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 2, y + 2, z))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 2, y + 2, z), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 1, y + 2, z))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 1, y + 2, z), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 2, y + 2, z))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 2, y + 2, z), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x, y + 2, z + 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x, y + 2, z + 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 1, y + 2, z + 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 1, y + 2, z + 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 2, y + 2, z + 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 2, y + 2, z + 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 1, y + 2, z + 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 1, y + 2, z + 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 2, y + 2, z + 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 2, y + 2, z + 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x, y + 2, z + 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x, y + 2, z + 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 1, y + 2, z + 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 1, y + 2, z + 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 2, y + 2, z + 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 2, y + 2, z + 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 1, y + 2, z + 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 1, y + 2, z + 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 2, y + 2, z + 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 2, y + 2, z + 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x, y + 2, z - 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x, y + 2, z - 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 1, y + 2, z - 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 1, y + 2, z - 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 2, y + 2, z - 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 2, y + 2, z - 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 1, y + 2, z - 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 1, y + 2, z - 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 2, y + 2, z - 1))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 2, y + 2, z - 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x, y + 2, z + 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x, y + 2, z + 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 1, y + 2, z - 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 1, y + 2, z - 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 2, y + 2, z - 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x + 2, y + 2, z - 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 1, y + 2, z - 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 1, y + 2, z - 2), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 2, y + 2, z - 2))).is(BlockTags.SAND)) {
                world.setBlock(BlockPos.containing(x - 2, y + 2, z - 2), Blocks.AIR.defaultBlockState(), 3);
            }
            //5x7
        }
    }
}
