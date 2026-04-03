package com.supheria.solar_apocalypse_core.transforms.plant;

import com.supheria.solar_apocalypse_core.BlockTransform;
import com.supheria.solar_apocalypse_core.transforms.rule.TransformRule;
import com.supheria.solar_apocalypse_core.world.SolarStage;
import net.minecraft.world.level.block.Blocks;

import static com.supheria.solar_apocalypse_core.transforms.rule.TransformActions.setBlock;
import static com.supheria.solar_apocalypse_core.transforms.rule.TransformConditions.*;
import static com.supheria.solar_apocalypse_core.transforms.rule.TransformRule.when;

/**
 * 花盆内植物掉落（含植物的花盆 → 空花盆）。
 */
public class FlowerPotDecay {

    public static final BlockTransform TRANSFORM = TransformRule.rulesOf(
            // 阶段1-5：天空可见，非花盆，概率→花盆
            when(stageIsEruptionPhase().and(sky()).and(isBlock(Blocks.FLOWER_POT).negate()).and(randomDayRate()),
                    setBlock(Blocks.FLOWER_POT)),
            // 阶段2：晴天+高于安全高度，非花盆，概率→花盆
            when(stageExact(SolarStage.STAGE_2).and(daytime()).and(aboveSafeHeight())
                    .and(isBlock(Blocks.FLOWER_POT).negate()).and(randomDayRate()),
                    setBlock(Blocks.FLOWER_POT)),
            // 阶段3：高于安全高度，非花盆→花盆
            when(stageExact(SolarStage.STAGE_3).and(aboveSafeHeight()).and(isBlock(Blocks.FLOWER_POT).negate()),
                    setBlock(Blocks.FLOWER_POT)),
            // 阶段4：高于安全高度，非花盆→花盆
            when(stageExact(SolarStage.STAGE_4).and(aboveSafeHeight()).and(isBlock(Blocks.FLOWER_POT).negate()),
                    setBlock(Blocks.FLOWER_POT)),
            // 阶段5：高于安全高度，非花盆→花盆
            when(stageExact(SolarStage.STAGE_5).and(aboveSafeHeight()).and(isBlock(Blocks.FLOWER_POT).negate()),
                    setBlock(Blocks.FLOWER_POT))
    );

    private FlowerPotDecay() {}
}
