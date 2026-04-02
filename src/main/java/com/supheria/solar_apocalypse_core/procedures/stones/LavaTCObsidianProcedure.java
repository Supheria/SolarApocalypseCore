package com.supheria.solar_apocalypse_core.procedures.stones;

import com.supheria.solar_apocalypse_core.network.SapModVariables;
import com.supheria.solar_apocalypse_core.procedures.util.BlockSpreadUtils;
import com.supheria.solar_apocalypse_core.world.SolarPhase;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;

/**
 * 第六阶段（坍缩）中岩浆逐步转换为黑曜石。
 * 相邻有水时概率提升（模拟自然冷却）。
 */
public class LavaTCObsidianProcedure {
    public static void execute(LevelAccessor world, double x, double y, double z) {
        if (SapModVariables.MapVariables.get(world).getCurrentPhase() != SolarPhase.COLLAPSE) return;
        if (!BlockSpreadUtils.isOverworld(world, x, y, z)) return;

        double probability = BlockSpreadUtils.hasAdjacentWater(world, x, y, z) ? 0.8 : 0.3;
        if (Mth.nextDouble(RandomSource.create(), 0, 1) <= probability) {
            world.setBlock(BlockPos.containing(x, y, z), Blocks.OBSIDIAN.defaultBlockState(), 3);
        }
    }
}
