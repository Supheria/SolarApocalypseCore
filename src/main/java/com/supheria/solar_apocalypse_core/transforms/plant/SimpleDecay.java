package com.supheria.solar_apocalypse_core.transforms.plant;

import com.supheria.solar_apocalypse_core.BlockTransform;
import com.supheria.solar_apocalypse_core.transforms.rule.TransformRule;
import com.supheria.solar_apocalypse_core.world.SolarStage;
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
            when(stageIsEruptionPhase().and(sky()).and(randomDayRate()),
                    setBlock(Blocks.AIR)),
            // 阶段2：白天 + 高于安全高度 + 概率触发 → 空气
            when(stageExact(SolarStage.STAGE_2).and(daytime()).and(aboveSafeHeight()).and(randomDayRate()),
                    setBlock(Blocks.AIR)),
            // 阶段3：高于安全高度 → 空气
            when(stageExact(SolarStage.STAGE_3).and(aboveSafeHeight()),
                    setBlock(Blocks.AIR)),
            // 阶段4：高于安全高度 → 空气
            when(stageExact(SolarStage.STAGE_4).and(aboveSafeHeight()),
                    setBlock(Blocks.AIR)),
            // 阶段5：高于安全高度 → 空气
            when(stageExact(SolarStage.STAGE_5).and(aboveSafeHeight()),
                    setBlock(Blocks.AIR))
    );

    private SimpleDecay() {}
}
