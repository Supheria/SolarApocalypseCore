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
            // 第二阶段起，露天可燃结构开始持续焚毁。
            when(stageRange(SolarStage.STAGE_2, SolarStage.STAGE_6).and(daytime()).and(sky()).and(noRain()),
                    destroyBlockWithDrops()),
            // 第三至第五阶段：高温带内露天结构会持续焚毁
            when(stageRange(SolarStage.STAGE_3, SolarStage.STAGE_6).and(aboveSafeHeight()).and(sky()),
                    destroyBlockWithDrops())
    );

    private WoodBurn() {}
}
