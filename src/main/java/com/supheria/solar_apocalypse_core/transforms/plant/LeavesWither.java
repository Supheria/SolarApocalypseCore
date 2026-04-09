package com.supheria.solar_apocalypse_core.transforms.plant;

import com.supheria.solar_apocalypse_core.BlockTransform;
import com.supheria.solar_apocalypse_core.init.SolarModBlocks;
import com.supheria.solar_apocalypse_core.transforms.rule.TransformAction;
import com.supheria.solar_apocalypse_core.transforms.rule.TransformCondition;
import com.supheria.solar_apocalypse_core.transforms.rule.TransformRule;
import com.supheria.solar_apocalypse_core.world.SolarStage;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.state.BlockState;

import static com.supheria.solar_apocalypse_core.transforms.rule.TransformActions.*;
import static com.supheria.solar_apocalypse_core.transforms.rule.TransformConditions.*;
import static com.supheria.solar_apocalypse_core.transforms.rule.TransformRule.when;

/**
 * 树叶枯萎与燃烧过程。
 */
public class LeavesWither {

    /** 叶片谓词：匹配任意树叶方块。 */
    private static final java.util.function.Predicate<BlockState> IS_LEAVES =
            bs -> bs.is(BlockTags.LEAVES);

    /** 中心及4邻树叶直接掉落并销毁。 */
    private static final TransformAction FIRE_4H = destroyCenterAnd4HWithDrops(IS_LEAVES);

    /** 天空可见 或 正上方是枯萎树叶。 */
    private static final TransformCondition SKY_OR_WITHER_ABOVE =
            sky().or((world, x, y, z, stage) ->
                    world.getBlockState(BlockPos.containing(x, y + 1, z)).getBlock()
                            == SolarModBlocks.WITHERED_LEAVES.get());

    /** 当前方块是枯萎树叶。 */
    private static final TransformCondition IS_WITHERED =
            isBlock(SolarModBlocks.WITHERED_LEAVES.get());

    /** 当前方块不是枯萎树叶。 */
    private static final TransformCondition NOT_WITHERED = IS_WITHERED.negate();

    /** 阶段2-5 softBase：白天 + 不下雨。 */
    private static final TransformCondition SOFT_BASE_2 = daytime().and(noRain());

    public static final BlockTransform TRANSFORM = TransformRule.rulesOf(
            // 阶段2-5（非枯萎叶）：白天 + 天空/枯萎叶在上 + 不下雨 + 快速概率 → 枯萎树叶
            when(stageRange(SolarStage.STAGE_2, SolarStage.STAGE_6).and(NOT_WITHERED).and(SOFT_BASE_2).and(SKY_OR_WITHER_ABOVE)
                            .and(randomDayWoodFast()),
                    setBlock(SolarModBlocks.WITHERED_LEAVES.get())),

            // 阶段1-5（非枯萎叶）：白天 + 天空/枯萎叶在上 + 不下雨 + 慢速概率 → 枯萎树叶
            when(stageExact(SolarStage.STAGE_1).and(NOT_WITHERED).and(SOFT_BASE_2).and(SKY_OR_WITHER_ABOVE)
                            .and(daytime()).and(randomDayWoodSlow()),
                    setBlock(SolarModBlocks.WITHERED_LEAVES.get())),

            // 阶段2-5（非枯萎叶）：白天 + 不下雨 + 高于安全高度 + 快速概率 → 枯萎树叶
            when(stageRange(SolarStage.STAGE_2, SolarStage.STAGE_6).and(NOT_WITHERED).and(SOFT_BASE_2)
                            .and(aboveSafeHeight()).and(randomDayWoodFast()),
                    setBlock(SolarModBlocks.WITHERED_LEAVES.get())),

            // 阶段2-5（枯萎叶）：白天 + 天空/枯萎叶在上 + 不下雨 + 快速概率 → 掉落并销毁
            when(stageRange(SolarStage.STAGE_2, SolarStage.STAGE_6).and(IS_WITHERED).and(SOFT_BASE_2).and(SKY_OR_WITHER_ABOVE)
                            .and(randomDayWoodFast()),
                    destroyBlockWithDrops()),

            // 阶段3-5（枯萎叶）：白天 + 天空/枯萎叶在上 + 不下雨 + 概率 → 掉落并销毁
            when(stageRange(SolarStage.STAGE_3, SolarStage.STAGE_6).and(IS_WITHERED).and(SOFT_BASE_2).and(SKY_OR_WITHER_ABOVE)
                            .and(randomDayWood()),
                    destroyBlockWithDrops()),

            // 阶段2（枯萎叶）：白天 + 不下雨 + 高于安全高度 + 快速概率 → 掉落并销毁
            when(stageExact(SolarStage.STAGE_2).and(IS_WITHERED).and(SOFT_BASE_2).and(aboveSafeHeight())
                            .and(randomDayWoodFast()),
                    destroyBlockWithDrops()),

            // 阶段2-5（枯萎叶）：扩散火焰到4邻树叶
            when(stageRange(SolarStage.STAGE_2, SolarStage.STAGE_6).and(IS_WITHERED),
                    FIRE_4H),

            // 阶段2-5（非枯萎叶）：高于安全高度 → 枯萎叶
            when(stageRange(SolarStage.STAGE_2, SolarStage.STAGE_6).and(NOT_WITHERED).and(aboveSafeHeight()),
                    setBlock(SolarModBlocks.WITHERED_LEAVES.get()))
    );

    private LeavesWither() {}
}
