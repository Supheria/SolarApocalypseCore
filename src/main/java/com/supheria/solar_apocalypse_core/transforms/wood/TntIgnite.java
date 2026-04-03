package com.supheria.solar_apocalypse_core.transforms.wood;

import com.supheria.solar_apocalypse_core.BlockTransform;
import com.supheria.solar_apocalypse_core.transforms.rule.TransformRule;
import net.minecraft.world.level.block.Blocks;

import static com.supheria.solar_apocalypse_core.transforms.rule.TransformActions.setBlockAbove;
import static com.supheria.solar_apocalypse_core.transforms.rule.TransformConditions.*;
import static com.supheria.solar_apocalypse_core.transforms.rule.TransformRule.when;

/**
 * TNT 引燃过程。
 */
public class TntIgnite {

    public static final BlockTransform TRANSFORM = TransformRule.rulesOf(
            // 阶段1-5：天空+晴天+无雨+dayTime>=48000+上方为空气，概率点火
            when(stageRange(1, 6).and(sky()).and(noRain()).and(daytime())
                    .and(dayTimeMin(48000)).and(airAbove()).and(randomDayWood()),
                    setBlockAbove(Blocks.FIRE)),
            // 阶段2：天空+晴天+无雨+dayTime>=216000+上方为空气，慢速概率点火
            when(stageExact(2).and(sky()).and(noRain()).and(daytime())
                    .and(dayTimeMin(216000)).and(airAbove()).and(randomDayWoodSlow()),
                    setBlockAbove(Blocks.FIRE)),
            // 阶段3：高于安全高度+上方为空气，直接点火
            when(stageExact(3).and(aboveSafeHeight(2)).and(airAbove()),
                    setBlockAbove(Blocks.FIRE)),
            // 阶段4-5：y>=8+上方为空气，直接点火
            when(stageRange(4, 6).and(minY(8)).and(airAbove()),
                    setBlockAbove(Blocks.FIRE))
    );

    private TntIgnite() {}
}
