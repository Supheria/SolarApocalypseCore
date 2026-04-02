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

public class GrassBlockTCDirtProcedure {
    public static void execute(LevelAccessor world, double x, double y, double z) {
        if (!BlockSpreadUtils.isOverworld(world, x, y, z)) return;
        var vars = SapModVariables.MapVariables.get(world);
        int stage = (int) vars.SolarFlare;
        boolean isNight = vars.TodayTime > 12566 && vars.TodayTime < 23450;

        // 阶段 1-5：白天、有天空可见、不下雨时，概率性转为泥土
        if (stage >= 1 && stage < 6
                && !isNight
                && world.canSeeSkyFromBelowWater(BlockPos.containing(x, y + 1, z))
                && !world.getLevelData().isRaining()
                && world.dayTime() >= 1000
                && Mth.nextDouble(RandomSource.create(), 0, 10) <= world.dayTime() / 24000.0 + 1) {
            world.setBlock(BlockPos.containing(x, y, z), Blocks.DIRT.defaultBlockState(), 3);
        }

        // 阶段 2-5：有天空可见时，立即转为粗泥土
        if (stage >= 2 && stage < 6
                && world.canSeeSkyFromBelowWater(BlockPos.containing(x, y + 1, z))) {
            world.setBlock(BlockPos.containing(x, y, z), Blocks.COARSE_DIRT.defaultBlockState(), 3);
        }

        // 阶段 3：高于安全高度时，转为沙子并向 MOIST_DIRT 4邻扩散
        if (stage == 3 && y >= StageHeightConfig.getSafeHeight(2)) {
            BlockSpreadUtils.spreadBlock(world, x, y, z,
                    Blocks.SAND.defaultBlockState(),
                    bs -> bs.is(SapModTags.Blocks.MOIST_DIRT),
                    BlockSpreadUtils.OFFSETS_4H);
        }

        // 阶段 4：高于安全高度时，转为尘土并向 MOIST_DIRT 8邻扩散
        if (stage == 4 && y >= StageHeightConfig.getSafeHeight(4)) {
            BlockSpreadUtils.spreadBlock(world, x, y, z,
                    SapModBlocks.DUST.get().defaultBlockState(),
                    bs -> bs.is(SapModTags.Blocks.MOIST_DIRT),
                    BlockSpreadUtils.OFFSETS_8H);
        }

        // 阶段 5：高于 y=8 时，转为空气并向 DIRT 17邻扩散（含下层）
        if (stage == 5 && y >= 8) {
            BlockSpreadUtils.spreadBlock(world, x, y, z,
                    Blocks.AIR.defaultBlockState(),
                    bs -> bs.is(BlockTags.DIRT),
                    BlockSpreadUtils.OFFSETS_17);
        }
    }
}
