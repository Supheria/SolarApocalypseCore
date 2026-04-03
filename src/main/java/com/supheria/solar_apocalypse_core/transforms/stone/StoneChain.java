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
            // 阶段3-5：天空可见，概率性 → 砾石
            when(stageRange(SolarStage.STAGE_3, SolarStage.STAGE_6).and(sky()).and(randomDayVariable()),
                    setBlock(Blocks.GRAVEL)),
            // 阶段5：y>=8 且上方为空气 → 砾石
            when(stageExact(SolarStage.STAGE_5).and(minY(8)).and(airAbove()),
                    setBlock(Blocks.GRAVEL))
    );

    // 粘土 → 陶土
    public static final BlockTransform CLAY = TransformRule.rulesOf(
            // 阶段3-5：天空可见，概率性 → 陶土
            when(stageRange(SolarStage.STAGE_3, SolarStage.STAGE_6).and(sky()).and(randomDayVariable()),
                    setBlock(Blocks.TERRACOTTA)),
            // 阶段4-5：y>=8，直接 → 陶土
            when(stageRange(SolarStage.STAGE_4, SolarStage.STAGE_6).and(minY(8)),
                    setBlock(Blocks.TERRACOTTA))
    );

    // 砾石 → 岩浆
    public static final BlockTransform GRAVEL = TransformRule.rulesOf(
            // 阶段4-5：天空可见或相邻岩浆，高于阶段4/5安全高度最小值，50%概率 → 岩浆
            when(stageRange(SolarStage.STAGE_4, SolarStage.STAGE_6).and(sky().or(adjacentLava())).and(aboveMinOf(SolarStage.STAGE_4, SolarStage.STAGE_5)).and(random50()),
                    setBlock(Blocks.LAVA)),
            // 阶段5：高于安全高度，直接 → 岩浆
            when(stageExact(SolarStage.STAGE_5).and(aboveSafeHeight()),
                    setBlock(Blocks.LAVA)),
            // 坍缺阶段：概率由配置决定 → 岩浆
            when(stageExact(SolarStage.STAGE_6).and(randomCollapse()),
                    setBlock(Blocks.LAVA))
    );

    private StoneChain() {}
}
