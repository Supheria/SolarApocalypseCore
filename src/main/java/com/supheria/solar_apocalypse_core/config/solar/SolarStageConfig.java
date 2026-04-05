package com.supheria.solar_apocalypse_core.config.solar;

import com.supheria.solar_apocalypse_core.world.SolarStage;

/**
 * 太阳爆发阶段常量。
 */
public final class SolarStageConfig {

    public static final int STAGE_5_START_TIME = 648000;
    public static final int STAGE_6_START_TIME = 960000;

    public static final int COLLAPSE_MAX_SNOW_LAYER = 6;
    public static final int COLLAPSE_SNOW_ACCUMULATION_RATE = 100;
    public static final double COLLAPSE_DAY_BRIGHTNESS_FACTOR = 0.3;
    public static final int COLLAPSE_BLOCK_TRANSFORM_RATE = 2;
    public static final int COLLAPSE_SNOW_SAMPLE_COUNT = 10;
    public static final int COLLAPSE_SNOW_SAMPLE_DIAMETER = 32;
    public static final int COLLAPSE_SNOW_SAMPLE_OFFSET = 16;
    public static final float COLLAPSE_BIOME_TEMPERATURE = -0.5f;
    public static final int COLLAPSE_MIN_SKY_DARKEN = 11;

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

    public static int getRandomTickingLevel(SolarStage stage) {
        if (stage == null || !stage.isAtLeast(SolarStage.STAGE_1)) {
            return RANDOM_TICKING_LEVELS[0];
        }
        return RANDOM_TICKING_LEVELS[stage.ordinal() - 1];
    }

    private SolarStageConfig() {}
}
