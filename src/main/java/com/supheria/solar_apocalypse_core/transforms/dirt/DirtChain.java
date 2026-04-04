package com.supheria.solar_apocalypse_core.transforms.dirt;

import com.supheria.solar_apocalypse_core.BlockTransform;
import com.supheria.solar_apocalypse_core.init.SapModBlocks;
import com.supheria.solar_apocalypse_core.init.SapModTags;
import com.supheria.solar_apocalypse_core.transforms.rule.TransformCondition;
import com.supheria.solar_apocalypse_core.transforms.rule.TransformRule;
import com.supheria.solar_apocalypse_core.world.SolarStage;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;

import static com.supheria.solar_apocalypse_core.transforms.rule.TransformActions.*;
import static com.supheria.solar_apocalypse_core.transforms.rule.TransformConditions.*;
import static com.supheria.solar_apocalypse_core.transforms.rule.TransformConditions.aboveSafeHeight;
import static com.supheria.solar_apocalypse_core.transforms.rule.TransformRule.when;

/**
 * 泥土/沙子链的所有方块转换规则，集中定义于此。
 *
 * <ul>
 *   <li>{@link #GRASS_BLOCK} — 草方块 → 泥土链</li>
 *   <li>{@link #DIRT}        — 泥土 → 粗泥土链</li>
 *   <li>{@link #COARSE_DIRT} — 粗泥土 → 碎泥土链</li>
 *   <li>{@link #CRUSHED_DIRT}— 碎泥土 → 沙子链</li>
 *   <li>{@link #SAND}        — 沙子 → 尘土链</li>
 *   <li>{@link #DUST}        — 尘土消散</li>
 * </ul>
 */
public final class DirtChain {

    private static final TransformCondition SOFT_BASE = daytime().and(sky()).and(noRain());

    // 草方块 → 泥土链
    public static final BlockTransform GRASS_BLOCK = TransformRule.rulesOf(
            // 阶段1-5：白天+露天+不下雨，概率性 → 泥土
            when(stageIsEruptionPhase().and(SOFT_BASE).and(randomDaySoft()),
                    setBlock(Blocks.DIRT)),
            // 阶段2-5：天空可见，立即 → 粗泥土
            when(stageRange(SolarStage.STAGE_2, SolarStage.STAGE_6).and(sky()),
                    setBlock(Blocks.COARSE_DIRT)),
            // 阶段3：高于安全高度，→ 沙子，并向 MOIST_DIRT 4邻扩散
            when(stageExact(SolarStage.STAGE_3).and(aboveSafeHeight()),
                    spread4H(Blocks.SAND.defaultBlockState(), bs -> bs.is(SapModTags.Blocks.MOIST_DIRT))),
            // 阶段4：高于安全高度，→ 尘土，并向 MOIST_DIRT 8邻扩散
            when(stageExact(SolarStage.STAGE_4).and(aboveSafeHeight()),
                    spread8H(SapModBlocks.DUST.get().defaultBlockState(), bs -> bs.is(SapModTags.Blocks.MOIST_DIRT))),
            // 阶段5：高于 y=8，→ 空气，并向 DIRT 17邻扩散（含下层）
            when(stageExact(SolarStage.STAGE_5).and(aboveSafeHeight()),
                    spread17(Blocks.AIR.defaultBlockState(), bs -> bs.is(BlockTags.DIRT)))
    );

    // 泥土 → 粗泥土链
    public static final BlockTransform DIRT = TransformRule.rulesOf(
            // 阶段1-5：软转换 → 粗泥土
            when(stageIsEruptionPhase().and(SOFT_BASE).and(randomDaySoft()),
                    setBlock(Blocks.COARSE_DIRT)),
            // 阶段2：露天，→ 沙子
            when(stageExact(SolarStage.STAGE_2).and(sky()),
                    setBlock(Blocks.SAND)),
            // 阶段3：高于安全高度，→ 尘土，向 DIRT(mod) 4邻扩散
            when(stageExact(SolarStage.STAGE_3).and(aboveSafeHeight()),
                    spread4H(SapModBlocks.DUST.get().defaultBlockState(), bs -> bs.is(SapModTags.Blocks.DIRT))),
            // 阶段4：高于安全高度，→ 空气，向 DIRT 8邻扩散
            when(stageExact(SolarStage.STAGE_4).and(aboveSafeHeight()),
                    spread8H(Blocks.AIR.defaultBlockState(), bs -> bs.is(BlockTags.DIRT))),
            // 阶段5：高于安全高度，→ 空气，向 DIRT 17邻扩散（含下层）
            when(stageExact(SolarStage.STAGE_5).and(aboveSafeHeight()),
                    spread17(Blocks.AIR.defaultBlockState(), bs -> bs.is(BlockTags.DIRT)))
    );

