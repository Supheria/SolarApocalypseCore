package com.supheria.solar_apocalypse_core.transforms.fluid;

import com.supheria.solar_apocalypse_core.BlockTransform;
import com.supheria.solar_apocalypse_core.transforms.rule.TransformAction;
import com.supheria.solar_apocalypse_core.transforms.rule.TransformCondition;
import com.supheria.solar_apocalypse_core.transforms.rule.TransformRule;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import static com.supheria.solar_apocalypse_core.transforms.rule.TransformConditions.*;
import static com.supheria.solar_apocalypse_core.transforms.rule.TransformRule.when;

/**
 * 含水方块去水过程（waterlogged → false）。
 */
public class WaterloggedDry {

    // 方块当前处于含水状态
    private static final TransformCondition IS_WATERLOGGED = (world, x, y, z, stage) ->
            world.getBlockState(BlockPos.containing(x, y, z)).getBlock()
                    .getStateDefinition().getProperty("waterlogged") instanceof BooleanProperty bp
            && world.getBlockState(BlockPos.containing(x, y, z)).getValue(bp);

    // 移除含水属性
    private static final TransformAction DEWATERLOG = (world, x, y, z) -> {
        BlockPos pos = BlockPos.containing(x, y, z);
        if (world.getBlockState(pos).getBlock().getStateDefinition().getProperty("waterlogged")
                instanceof BooleanProperty prop) {
            world.setBlock(pos, world.getBlockState(pos).setValue(prop, false), 3);
        }
    };

    public static final BlockTransform TRANSFORM = TransformRule.rulesOf(
            // 阶段2-5：天空+晴天，概率去水
            when(stageRange(2, 6).and(sky()).and(daytime()).and(IS_WATERLOGGED).and(randomDayRate()), DEWATERLOG),
            // 阶段3：y>=63，直接去水
            when(stageExact(3).and(minY(63)).and(IS_WATERLOGGED), DEWATERLOG),
            // 阶段4-5：y>=8，直接去水
            when(stageRange(4, 6).and(minY(8)).and(IS_WATERLOGGED), DEWATERLOG)
    );

    private WaterloggedDry() {}
}
