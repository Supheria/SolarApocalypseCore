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
            when(stageIsEruptionPhase().and(daytime()).and(sky()).and(noRain()),
                    setBlock(Blocks.AIR)),
            when(stageRange(SolarStage.STAGE_4, SolarStage.STAGE_6).and(aboveSafeHeight()),
                    setBlock(Blocks.AIR))
    );

    private SmallPlantDecay() {}
}