    // 粗泥土 → 碎泥土链
    public static final BlockTransform COARSE_DIRT = TransformRule.rulesOf(
            // 阶段1-5：软转换 → 碎泥土
            when(stageIsEruptionPhase().and(SOFT_BASE).and(randomDaySoft()),
                    setBlock(SapModBlocks.CRUSHED_DIRT.get())),
            // 阶段2：露天，→ 沙子
            when(stageExact(SolarStage.STAGE_2).and(sky()),
                    setBlock(Blocks.SAND)),
            // 阶段3：高于安全高度，→ 尘土，向 HARD_DIRT 4邻扩散
            when(stageExact(SolarStage.STAGE_3).and(aboveSafeHeight()),
                    spread4H(SapModBlocks.DUST.get().defaultBlockState(), bs -> bs.is(SapModTags.Blocks.HARD_DIRT))),
            // 阶段4：高于安全高度，→ 空气，向 DIRT 8邻扩散
            when(stageRange(SolarStage.STAGE_4, SolarStage.STAGE_6).and(aboveSafeHeight()),
                    spread8H(Blocks.AIR.defaultBlockState(), bs -> bs.is(BlockTags.DIRT))),
            // 阶段5：高于安全高度，→ 空气，向 DIRT 17邻扩散（含下层）
            when(stageExact(SolarStage.STAGE_5).and(aboveSafeHeight()),
                    spread17(Blocks.AIR.defaultBlockState(), bs -> bs.is(BlockTags.DIRT)))
    );

    // 碎泥土 → 沙子链
    public static final BlockTransform CRUSHED_DIRT = TransformRule.rulesOf(
            // 阶段1-5：软转换 → 沙子
            when(stageIsEruptionPhase().and(SOFT_BASE).and(randomDaySoftSlow()),
                    setBlock(Blocks.SAND)),
            // 阶段2：天空，→ 尘土
            when(stageExact(SolarStage.STAGE_2).and(sky()),
                    setBlock(SapModBlocks.DUST.get())),
            // 阶段3：高于安全高度，→ 空气，向碎泥土4邻扩散
            when(stageExact(SolarStage.STAGE_3).and(aboveSafeHeight()),
                    spread4H(Blocks.AIR.defaultBlockState(), bs -> bs.getBlock() == SapModBlocks.CRUSHED_DIRT.get())),
            // 阶段4：高于安全高度，→ 空气，向碎泥土8邻扩散
            when(stageExact(SolarStage.STAGE_4).and(aboveSafeHeight()),
                    spread8H(Blocks.AIR.defaultBlockState(), bs -> bs.getBlock() == SapModBlocks.CRUSHED_DIRT.get())),
            // 阶段5：高于安全高度，→ 空气，向碎泥土17邻扩散（含下层）
            when(stageExact(SolarStage.STAGE_5).and(aboveSafeHeight()),
                    spread17(Blocks.AIR.defaultBlockState(), bs -> bs.getBlock() == SapModBlocks.CRUSHED_DIRT.get()))
    );

    // 沙子 → 尘土链
    public static final BlockTransform SAND = TransformRule.rulesOf(
            // 阶段2-5：白天+露天+上方无水+不下雨，1/10概率 → 尘土
            when(stageRange(SolarStage.STAGE_2, SolarStage.STAGE_6).and(daytime()).and(sky()).and(noWaterAbove()).and(noRain()).and(randomOneIn10()),
                    setBlock(SapModBlocks.DUST.get())),
            // 阶段3：白天+上方无水+高于安全高度，→ 空气，向沙子17邻扩散
            when(stageExact(SolarStage.STAGE_3).and(daytime()).and(noWaterAbove()).and(aboveSafeHeight()),
                    spread17(Blocks.AIR.defaultBlockState(), bs -> bs.is(BlockTags.SAND))),
            // 阶段4：高于 y=32，→ 空气，向沙子5×5扩散（含下层）
            when(stageExact(SolarStage.STAGE_4).and(aboveSafeHeight()),
                    spread5x5(Blocks.AIR.defaultBlockState(), bs -> bs.is(BlockTags.SAND))),
            // 阶段5：高于 y=8，→ 空气，向沙子5×5扩散（含下层）
            when(stageExact(SolarStage.STAGE_5).and(aboveSafeHeight()),
                    spread5x5(Blocks.AIR.defaultBlockState(), bs -> bs.is(BlockTags.SAND)))
    );

    // 尘土消散
    public static final BlockTransform DUST = TransformRule.rulesOf(
            // 阶段2-4：高于安全高度，向8邻扩散 → 空气
            when(stageRange(SolarStage.STAGE_2, SolarStage.STAGE_5).and(aboveSafeHeight()),
                    spread8H(Blocks.AIR.defaultBlockState(), bs -> bs.getBlock() == SapModBlocks.DUST.get())),
            // 阶段5：高于安全高度，向17邻扩散（含下层）→ 空气
            when(stageExact(SolarStage.STAGE_5).and(aboveSafeHeight()),
                    spread17(Blocks.AIR.defaultBlockState(), bs -> bs.getBlock() == SapModBlocks.DUST.get()))
    );

    private DirtChain() {}
}
