package com.supheria.solar_apocalypse_core.transforms.plant;

import com.supheria.solar_apocalypse_core.BlockTransform;
import com.supheria.solar_apocalypse_core.init.SapModBlocks;
import com.supheria.solar_apocalypse_core.transforms.rule.TransformAction;
import com.supheria.solar_apocalypse_core.transforms.rule.TransformCondition;
import com.supheria.solar_apocalypse_core.transforms.rule.TransformRule;
import com.supheria.solar_apocalypse_core.transforms.util.BlockSpreadUtils;
import com.supheria.solar_apocalypse_core.world.SolarStage;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
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

    /** 向4邻扩散火焰。 */
    private static final TransformAction FIRE_4H =
            spread4H(Blocks.FIRE.defaultBlockState(), IS_LEAVES);

    /** 向8邻扩散火焰。 */
    private static final TransformAction FIRE_8H =
            spread8H(Blocks.FIRE.defaultBlockState(), IS_LEAVES);

    /** 向17邻（8H+下层）扩散火焰。 */
    private static final TransformAction FIRE_17 =
            spread17(Blocks.FIRE.defaultBlockState(), IS_LEAVES);

    /** 向17邻（8H+下层）扩散空气。 */
    private static final TransformAction AIR_17 =
            spread17(Blocks.AIR.defaultBlockState(), IS_LEAVES);

    /** 天空可见 或 正上方是枯萎树叶。 */
    private static final TransformCondition SKY_OR_WITHER_ABOVE =
            sky().or((world, x, y, z, stage) ->
                    world.getBlockState(BlockPos.containing(x, y + 1, z)).getBlock()
                            == SapModBlocks.WITHERED_LEAVES.get());

    /** 当前方块是枯萎树叶。 */
    private static final TransformCondition IS_WITHERED =
            isBlock(SapModBlocks.WITHERED_LEAVES.get());

    /** 当前方块不是枯萎树叶。 */
    private static final TransformCondition NOT_WITHERED = IS_WITHERED.negate();

    /** 阶段2-5 softBase：白天 + 不下雨。 */
    private static final TransformCondition SOFT_BASE_2 = daytime().and(noRain());

    /**
     * 阶段4 y>=64 的二选一动作：
     * 50% → 扩散火焰到中心+8邻树叶
     * 50% → 中心变空气，8邻树叶变尘土（中心与邻居目标不同，不能用 spreadBlock）
     */
    private static final TransformAction STAGE4_FIRE_OR_DUST =
            coinFlip(FIRE_8H,
                    (world, x, y, z) -> {
                        BlockPos center = BlockPos.containing(x, y, z);
                        world.setBlock(center, Blocks.AIR.defaultBlockState(), 3);
                        for (int[] o : BlockSpreadUtils.OFFSETS_8H) {
                            BlockPos neighbor = center.offset(o[0], o[1], o[2]);
                            if (world.getBlockState(neighbor).is(BlockTags.LEAVES)) {
                                world.setBlock(neighbor, SapModBlocks.DUST.get().defaultBlockState(), 3);
                            }
                        }
                    });

    public static final BlockTransform TRANSFORM = TransformRule.rulesOf(
            // 阶段1-5（非枯萎叶）：白天 + 天空/枯萎叶在上 + 不下雨 + 慢速概率 → 枯萎树叶
            when(stageIsEruptionPhase().and(NOT_WITHERED).and(SOFT_BASE_2).and(SKY_OR_WITHER_ABOVE)
                            .and(daytime()).and(randomDayWoodSlow()),
                    setBlock(SapModBlocks.WITHERED_LEAVES.get())),

            // 阶段2-5（非枯萎叶）：白天 + 不下雨 + 高于安全高度 → 枯萎树叶
            when(stageRange(SolarStage.STAGE_2, SolarStage.STAGE_6).and(NOT_WITHERED).and(SOFT_BASE_2)
                            .and(aboveSafeHeight()).and(randomDayWood()),
                    setBlock(SapModBlocks.WITHERED_LEAVES.get())),

            // 阶段2-5（枯萎叶）：白天 + 天空/枯萎叶在上 + 不下雨 + 概率 → 上方点火
            when(stageRange(SolarStage.STAGE_2, SolarStage.STAGE_6).and(IS_WITHERED).and(SOFT_BASE_2).and(SKY_OR_WITHER_ABOVE)
                            .and(randomDayWood()),
                    setBlockAbove(Blocks.FIRE)),

            // 阶段2（枯萎叶）：白天 + 不下雨 + 高于安全高度 + 慢速概率 → 点火
            when(stageExact(SolarStage.STAGE_2).and(IS_WITHERED).and(SOFT_BASE_2).and(aboveSafeHeight())
                            .and(randomDayWoodSlow()),
                    setBlock(Blocks.FIRE)),

            // 阶段3-5（枯萎叶）：直接扩散火焰到4邻树叶
            when(stageRange(SolarStage.STAGE_3, SolarStage.STAGE_6).and(IS_WITHERED),
                    FIRE_4H),

            // 阶段3-5（非枯萎叶）：高于安全高度 + 概率 → 扩散火焰到4邻树叶
            when(stageRange(SolarStage.STAGE_3, SolarStage.STAGE_6).and(NOT_WITHERED).and(aboveSafeHeight()),
                    FIRE_4H),

            // 阶段3-5（非枯萎叶）：高于安全高度 → 枯萎叶
            when(stageRange(SolarStage.STAGE_3, SolarStage.STAGE_6).and(NOT_WITHERED).and(aboveSafeHeight()),
                    setBlock(SapModBlocks.WITHERED_LEAVES.get())),

            // 阶段4：高于安全高度 → 50% 扩散火焰8H 或 50% 空气+树叶→尘土8H
            when(stageExact(SolarStage.STAGE_4).and(aboveSafeHeight()),
                    STAGE4_FIRE_OR_DUST),

            // 阶段5：高于安全高度 → 50% 扩散火焰17 或 50% 扩散空气17
            when(stageExact(SolarStage.STAGE_5).and(aboveSafeHeight()),
                    coinFlip(FIRE_17, AIR_17)),

            // 阶段6：露天 -> 消失
            when(stageExact(SolarStage.STAGE_6).and(sky()),
                    setBlock(Blocks.AIR))
    );

    private LeavesWither() {}
}
