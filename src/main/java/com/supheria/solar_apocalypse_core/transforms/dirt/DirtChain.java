package com.supheria.solar_apocalypse_core.transforms.dirt;

import com.supheria.solar_apocalypse_core.BlockTransform;
import com.supheria.solar_apocalypse_core.init.SolarModBlocks;
import com.supheria.solar_apocalypse_core.init.SolarModTags;
import com.supheria.solar_apocalypse_core.transforms.rule.TransformCondition;
import com.supheria.solar_apocalypse_core.transforms.rule.TransformRule;
import com.supheria.solar_apocalypse_core.world.SolarStage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;

import static com.supheria.solar_apocalypse_core.transforms.util.BlockSpreadUtils.setBlockIfChanged;

import static com.supheria.solar_apocalypse_core.transforms.rule.TransformActions.*;
import static com.supheria.solar_apocalypse_core.transforms.rule.TransformConditions.*;
import static com.supheria.solar_apocalypse_core.transforms.rule.TransformConditions.aboveSafeHeight;
import static com.supheria.solar_apocalypse_core.transforms.rule.TransformRule.when;

/**
 * 泥土系地表在太阳灾变中的连续退化规则集合。
 *
 * <p>这组规则描述的是一条从“有机/湿润地表”逐步走向“干裂、粉化、消失”的演进链：
 * 草方块、泥土、粗泥土、碎泥土、沙子与尘土各自都有独立入口，但整体方向保持一致。
 * 随阶段加深，规则会从白天概率性退化，逐步过渡为按高度阈值触发的快速扩散与清除。
 */
public final class DirtChain {

    /**
     * 软退化的共享前置条件：白天、露天且未下雨。
     * 这类条件主要服务于前中期的缓慢干化，而不是后期的强制性地表清除。
     */
    private static final TransformCondition SOFT_BASE = daytime().and(sky()).and(noRain());

    // 草方块是最外层、最温和的地表起点：先失去湿润层，再逐步沙化与整体剥离。
    public static final BlockTransform GRASS_BLOCK = TransformRule.rulesOf(
            // 阶段1-5：白天+露天+不下雨，概率性 → 泥土
            when(stageIsEruptionPhase().and(SOFT_BASE).and(randomDaySoft()),
                    setBlock(Blocks.DIRT)),
            // 阶段2-5：天空可见，立即 → 粗泥土
            when(stageRange(SolarStage.STAGE_2, SolarStage.STAGE_6).and(sky()),
                    setBlock(Blocks.COARSE_DIRT)),
            // 阶段3：高于安全高度，→ 沙子，并向 MOIST_DIRT 4邻扩散
            when(stageExact(SolarStage.STAGE_3).and(aboveSafeHeight()),
                    spread4H(Blocks.SAND.defaultBlockState(), bs -> bs.is(SolarModTags.Blocks.MOIST_DIRT))),
            // 阶段4：高于安全高度，→ 尘土，并向 MOIST_DIRT 8邻限额扩散
            when(stageExact(SolarStage.STAGE_4).and(aboveSafeHeight()).and(stageRateScaled(0.9)),
                    spread8HRateLimited(SolarModBlocks.DUST.get().defaultBlockState(), bs -> bs.is(SolarModTags.Blocks.MOIST_DIRT), 0.9)),
            // 阶段5：高于 y=8，→ 空气，并向 DIRT 17邻限额扩散（含下层）
            when(stageExact(SolarStage.STAGE_5).and(aboveSafeHeight()).and(stageRateScaled(1.1)),
                    spread17SameLayerFirstRateLimited(Blocks.AIR.defaultBlockState(), bs -> bs.is(BlockTags.DIRT), 1.1))
    );

