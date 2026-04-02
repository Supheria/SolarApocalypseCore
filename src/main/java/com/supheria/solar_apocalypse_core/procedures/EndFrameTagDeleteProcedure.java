package com.supheria.solar_apocalypse_core.procedures;

import com.supheria.solar_apocalypse_core.Procedure;
import com.supheria.solar_apocalypse_core.procedures.transform.TransformRule;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import static com.supheria.solar_apocalypse_core.procedures.transform.TransformRule.when;

public class EndFrameTagDeleteProcedure {

    private static final Procedure INSTANCE = TransformRule.rulesOf(
            when((world, x, y, z, stage) -> stage < 6, (world, x, y, z) -> {
                BlockPos pos = BlockPos.containing(x, y, z);
                if (world.getBlockState(pos).getBlock().getStateDefinition().getProperty("eye")
                        instanceof BooleanProperty prop
                        && world.getBlockState(pos).getValue(prop)) {
                    world.setBlock(pos, world.getBlockState(pos).setValue(prop, false), 3);
                }
            })
    );

    public static void execute(LevelAccessor world, double x, double y, double z) {
        INSTANCE.call(world, x, y, z);
    }
}
