package com.supheria.solar_apocalypse_core.procedures;

import com.supheria.solar_apocalypse_core.Procedure;
import com.supheria.solar_apocalypse_core.procedures.transform.TransformRule;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;

import static com.supheria.solar_apocalypse_core.procedures.transform.TransformActions.*;
import static com.supheria.solar_apocalypse_core.procedures.transform.TransformConditions.*;
import static com.supheria.solar_apocalypse_core.procedures.transform.TransformRule.when;

/**
 * 木制/羊毛方块燃烧消失过程。
 */
public class WoodBlockFProcedure {

    private static final Procedure INSTANCE = TransformRule.rulesOf(
            // 阶段1-5：白天 + 天空 + 不下雨 + dayTime>=48000 + 概率 → 空气
            when(stageRange(1, 6).and(daytime()).and(sky()).and(noRain()).and(dayTimeMin(48000)).and(randomDayWood()),
                    setBlock(Blocks.AIR)),
            // 阶段2：白天 + 天空 + 不下雨 + dayTime>=216000 + 慢速概率 → 空气
            when(stageExact(2).and(daytime()).and(sky()).and(noRain()).and(dayTimeMin(216000)).and(randomDayWoodSlow()),
                    setBlock(Blocks.AIR)),
            // 阶段3（dayTime<360000）：天空 + 慢速概率 → 空气
            when(stageExact(3).and(dayTimeLessThan(360000)).and(sky()).and(randomDayWoodSlow()),
                    setBlock(Blocks.AIR)),
            // 阶段3（dayTime>=360000）：y>=63 + 天空 + 慢速概率 → 空气
            when(stageExact(3).and(dayTimeMin(360000)).and(minY(63)).and(sky()).and(randomDayWoodSlow()),
                    setBlock(Blocks.AIR)),
            // 阶段4-5：y>=8 + 天空 + 快速概率 → 空气
            when(stageRange(4, 6).and(minY(8)).and(sky()).and(randomDayWoodFast()),
                    setBlock(Blocks.AIR)),
            // 阶段5：y>=8 + 天空 → 空气（必然触发）
            when(stageExact(5).and(minY(8)).and(sky()),
                    setBlock(Blocks.AIR))
    );

    public static void execute(LevelAccessor world, double x, double y, double z) {
        INSTANCE.call(world, x, y, z);
    }
}
