package com.supheria.solar_apocalypse_core.transforms.stone;

import com.supheria.solar_apocalypse_core.BlockTransform;
import com.supheria.solar_apocalypse_core.transforms.rule.TransformRule;
import com.supheria.solar_apocalypse_core.world.SolarStage;
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
                when(stageRange(SolarStage.STAGE_2, SolarStage.STAGE_6).and(sky().or(adjacentLava())).and(aboveSafeHeight()).and(randomDayVariable()),
                        setBlock(target)),
                when(stageExact(SolarStage.STAGE_3).and(aboveSafeHeight()).and(randomDayVariable()),
                        setBlock(target)),
                when(stageRange(SolarStage.STAGE_4, SolarStage.STAGE_6).and(aboveSafeHeight()),
                        setBlock(target))
        );
    }

    private StoneTo() {}
}
