package com.supheria.solar_apocalypse_core.transforms.stone;

import com.supheria.solar_apocalypse_core.BlockTransform;
import com.supheria.solar_apocalypse_core.network.SapModVariables;
import com.supheria.solar_apocalypse_core.transforms.util.BlockSpreadUtils;
import com.supheria.solar_apocalypse_core.world.SolarStageHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;

/**
 * 第六阶段（坍缺）中岩浆逐步转换为黑曜石。
 * 相邻有水时概率提升（模拟自然冷却）。
 */
public class LavaToObsidian {

    public static final BlockTransform TRANSFORM = LavaToObsidian::transform;

    private static void transform(LevelAccessor world, double x, double y, double z) {
        if (SapModVariables.MapVariables.get(world).getCurrentStage() != SolarStageHelper.STAGE_6) return;
        if (!BlockSpreadUtils.isOverworld(world, x, y, z)) return;

        double probability = BlockSpreadUtils.hasAdjacentWater(world, x, y, z) ? 0.8 : 0.3;
        if (Mth.nextDouble(RandomSource.create(), 0, 1) <= probability) {
            world.setBlock(BlockPos.containing(x, y, z), Blocks.OBSIDIAN.defaultBlockState(), 3);
        }
    }

    private LavaToObsidian() {}
}
