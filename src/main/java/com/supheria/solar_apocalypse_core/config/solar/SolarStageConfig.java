package com.supheria.solar_apocalypse_core.config.solar;

import com.supheria.solar_apocalypse_core.world.SolarStage;

/**
 * 太阳阶段系统使用的集中配置常量。
 *
 * <p>这里不再走外部 TOML，而是把阶段边界、坍缺期环境参数和 random tick 强度统一收口为代码常量，
 * 供阶段推进、环境渲染和方块转换系统共享同一套权威数值。
 */
public final class SolarStageConfig {

    /** 第五阶段开始的累计世界时间。前四阶段会平均切分到这一时刻之前。 */
    public static final int STAGE_5_START_TIME = 648000;
    /** 第六阶段（坍缺）开始的累计世界时间。 */
    public static final int STAGE_6_START_TIME = 960000;

    /** 坍缺期自然积雪允许达到的最大层数。 */
    public static final int COLLAPSE_MAX_SNOW_LAYER = 6;
    /** 坍缺期积雪采样/堆积的基础节奏。 */
    public static final int COLLAPSE_SNOW_ACCUMULATION_RATE = 10;
    /** 坍缺期白天亮度缩放系数，用于整体压暗天空表现。 */
    public static final double COLLAPSE_DAY_BRIGHTNESS_FACTOR = 0.3;
    /** 坍缺期额外方块转换逻辑的触发频率。 */
    public static final int COLLAPSE_BLOCK_TRANSFORM_RATE = 2;
    /** 单次雪层处理采样的候选点数量。 */
    public static final int COLLAPSE_SNOW_SAMPLE_COUNT = 10;
    /** 雪层采样区域直径。 */
    public static final int COLLAPSE_SNOW_SAMPLE_DIAMETER = 32;
    /** 雪层采样相对中心点的偏移半径。 */
    public static final int COLLAPSE_SNOW_SAMPLE_OFFSET = 16;
    /** 坍缺期统一覆盖的极寒群系温度。 */
    public static final float COLLAPSE_BIOME_TEMPERATURE = -0.5f;
    /** 坍缺期天空最小变暗值。 */
    public static final int COLLAPSE_MIN_SKY_DARKEN = 11;

    /**
     * 各阶段对应的 randomTickSpeed。
     * 下标按 STAGE_1 ~ STAGE_6 顺序排列；NONE 与未初始化状态回退到第一档。
     */
    private static final int[] RANDOM_TICKING_LEVELS = {4, 5, 8, 9, 10, 10};

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

    public static float getCollapseDayBrightnessFactor() {
        return (float) COLLAPSE_DAY_BRIGHTNESS_FACTOR;
    }

    public static int getCollapseBlockTransformRate() {
        return COLLAPSE_BLOCK_TRANSFORM_RATE;
    }

    public static int getCollapseSnowSampleCount() {
        return COLLAPSE_SNOW_SAMPLE_COUNT;
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

    /**
     * 返回指定阶段对应的 randomTickSpeed。
     * 未初始化阶段不会返回 0，而是沿用第一阶段强度，避免阶段系统启动前出现过低刷新频率。
     */
    public static int getRandomTickingLevel(SolarStage stage) {
        if (stage == null || !stage.isAtLeast(SolarStage.STAGE_1)) {
            return RANDOM_TICKING_LEVELS[0];
        }
        return RANDOM_TICKING_LEVELS[stage.ordinal() - 1];
    }

    private SolarStageConfig() {}
}
