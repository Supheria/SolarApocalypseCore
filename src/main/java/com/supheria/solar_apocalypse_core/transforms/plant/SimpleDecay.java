package com.supheria.solar_apocalypse_core.transforms.plant;

import com.supheria.solar_apocalypse_core.BlockTransform;
import com.supheria.solar_apocalypse_core.transforms.rule.TransformRule;
import net.minecraft.world.level.block.Blocks;

import static com.supheria.solar_apocalypse_core.transforms.rule.TransformActions.*;
import static com.supheria.solar_apocalypse_core.transforms.rule.TransformConditions.*;
import static com.supheria.solar_apocalypse_core.transforms.rule.TransformRule.when;

/**
 * 简单方块消失（花草、旗帜、蜡烛等零碎物）。
 */
public class SimpleDecay {

    public static final BlockTransform TRANSFORM = TransformRule.rulesOf(
            // 阶段1-5：天空可见 + 概率触发 → 空气
            when(stageRange(1, 6).and(sky()).and(randomDayRate()),
                    setBlock(Blocks.AIR)),
            // 阶段2：白天 + 高于阶段2安全高度 + 概率触发 → 空气
            when(stageExact(2).and(daytime()).and(aboveSafeHeight(2)).and(randomDayRate()),
                    setBlock(Blocks.AIR)),
            // 阶段3：高于阶段2安全高度 → 空气
            when(stageExact(3).and(aboveSafeHeight(2)),
                    setBlock(Blocks.AIR)),
            // 阶段4：高于阶段4安全高度 → 空气
            when(stageExact(4).and(aboveSafeHeight(4)),
                    setBlock(Blocks.AIR)),
            // 阶段5：高于阶段5安全高度 → 空气
            when(stageExact(5).and(aboveSafeHeight(5)),
                    setBlock(Blocks.AIR))
    );

    private SimpleDecay() {}
}
