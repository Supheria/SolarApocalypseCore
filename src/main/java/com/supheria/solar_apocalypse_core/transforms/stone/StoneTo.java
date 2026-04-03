package com.supheria.solar_apocalypse_core.transforms.stone;

import com.supheria.solar_apocalypse_core.BlockTransform;
import com.supheria.solar_apocalypse_core.transforms.rule.TransformRule;
import net.minecraft.world.level.block.Block;

import static com.supheria.solar_apocalypse_core.transforms.rule.TransformActions.*;
import static com.supheria.solar_apocalypse_core.transforms.rule.TransformConditions.*;
import static com.supheria.solar_apocalypse_core.transforms.rule.TransformRule.when;

/**
 * 通用石头系方块转换工厂（石头 → 卵石系列）。
 *
 * <p>使用方式：{@code StoneTo.of(Blocks.COBBLESTONE)}</p>
 */
public class StoneTo {

    public static BlockTransform of(Block target) {
        return TransformRule.rulesOf(
                when(stageRange(2, 6).and(sky().or(adjacentLava())).and(aboveMinSafeHeight()).and(randomDayVariable(16000)),
                        setBlock(target)),
                when(stageExact(3).and(aboveSafeHeight(2)).and(randomDayVariable(16000)),
                        setBlock(target)),
                when(stageRange(4, 6).and(aboveSafeHeight(4)),
                        setBlock(target))
        );
    }

    private StoneTo() {}
}