    // 泥土比草方块少一层缓冲，因此从第二阶段开始会更快暴露为沙化/粉化结果。
    public static final BlockTransform DIRT = TransformRule.rulesOf(
            // 阶段1-5：软转换 → 粗泥土
            when(stageIsEruptionPhase().and(SOFT_BASE).and(randomDaySoft()),
                    setBlock(Blocks.COARSE_DIRT)),
            // 阶段2：露天，→ 沙子
            when(stageExact(SolarStage.STAGE_2).and(sky()),
                    setBlock(Blocks.SAND)),
            // 阶段3：高于安全高度，→ 尘土，向 DIRT(mod) 4邻扩散
            when(stageExact(SolarStage.STAGE_3).and(aboveSafeHeight()),
                    spread4H(SolarModBlocks.DUST.get().defaultBlockState(), bs -> bs.is(SolarModTags.Blocks.DIRT))),
            // 阶段4：高于安全高度，→ 空气，向 DIRT 8邻限额扩散
            when(stageExact(SolarStage.STAGE_4).and(aboveSafeHeight()).and(stageRateScaled(0.9)),
                    spread8HRateLimited(Blocks.AIR.defaultBlockState(), bs -> bs.is(BlockTags.DIRT), 0.9)),
            // 阶段5：高于安全高度，→ 空气，向 DIRT 17邻限额扩散（含下层）
            when(stageExact(SolarStage.STAGE_5).and(aboveSafeHeight()).and(stageRateScaled(1.1)),
                    spread17SameLayerFirstRateLimited(Blocks.AIR.defaultBlockState(), bs -> bs.is(BlockTags.DIRT), 1.1))
    );

    // 粗泥土处于链路中段：既承接前期干裂，也在后期迅速坍成空气清除带。
    public static final BlockTransform COARSE_DIRT = TransformRule.rulesOf(
            // 阶段1-5：软转换 → 碎泥土
            when(stageIsEruptionPhase().and(SOFT_BASE).and(randomDaySoft()),
                    setBlock(SolarModBlocks.CRUSHED_DIRT.get())),
            // 阶段2：露天，→ 沙子
            when(stageExact(SolarStage.STAGE_2).and(sky()),
                    setBlock(Blocks.SAND)),
            // 阶段3：高于安全高度，→ 尘土，向 HARD_DIRT 4邻扩散
            when(stageExact(SolarStage.STAGE_3).and(aboveSafeHeight()),
                    spread4H(SolarModBlocks.DUST.get().defaultBlockState(), bs -> bs.is(SolarModTags.Blocks.HARD_DIRT))),
            // 阶段4：高于安全高度，→ 空气，向 DIRT 8邻限额扩散
            when(stageRange(SolarStage.STAGE_4, SolarStage.STAGE_6).and(aboveSafeHeight()).and(stageRateScaled(0.9)),
                    spread8HRateLimited(Blocks.AIR.defaultBlockState(), bs -> bs.is(BlockTags.DIRT), 0.9)),
            // 阶段5：高于安全高度，→ 空气，向 DIRT 17邻限额扩散（含下层）
            when(stageExact(SolarStage.STAGE_5).and(aboveSafeHeight()).and(stageRateScaled(1.1)),
                    spread17SameLayerFirstRateLimited(Blocks.AIR.defaultBlockState(), bs -> bs.is(BlockTags.DIRT), 1.1))
    );

    // 碎泥土已接近最终沙化形态，因此从第三阶段起几乎只剩快速扩散式清除。
    public static final BlockTransform CRUSHED_DIRT = TransformRule.rulesOf(
            // 阶段1-5：软转换 → 沙子
            when(stageIsEruptionPhase().and(SOFT_BASE).and(randomDaySoftSlow()),
                    setBlock(Blocks.SAND)),
            // 阶段2：天空，→ 尘土
            when(stageExact(SolarStage.STAGE_2).and(sky()),
                    setBlock(SolarModBlocks.DUST.get())),
            // 阶段3：高于安全高度，→ 空气，向碎泥土4邻扩散
            when(stageExact(SolarStage.STAGE_3).and(aboveSafeHeight()),
                    spread4H(Blocks.AIR.defaultBlockState(), bs -> bs.getBlock() == SolarModBlocks.CRUSHED_DIRT.get())),
            // 阶段4：高于安全高度，→ 空气，向碎泥土8邻限额扩散
            when(stageExact(SolarStage.STAGE_4).and(aboveSafeHeight()).and(stageRateScaled(0.95)),
                    spread8HRateLimited(Blocks.AIR.defaultBlockState(), bs -> bs.getBlock() == SolarModBlocks.CRUSHED_DIRT.get(), 0.95)),
            // 阶段5：高于安全高度，→ 空气，向碎泥土17邻限额扩散（含下层）
            when(stageExact(SolarStage.STAGE_5).and(aboveSafeHeight()).and(stageRateScaled(1.1)),
                    spread17SameLayerFirstRateLimited(Blocks.AIR.defaultBlockState(), bs -> bs.getBlock() == SolarModBlocks.CRUSHED_DIRT.get(), 1.1))
    );

