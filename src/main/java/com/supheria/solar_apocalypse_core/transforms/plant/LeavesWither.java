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
            // 爆发阶段：露天叶片先枯萎，再进入焚毁阶段
            when(stageIsEruptionPhase().and(NOT_WITHERED).and(SOFT_BASE_2).and(SKY_OR_WITHER_ABOVE),
                    setBlock(SolarModBlocks.WITHERED_LEAVES.get())),
            when(stageRange(SolarStage.STAGE_2, SolarStage.STAGE_6).and(NOT_WITHERED).and(aboveSafeHeight()),
                    setBlock(SolarModBlocks.WITHERED_LEAVES.get())),
            // 枯萎叶在爆发阶段会被持续清除
            when(stageRange(SolarStage.STAGE_2, SolarStage.STAGE_6).and(IS_WITHERED).and(SOFT_BASE_2).and(SKY_OR_WITHER_ABOVE),
                    destroyBlockWithDrops()),
            when(stageRange(SolarStage.STAGE_2, SolarStage.STAGE_6).and(IS_WITHERED).and(aboveSafeHeight()),
                    destroyBlockWithDrops()),
            // 枯萎叶会把整片树冠一起拖入焚毁
            when(stageRange(SolarStage.STAGE_2, SolarStage.STAGE_6).and(IS_WITHERED),
                    FIRE_4H),
            when(stageRange(SolarStage.STAGE_2, SolarStage.STAGE_6).and(NOT_WITHERED).and(aboveSafeHeight()),
                    setBlock(SolarModBlocks.WITHERED_LEAVES.get()))
    );

    private LeavesWither() {}
}
