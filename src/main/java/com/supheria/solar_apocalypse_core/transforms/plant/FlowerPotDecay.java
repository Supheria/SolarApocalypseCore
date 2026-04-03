package com.supheria.solar_apocalypse_core.transforms.plant;

import com.supheria.solar_apocalypse_core.BlockTransform;
import com.supheria.solar_apocalypse_core.transforms.rule.TransformRule;
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
            when(stageRange(1, 6).and(sky()).and(isBlock(Blocks.FLOWER_POT).negate()).and(randomDayRate()),
                    setBlock(Blocks.FLOWER_POT)),
            // 阶段2：晴天+高于安全高度，非花盆，概率→花盆
            when(stageExact(2).and(daytime()).and(aboveSafeHeight(2))
                    .and(isBlock(Blocks.FLOWER_POT).negate()).and(randomDayRate()),
                    setBlock(Blocks.FLOWER_POT)),
            // 阶段3：高于安全高度，非花盆→花盆
            when(stageExact(3).and(aboveSafeHeight(2)).and(isBlock(Blocks.FLOWER_POT).negate()),
                    setBlock(Blocks.FLOWER_POT)),
            // 阶段4：高于安全高度，非花盆→花盆
            when(stageExact(4).and(aboveSafeHeight(4)).and(isBlock(Blocks.FLOWER_POT).negate()),
                    setBlock(Blocks.FLOWER_POT)),
            // 阶段5：y>=8，非花盆→花盆
            when(stageExact(5).and(minY(8)).and(isBlock(Blocks.FLOWER_POT).negate()),
                    setBlock(Blocks.FLOWER_POT))
    );

    private FlowerPotDecay() {}
}
