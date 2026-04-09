package com.supheria.solar_apocalypse_core.config.solar;

import com.supheria.solar_apocalypse_core.world.SolarStage;

/**
 * 太阳阶段系统使用的集中配置常量。
 *
 * <p>这里不再走外部 TOML，而是把阶段边界、坍缺期环境参数和 random tick 强度统一收口为代码常量，
 * 供阶段推进、环境渲染和方块转换系统共享同一套权威数值。
 */
public final class SolarStageConfig {

    /** 第二阶段开始的累计世界时间（第 5 天结束后）。 */
    public static final int STAGE_2_START_TIME = 120000;
    /** 第三阶段开始的累计世界时间（第 12 天结束后）。 */
    public static final int STAGE_3_START_TIME = 288000;
    /** 第四阶段开始的累计世界时间（第 20 天结束后）。 */
    public static final int STAGE_4_START_TIME = 480000;
    /** 第五阶段开始的累计世界时间（第 30 天结束后）。 */
    public static final int STAGE_5_START_TIME = 720000;
    /** 第六阶段（坍缺）开始的累计世界时间（第 50 天结束后）。 */
    public static final int STAGE_6_START_TIME = 1200000;

    /** 坍缺期自然积雪允许达到的最大层数。 */
    public static final int COLLAPSE_MAX_SNOW_LAYER = 6;
    /** 坍缺期雪层采样/堆积节奏。 */
    public static final int COLLAPSE_SNOW_ACCUMULATION_RATE = 4;
    /** 坍缺期水面冻结节奏。 */
    public static final int COLLAPSE_WATER_FREEZE_RATE = 6;
    /** 坍缺期白天亮度缩放系数，用于整体压暗天空表现。 */
    public static final double COLLAPSE_DAY_BRIGHTNESS_FACTOR = 0.3;
    /** 坍缺期额外方块转换逻辑的触发频率。 */
    public static final int COLLAPSE_BLOCK_TRANSFORM_RATE = 2;
    /** 单次雪层处理采样的候选点数量。 */
    public static final int COLLAPSE_SNOW_SAMPLE_COUNT = 4;
    /** 单次冻结处理采样的候选点数量。 */
    public static final int COLLAPSE_WATER_SAMPLE_COUNT = 3;
    /** 单次雪推进允许消耗的最大步骤数。 */
    public static final int COLLAPSE_SNOW_STEP_BUDGET = 3;
    /** 雪层采样区域直径。 */
    public static final int COLLAPSE_SNOW_SAMPLE_DIAMETER = 32;
    /** 雪层采样相对中心点的偏移半径。 */
    public static final int COLLAPSE_SNOW_SAMPLE_OFFSET = 16;
    /** 坍缺期统一覆盖的极寒群系温度。 */
    public static final float COLLAPSE_BIOME_TEMPERATURE = -0.5f;
    /** 坍缺期天空最小变暗值。 */
    public static final int COLLAPSE_MIN_SKY_DARKEN = 11;
    /** 玩家重新放水时，清理假空气的球形半径。 */
    public static final int EVAPORATED_VOID_WATER_CLEANUP_RADIUS = 15;

    /**
     * 各阶段对应的 randomTickSpeed。
     * 下标按 STAGE_1 ~ STAGE_6 顺序排列；NONE 与未初始化状态回退到第一档。
     *
     * <p>本模组会把大量方块接入自定义环境 random tick，因此第三阶段后如果继续维持
     * 8~10 的高档位，服务端主线程容易被方块更新挤占，进而让实体移动、受击和 AI
     * 表现出明显卡顿。这里收敛中后期档位，优先保证实体更新稳定。</p>
     */
    private static final int UNIFORM_RANDOM_TICKING_LEVEL = 5;
    /** 各阶段转换触发率。后期阶段适当提高，恢复阶段性推进速度。 */
    private static final double[] STAGE_TRANSFORM_RATES = {0.22, 0.38, 0.62, 0.82, 0.96, 1.0};
    /** 各阶段通用单次扩散预算。统一固定，避免高阶段主线程负载继续抬升。 */
    private static final int UNIFORM_STAGE_SPREAD_BUDGET = 4;
    /** 水蒸发链的单次扩散预算。统一固定，避免第五阶段水链过重。 */
    private static final int UNIFORM_WATER_SPREAD_BUDGET = 5;

    public static int getStage2StartTime() {
        return STAGE_2_START_TIME;
    }

    public static int getStage3StartTime() {
        return STAGE_3_START_TIME;
    }

    public static int getStage4StartTime() {
        return STAGE_4_START_TIME;
    }

    public static int getStage5StartTime() {
        return STAGE_5_START_TIME;
    }

    public static int getStage6StartTime() {
        return STAGE_6_START_TIME;
    }

    public static int getCollapseMaxSnowLayer() {
        return COLLAPSE_MAX_SNOW_LAYER;
    }

    public static int getCollapseSnowAccumulationRate() {
        return COLLAPSE_SNOW_ACCUMULATION_RATE;
    }

    public static int getCollapseWaterFreezeRate() {
        return COLLAPSE_WATER_FREEZE_RATE;
    }

    public static float getCollapseDayBrightnessFactor() {
        return (float) COLLAPSE_DAY_BRIGHTNESS_FACTOR;
    }

    public static int getCollapseBlockTransformRate() {
        return COLLAPSE_BLOCK_TRANSFORM_RATE;
    }

    public static int getCollapseSnowSampleCount() {
        return COLLAPSE_SNOW_SAMPLE_COUNT;
    }

    public static int getCollapseWaterSampleCount() {
        return COLLAPSE_WATER_SAMPLE_COUNT;
    }

    public static int getCollapseSnowStepBudget() {
        return COLLAPSE_SNOW_STEP_BUDGET;
    }

    public static int getCollapseSnowSampleDiameter() {
        return COLLAPSE_SNOW_SAMPLE_DIAMETER;
    }

    public static int getCollapseSnowSampleOffset() {
        return COLLAPSE_SNOW_SAMPLE_OFFSET;
    }

    public static float getCollapseBiomeTemperature() {
        return COLLAPSE_BIOME_TEMPERATURE;
    }

    public static int getCollapseMinSkyDarken() {
        return COLLAPSE_MIN_SKY_DARKEN;
    }

    public static int getEvaporatedVoidWaterCleanupRadius() {
        return EVAPORATED_VOID_WATER_CLEANUP_RADIUS;
    }

    /**
     * 返回指定阶段对应的 randomTickSpeed。
     * 未初始化阶段不会返回 0，而是沿用第一阶段强度，避免阶段系统启动前出现过低刷新频率。
     */
    public static int getRandomTickingLevel(SolarStage stage) {
        return UNIFORM_RANDOM_TICKING_LEVEL;
    }

    public static double getStageTransformRate(SolarStage stage) {
        return STAGE_TRANSFORM_RATES[getStageIndex(stage)];
    }

    public static int getStageSpreadBudget(SolarStage stage) {
        return UNIFORM_STAGE_SPREAD_BUDGET;
    }

    public static int getWaterSpreadBudget(SolarStage stage) {
        return UNIFORM_WATER_SPREAD_BUDGET;
    }

    private static int getStageIndex(SolarStage stage) {
        if (stage == null || !stage.isAtLeast(SolarStage.STAGE_1)) {
            return 0;
        }
        return Math.min(STAGE_TRANSFORM_RATES.length - 1, stage.ordinal() - 1);
    }

    private SolarStageConfig() {}
}
