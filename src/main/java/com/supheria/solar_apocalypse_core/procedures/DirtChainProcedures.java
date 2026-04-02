package com.supheria.solar_apocalypse_core.procedures;

import com.supheria.solar_apocalypse_core.Procedure;
import com.supheria.solar_apocalypse_core.config.solar.StageHeightConfig;
import com.supheria.solar_apocalypse_core.init.SapModBlocks;
import com.supheria.solar_apocalypse_core.init.SapModTags;
import com.supheria.solar_apocalypse_core.procedures.transform.TransformCondition;
import com.supheria.solar_apocalypse_core.procedures.transform.TransformRule;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;

import static com.supheria.solar_apocalypse_core.procedures.transform.TransformActions.*;
import static com.supheria.solar_apocalypse_core.procedures.transform.TransformConditions.*;
import static com.supheria.solar_apocalypse_core.procedures.transform.TransformRule.when;

/**
 * 泥土/沙子链的所有方块转换规则，集中定义于此。
 *
 * <p>原本分散在 5 个独立 TC 类中的逻辑，现统一为可读的规则声明：</p>
 * <ul>
 *   <li>{@link #GRASS_BLOCK} — 草方块 → 泥土链</li>
 *   <li>{@link #DIRT}        — 泥土 → 粗泥土链</li>
 *   <li>{@link #COARSE_DIRT} — 粗泥土 → 碎泥土链</li>
 *   <li>{@link #CRUSHED_DIRT}— 碎泥土 → 沙子链</li>
 *   <li>{@link #SAND}        — 沙子 → 尘土链</li>
 * </ul>
 *
 * <h3>规则执行顺序</h3>
 * 各规则按声明顺序执行（非 else-if），后续规则可覆盖先前规则的结果，
 * 与原始代码行为一致。
 */
public final class DirtChainProcedures {

    // 共享的软转换条件基础（白天 + 天空可见 + 不下雨）
    private static final TransformCondition SOFT_BASE = daytime().and(sky()).and(noRain());

    // -----------------------------------------------------------------------
    // 草方块 → 泥土链
    // 原: GrassBlockTCDirtProcedure.java (143行)
    // -----------------------------------------------------------------------
    public static final Procedure GRASS_BLOCK = TransformRule.rulesOf(
            // 阶段1-5：白天+天空+不下雨+dayTime≥1000，概率性 → 泥土
            when(stageRange(1, 6).and(SOFT_BASE).and(dayTimeMin(1000)).and(randomDaySoft()),
                    setBlock(Blocks.DIRT)),
            // 阶段2-5：天空可见，立即 → 粗泥土
            when(stageRange(2, 6).and(sky()),
                    setBlock(Blocks.COARSE_DIRT)),
            // 阶段3：高于安全高度，→ 沙子，并向 MOIST_DIRT 4邻扩散
            when(stageExact(3).and(aboveSafeHeight(2)),
                    spread4H(Blocks.SAND.defaultBlockState(), bs -> bs.is(SapModTags.Blocks.MOIST_DIRT))),
            // 阶段4：高于安全高度，→ 尘土，并向 MOIST_DIRT 8邻扩散
            when(stageExact(4).and(aboveSafeHeight(4)),
                    spread8H(SapModBlocks.DUST.get().defaultBlockState(), bs -> bs.is(SapModTags.Blocks.MOIST_DIRT))),
            // 阶段5：高于 y=8，→ 空气，并向 DIRT 17邻扩散（含下层）
            when(stageExact(5).and(minY(8)),
                    spread17(Blocks.AIR.defaultBlockState(), bs -> bs.is(BlockTags.DIRT)))
    );

    // -----------------------------------------------------------------------
    // 泥土 → 粗泥土链
    // 原: DirtTCCoarseDirtProcedure.java (135行)
    // -----------------------------------------------------------------------
    public static final Procedure DIRT = TransformRule.rulesOf(
            // 阶段1-5：软转换 → 粗泥土
            when(stageRange(1, 6).and(SOFT_BASE).and(dayTimeMin(24000)).and(randomDaySoft()),
                    setBlock(Blocks.COARSE_DIRT)),
            // 阶段2：天空+dayTime≥168000，→ 沙子
            when(stageExact(2).and(sky()).and(dayTimeMin(168000)),
                    setBlock(Blocks.SAND)),
            // 阶段3：高于安全高度，→ 尘土，向 DIRT(mod) 4邻扩散
            when(stageExact(3).and(aboveSafeHeight(2)),
                    spread4H(SapModBlocks.DUST.get().defaultBlockState(), bs -> bs.is(SapModTags.Blocks.DIRT))),
            // 阶段4-5：高于安全高度，→ 空气，向 DIRT 8邻扩散
            when(stageRange(4, 6).and(aboveSafeHeight(4)),
                    spread8H(Blocks.AIR.defaultBlockState(), bs -> bs.is(BlockTags.DIRT))),
            // 阶段5：高于阶段5安全高度，→ 空气，向 DIRT 17邻扩散（含下层）
            when(stageExact(5).and(aboveSafeHeight(5)),
                    spread17(Blocks.AIR.defaultBlockState(), bs -> bs.is(BlockTags.DIRT)))
    );

