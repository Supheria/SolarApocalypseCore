package com.supheria.solar_apocalypse_core.transforms.misc;

import com.supheria.solar_apocalypse_core.BlockTransform;
import com.supheria.solar_apocalypse_core.transforms.rule.TransformRule;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import static com.supheria.solar_apocalypse_core.transforms.rule.TransformRule.when;

/**
 * 末地传送门框架清除眼睛（eye=true → false）。
 */
public class EndFrameClear {

    public static final BlockTransform TRANSFORM = TransformRule.rulesOf(
            when((world, x, y, z, stage) -> stage < 6, (world, x, y, z) -> {
                BlockPos pos = BlockPos.containing(x, y, z);
                if (world.getBlockState(pos).getBlock().getStateDefinition().getProperty("eye")
                        instanceof BooleanProperty prop
                        && world.getBlockState(pos).getValue(prop)) {
                    world.setBlock(pos, world.getBlockState(pos).setValue(prop, false), 3);
                }
            })
    );

    private EndFrameClear() {}
}
