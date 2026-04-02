package com.supheria.solar_apocalypse_core.procedures;

import com.supheria.solar_apocalypse_core.config.solar.StageHeightConfig;
import com.supheria.solar_apocalypse_core.init.SapModBlocks;
import com.supheria.solar_apocalypse_core.network.SapModVariables;
import com.supheria.solar_apocalypse_core.procedures.util.BlockSpreadUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class SandTCDustProcedure {
    public static void execute(LevelAccessor world, double x, double y, double z) {
        if (!BlockSpreadUtils.isOverworld(world, x, y, z)) return;
        var vars = SapModVariables.MapVariables.get(world);
        int stage = (int) vars.SolarFlare;
        boolean isNight = vars.TodayTime > 12566 && vars.TodayTime < 23450;

        // 阶段 2-5：白天、天空可见、上方无水、不下雨时，1/10 概率转为尘土
        if (stage >= 2 && stage < 6
                && !isNight
                && world.canSeeSkyFromBelowWater(BlockPos.containing(x, y + 1, z))
                && world.getBlockState(BlockPos.containing(x, y + 1, z)).getBlock() != Blocks.WATER
                && !world.getLevelData().isRaining()
                && Mth.nextInt(RandomSource.create(), 1, 10) == 1) {
            world.setBlock(BlockPos.containing(x, y, z), SapModBlocks.DUST.get().defaultBlockState(), 3);
        }

        // 阶段 3：白天、上方无水、高于安全高度，转为空气并向沙子17邻扩散（含下层）
        if (stage == 3
                && !isNight
                && world.getBlockState(BlockPos.containing(x, y + 1, z)).getBlock() != Blocks.WATER
                && y >= StageHeightConfig.getSafeHeight(2)) {
            BlockSpreadUtils.spreadBlock(world, x, y, z,
                    Blocks.AIR.defaultBlockState(),
                    bs -> bs.is(BlockTags.SAND),
                    BlockSpreadUtils.OFFSETS_17);
        }

        // 阶段 4：高于 y=32，转为空气并向沙子5×5范围扩散（含下层）
        if (stage == 4 && y >= 32) {
            BlockSpreadUtils.spreadBlock(world, x, y, z,
                    Blocks.AIR.defaultBlockState(),
                    bs -> bs.is(BlockTags.SAND),
                    BlockSpreadUtils.OFFSETS_5X5);
        }

        // 阶段 5：高于 y=8，转为空气并向沙子5×5范围扩散（含下层）
        if (stage == 5 && y >= 8) {
            BlockSpreadUtils.spreadBlock(world, x, y, z,
                    Blocks.AIR.defaultBlockState(),
                    bs -> bs.is(BlockTags.SAND),
                    BlockSpreadUtils.OFFSETS_5X5);
        }
    }
}
