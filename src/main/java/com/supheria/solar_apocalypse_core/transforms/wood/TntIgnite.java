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
            // 阶段1-5：露天+晴天+无雨+上方为空气，概率点火
            when(stageIsEruptionPhase().and(sky()).and(noRain()).and(daytime())
                    .and(airAbove()).and(randomDayWood()),
                    setBlockAbove(Blocks.FIRE)),
            // 阶段2：露天+晴天+无雨+上方为空气，慢速概率点火
            when(stageExact(SolarStage.STAGE_2).and(sky()).and(noRain()).and(daytime())
                    .and(airAbove()).and(randomDayWoodSlow()),
                    setBlockAbove(Blocks.FIRE)),
            // 阶段3：高于安全高度+上方为空气，直接点火
            when(stageExact(SolarStage.STAGE_3).and(aboveSafeHeight()).and(airAbove()),
                    setBlockAbove(Blocks.FIRE)),
            // 阶段4-5：高于安全高度+上方为空气，直接点火
            when(stageRange(SolarStage.STAGE_4, SolarStage.STAGE_6).and(aboveSafeHeight()).and(airAbove()),
                    setBlockAbove(Blocks.FIRE))
    );

    private TntIgnite() {}
}
