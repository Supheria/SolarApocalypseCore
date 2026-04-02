package com.supheria.solar_apocalypse_core.procedures;

import com.supheria.solar_apocalypse_core.Procedure;
import com.supheria.solar_apocalypse_core.procedures.transform.TransformRule;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;

import static com.supheria.solar_apocalypse_core.procedures.transform.TransformActions.setBlockAbove;
import static com.supheria.solar_apocalypse_core.procedures.transform.TransformConditions.*;
import static com.supheria.solar_apocalypse_core.procedures.transform.TransformRule.when;

public class TNTFProcedure {

    private static final Procedure INSTANCE = TransformRule.rulesOf(
            // 阶段1-5：天空+晴天+无雨+dayTime>=48000+上方为空气，概率点火
            when(stageRange(1, 6).and(sky()).and(noRain()).and(daytime())
                    .and(dayTimeMin(48000)).and(airAbove()).and(randomDayWood()),
                    setBlockAbove(Blocks.FIRE)),
            // 阶段2：天空+晴天+无雨+dayTime>=216000+上方为空气，慢速概率点火
            when(stageExact(2).and(sky()).and(noRain()).and(daytime())
                    .and(dayTimeMin(216000)).and(airAbove()).and(randomDayWoodSlow()),
                    setBlockAbove(Blocks.FIRE)),
            // 阶段3：高于安全高度+上方为空气，直接点火
            when(stageExact(3).and(aboveSafeHeight(2)).and(airAbove()),
                    setBlockAbove(Blocks.FIRE)),
            // 阶段4-5：y>=8+上方为空气，直接点火
            when(stageRange(4, 6).and(minY(8)).and(airAbove()),
                    setBlockAbove(Blocks.FIRE))
    );

    public static void execute(LevelAccessor world, double x, double y, double z) {
        INSTANCE.call(world, x, y, z);
    }
}
