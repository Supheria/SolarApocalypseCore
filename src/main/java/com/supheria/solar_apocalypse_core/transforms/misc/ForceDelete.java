package com.supheria.solar_apocalypse_core.transforms.misc;

import com.supheria.solar_apocalypse_core.BlockTransform;
import com.supheria.solar_apocalypse_core.transforms.rule.TransformRule;
import net.minecraft.world.level.block.Blocks;

import static com.supheria.solar_apocalypse_core.transforms.rule.TransformActions.setBlock;
import static com.supheria.solar_apocalypse_core.transforms.rule.TransformRule.when;

/**
 * 无条件强制删除方块（传送门等）。
 */
public class ForceDelete {

    public static final BlockTransform TRANSFORM = TransformRule.rulesOf(
            when((world, x, y, z, stage) -> stage < 6, setBlock(Blocks.AIR))
    );

    private ForceDelete() {}
}
