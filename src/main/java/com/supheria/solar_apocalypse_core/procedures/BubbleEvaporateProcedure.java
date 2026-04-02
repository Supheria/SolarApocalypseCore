package com.supheria.solar_apocalypse_core.procedures;

import com.supheria.solar_apocalypse_core.Procedure;
import com.supheria.solar_apocalypse_core.procedures.transform.TransformRule;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;

import static com.supheria.solar_apocalypse_core.procedures.transform.TransformActions.*;
import static com.supheria.solar_apocalypse_core.procedures.transform.TransformConditions.*;
import static com.supheria.solar_apocalypse_core.procedures.transform.TransformRule.when;

/**
 * 气泡柱消散过程。
 * 原实现有 247 行重复的手动扩散代码，现通过 TransformRule + spread17 统一处理。
 */
public class BubbleEvaporateProcedure {

    private static final Procedure INSTANCE = TransformRule.rulesOf(
            // 阶段2-5：白天 + 天空可见 + 概率触发，向17邻扩散 → 空气
            when(stageRange(2, 6).and(daytime()).and(sky()).and(randomDayRate()),
                    spread17(Blocks.AIR.defaultBlockState(), bs -> bs.getBlock() == Blocks.BUBBLE_COLUMN)),
            // 阶段3-5：白天 + dayTime>=384000 + 高于阶段2安全高度，向17邻扩散 → 空气
            when(stageRange(3, 6).and(daytime()).and(dayTimeMin(384000)).and(aboveSafeHeight(2)),
                    spread17(Blocks.AIR.defaultBlockState(), bs -> bs.getBlock() == Blocks.BUBBLE_COLUMN)),
            // 阶段4：白天 + 高于阶段4安全高度，向17邻扩散 → 空气
            when(stageExact(4).and(daytime()).and(aboveSafeHeight(4)),
                    spread17(Blocks.AIR.defaultBlockState(), bs -> bs.getBlock() == Blocks.BUBBLE_COLUMN)),
            // 阶段5：高于阶段5安全高度，向17邻扩散 → 空气
            when(stageExact(5).and(aboveSafeHeight(5)),
                    spread17(Blocks.AIR.defaultBlockState(), bs -> bs.getBlock() == Blocks.BUBBLE_COLUMN))
    );

    public static void execute(LevelAccessor world, double x, double y, double z) {
        INSTANCE.call(world, x, y, z);
    }
}
