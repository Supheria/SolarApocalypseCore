package com.supheria.solar_apocalypse_core.procedures;

import com.supheria.solar_apocalypse_core.Procedure;
import com.supheria.solar_apocalypse_core.procedures.transform.TransformAction;
import com.supheria.solar_apocalypse_core.procedures.transform.TransformCondition;
import com.supheria.solar_apocalypse_core.procedures.transform.TransformRule;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import static com.supheria.solar_apocalypse_core.procedures.transform.TransformConditions.*;
import static com.supheria.solar_apocalypse_core.procedures.transform.TransformRule.when;

public class WaterTagDeleteProcedure {

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

    private static final Procedure INSTANCE = TransformRule.rulesOf(
            // 阶段2-5：天空+晴天，概率去水
            when(stageRange(2, 6).and(sky()).and(daytime()).and(IS_WATERLOGGED).and(randomDayRate()), DEWATERLOG),
            // 阶段3：y>=63，直接去水
            when(stageExact(3).and(minY(63)).and(IS_WATERLOGGED), DEWATERLOG),
            // 阶段4-5：y>=8，直接去水
            when(stageRange(4, 6).and(minY(8)).and(IS_WATERLOGGED), DEWATERLOG)
    );

    public static void execute(LevelAccessor world, double x, double y, double z) {
        INSTANCE.call(world, x, y, z);
    }
}
