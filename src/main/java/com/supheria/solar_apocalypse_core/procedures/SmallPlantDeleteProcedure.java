package com.supheria.solar_apocalypse_core.procedures;

import com.supheria.solar_apocalypse_core.Procedure;
import com.supheria.solar_apocalypse_core.procedures.transform.TransformRule;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;

import static com.supheria.solar_apocalypse_core.procedures.transform.TransformActions.*;
import static com.supheria.solar_apocalypse_core.procedures.transform.TransformConditions.*;
import static com.supheria.solar_apocalypse_core.procedures.transform.TransformRule.when;

/**
 * 小型植物删除过程（树苗、竹子等）。
 */
public class SmallPlantDeleteProcedure {

    private static final Procedure INSTANCE = TransformRule.rulesOf(
            // 阶段1-5：白天 + 天空可见 + 不下雨 + 概率触发 → 空气
            when(stageRange(1, 6).and(daytime()).and(sky()).and(noRain()).and(randomDayRate()),
                    setBlock(Blocks.AIR)),
            // 阶段2：天空可见 + 不下雨 + 概率触发 → 空气（无白天限制，夜间也触发）
            when(stageExact(2).and(sky()).and(noRain()).and(randomDayRate()),
                    setBlock(Blocks.AIR)),
            // 阶段3：y>=63 → 空气
            when(stageExact(3).and(minY(63)),
                    setBlock(Blocks.AIR)),
            // 阶段4-5：y>=8 → 空气
            when(stageRange(4, 6).and(minY(8)),
                    setBlock(Blocks.AIR))
    );

    public static void execute(LevelAccessor world, double x, double y, double z) {
        INSTANCE.call(world, x, y, z);
    }
}
