package com.supheria.solar_apocalypse_core.procedures;

import com.supheria.solar_apocalypse_core.config.solar.StageHeightConfig;
import com.supheria.solar_apocalypse_core.init.SapModBlocks;
import com.supheria.solar_apocalypse_core.init.SapModTags;
import com.supheria.solar_apocalypse_core.network.SapModVariables;
import com.supheria.solar_apocalypse_core.procedures.util.BlockSpreadUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class CoarseDirtTCCrushedDirtBlockProcedure {
    public static void execute(LevelAccessor world, double x, double y, double z) {
        if (!BlockSpreadUtils.isOverworld(world, x, y, z)) return;
        var vars = SapModVariables.MapVariables.get(world);
        int stage = (int) vars.SolarFlare;
        boolean isNight = vars.TodayTime > 12566 && vars.TodayTime < 23450;

        // 阶段 1-5：白天、天空可见、不下雨、dayTime≥24000 时，概率性转为碎泥土
        if (stage >= 1 && stage < 6
                && !isNight
                && world.canSeeSkyFromBelowWater(BlockPos.containing(x, y + 1, z))
                && world.dayTime() >= 24000
                && !world.getLevelData().isRaining()
                && Mth.nextDouble(RandomSource.create(), 0, 10) <= world.dayTime() / 24000.0 + 1) {
            world.setBlock(BlockPos.containing(x, y, z), SapModBlocks.CRUSHED_DIRT.get().defaultBlockState(), 3);
        }

        // 阶段 2：天空可见且 dayTime≥168000，立即转为沙子
        if (stage == 2
                && world.canSeeSkyFromBelowWater(BlockPos.containing(x, y + 1, z))
                && world.dayTime() >= 168000) {
            world.setBlock(BlockPos.containing(x, y, z), Blocks.SAND.defaultBlockState(), 3);
        }
        // 阶段 3：高于安全高度，转为尘土并向 HARD_DIRT 4邻扩散
        else if (stage == 3 && y >= StageHeightConfig.getSafeHeight(2)) {
            BlockSpreadUtils.spreadBlock(world, x, y, z,
                    SapModBlocks.DUST.get().defaultBlockState(),
                    bs -> bs.is(SapModTags.Blocks.HARD_DIRT),
                    BlockSpreadUtils.OFFSETS_4H);
        }
        // 阶段 4-5：高于安全高度，转为空气并向 DIRT 8邻扩散
        else if (stage >= 4 && stage < 6 && y >= StageHeightConfig.getSafeHeight(4)) {
            BlockSpreadUtils.spreadBlock(world, x, y, z,
                    Blocks.AIR.defaultBlockState(),
                    bs -> bs.is(BlockTags.DIRT),
                    BlockSpreadUtils.OFFSETS_8H);
        }

        // 阶段 5：高于阶段5安全高度，进一步向 DIRT 17邻扩散（含下层）
        if (stage == 5 && y >= StageHeightConfig.getSafeHeight(5)) {
            BlockSpreadUtils.spreadBlock(world, x, y, z,
                    Blocks.AIR.defaultBlockState(),
                    bs -> bs.is(BlockTags.DIRT),
                    BlockSpreadUtils.OFFSETS_17);
        }
    }
}
