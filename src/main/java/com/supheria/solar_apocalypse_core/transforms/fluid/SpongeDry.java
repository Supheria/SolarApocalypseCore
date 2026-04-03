package com.supheria.solar_apocalypse_core.transforms.fluid;

import com.supheria.solar_apocalypse_core.BlockTransform;
import com.supheria.solar_apocalypse_core.transforms.rule.TransformAction;
import com.supheria.solar_apocalypse_core.transforms.rule.TransformRule;
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
            // 阶段1-5：天空可见+晴天+无雨，湿海绵→干海绵
            when(stageRange(1, 6).and(sky()).and(noRain()).and(daytime())
                    .and(isBlock(Blocks.WET_SPONGE)).and(randomDayRate()),
                    setBlock(Blocks.SPONGE)),
            // 阶段2：晴天+高于安全高度，湿→干或干→空
            when(stageExact(2).and(daytime()).and(aboveSafeHeight(2)).and(randomDayRate()), SPONGE_DRY),
            // 阶段3：y>=63，湿→干或干→空
            when(stageExact(3).and(minY(63)).and(randomDayRate()), SPONGE_DRY),
            // 阶段4：y>=8，湿→干或干→空
            when(stageExact(4).and(minY(8)).and(randomDayRate()), SPONGE_DRY),
            // 阶段5：y>=8，直接删除
            when(stageExact(5).and(minY(8)), setBlock(Blocks.AIR))
    );

    private SpongeDry() {}
}
