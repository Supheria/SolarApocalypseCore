package com.supheria.solar_apocalypse_core.transforms.plant;

import com.supheria.solar_apocalypse_core.BlockTransform;
import com.supheria.solar_apocalypse_core.transforms.rule.TransformRule;
import com.supheria.solar_apocalypse_core.world.SolarStage;
import net.minecraft.world.level.block.Blocks;

import static com.supheria.solar_apocalypse_core.transforms.rule.TransformActions.setBlock;
import static com.supheria.solar_apocalypse_core.transforms.rule.TransformConditions.*;
import static com.supheria.solar_apocalypse_core.transforms.rule.TransformRule.when;

/**
 * 生态性小型植物与物件的统一衰亡规则。
 */
public final class PlantObjectDecay {

    public static final BlockTransform TRANSFORM = TransformRule.rulesOf(
            when(stageIsEruptionPhase().and(daytime()).and(sky()).and(noRain()),
                    setBlock(Blocks.AIR)),
            when(stageRange(SolarStage.STAGE_3, SolarStage.STAGE_6).and(aboveSafeHeight()),
                    setBlock(Blocks.AIR))
    );

    private PlantObjectDecay() {}
}
