package com.supheria.solar_apocalypse_core.transforms.rule;

import com.supheria.solar_apocalypse_core.config.solar.SolarStageConfig;
import com.supheria.solar_apocalypse_core.config.solar.StageHeightConfig;
import com.supheria.solar_apocalypse_core.network.SapModVariables;
import com.supheria.solar_apocalypse_core.transforms.util.BlockSpreadUtils;
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

    // -----------------------------------------------------------------------
    // 阶段 (Stage)
    // -----------------------------------------------------------------------

    /** stage >= min && stage < maxExclusive */
    public static TransformCondition stageRange(int min, int maxExclusive) {
        return (world, x, y, z, stage) -> stage >= min && stage < maxExclusive;
    }

    /** stage == s */
    public static TransformCondition stageExact(int s) {
        return (world, x, y, z, stage) -> stage == s;
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

    /** 当前是白天（TodayTime 不在夜间区间 12566–23450） */
    public static TransformCondition daytime() {
        return (world, x, y, z, stage) -> {
            long t = (long)SapModVariables.MapVariables.get(world).TodayTime;
            return !(t > 12566 && t < 23450);
        };
    }

    /** world.dayTime() >= minTicks */
    public static TransformCondition dayTimeMin(long minTicks) {
        return (world, x, y, z, stage) -> world.dayTime() >= minTicks;
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

    /** y >= threshold */
    public static TransformCondition minY(double threshold) {
        return (world, x, y, z, stage) -> y >= threshold;
    }

    /** y >= StageHeightConfig.getSafeHeight(forStage) - lazy evaluation */
    public static TransformCondition aboveSafeHeight(int forStage) {
        return (world, x, y, z, stage) -> y >= StageHeightConfig.getSafeHeight(forStage);
    }

    /** y >= StageHeightConfig.getExtraDamageHeight(forStage) - lazy evaluation */
    public static TransformCondition aboveExtraDamageHeight(int forStage) {
        return (world, x, y, z, stage) -> y >= StageHeightConfig.getExtraDamageHeight(forStage);
    }

    /**
     * y >= min(getSafeHeight(2..5)) - lazy evaluation。
     * 用于石头 TC 阶段2-5：取各阶段安全高度的最小值。
     */
    public static TransformCondition aboveMinSafeHeight() {
        return (world, x, y, z, stage) -> {
            int minH = Math.min(
                    Math.min(StageHeightConfig.getSafeHeight(2), StageHeightConfig.getSafeHeight(3)),
                    Math.min(StageHeightConfig.getSafeHeight(4), StageHeightConfig.getSafeHeight(5)));
            return y >= minH;
        };
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
                Mth.nextDouble(RandomSource.create(), 0, 10) <= world.dayTime() / 24000.0 + 1;
    }

    /**
     * nextDouble(0, 15) <= dayTime/24000 + 1
     * （碎泥土→沙子软转换，略低概率）
     */
    public static TransformCondition randomDaySoftSlow() {
        return (world, x, y, z, stage) ->
                Mth.nextDouble(RandomSource.create(), 0, 15) <= world.dayTime() / 24000.0 + 1;
    }

    /**
     * nextDouble(0, dayTime/24000 + 5) <= dayTime/thresholdDivisor
     * （石头/卵石 TC 的概率公式）
     */
    public static TransformCondition randomDayVariable(double thresholdDivisor) {
        return (world, x, y, z, stage) ->
                Mth.nextDouble(RandomSource.create(), 0, world.dayTime() / 24000.0 + 5)
                        <= world.dayTime() / thresholdDivisor;
    }

    /**
     * nextInt(1, 10) == 1（约10%概率）
     * （沙子→尘土软转换）
     */
    public static TransformCondition randomOneIn10() {
        return (world, x, y, z, stage) ->
                Mth.nextInt(RandomSource.create(), 1, 10) == 1;
    }

    /**
     * nextDouble(0, D+1) <= D，其中 D = dayTime/24000（整数除法）。
     * （普通方块随天累积删除概率，SimpleDecay / BubbleEvaporate / WaterEvaporate 等共享公式）
     */
    public static TransformCondition randomDayRate() {
        return (world, x, y, z, stage) -> {
            long d = world.dayTime() / 24000;
            return Mth.nextDouble(RandomSource.create(), 0, d + 1) <= d;
        };
    }

    /**
     * nextDouble(0, 10) <= (D/2)+2，其中 D = dayTime/24000（整数除法）。
     * （木制方块阶段1-5的燃烧概率）
     */
    public static TransformCondition randomDayWood() {
        return (world, x, y, z, stage) -> {
            long d = world.dayTime() / 24000;
            return Mth.nextDouble(RandomSource.create(), 0, 10) <= (d / 2) + 2;
        };
    }

    /**
     * nextDouble(0, 15) <= (D/4)+3，其中 D = dayTime/24000（整数除法）。
     * （木制方块阶段2-3的慢速燃烧概率）
     */
    public static TransformCondition randomDayWoodSlow() {
        return (world, x, y, z, stage) -> {
            long d = world.dayTime() / 24000;
            return Mth.nextDouble(RandomSource.create(), 0, 15) <= (d / 4) + 3;
        };
    }

    /**
     * nextDouble(0, 15) <= (D/2)+3，其中 D = dayTime/24000（整数除法）。
     * （木制方块阶段4-5的快速燃烧概率）
     */
    public static TransformCondition randomDayWoodFast() {
        return (world, x, y, z, stage) -> {
            long d = world.dayTime() / 24000;
            return Mth.nextDouble(RandomSource.create(), 0, 15) <= (d / 2) + 3;
        };
    }

    /** world.dayTime() < maxTicks */
    public static TransformCondition dayTimeLessThan(long maxTicks) {
        return (world, x, y, z, stage) -> world.dayTime() < maxTicks;
    }

    /** 50% 随机触发（nextDouble(0,2) <= 1）。 */
    public static TransformCondition random50() {
        return (world, x, y, z, stage) ->
                Mth.nextDouble(RandomSource.create(), 0, 2) <= 1;
    }

    /**
     * 坍缩阶段的概率转换条件，概率由配置项 collapseBlockTransformRate 决定：
     * probability = min(1.0, transformRate / 2.0)
     */
    public static TransformCondition randomCollapse() {
        return (world, x, y, z, stage) -> {
            int rate = SolarStageConfig.SOLAR_STAGE_VALUES.collapseBlockTransformRate.get();
            double p = Math.min(1.0, (double) rate / 2.0);
            return Mth.nextDouble(RandomSource.create(), 0, 1) <= p;
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
    public static TransformCondition aboveMinOf(int stageA, int stageB) {
        return (world, x, y, z, stage) ->
                y >= Math.min(StageHeightConfig.getSafeHeight(stageA), StageHeightConfig.getSafeHeight(stageB));
    }

    /** y > StageHeightConfig.getWaterEvapHeight(stage)（水蒸发高度阈值，严格大于） */
    public static TransformCondition aboveWaterEvapHeight() {
        return (world, x, y, z, stage) -> y > StageHeightConfig.getWaterEvapHeight(stage);
    }

    private TransformConditions() {}
}
