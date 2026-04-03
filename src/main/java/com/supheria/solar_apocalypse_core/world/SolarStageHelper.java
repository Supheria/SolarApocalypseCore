package com.supheria.solar_apocalypse_core.world;

import com.supheria.solar_apocalypse_core.config.solar.SolarStageConfig;

/**
 * 太阳阶段辅助工具类
 * 提供阶段判定、时间计算、进度查询等功能
 *
 * 阶段常量：
 * 0 - 未初始化
 * 1 - 爆发初期
 * 2 - 爆发前期
 * 3 - 爆发中期
 * 4 - 爆发后期
 * 5 - 终极期
 * 6 - 恒冬期
 */
public class SolarStageHelper {
    // 阶段常量定义
    public static final int STAGE_NONE = 0;
    public static final int STAGE_1 = 1;
    public static final int STAGE_2 = 2;
    public static final int STAGE_3 = 3;
    public static final int STAGE_4 = 4;
    public static final int STAGE_5 = 5;
    public static final int STAGE_6 = 6;

    /**
     * 根据游戏时间获取当前阶段
     * 前4个阶段自动平均分配到 stage5StartTime 前
     */
    public static int getPhaseByDayTime(long dayTime) {
        long stage5Start = SolarStageConfig.SOLAR_STAGE_VALUES.stage5StartTime.get();
        long stage6Start = SolarStageConfig.SOLAR_STAGE_VALUES.stage6StartTime.get();

        if (dayTime < stage5Start) {
            // 前4个阶段平均分配
            long perPhase = stage5Start / 4;
            if (dayTime < perPhase) {
                return STAGE_1;
            } else if (dayTime < perPhase * 2) {
                return STAGE_2;
            } else if (dayTime < perPhase * 3) {
                return STAGE_3;
            } else {
                return STAGE_4;
            }
        } else if (dayTime < stage6Start) {
            return STAGE_5;
        } else {
            return STAGE_6;
        }
    }

    /**
     * 获取指定阶段的时间范围 [startTime, endTime)
     * 用于太阳渲染的进度计算
     */
    public static long[] getPhaseTimeRange(int phase) {
        long stage5Start = SolarStageConfig.SOLAR_STAGE_VALUES.stage5StartTime.get();
        long stage6Start = SolarStageConfig.SOLAR_STAGE_VALUES.stage6StartTime.get();
        long perPhase = stage5Start / 4;

        return switch (phase) {
            case STAGE_1 -> new long[]{0, perPhase};
            case STAGE_2 -> new long[]{perPhase, perPhase * 2};
            case STAGE_3 -> new long[]{perPhase * 2, perPhase * 3};
            case STAGE_4 -> new long[]{perPhase * 3, stage5Start};
            case STAGE_5 -> new long[]{stage5Start, stage6Start};
            case STAGE_6 -> new long[]{stage6Start, Long.MAX_VALUE};
            case STAGE_NONE -> new long[]{0, 0};
            default -> new long[]{0, 0};
        };
    }

    /**
     * 判断是否为爆发阶段（1-5）
     */
    public static boolean isEruptionPhase(int phase) {
        return phase > 0 && phase <= 5;
    }

    /**
     * 判断是否为坍缩阶段
     */
    public static boolean isCollapsePhase(int phase) {
        return phase == STAGE_6;
    }

    /**
     * 获取爆发等级 (1-5)
     * 用于伤害计算和太阳渲染进度
     */
    public static int getEruptionLevel(int phase) {
        return Math.min(phase, 5);
    }

    /**
     * 获取当前阶段在总时间中的进度 (0.0 - 1.0)
     * 用于太阳渲染的连续变化
     */
    public static float getPhaseProgress(long dayTime, int phase) {
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

    /**
     * 获取阶段的中文显示名称
     */
    public static String getDisplayName(int stage) {
        return switch (stage) {
            case STAGE_1 -> "爆发初期";
            case STAGE_2 -> "爆发前期";
            case STAGE_3 -> "爆发中期";
            case STAGE_4 -> "爆发后期";
            case STAGE_5 -> "终极期";
            case STAGE_6 -> "恒冬期";
            case STAGE_NONE -> "未初始化";
            default -> "未知";
        };
    }
}
