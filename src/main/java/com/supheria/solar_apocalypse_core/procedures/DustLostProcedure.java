package com.supheria.solar_apocalypse_core.procedures;
import com.supheria.solar_apocalypse_core.config.solar.StageHeightConfig;

import com.supheria.solar_apocalypse_core.network.SapModVariables;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.BlockPos;
import com.supheria.solar_apocalypse_core.init.SapModBlocks;

public class DustLostProcedure {
    public static void execute(LevelAccessor world, double x, double y, double z) {
        int stage = (int) SapModVariables.MapVariables.get(world).SolarFlare;
        if (world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_overworld")))
                && stage >= 2 && stage < 6
                && world.dayTime() >= 144000
                && y >= StageHeightConfig.getSafeHeight(2)) {
            world.setBlock(BlockPos.containing(x, y, z), Blocks.AIR.defaultBlockState(), 3);
            if ((world.getBlockState(BlockPos.containing(x + 1, y, z))).getBlock() == SapModBlocks.DUST.get()) {
                world.setBlock(BlockPos.containing(x + 1, y, z), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 1, y, z))).getBlock() == SapModBlocks.DUST.get()) {
                world.setBlock(BlockPos.containing(x - 1, y, z), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x, y, z + 1))).getBlock() == SapModBlocks.DUST.get()) {
                world.setBlock(BlockPos.containing(x, y, z + 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x, y, z - 1))).getBlock() == SapModBlocks.DUST.get()) {
                world.setBlock(BlockPos.containing(x, y, z - 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 1, y, z + 1))).getBlock() == SapModBlocks.DUST.get()) {
                world.setBlock(BlockPos.containing(x + 1, y, z + 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 1, y, z + 1))).getBlock() == SapModBlocks.DUST.get()) {
                world.setBlock(BlockPos.containing(x - 1, y, z + 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 1, y, z - 1))).getBlock() == SapModBlocks.DUST.get()) {
                world.setBlock(BlockPos.containing(x + 1, y, z - 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 1, y, z - 1))).getBlock() == SapModBlocks.DUST.get()) {
                world.setBlock(BlockPos.containing(x - 1, y, z - 1), Blocks.AIR.defaultBlockState(), 3);
            }
        }
        if (world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_overworld")))
                && stage == 5
                && y >= StageHeightConfig.getSafeHeight(5)) {
            world.setBlock(BlockPos.containing(x, y, z), Blocks.AIR.defaultBlockState(), 3);
            if ((world.getBlockState(BlockPos.containing(x + 1, y, z))).getBlock() == SapModBlocks.DUST.get()) {
                world.setBlock(BlockPos.containing(x + 1, y, z), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 1, y, z))).getBlock() == SapModBlocks.DUST.get()) {
                world.setBlock(BlockPos.containing(x - 1, y, z), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x, y, z + 1))).getBlock() == SapModBlocks.DUST.get()) {
                world.setBlock(BlockPos.containing(x, y, z + 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x, y, z - 1))).getBlock() == SapModBlocks.DUST.get()) {
                world.setBlock(BlockPos.containing(x, y, z - 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 1, y, z + 1))).getBlock() == SapModBlocks.DUST.get()) {
                world.setBlock(BlockPos.containing(x + 1, y, z + 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 1, y, z + 1))).getBlock() == SapModBlocks.DUST.get()) {
                world.setBlock(BlockPos.containing(x - 1, y, z + 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 1, y, z - 1))).getBlock() == SapModBlocks.DUST.get()) {
                world.setBlock(BlockPos.containing(x + 1, y, z - 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 1, y, z - 1))).getBlock() == SapModBlocks.DUST.get()) {
                world.setBlock(BlockPos.containing(x - 1, y, z - 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 1, y - 1, z))).getBlock() == SapModBlocks.DUST.get()) {
                world.setBlock(BlockPos.containing(x + 1, y - 1, z), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 1, y - 1, z))).getBlock() == SapModBlocks.DUST.get()) {
                world.setBlock(BlockPos.containing(x - 1, y - 1, z), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x, y - 1, z + 1))).getBlock() == SapModBlocks.DUST.get()) {
                world.setBlock(BlockPos.containing(x, y - 1, z + 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x, y - 1, z - 1))).getBlock() == SapModBlocks.DUST.get()) {
                world.setBlock(BlockPos.containing(x, y - 1, z - 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 1, y - 1, z + 1))).getBlock() == SapModBlocks.DUST.get()) {
                world.setBlock(BlockPos.containing(x + 1, y - 1, z + 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 1, y - 1, z + 1))).getBlock() == SapModBlocks.DUST.get()) {
                world.setBlock(BlockPos.containing(x - 1, y - 1, z + 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x + 1, y - 1, z - 1))).getBlock() == SapModBlocks.DUST.get()) {
                world.setBlock(BlockPos.containing(x + 1, y - 1, z - 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x - 1, y - 1, z - 1))).getBlock() == SapModBlocks.DUST.get()) {
                world.setBlock(BlockPos.containing(x - 1, y - 1, z - 1), Blocks.AIR.defaultBlockState(), 3);
            }
            if ((world.getBlockState(BlockPos.containing(x, y - 1, z))).getBlock() == SapModBlocks.DUST.get()) {
                world.setBlock(BlockPos.containing(x, y - 1, z), Blocks.AIR.defaultBlockState(), 3);
            }
        }
    }
}