    public static final BlockTransform SANDSTONE = TransformRule.rulesOf(
            when(stageRange(SolarStage.STAGE_2, SolarStage.STAGE_6).and(sky()).and(noRain()).and(randomDaySoft()),
                    DirtChain::turnSandstoneIntoSand),
            when(stageExact(SolarStage.STAGE_3).and(aboveSafeHeight()),
                    DirtChain::turnSandstoneIntoSand),
            when(stageRange(SolarStage.STAGE_4, SolarStage.STAGE_6).and(aboveSafeHeight()),
                    DirtChain::turnSandstoneIntoSand)
    );

    // 沙子阶段代表地表已完全失去结构，后续重点从“继续干化”转向“大面积塌空”。
    public static final BlockTransform SAND = TransformRule.rulesOf(
            // 阶段2-5：白天+露天+上方无水+不下雨，1/10概率 → 尘土
            when(stageRange(SolarStage.STAGE_2, SolarStage.STAGE_6).and(daytime()).and(sky()).and(noWaterAbove()).and(noRain()).and(randomOneIn10()),
                    setBlock(SolarModBlocks.DUST.get())),
            // 阶段3：白天+上方无水+高于安全高度，→ 空气，向沙子17邻限额扩散
            when(stageExact(SolarStage.STAGE_3).and(daytime()).and(noWaterAbove()).and(aboveSafeHeight()).and(stageRateScaled(0.85)),
                    spread17SameLayerFirstRateLimited(Blocks.AIR.defaultBlockState(), bs -> bs.is(BlockTags.SAND), 0.85)),
            // 阶段4：高于 y=32，→ 空气，向沙子5×5限额扩散（含下层）
            when(stageExact(SolarStage.STAGE_4).and(aboveSafeHeight()).and(stageRateScaled(0.95)),
                    spread5x5SameLayerFirstRateLimited(Blocks.AIR.defaultBlockState(), bs -> bs.is(BlockTags.SAND), 0.95)),
            // 阶段5：高于 y=8，→ 空气，向沙子5×5限额扩散（含下层）
            when(stageExact(SolarStage.STAGE_5).and(aboveSafeHeight()).and(stageRateScaled(1.1)),
                    spread5x5SameLayerFirstRateLimited(Blocks.AIR.defaultBlockState(), bs -> bs.is(BlockTags.SAND), 1.1))
    );

    // 尘土是链路末端：不再继续转化为别的固体，而是作为短暂残留物被扩散删除。
    public static final BlockTransform DUST = TransformRule.rulesOf(
            // 阶段2-4：高于安全高度，向8邻限额扩散 → 空气
            when(stageRange(SolarStage.STAGE_2, SolarStage.STAGE_5).and(aboveSafeHeight()).and(stageRateScaled(0.9)),
                    spread8HRateLimited(Blocks.AIR.defaultBlockState(), bs -> bs.getBlock() == SolarModBlocks.DUST.get(), 0.9)),
            // 阶段5：高于安全高度，向17邻限额扩散（含下层）→ 空气
            when(stageExact(SolarStage.STAGE_5).and(aboveSafeHeight()).and(stageRateScaled(1.1)),
                    spread17SameLayerFirstRateLimited(Blocks.AIR.defaultBlockState(), bs -> bs.getBlock() == SolarModBlocks.DUST.get(), 1.1))
    );

    private static void turnSandstoneIntoSand(net.minecraft.world.level.LevelAccessor world, double x, double y, double z) {
        BlockPos pos = BlockPos.containing(x, y, z);
        BlockState currentState = world.getBlockState(pos);
        String blockId = Objects.requireNonNull(BuiltInRegistries.BLOCK.getKey(currentState.getBlock())).getPath();
        boolean redVariant = blockId.contains("red_sandstone");
        setBlockIfChanged(world, pos, redVariant ? Blocks.RED_SAND.defaultBlockState() : Blocks.SAND.defaultBlockState());
    }

    private DirtChain() {}
}
