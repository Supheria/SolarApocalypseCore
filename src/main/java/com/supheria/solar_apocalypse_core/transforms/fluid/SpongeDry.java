package com.supheria.solar_apocalypse_core.transforms.fluid;

import com.supheria.solar_apocalypse_core.BlockTransform;
import com.supheria.solar_apocalypse_core.transforms.rule.TransformAction;
import com.supheria.solar_apocalypse_core.transforms.rule.TransformRule;
import com.supheria.solar_apocalypse_core.world.SolarStage;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;

import static com.supheria.solar_apocalypse_core.transforms.rule.TransformActions.setBlock;
import static com.supheria.solar_apocalypse_core.transforms.rule.TransformConditions.*;
import static com.supheria.solar_apocalypse_core.transforms.rule.TransformRule.when;

/**
 * 海绵干燥过程（湿海绵→干海绵→空气）。
 */
public class SpongeDry {

    // 湿海绵→干海绵 / 干海绵→空气（读取调用时的当前方块状态）
    private static final TransformAction SPONGE_DRY = (world, x, y, z) -> {
        BlockPos pos = BlockPos.containing(x, y, z);
        var block = world.getBlockState(pos).getBlock();
        if (block == Blocks.WET_SPONGE) {
            world.setBlock(pos, Blocks.SPONGE.defaultBlockState(), 3);
        } else if (block == Blocks.SPONGE) {
            world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        }
    };

    public static final BlockTransform TRANSFORM = TransformRule.rulesOf(
            when(stageIsEruptionPhase().and(sky()).and(noRain()).and(daytime())
                    .and(isBlock(Blocks.WET_SPONGE)),
                    setBlock(Blocks.SPONGE)),
            when(stageRange(SolarStage.STAGE_2, SolarStage.STAGE_5).and(aboveSafeHeight()), SPONGE_DRY),
            when(stageExact(SolarStage.STAGE_5).and(aboveSafeHeight()), setBlock(Blocks.AIR))
    );

    private SpongeDry() {}
}
