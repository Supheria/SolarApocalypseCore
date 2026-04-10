package com.supheria.solar_apocalypse_core.transforms.wood;

import com.supheria.solar_apocalypse_core.BlockTransform;
import com.supheria.solar_apocalypse_core.transforms.rule.TransformRule;
import com.supheria.solar_apocalypse_core.world.SolarStage;
import net.minecraft.world.level.block.Blocks;

import static com.supheria.solar_apocalypse_core.transforms.rule.TransformActions.setBlockAbove;
import static com.supheria.solar_apocalypse_core.transforms.rule.TransformConditions.*;
import static com.supheria.solar_apocalypse_core.transforms.rule.TransformConditions.aboveSafeHeight;
import static com.supheria.solar_apocalypse_core.transforms.rule.TransformRule.when;

/**
 * TNT 引燃过程。
 */
public class TntIgnite {

    public static final BlockTransform TRANSFORM = TransformRule.rulesOf(
            // 爆发阶段：露天 TNT 会进入持续点火态
            when(stageIsEruptionPhase().and(sky()).and(noRain()).and(daytime())
                    .and(airAbove()),
                    setBlockAbove(Blocks.FIRE)),
            when(stageRange(SolarStage.STAGE_4, SolarStage.STAGE_6).and(aboveSafeHeight()).and(airAbove()),
                    setBlockAbove(Blocks.FIRE))
    );

    private TntIgnite() {}
}
