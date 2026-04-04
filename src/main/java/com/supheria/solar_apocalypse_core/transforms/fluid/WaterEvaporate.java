package com.supheria.solar_apocalypse_core.transforms.fluid;

import com.supheria.solar_apocalypse_core.BlockTransform;
import com.supheria.solar_apocalypse_core.transforms.rule.TransformRule;
import com.supheria.solar_apocalypse_core.transforms.util.BlockSpreadUtils;
import com.supheria.solar_apocalypse_core.world.SolarStage;

import static com.supheria.solar_apocalypse_core.transforms.rule.TransformActions.spreadWater;
import static com.supheria.solar_apocalypse_core.transforms.rule.TransformConditions.*;
import static com.supheria.solar_apocalypse_core.transforms.rule.TransformConditions.daytime;
import static com.supheria.solar_apocalypse_core.transforms.rule.TransformRule.when;

/**
 * 水方块蒸发过程。
 */
public class WaterEvaporate {

    public static final BlockTransform TRANSFORM = TransformRule.rulesOf(
            // 阶段2-5：有露天+晴天+超过蒸发高度，spread17蒸发水
            when(stageRange(SolarStage.STAGE_2, SolarStage.STAGE_6).and(sky()).and(daytime()).and(aboveWaterEvaporateHeight()).and(randomDayRate()),
                    spreadWater(BlockSpreadUtils.OFFSETS_17)),
            // 阶段3：晴天且超过蒸发高度，额外spread17
            when(stageExact(SolarStage.STAGE_3).and(daytime()).and(aboveWaterEvaporateHeight()),
                    spreadWater(BlockSpreadUtils.OFFSETS_17)),
            // 阶段4：晴天且超过蒸发高度，5×5×4层大范围蒸发
            when(stageExact(SolarStage.STAGE_4).and(daytime()).and(aboveWaterEvaporateHeight()),
                    spreadWater(BlockSpreadUtils.OFFSETS_WATER_5X5_4)),
            // 阶段5：超过蒸发高度，5×5×10层超大范围蒸发（无需晴天）
            when(stageExact(SolarStage.STAGE_5).and(aboveWaterEvaporateHeight()),
                    spreadWater(BlockSpreadUtils.OFFSETS_WATER_5X5_10))
    );

    private WaterEvaporate() {}
}
