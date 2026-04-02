package com.supheria.solar_apocalypse_core.procedures;

import com.supheria.solar_apocalypse_core.Procedure;
import com.supheria.solar_apocalypse_core.procedures.transform.TransformRule;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;

import static com.supheria.solar_apocalypse_core.procedures.transform.TransformActions.*;
import static com.supheria.solar_apocalypse_core.procedures.transform.TransformConditions.*;
import static com.supheria.solar_apocalypse_core.procedures.transform.TransformRule.when;

/**
 * 简单方块删除过程（花草、旗帜、蜡烛等零碎物）。
 */
public class SimpleDeleteProcedure {

    private static final Procedure INSTANCE = TransformRule.rulesOf(
            // 阶段1-5：天空可见 + 概率触发 → 空气
            when(stageRange(1, 6).and(sky()).and(randomDayRate()),
                    setBlock(Blocks.AIR)),
            // 阶段2：白天 + 高于阶段2安全高度 + 概率触发 → 空气
            when(stageExact(2).and(daytime()).and(aboveSafeHeight(2)).and(randomDayRate()),
                    setBlock(Blocks.AIR)),
            // 阶段3：高于阶段2安全高度 → 空气
            when(stageExact(3).and(aboveSafeHeight(2)),
                    setBlock(Blocks.AIR)),
            // 阶段4：高于阶段4安全高度 → 空气
            when(stageExact(4).and(aboveSafeHeight(4)),
                    setBlock(Blocks.AIR)),
            // 阶段5：高于阶段5安全高度 → 空气
            when(stageExact(5).and(aboveSafeHeight(5)),
                    setBlock(Blocks.AIR))
    );

    public static void execute(LevelAccessor world, double x, double y, double z) {
        INSTANCE.call(world, x, y, z);
    }
}
