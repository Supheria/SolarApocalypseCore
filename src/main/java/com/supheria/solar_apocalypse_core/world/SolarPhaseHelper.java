package com.supheria.solar_apocalypse_core.world;

import com.supheria.solar_apocalypse_core.config.solar.SolarStageConfig;

/**
 * 太阳阶段辅助工具类
 * 提供阶段判定、时间计算、进度查询等功能
 */
public class SolarPhaseHelper {

    /**
     * 根据游戏时间获取当前阶段
     * 前4个阶段自动平均分配到 stage5StartTime 前
     */
    public static SolarPhase getPhaseByDayTime(long dayTime) {
        long stage5Start = SolarStageConfig.SOLAR_STAGE_VALUES.stage5StartTime.get();
        long stage6Start = SolarStageConfig.SOLAR_STAGE_VALUES.stage6StartTime.get();

        if (dayTime < stage5Start) {
            // 前4个阶段平均分配
            long perPhase = stage5Start / 4;
            if (dayTime < perPhase) {
                return SolarPhase.WARMING;
            } else if (dayTime < perPhase * 2) {
                return SolarPhase.ACCELERATION;
            } else if (dayTime < perPhase * 3) {
                return SolarPhase.PEAK;
            } else {
                return SolarPhase.CRITICAL;
            }
        } else if (dayTime < stage6Start) {
            return SolarPhase.ULTIMATE;
        } else {
            return SolarPhase.COLLAPSE;
        }
    }

    /**
     * 获取指定阶段的时间范围 [startTime, endTime)
     * 用于太阳渲染的进度计算
     */
    public static long[] getPhaseTimeRange(SolarPhase phase) {
        long stage5Start = SolarStageConfig.SOLAR_STAGE_VALUES.stage5StartTime.get();
        long stage6Start = SolarStageConfig.SOLAR_STAGE_VALUES.stage6StartTime.get();
        long perPhase = stage5Start / 4;

        return switch (phase) {
            case WARMING -> new long[]{0, perPhase};
            case ACCELERATION -> new long[]{perPhase, perPhase * 2};
            case PEAK -> new long[]{perPhase * 2, perPhase * 3};
            case CRITICAL -> new long[]{perPhase * 3, stage5Start};
            case ULTIMATE -> new long[]{stage5Start, stage6Start};
            case COLLAPSE -> new long[]{stage6Start, Long.MAX_VALUE};
            case NONE -> new long[]{0, 0};
        };
    }

    /**
     * 判断是否为爆发阶段（1-5）
     */
    public static boolean isEruptionPhase(SolarPhase phase) {
        int ordinal = phase.ordinal();
        return ordinal > 0 && ordinal <= 5;
    }

    /**
     * 判断是否为坍缩阶段
     */
    public static boolean isCollapsePhase(SolarPhase phase) {
        return phase == SolarPhase.COLLAPSE;
    }

    /**
     * 获取爆发等级 (1-5)
     * 用于伤害计算和太阳渲染进度
     */
    public static int getEruptionLevel(SolarPhase phase) {
        return Math.min(phase.ordinal(), 5);
    }

    /**
     * 获取当前阶段在总时间中的进度 (0.0 - 1.0)
     * 用于太阳渲染的连续变化
     */
    public static float getPhaseProgress(long dayTime, SolarPhase phase) {
        long[] timeRange = getPhaseTimeRange(phase);
        long start = timeRange[0];
        long end = timeRange[1];

        if (end == Long.MAX_VALUE) {
            // COLLAPSE 阶段永不结束，返回 1.0
            return 1.0f;
        }

        if (end <= start) {
            return 0.0f;
        }

        long elapsed = dayTime - start;
        long duration = end - start;
        return Math.min(1.0f, Math.max(0.0f, (float) elapsed / duration));
    }
}
