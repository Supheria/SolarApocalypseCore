package com.supheria.solar_apocalypse_core.transforms.fluid;

import com.supheria.solar_apocalypse_core.BlockTransform;
import com.supheria.solar_apocalypse_core.transforms.rule.TransformRule;
import net.minecraft.world.level.block.Blocks;

import static com.supheria.solar_apocalypse_core.transforms.rule.TransformActions.*;
import static com.supheria.solar_apocalypse_core.transforms.rule.TransformConditions.*;
import static com.supheria.solar_apocalypse_core.transforms.rule.TransformRule.when;

/**
 * 气泡柱消散过程。
 */
public class BubbleEvaporate {

    public static final BlockTransform TRANSFORM = TransformRule.rulesOf(
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

    private BubbleEvaporate() {}
}
