package com.supheria.solar_apocalypse_core.procedures.transform;

import com.supheria.solar_apocalypse_core.config.solar.StageHeightConfig;
import com.supheria.solar_apocalypse_core.network.SapModVariables;
import com.supheria.solar_apocalypse_core.procedures.util.BlockSpreadUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
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

    private TransformConditions() {}
}
