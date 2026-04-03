package com.supheria.solar_apocalypse_core.transforms.fluid;

import com.supheria.solar_apocalypse_core.BlockTransform;
import com.supheria.solar_apocalypse_core.transforms.rule.TransformRule;
import com.supheria.solar_apocalypse_core.transforms.util.BlockSpreadUtils;

import static com.supheria.solar_apocalypse_core.transforms.rule.TransformActions.spreadWater;
import static com.supheria.solar_apocalypse_core.transforms.rule.TransformConditions.*;
import static com.supheria.solar_apocalypse_core.transforms.rule.TransformRule.when;

/**
 * 水方块蒸发过程。
 */
public class WaterEvaporate {

    public static final BlockTransform TRANSFORM = TransformRule.rulesOf(
            // 阶段2-5：有天空+晴天+超过蒸发高度，spread17蒸发水
            when(stageRange(2, 6).and(sky()).and(daytime()).and(aboveWaterEvapHeight()).and(randomDayRate()),
                    spreadWater(BlockSpreadUtils.OFFSETS_17)),
            // 阶段3：日累积>=384000天且晴天，y>=63，额外spread17
            when(stageExact(3).and(dayTimeMin(384000)).and(daytime()).and(minY(63)),
                    spreadWater(BlockSpreadUtils.OFFSETS_17)),
            // 阶段4：晴天且y>=8，5×5×4层大范围蒸发
            when(stageExact(4).and(daytime()).and(minY(8)),
                    spreadWater(BlockSpreadUtils.OFFSETS_WATER_5X5_4)),
            // 阶段5：y>=8，5×5×10层超大范围蒸发（无需晴天）
            when(stageExact(5).and(minY(8)),
                    spreadWater(BlockSpreadUtils.OFFSETS_WATER_5X5_10))
    );

    private WaterEvaporate() {}
}
