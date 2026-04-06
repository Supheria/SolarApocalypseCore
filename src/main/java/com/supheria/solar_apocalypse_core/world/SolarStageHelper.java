package com.supheria.solar_apocalypse_core.world;

import com.supheria.solar_apocalypse_core.config.solar.SolarStageConfig;

/**
 * 太阳阶段判定与时间拆分的权威工具类。
 *
 * <p>阶段切换、进度计算与昼夜窗口判断都以这里的规则为准。
 */
public final class SolarStageHelper {
    public static final long DAY_TICKS = 24000L;
    public static final long DAYTIME_END_TICKS = 12000L;
    public static final long NIGHT_START_TICKS = 12566L;
    public static final long NIGHT_END_TICKS = 23450L;

    /**
     * 根据累计世界时间推导当前应处于哪个太阳阶段。
     * 这是阶段切换逻辑的权威入口，供服务端推进与客户端展示共用。
     */
    public static SolarStage getPhaseByDayTime(long dayTime) {
        long stage2Start = SolarStageConfig.getStage2StartTime();
        long stage3Start = SolarStageConfig.getStage3StartTime();
        long stage4Start = SolarStageConfig.getStage4StartTime();
        long stage5Start = SolarStageConfig.getStage5StartTime();
        long stage6Start = SolarStageConfig.getStage6StartTime();

        if (dayTime < stage2Start) {
            return SolarStage.STAGE_1;
        } else if (dayTime < stage3Start) {
            return SolarStage.STAGE_2;
        } else if (dayTime < stage4Start) {
            return SolarStage.STAGE_3;
        } else if (dayTime < stage5Start) {
            return SolarStage.STAGE_4;
        } else if (dayTime < stage6Start) {
            return SolarStage.STAGE_5;
        } else {
            return SolarStage.STAGE_6;
        }
    }

    /**
     * 返回指定阶段在累计时间轴上的起止区间。
     * 主要用于 HUD、渲染或其它需要计算阶段进度的展示逻辑。
     */
    public static long[] getPhaseTimeRange(SolarStage stage) {
        long stage2Start = SolarStageConfig.getStage2StartTime();
        long stage3Start = SolarStageConfig.getStage3StartTime();
        long stage4Start = SolarStageConfig.getStage4StartTime();
        long stage5Start = SolarStageConfig.getStage5StartTime();
        long stage6Start = SolarStageConfig.getStage6StartTime();

        return switch (stage) {
            case STAGE_1 -> new long[]{0, stage2Start};
            case STAGE_2 -> new long[]{stage2Start, stage3Start};
            case STAGE_3 -> new long[]{stage3Start, stage4Start};
            case STAGE_4 -> new long[]{stage4Start, stage5Start};
            case STAGE_5 -> new long[]{stage5Start, stage6Start};
            case STAGE_6 -> new long[]{stage6Start, Long.MAX_VALUE};
            case NONE -> new long[]{0, 0};
        };
    }

    /**
     * 计算给定时间点在某个阶段区间内的相对进度。
     * 若阶段没有上界（第六阶段），当前实现固定返回 1，表示已进入最终阶段。
     */
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
