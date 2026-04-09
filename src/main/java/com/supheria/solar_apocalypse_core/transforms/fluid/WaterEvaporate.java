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
            // 阶段2：仅对白天露天且高于安全高度的部分水进行弱蒸发
            when(stageExact(SolarStage.STAGE_2).and(waterSource()).and(daytime()).and(aboveWaterEvaporateHeight()).and(randomDayRate()).and(stageRate()),
                    spreadWaterRateLimited(BlockSpreadUtils.OFFSETS_WATER_5X5_10, 0.6, EVAPORATED_VOID)),
            // 阶段3：超过蒸发高度，但仍保持17邻限额推进以控制卡顿
            when(stageExact(SolarStage.STAGE_3).and(waterSource()).and(aboveWaterEvaporateHeight()).and(stageRate()),
                    spreadWaterRateLimited(BlockSpreadUtils.OFFSETS_WATER_5X5_10, 1.0, EVAPORATED_VOID)),
            // 阶段4：超过蒸发高度，升级为更大范围的5×5×4层蒸发
            when(stageExact(SolarStage.STAGE_4).and(waterSource()).and(aboveWaterEvaporateHeight()).and(stageRate()),
                    spreadWaterRateLimited(BlockSpreadUtils.OFFSETS_WATER_5X5_10, 0.95, EVAPORATED_VOID)),
            // 阶段5：超过蒸发高度，维持最强蒸发，但拆成持续限额推进
            when(stageExact(SolarStage.STAGE_5).and(waterSource()).and(aboveWaterEvaporateHeight()).and(stageRate()),
                    spreadWaterRateLimited(BlockSpreadUtils.OFFSETS_WATER_5X5_10, 1.15, EVAPORATED_VOID))
    );

    private WaterEvaporate() {}
}
