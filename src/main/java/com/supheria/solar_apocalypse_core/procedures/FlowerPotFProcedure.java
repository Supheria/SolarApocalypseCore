package com.supheria.solar_apocalypse_core.procedures;

import com.supheria.solar_apocalypse_core.Procedure;
import com.supheria.solar_apocalypse_core.procedures.transform.TransformRule;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;

import static com.supheria.solar_apocalypse_core.procedures.transform.TransformActions.setBlock;
import static com.supheria.solar_apocalypse_core.procedures.transform.TransformConditions.*;
import static com.supheria.solar_apocalypse_core.procedures.transform.TransformRule.when;

public class FlowerPotFProcedure {

    private static final Procedure INSTANCE = TransformRule.rulesOf(
            // 阶段1-5：天空可见，非花盆，概率→花盆
            when(stageRange(1, 6).and(sky()).and(isBlock(Blocks.FLOWER_POT).negate()).and(randomDayRate()),
                    setBlock(Blocks.FLOWER_POT)),
            // 阶段2：晴天+高于安全高度，非花盆，概率→花盆
            when(stageExact(2).and(daytime()).and(aboveSafeHeight(2))
                    .and(isBlock(Blocks.FLOWER_POT).negate()).and(randomDayRate()),
                    setBlock(Blocks.FLOWER_POT)),
            // 阶段3：高于安全高度，非花盆→花盆
            when(stageExact(3).and(aboveSafeHeight(2)).and(isBlock(Blocks.FLOWER_POT).negate()),
                    setBlock(Blocks.FLOWER_POT)),
            // 阶段4：高于安全高度，非花盆→花盆
            when(stageExact(4).and(aboveSafeHeight(4)).and(isBlock(Blocks.FLOWER_POT).negate()),
                    setBlock(Blocks.FLOWER_POT)),
            // 阶段5：y>=8，非花盆→花盆
            when(stageExact(5).and(minY(8)).and(isBlock(Blocks.FLOWER_POT).negate()),
                    setBlock(Blocks.FLOWER_POT))
    );

    public static void execute(LevelAccessor world, double x, double y, double z) {
        INSTANCE.call(world, x, y, z);
    }
}
