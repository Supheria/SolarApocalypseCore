package com.supheria.solar_apocalypse_core.procedures.stones;

import com.supheria.solar_apocalypse_core.config.solar.StageHeightConfig;
import com.supheria.solar_apocalypse_core.network.SapModVariables;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

/**
 * 通用石头系方块转换过程。
 * 替代以下8个几乎完全相同的类：
 *   StoneTCCobblestoneProcedure, StoneTCCobblestoneSlabProcedure,
 *   StoneTCCobblestoneStairsProcedure, StoneTCCobblestoneWallProcedure,
 *   StoneTCCobbledDeepslateProcedure, StoneTCCobbledDeepslateSlabProcedure,
 *   StoneTCCobbledDeepslateStairsProcedure, StoneTCCobbledDeepslateWallProcedure
 *
 * 使用方式（在 SolarApocalypseCoreMod 中）：
 *   return (w, x, y, z) -> StoneTCProcedure.execute(Blocks.COBBLESTONE, w, x, y, z);
 */
public class StoneTCProcedure {

    private static final TagKey<net.minecraft.world.level.biome.Biome> IS_OVERWORLD =
            TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_overworld"));

    public static void execute(Block target, LevelAccessor world, double x, double y, double z) {
        int stage = (int) SapModVariables.MapVariables.get(world).SolarFlare;

        if (!world.getBiome(BlockPos.containing(x, y, z)).is(IS_OVERWORLD)) {
            return;
        }

        // 阶段 2-5：需要暴露天空或相邻岩浆，高于各阶段安全高度的最小值，满足概率
        if (stage >= 2 && stage < 6) {
            int minHeight = Math.min(
                    Math.min(StageHeightConfig.getSafeHeight(2), StageHeightConfig.getSafeHeight(3)),
                    Math.min(StageHeightConfig.getSafeHeight(4), StageHeightConfig.getSafeHeight(5)));
            if ((world.canSeeSkyFromBelowWater(BlockPos.containing(x, y + 1, z)) || hasAdjacentLava(world, x, y, z))
                    && y >= minHeight
                    && isRandomTriggered(world)) {
                world.setBlock(BlockPos.containing(x, y, z), target.defaultBlockState(), 3);
            }
        }

        // 阶段 3：无需邻接条件，高于阶段2安全高度，满足概率
        if (stage == 3) {
            if (y >= StageHeightConfig.getSafeHeight(2) && isRandomTriggered(world)) {
                world.setBlock(BlockPos.containing(x, y, z), target.defaultBlockState(), 3);
            }
        }

        // 阶段 4-5：无需邻接条件，无需概率，高于阶段4安全高度即触发
        if (stage >= 4 && stage < 6) {
            if (y >= StageHeightConfig.getSafeHeight(4)) {
                world.setBlock(BlockPos.containing(x, y, z), target.defaultBlockState(), 3);
            }
        }
    }

    private static boolean hasAdjacentLava(LevelAccessor world, double x, double y, double z) {
        return world.getBlockState(BlockPos.containing(x, y + 1, z)).getBlock() == Blocks.LAVA
                || world.getBlockState(BlockPos.containing(x, y - 1, z)).getBlock() == Blocks.LAVA
                || world.getBlockState(BlockPos.containing(x + 1, y, z)).getBlock() == Blocks.LAVA
                || world.getBlockState(BlockPos.containing(x - 1, y, z)).getBlock() == Blocks.LAVA
                || world.getBlockState(BlockPos.containing(x, y, z + 1)).getBlock() == Blocks.LAVA
                || world.getBlockState(BlockPos.containing(x, y, z - 1)).getBlock() == Blocks.LAVA;
    }

    private static boolean isRandomTriggered(LevelAccessor world) {
        return Mth.nextDouble(RandomSource.create(), 0, (world.dayTime() / 24000.0) + 5) <= (world.dayTime() / 16000.0);
    }
}