    // -----------------------------------------------------------------------
    // 粗泥土 → 碎泥土链
    // 原: CoarseDirtTCCrushedDirtBlockProcedure.java (135行)
    // -----------------------------------------------------------------------
    public static final Procedure COARSE_DIRT = TransformRule.rulesOf(
            // 阶段1-5：软转换 → 碎泥土
            when(stageRange(1, 6).and(SOFT_BASE).and(dayTimeMin(24000)).and(randomDaySoft()),
                    setBlock(SapModBlocks.CRUSHED_DIRT.get())),
            // 阶段2：天空+dayTime≥168000，→ 沙子
            when(stageExact(2).and(sky()).and(dayTimeMin(168000)),
                    setBlock(Blocks.SAND)),
            // 阶段3：高于安全高度，→ 尘土，向 HARD_DIRT 4邻扩散
            when(stageExact(3).and(aboveSafeHeight(2)),
                    spread4H(SapModBlocks.DUST.get().defaultBlockState(), bs -> bs.is(SapModTags.Blocks.HARD_DIRT))),
            // 阶段4-5：高于安全高度，→ 空气，向 DIRT 8邻扩散
            when(stageRange(4, 6).and(aboveSafeHeight(4)),
                    spread8H(Blocks.AIR.defaultBlockState(), bs -> bs.is(BlockTags.DIRT))),
            // 阶段5：高于阶段5安全高度，→ 空气，向 DIRT 17邻扩散（含下层）
            when(stageExact(5).and(aboveSafeHeight(5)),
                    spread17(Blocks.AIR.defaultBlockState(), bs -> bs.is(BlockTags.DIRT)))
    );

    // -----------------------------------------------------------------------
    // 碎泥土 → 沙子链
    // 原: CrushedDirtTCSandProcedure.java (136行)
    // -----------------------------------------------------------------------
    public static final Procedure CRUSHED_DIRT = TransformRule.rulesOf(
            // 阶段1-5：软转换 → 沙子（概率更低，dayTime≥48000）
            when(stageRange(1, 6).and(SOFT_BASE).and(dayTimeMin(48000)).and(randomDaySoftSlow()),
                    setBlock(Blocks.SAND)),
            // 阶段2：天空+dayTime≥192000，→ 尘土
            when(stageExact(2).and(sky()).and(dayTimeMin(192000)),
                    setBlock(SapModBlocks.DUST.get())),
            // 阶段3：dayTime≥360000+高于安全高度，→ 空气，向碎泥土4邻扩散
            when(stageExact(3).and(dayTimeMin(360000)).and(aboveSafeHeight(2)),
                    spread4H(Blocks.AIR.defaultBlockState(), bs -> bs.getBlock() == SapModBlocks.CRUSHED_DIRT.get())),
            // 阶段4-5：dayTime≥504000+高于安全高度，→ 空气，向碎泥土8邻扩散
            when(stageRange(4, 6).and(dayTimeMin(504000)).and(aboveSafeHeight(4)),
                    spread8H(Blocks.AIR.defaultBlockState(), bs -> bs.getBlock() == SapModBlocks.CRUSHED_DIRT.get())),
            // 阶段5：高于阶段5安全高度，→ 空气，向碎泥土17邻扩散（含下层）
            when(stageExact(5).and(aboveSafeHeight(5)),
                    spread17(Blocks.AIR.defaultBlockState(), bs -> bs.getBlock() == SapModBlocks.CRUSHED_DIRT.get()))
    );

    // -----------------------------------------------------------------------
    // 沙子 → 尘土链
    // 原: SandTCDustProcedure.java (1004行)
    // -----------------------------------------------------------------------
    public static final Procedure SAND = TransformRule.rulesOf(
            // 阶段2-5：白天+天空+上方无水+不下雨，1/10概率 → 尘土
            when(stageRange(2, 6).and(daytime()).and(sky()).and(noWaterAbove()).and(noRain()).and(randomOneIn10()),
                    setBlock(SapModBlocks.DUST.get())),
            // 阶段3：白天+上方无水+高于安全高度，→ 空气，向沙子17邻扩散
            when(stageExact(3).and(daytime()).and(noWaterAbove()).and(aboveSafeHeight(2)),
                    spread17(Blocks.AIR.defaultBlockState(), bs -> bs.is(BlockTags.SAND))),
            // 阶段4：高于 y=32，→ 空气，向沙子5×5扩散（含下层）
            when(stageExact(4).and(minY(32)),
                    spread5x5(Blocks.AIR.defaultBlockState(), bs -> bs.is(BlockTags.SAND))),
            // 阶段5：高于 y=8，→ 空气，向沙子5×5扩散（含下层）
            when(stageExact(5).and(minY(8)),
                    spread5x5(Blocks.AIR.defaultBlockState(), bs -> bs.is(BlockTags.SAND)))
    );

    private DirtChainProcedures() {}
}
