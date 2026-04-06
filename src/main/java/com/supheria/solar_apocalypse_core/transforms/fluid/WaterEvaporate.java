package com.supheria.solar_apocalypse_core.transforms.fluid;

import com.supheria.solar_apocalypse_core.BlockTransform;
import com.supheria.solar_apocalypse_core.transforms.rule.TransformRule;
import com.supheria.solar_apocalypse_core.transforms.util.BlockSpreadUtils;
import com.supheria.solar_apocalypse_core.world.SolarStage;

import static com.supheria.solar_apocalypse_core.transforms.rule.TransformActions.spreadWaterRateLimited;
import static com.supheria.solar_apocalypse_core.transforms.rule.TransformConditions.*;
import static com.supheria.solar_apocalypse_core.transforms.rule.TransformConditions.daytime;
import static com.supheria.solar_apocalypse_core.transforms.rule.TransformRule.when;

/**
 * 水方块蒸发过程。
 */
public class WaterEvaporate {

    public static final BlockTransform TRANSFORM = TransformRule.rulesOf(
            // 阶段2-5：有露天+晴天+超过蒸发高度，按阶段速率小批量蒸发
            when(stageRange(SolarStage.STAGE_2, SolarStage.STAGE_6).and(sky()).and(daytime()).and(aboveWaterEvaporateHeight()).and(randomDayRate()).and(stageRateScaled(0.75)),
                    spreadWaterRateLimited(BlockSpreadUtils.OFFSETS_17, 0.8)),
            // 阶段3：晴天且超过蒸发高度，额外spread17，但改为限额推进
            when(stageExact(SolarStage.STAGE_3).and(daytime()).and(aboveWaterEvaporateHeight()).and(stageRateScaled(0.9)),
                    spreadWaterRateLimited(BlockSpreadUtils.OFFSETS_17, 1.0)),
            // 阶段4：晴天且超过蒸发高度，改为高频限额的5×5×4层蒸发
            when(stageExact(SolarStage.STAGE_4).and(daytime()).and(aboveWaterEvaporateHeight()).and(stageRateScaled(1.0)),
                    spreadWaterRateLimited(BlockSpreadUtils.OFFSETS_WATER_5X5_4, 1.0)),
            // 阶段5：超过蒸发高度，维持最强蒸发，但拆成持续限额推进
            when(stageExact(SolarStage.STAGE_5).and(aboveWaterEvaporateHeight()).and(stageRateScaled(1.15)),
                    spreadWaterRateLimited(BlockSpreadUtils.OFFSETS_WATER_5X5_10, 1.15))
    );

    private WaterEvaporate() {}
}
