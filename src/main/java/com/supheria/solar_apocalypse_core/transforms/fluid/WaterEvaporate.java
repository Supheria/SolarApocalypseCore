package com.supheria.solar_apocalypse_core.transforms.fluid;

import com.supheria.solar_apocalypse_core.BlockTransform;
import com.supheria.solar_apocalypse_core.init.SolarModBlocks;
import com.supheria.solar_apocalypse_core.transforms.rule.TransformRule;
import com.supheria.solar_apocalypse_core.transforms.util.BlockSpreadUtils;
import com.supheria.solar_apocalypse_core.world.SolarStage;

import static com.supheria.solar_apocalypse_core.transforms.rule.TransformActions.spreadWaterRateLimited;
import static com.supheria.solar_apocalypse_core.transforms.rule.TransformConditions.*;
import static com.supheria.solar_apocalypse_core.transforms.rule.TransformConditions.daytime;
import static com.supheria.solar_apocalypse_core.transforms.rule.TransformRule.when;

/**
 * 水方块蒸发过程。
 */
public class WaterEvaporate {
    private static final net.minecraft.world.level.block.state.BlockState EVAPORATED_VOID =
            SolarModBlocks.EVAPORATED_VOID.get().defaultBlockState();

    public static final BlockTransform TRANSFORM = TransformRule.rulesOf(
            when(stageExact(SolarStage.STAGE_2).and(waterSource()).and(daytime()).and(aboveWaterEvaporateHeight()),
                    spreadWaterRateLimited(BlockSpreadUtils.OFFSETS_WATER_5X5_10, 1.0, EVAPORATED_VOID)),
            when(stageExact(SolarStage.STAGE_3).and(waterSource()).and(aboveWaterEvaporateHeight()),
                    spreadWaterRateLimited(BlockSpreadUtils.OFFSETS_WATER_5X5_10, 1.35, EVAPORATED_VOID)),
            when(stageExact(SolarStage.STAGE_4).and(waterSource()).and(aboveWaterEvaporateHeight()),
                    spreadWaterRateLimited(BlockSpreadUtils.OFFSETS_WATER_5X5_10, 1.6, EVAPORATED_VOID)),
            when(stageExact(SolarStage.STAGE_5).and(waterSource()).and(aboveWaterEvaporateHeight()),
                    spreadWaterRateLimited(BlockSpreadUtils.OFFSETS_WATER_5X5_10, 2.0, EVAPORATED_VOID))
    );

    private WaterEvaporate() {}
}
