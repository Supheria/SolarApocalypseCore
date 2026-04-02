package com.supheria.solar_apocalypse_core.procedures;

import com.supheria.solar_apocalypse_core.Procedure;
import com.supheria.solar_apocalypse_core.procedures.transform.TransformRule;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;

import static com.supheria.solar_apocalypse_core.procedures.transform.TransformActions.setBlock;
import static com.supheria.solar_apocalypse_core.procedures.transform.TransformRule.when;

public class DeleteUnconditionallyProcedure {

    private static final Procedure INSTANCE = TransformRule.rulesOf(
            when((world, x, y, z, stage) -> stage < 6, setBlock(Blocks.AIR))
    );

    public static void execute(LevelAccessor world, double x, double y, double z) {
        INSTANCE.call(world, x, y, z);
    }
}
