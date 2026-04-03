package com.supheria.solar_apocalypse_core.transforms.wood;

import com.supheria.solar_apocalypse_core.BlockTransform;
import com.supheria.solar_apocalypse_core.transforms.rule.TransformRule;
import net.minecraft.world.level.block.Blocks;

import static com.supheria.solar_apocalypse_core.transforms.rule.TransformActions.*;
import static com.supheria.solar_apocalypse_core.transforms.rule.TransformConditions.*;
import static com.supheria.solar_apocalypse_core.transforms.rule.TransformRule.when;

/**
 * 木制/羊毛方块燃烧消失过程。
 */
public class WoodBurn {

    public static final BlockTransform TRANSFORM = TransformRule.rulesOf(
            // 阶段1-5：白天 + 天空 + 不下雨 + dayTime>=48000 + 概率 → 空气
            when(stageRange(1, 6).and(daytime()).and(sky()).and(noRain()).and(dayTimeMin(48000)).and(randomDayWood()),
                    setBlock(Blocks.AIR)),
            // 阶段2：白天 + 天空 + 不下雨 + dayTime>=216000 + 慢速概率 → 空气
            when(stageExact(2).and(daytime()).and(sky()).and(noRain()).and(dayTimeMin(216000)).and(randomDayWoodSlow()),
                    setBlock(Blocks.AIR)),
            // 阶段3（dayTime<360000）：天空 + 慢速概率 → 空气
            when(stageExact(3).and(dayTimeLessThan(360000)).and(sky()).and(randomDayWoodSlow()),
                    setBlock(Blocks.AIR)),
            // 阶段3（dayTime>=360000）：y>=63 + 天空 + 慢速概率 → 空气
            when(stageExact(3).and(dayTimeMin(360000)).and(minY(63)).and(sky()).and(randomDayWoodSlow()),
                    setBlock(Blocks.AIR)),
            // 阶段4-5：y>=8 + 天空 + 快速概率 → 空气
            when(stageRange(4, 6).and(minY(8)).and(sky()).and(randomDayWoodFast()),
                    setBlock(Blocks.AIR)),
            // 阶段5：y>=8 + 天空 → 空气（必然触发）
            when(stageExact(5).and(minY(8)).and(sky()),
                    setBlock(Blocks.AIR))
    );

    private WoodBurn() {}
}
