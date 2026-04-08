package com.supheria.solar_apocalypse_core.transforms.wood;

import com.supheria.solar_apocalypse_core.BlockTransform;
import com.supheria.solar_apocalypse_core.transforms.rule.TransformRule;
import com.supheria.solar_apocalypse_core.world.SolarStage;

import static com.supheria.solar_apocalypse_core.transforms.rule.TransformActions.*;
import static com.supheria.solar_apocalypse_core.transforms.rule.TransformConditions.*;
import static com.supheria.solar_apocalypse_core.transforms.rule.TransformConditions.aboveSafeHeight;
import static com.supheria.solar_apocalypse_core.transforms.rule.TransformRule.when;

/**
 * 木制/羊毛方块燃烧消失过程。
 */
public class WoodBurn {

    public static final BlockTransform TRANSFORM = TransformRule.rulesOf(
            // 阶段1-5：白天 + 露天 + 不下雨 + 概率 → 掉落并销毁
            when(stageIsEruptionPhase().and(daytime()).and(sky()).and(noRain()).and(randomDayWood()),
                    destroyBlockWithDrops()),
            // 阶段2：白天 + 露天 + 不下雨 + 慢速概率 → 掉落并销毁
            when(stageExact(SolarStage.STAGE_2).and(daytime()).and(sky()).and(noRain()).and(randomDayWoodSlow()),
                    destroyBlockWithDrops()),
            // 阶段3：高于安全高度 + 露天 + 慢速概率 → 掉落并销毁
            when(stageExact(SolarStage.STAGE_3).and(aboveSafeHeight()).and(sky()).and(randomDayWoodSlow()),
                    destroyBlockWithDrops()),
            // 阶段4-5：高于安全高度 + 露天 + 快速概率 → 掉落并销毁
            when(stageRange(SolarStage.STAGE_4, SolarStage.STAGE_6).and(aboveSafeHeight()).and(sky()).and(randomDayWoodFast()),
                    destroyBlockWithDrops()),
            // 阶段5：高于安全高度 + 露天 → 掉落并销毁（必然触发）
            when(stageExact(SolarStage.STAGE_5).and(aboveSafeHeight()).and(sky()),
                    destroyBlockWithDrops())
    );

    private WoodBurn() {}
}
