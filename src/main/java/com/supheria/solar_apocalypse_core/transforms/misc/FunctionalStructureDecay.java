package com.supheria.solar_apocalypse_core.transforms.misc;

import com.supheria.solar_apocalypse_core.BlockTransform;
import com.supheria.solar_apocalypse_core.transforms.rule.TransformRule;
import com.supheria.solar_apocalypse_core.world.SolarStage;
import net.minecraft.world.level.block.Blocks;

import static com.supheria.solar_apocalypse_core.transforms.rule.TransformActions.setBlock;
import static com.supheria.solar_apocalypse_core.transforms.rule.TransformConditions.*;
import static com.supheria.solar_apocalypse_core.transforms.rule.TransformRule.when;

/**
 * 普通人工功能结构的统一失效规则。
 */
public final class FunctionalStructureDecay {

    public static final BlockTransform TRANSFORM = TransformRule.rulesOf(
            when(stageIsEruptionPhase().and(sky()),
                    setBlock(Blocks.AIR)),
            when(stageRange(SolarStage.STAGE_3, SolarStage.STAGE_6).and(aboveSafeHeight()),
                    setBlock(Blocks.AIR))
    );

    private FunctionalStructureDecay() {}
}
