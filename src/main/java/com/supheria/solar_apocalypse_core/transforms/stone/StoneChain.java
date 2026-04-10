package com.supheria.solar_apocalypse_core.transforms.stone;

import com.supheria.solar_apocalypse_core.BlockTransform;
import com.supheria.solar_apocalypse_core.transforms.rule.TransformRule;
import com.supheria.solar_apocalypse_core.world.SolarStage;
import net.minecraft.world.level.block.Blocks;

import static com.supheria.solar_apocalypse_core.transforms.rule.TransformActions.*;
import static com.supheria.solar_apocalypse_core.transforms.rule.TransformConditions.*;
import static com.supheria.solar_apocalypse_core.transforms.rule.TransformRule.when;

/**
 * 石头系方块转换链，集中定义于此。
 *
 * <ul>
 *   <li>{@link #COBBLESTONE} — 卵石 → 砾石链</li>
 *   <li>{@link #CLAY}        — 粘土 → 陶土链</li>
 *   <li>{@link #GRAVEL}      — 砾石 → 岩浆链（含坍缩阶段）</li>
 * </ul>
 *
 * <p>石头→卵石的转换通过 {@link StoneTo#of(net.minecraft.world.level.block.Block)} 工厂方法获取。</p>
 */
public final class StoneChain {

    // 卵石 → 砾石
    public static final BlockTransform COBBLESTONE = TransformRule.rulesOf(
            when(stageRange(SolarStage.STAGE_3, SolarStage.STAGE_6).and(sky()),
                    setBlock(Blocks.GRAVEL)),
            when(stageRange(SolarStage.STAGE_4, SolarStage.STAGE_6).and(aboveSafeHeight()),
                    setBlock(Blocks.GRAVEL))
    );

    // 粘土 → 陶土
    public static final BlockTransform CLAY = TransformRule.rulesOf(
            when(stageRange(SolarStage.STAGE_3, SolarStage.STAGE_6).and(sky()),
                    setBlock(Blocks.TERRACOTTA)),
            when(stageRange(SolarStage.STAGE_4, SolarStage.STAGE_6).and(aboveSafeHeight()),
                    setBlock(Blocks.TERRACOTTA))
    );

    // 砾石 → 岩浆
    public static final BlockTransform GRAVEL = TransformRule.rulesOf(
            when(stageRange(SolarStage.STAGE_4, SolarStage.STAGE_6).and(sky().or(adjacentLava())).and(aboveMinOf(SolarStage.STAGE_4, SolarStage.STAGE_5)),
                    setBlock(Blocks.LAVA)),
            when(stageExact(SolarStage.STAGE_5).and(aboveSafeHeight()),
                    setBlock(Blocks.LAVA)),
            when(stageExact(SolarStage.STAGE_6),
                    setBlock(Blocks.LAVA))
    );

    private StoneChain() {}
}
