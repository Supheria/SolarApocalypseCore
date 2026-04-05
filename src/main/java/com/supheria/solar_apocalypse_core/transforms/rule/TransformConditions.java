package com.supheria.solar_apocalypse_core.transforms.rule;

import com.supheria.solar_apocalypse_core.config.solar.SolarStageConfig;
import com.supheria.solar_apocalypse_core.config.solar.StageHeightConfig;
import com.supheria.solar_apocalypse_core.network.SolarModVariables;
import com.supheria.solar_apocalypse_core.transforms.util.BlockSpreadUtils;
import com.supheria.solar_apocalypse_core.world.SolarStage;
import com.supheria.solar_apocalypse_core.world.SolarStageHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

/**
 * 常用 {@link TransformCondition} 的静态工厂。
 *
 * <p>所有方法均返回无状态的 lambda，可安全地组合复用：</p>
 * <pre>
 * stageRange(2, 6).and(sky()).and(noRain()).and(randomDay(10))
 * </pre>
 */
public final class TransformConditions {
    private static final double RANDOM_DAY_SOFT_MAX = 10.0;
    private static final double RANDOM_DAY_SOFT_SLOW_MAX = 15.0;
    private static final double RANDOM_DAY_VARIABLE_BASE = 5.0;
    private static final double RANDOM_DAY_VARIABLE_DIVISOR = 16000.0;
    private static final int RANDOM_ONE_IN_TEN_MIN = 1;
    private static final int RANDOM_ONE_IN_TEN_MAX = 10;
    private static final double RANDOM_WOOD_MAX = 10.0;
    private static final double RANDOM_WOOD_SLOW_MAX = 15.0;
    private static final long RANDOM_WOOD_DIVISOR = 2L;
    private static final long RANDOM_WOOD_SLOW_DIVISOR = 4L;
    private static final long RANDOM_WOOD_FAST_DIVISOR = 2L;
    private static final int RANDOM_WOOD_BASE = 2;
    private static final int RANDOM_WOOD_SLOW_BASE = 3;
    private static final int RANDOM_WOOD_FAST_BASE = 3;
    private static final double RANDOM_50_MAX = 2.0;
    private static final double RANDOM_50_THRESHOLD = 1.0;
    private static final double COLLAPSE_RATE_NORMALIZER = 2.0;
    private static final double COLLAPSE_RANDOM_MAX = 1.0;

    // -----------------------------------------------------------------------
    // 阶段 (Stage)
    // -----------------------------------------------------------------------

    /** stage >= min && stage < maxExclusive */
    public static TransformCondition stageRange(SolarStage min, SolarStage maxExclusive) {
        return (world, x, y, z, stage) -> stage.isAtLeast(min) && stage.isBefore(maxExclusive);
    }

    /** stage == s */
    public static TransformCondition stageExact(SolarStage s) {
        return (world, x, y, z, stage) -> stage == s;
    }

    /**
     * 判断是否为爆发阶段（STAGE_1 - STAGE_5）
     */
    public static TransformCondition stageIsEruptionPhase() {
        return (world, x, y, z, stage) -> stage != SolarStage.NONE && stage != SolarStage.STAGE_6;
    }

    /**
     * 判断是否为坍缺阶段（STAGE_6）
     */
    public static TransformCondition stageIsCollapsePhase() {
        return (world, x, y, z, stage) -> stage == SolarStage.STAGE_6;
    }

    // -----------------------------------------------------------------------
    // 世界状态 (World State)
    // -----------------------------------------------------------------------

    /** 天空可见（上方一格） */
    public static TransformCondition sky() {
        return (world, x, y, z, stage) ->
                world.canSeeSkyFromBelowWater(BlockPos.containing(x, y + 1, z));
    }

    /** 上方一格无水方块 */
    public static TransformCondition noWaterAbove() {
        return (world, x, y, z, stage) ->
                world.getBlockState(BlockPos.containing(x, y + 1, z)).getBlock() != Blocks.WATER;
    }

