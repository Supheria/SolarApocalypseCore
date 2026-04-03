package com.supheria.solar_apocalypse_core.transforms.plant;

import com.supheria.solar_apocalypse_core.BlockTransform;
import com.supheria.solar_apocalypse_core.transforms.rule.TransformRule;
import com.supheria.solar_apocalypse_core.world.SolarStage;
import net.minecraft.world.level.block.Blocks;

import static com.supheria.solar_apocalypse_core.transforms.rule.TransformActions.*;
import static com.supheria.solar_apocalypse_core.transforms.rule.TransformConditions.*;
import static com.supheria.solar_apocalypse_core.transforms.rule.TransformRule.when;

/**
 * 小型植物消失（树苗、竹子等）。
 */
public class SmallPlantDecay {

    public static final BlockTransform TRANSFORM = TransformRule.rulesOf(
            // 阶段1-5：白天 + 天空可见 + 不下雨 + 概率触发 → 空气
            when(stageIsEruptionPhase().and(daytime()).and(sky()).and(noRain()).and(randomDayRate()),
                    setBlock(Blocks.AIR)),
            // 阶段2：天空可见 + 不下雨 + 概率触发 → 空气（无白天限制，夜间也触发）
            when(stageExact(SolarStage.STAGE_2).and(sky()).and(noRain()).and(randomDayRate()),
                    setBlock(Blocks.AIR)),
            // 阶段3：y>=63 → 空气
            when(stageExact(SolarStage.STAGE_3).and(aboveSafeHeight()),
                    setBlock(Blocks.AIR)),
            // 阶段4-5：y>=8 → 空气
            when(stageRange(SolarStage.STAGE_4, SolarStage.STAGE_6).and(aboveSafeHeight()),
                    setBlock(Blocks.AIR))
    );

    private SmallPlantDecay() {}
}
