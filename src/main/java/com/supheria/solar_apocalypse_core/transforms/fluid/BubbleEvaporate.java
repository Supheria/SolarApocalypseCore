package com.supheria.solar_apocalypse_core.transforms.fluid;

import com.supheria.solar_apocalypse_core.BlockTransform;
import com.supheria.solar_apocalypse_core.transforms.rule.TransformCondition;
import com.supheria.solar_apocalypse_core.transforms.rule.TransformRule;
import com.supheria.solar_apocalypse_core.world.SolarStage;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;

import static com.supheria.solar_apocalypse_core.transforms.rule.TransformActions.*;
import static com.supheria.solar_apocalypse_core.transforms.rule.TransformConditions.*;
import static com.supheria.solar_apocalypse_core.transforms.rule.TransformRule.when;

/**
 * 气泡柱消散过程。
 */
public class BubbleEvaporate {

    private static final TransformCondition DETACHED_FROM_WATER_SOURCE = (world, x, y, z, stage) -> {
        BlockPos pos = BlockPos.containing(x, y, z);
        BlockPos below = pos.below();
        return !world.getFluidState(below).isSource();
    };

    public static final BlockTransform TRANSFORM = TransformRule.rulesOf(
            // 阶段2：白天 + 天空可见 + 概率触发，限额清除少量气泡柱
            when(stageExact(SolarStage.STAGE_2).and(DETACHED_FROM_WATER_SOURCE).and(daytime()).and(sky()).and(randomDayRate()),
                    spread17RateLimited(Blocks.AIR.defaultBlockState(), bs -> bs.getBlock() == Blocks.BUBBLE_COLUMN, stage -> scaleBudget(stage, 0.8))),
            // 阶段3：白天 + 高于安全高度，较快清除气泡柱，但仍走限额扩散避免卡顿尖峰
            when(stageExact(SolarStage.STAGE_3).and(DETACHED_FROM_WATER_SOURCE).and(daytime()).and(aboveSafeHeight()).and(stageRateScaled(0.85)),
                    spread17RateLimited(Blocks.AIR.defaultBlockState(), bs -> bs.getBlock() == Blocks.BUBBLE_COLUMN, stage -> scaleBudget(stage, 1.0))),
            // 阶段4：白天 + 高于安全高度，继续增强，但保持限额扩散
            when(stageExact(SolarStage.STAGE_4).and(DETACHED_FROM_WATER_SOURCE).and(daytime()).and(aboveSafeHeight()),
                    spread17RateLimited(Blocks.AIR.defaultBlockState(), bs -> bs.getBlock() == Blocks.BUBBLE_COLUMN, stage -> scaleBudget(stage, 1.25))),
            // 阶段5：高于安全高度，维持更强的限额清除
            when(stageExact(SolarStage.STAGE_5).and(DETACHED_FROM_WATER_SOURCE).and(aboveSafeHeight()),
                    spread17RateLimited(Blocks.AIR.defaultBlockState(), bs -> bs.getBlock() == Blocks.BUBBLE_COLUMN, stage -> scaleBudget(stage, 1.5)))
    );

    private BubbleEvaporate() {}
}