    /** 当前不下雨 */
    public static TransformCondition noRain() {
        return (world, x, y, z, stage) -> !world.getLevelData().isRaining();
    }

    /** 当前是白天（currentTimeOfDay 不在夜间区间 12566–23450） */
    public static TransformCondition daytime() {
        return (world, x, y, z, stage) -> {
            long t = (long) SolarModVariables.MapVariables.get(world).currentTimeOfDay;
            return !SolarStageHelper.isNightWindow(t);
        };
    }

    /** 六面相邻有岩浆 */
    public static TransformCondition adjacentLava() {
        return (world, x, y, z, stage) -> BlockSpreadUtils.hasAdjacentLava(world, x, y, z);
    }

    /** 上方一格是空气 */
    public static TransformCondition airAbove() {
        return (world, x, y, z, stage) ->
                world.getBlockState(BlockPos.containing(x, y + 1, z)).getBlock() == Blocks.AIR;
    }

    // -----------------------------------------------------------------------
    // 高度 (Height)
    // -----------------------------------------------------------------------

    /** y >= StageHeightConfig.getSafeHeight(forStage) */
    public static TransformCondition aboveSafeHeight() {
        return (world, x, y, z, stage) -> y >= StageHeightConfig.getSafeHeight(stage);
    }

    /** y >= StageHeightConfig.getCozyHeight(forStage) */
    public static TransformCondition aboveCozyHeight() {
        return (world, x, y, z, stage) -> y >= StageHeightConfig.getCozyHeight(stage);
    }

    // -----------------------------------------------------------------------
    // 随机性 (Randomness)
    // -----------------------------------------------------------------------

    /**
     * nextDouble(0, 10) <= dayTime/24000 + 1
     * （泥土链软转换的概率公式）
     */
    public static TransformCondition randomDaySoft() {
        return (world, x, y, z, stage) ->
                Mth.nextDouble(RandomSource.create(), 0, RANDOM_DAY_SOFT_MAX)
                        <= world.dayTime() / (double) SolarStageHelper.DAY_TICKS + 1;
    }

    /**
     * nextDouble(0, 15) <= dayTime/24000 + 1
     * （碎泥土→沙子软转换，略低概率）
     */
    public static TransformCondition randomDaySoftSlow() {
        return (world, x, y, z, stage) ->
                Mth.nextDouble(RandomSource.create(), 0, RANDOM_DAY_SOFT_SLOW_MAX)
                        <= world.dayTime() / (double) SolarStageHelper.DAY_TICKS + 1;
    }

    /**
     * nextDouble(0, dayTime/24000 + 5) <= dayTime/thresholdDivisor
     * （石头/卵石 TC 的概率公式）
     */
    public static TransformCondition randomDayVariable() {
        return (world, x, y, z, stage) ->
                Mth.nextDouble(RandomSource.create(), 0,
                        world.dayTime() / (double) SolarStageHelper.DAY_TICKS + RANDOM_DAY_VARIABLE_BASE)
                        <= world.dayTime() / RANDOM_DAY_VARIABLE_DIVISOR;
    }

    /**
     * nextInt(1, 10) == 1（约10%概率）
     * （沙子→尘土软转换）
     */
    public static TransformCondition randomOneIn10() {
        return (world, x, y, z, stage) ->
                Mth.nextInt(RandomSource.create(), RANDOM_ONE_IN_TEN_MIN, RANDOM_ONE_IN_TEN_MAX) == RANDOM_ONE_IN_TEN_MIN;
    }

    /**
     * nextDouble(0, D+1) <= D，其中 D = dayTime/24000（整数除法）。
     * （普通方块随天累积删除概率，SimpleDecay / BubbleEvaporate / WaterEvaporate 等共享公式）
     */
    public static TransformCondition randomDayRate() {
        return (world, x, y, z, stage) -> {
            long d = SolarStageHelper.getDayIndex(world.dayTime());
            return Mth.nextDouble(RandomSource.create(), 0, d + 1) <= d;
        };
    }

