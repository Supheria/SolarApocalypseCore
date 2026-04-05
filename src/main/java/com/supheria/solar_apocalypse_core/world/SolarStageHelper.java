package com.supheria.solar_apocalypse_core.world;

import com.supheria.solar_apocalypse_core.config.solar.SolarStageConfig;

/**
 * 太阳阶段计算工具类
 * 提供时间计算、进度查询等核心逻辑
 *
 * 所有方法返回或接受 SolarStage 枚举
 */
public final class SolarStageHelper {
    public static final long DAY_TICKS = 24000L;
    public static final long DAYTIME_END_TICKS = 12000L;
    public static final long NIGHT_START_TICKS = 12566L;
    public static final long NIGHT_END_TICKS = 23450L;
    public static final int EARLY_STAGE_COUNT = 4;

    public static SolarStage getPhaseByDayTime(long dayTime) {
        long stage5Start = SolarStageConfig.getStage5StartTime();
        long stage6Start = SolarStageConfig.getStage6StartTime();
        long earlyStageLength = getEarlyStageLength();

        if (dayTime < stage5Start) {
            if (dayTime < earlyStageLength) {
                return SolarStage.STAGE_1;
            } else if (dayTime < earlyStageLength * 2) {
                return SolarStage.STAGE_2;
            } else if (dayTime < earlyStageLength * 3) {
                return SolarStage.STAGE_3;
            } else {
                return SolarStage.STAGE_4;
            }
        } else if (dayTime < stage6Start) {
            return SolarStage.STAGE_5;
        } else {
            return SolarStage.STAGE_6;
        }
    }

    public static long[] getPhaseTimeRange(SolarStage stage) {
        long stage5Start = SolarStageConfig.getStage5StartTime();
        long stage6Start = SolarStageConfig.getStage6StartTime();
        long earlyStageLength = getEarlyStageLength();

        return switch (stage) {
            case STAGE_1 -> new long[]{0, earlyStageLength};
            case STAGE_2 -> new long[]{earlyStageLength, earlyStageLength * 2};
            case STAGE_3 -> new long[]{earlyStageLength * 2, earlyStageLength * 3};
            case STAGE_4 -> new long[]{earlyStageLength * 3, stage5Start};
            case STAGE_5 -> new long[]{stage5Start, stage6Start};
            case STAGE_6 -> new long[]{stage6Start, Long.MAX_VALUE};
            case NONE -> new long[]{0, 0};
        };
    }

    public static float getPhaseProgress(long dayTime, SolarStage stage) {
        long[] timeRange = getPhaseTimeRange(stage);
        long start = timeRange[0];
        long end = timeRange[1];

        if (end == Long.MAX_VALUE) {
            return 1.0f;
        }

        if (end <= start) {
            return 0.0f;
        }

        long elapsed = dayTime - start;
        long duration = end - start;
        return Math.min(1.0f, Math.max(0.0f, (float) elapsed / duration));
    }

    public static int getRandomTickingLevel(SolarStage stage) {
        return SolarStageConfig.getRandomTickingLevel(stage);
    }

    public static long getEarlyStageLength() {
        return SolarStageConfig.getStage5StartTime() / EARLY_STAGE_COUNT;
    }

    public static long getTimeOfDay(long dayTime) {
        return Math.floorMod(dayTime, DAY_TICKS);
    }

    public static long getDayIndex(long dayTime) {
        return Math.floorDiv(dayTime, DAY_TICKS);
    }

    public static boolean isDaytime(long dayTime) {
        return getTimeOfDay(dayTime) < DAYTIME_END_TICKS;
    }

    public static boolean isNightWindow(long timeOfDay) {
        return timeOfDay > NIGHT_START_TICKS && timeOfDay < NIGHT_END_TICKS;
    }

    private SolarStageHelper() {}
}