    /**
     * nextDouble(0, 10) <= (D/2)+2，其中 D = dayTime/24000（整数除法）。
     * （木制方块阶段1-5的燃烧概率）
     */
    public static TransformCondition randomDayWood() {
        return (world, x, y, z, stage) -> {
            long d = SolarStageHelper.getDayIndex(world.dayTime());
            return Mth.nextDouble(RandomSource.create(), 0, RANDOM_WOOD_MAX) <= (d / RANDOM_WOOD_DIVISOR) + RANDOM_WOOD_BASE;
        };
    }

    /**
     * nextDouble(0, 15) <= (D/4)+3，其中 D = dayTime/24000（整数除法）。
     * （木制方块阶段2-3的慢速燃烧概率）
     */
    public static TransformCondition randomDayWoodSlow() {
        return (world, x, y, z, stage) -> {
            long d = SolarStageHelper.getDayIndex(world.dayTime());
            return Mth.nextDouble(RandomSource.create(), 0, RANDOM_WOOD_SLOW_MAX) <= (d / RANDOM_WOOD_SLOW_DIVISOR) + RANDOM_WOOD_SLOW_BASE;
        };
    }

    /**
     * nextDouble(0, 15) <= (D/2)+3，其中 D = dayTime/24000（整数除法）。
     * （木制方块阶段4-5的快速燃烧概率）
     */
    public static TransformCondition randomDayWoodFast() {
        return (world, x, y, z, stage) -> {
            long d = SolarStageHelper.getDayIndex(world.dayTime());
            return Mth.nextDouble(RandomSource.create(), 0, RANDOM_WOOD_SLOW_MAX) <= (d / RANDOM_WOOD_FAST_DIVISOR) + RANDOM_WOOD_FAST_BASE;
        };
    }

    /** world.dayTime() < maxTicks */
    public static TransformCondition dayTimeLessThan(long maxTicks) {
        return (world, x, y, z, stage) -> world.dayTime() < maxTicks;
    }

    /** 50% 随机触发（nextDouble(0,2) <= 1）。 */
    public static TransformCondition random50() {
        return (world, x, y, z, stage) ->
                Mth.nextDouble(RandomSource.create(), 0, RANDOM_50_MAX) <= RANDOM_50_THRESHOLD;
    }

    /**
     * 坍缩阶段的概率转换条件，概率由配置项 collapseBlockTransformRate 决定：
     * probability = min(1.0, transformRate / 2.0)
     */
    public static TransformCondition randomCollapse() {
        return (world, x, y, z, stage) -> {
            int rate = SolarStageConfig.getCollapseBlockTransformRate();
            double p = Math.min(COLLAPSE_RANDOM_MAX, rate / COLLAPSE_RATE_NORMALIZER);
            return Mth.nextDouble(RandomSource.create(), 0, COLLAPSE_RANDOM_MAX) <= p;
        };
    }

    /** 当前坐标处的方块为指定方块。 */
    public static TransformCondition isBlock(Block block) {
        return (world, x, y, z, stage) ->
                world.getBlockState(BlockPos.containing(x, y, z)).getBlock() == block;
    }

    /**
     * y >= min(getSafeHeight(stageA), getSafeHeight(stageB)) - lazy evaluation。
     * 用于砾石→岩浆阶段4-5：取两阶段安全高度的较小值，保证一致的触发下限。
     */
    public static TransformCondition aboveMinOf(SolarStage stageA, SolarStage stageB) {
        return (world, x, y, z, stage) ->
                y >= Math.min(StageHeightConfig.getSafeHeight(stageA), StageHeightConfig.getSafeHeight(stageB));
    }

    /** y > StageHeightConfig.getWaterEvapHeight(stage)（水蒸发高度阈值，严格大于） */
    public static TransformCondition aboveWaterEvaporateHeight() {
        return (world, x, y, z, stage) -> y > StageHeightConfig.getSafeHeight(stage);
    }

    /** y >= threshold (冰融化高度条件) */
    public static TransformCondition minIceHeight(double threshold) {
        return (world, x, y, z, stage) -> y >= threshold;
    }

    private TransformConditions() {}
}
