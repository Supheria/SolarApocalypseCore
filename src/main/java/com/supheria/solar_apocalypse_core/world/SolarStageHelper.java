package com.supheria.solar_apocalypse_core.world;

import com.supheria.solar_apocalypse_core.config.solar.SolarStageConfig;

/**
 * 太阳阶段计算工具类
 * 提供时间计算、进度查询等核心逻辑
 *
 * 所有方法返回或接受 SolarStage 枚举
 */
public class SolarStageHelper {

    /**
     * 根据游戏时间获取当前阶段
     * 前4个阶段自动平均分配到 stage5StartTime 前
     *
     * @param dayTime 游戏时间（ticks）
     * @return 对应的阶段
     */
    public static SolarStage getPhaseByDayTime(long dayTime) {
        long stage5Start = SolarStageConfig.SOLAR_STAGE_VALUES.stage5StartTime.get();
        long stage6Start = SolarStageConfig.SOLAR_STAGE_VALUES.stage6StartTime.get();

        if (dayTime < stage5Start) {
            // 前4个阶段平均分配
            long perPhase = stage5Start / 4;
            if (dayTime < perPhase) {
                return SolarStage.STAGE_1;
            } else if (dayTime < perPhase * 2) {
                return SolarStage.STAGE_2;
            } else if (dayTime < perPhase * 3) {
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

    /**
     * 获取指定阶段的时间范围 [startTime, endTime)
     * 用于太阳渲染的进度计算
     *
     * @param stage 阶段
     * @return [startTime, endTime] 时间范围数组
     */
    public static long[] getPhaseTimeRange(SolarStage stage) {
        long stage5Start = SolarStageConfig.SOLAR_STAGE_VALUES.stage5StartTime.get();
        long stage6Start = SolarStageConfig.SOLAR_STAGE_VALUES.stage6StartTime.get();
        long perPhase = stage5Start / 4;

        return switch (stage) {
            case STAGE_1 -> new long[]{0, perPhase};
            case STAGE_2 -> new long[]{perPhase, perPhase * 2};
            case STAGE_3 -> new long[]{perPhase * 2, perPhase * 3};
            case STAGE_4 -> new long[]{perPhase * 3, stage5Start};
            case STAGE_5 -> new long[]{stage5Start, stage6Start};
            case STAGE_6 -> new long[]{stage6Start, Long.MAX_VALUE};
            case NONE -> new long[]{0, 0};
        };
    }

    /**
     * 获取当前阶段在总时间中的进度 (0.0 - 1.0)
     * 用于太阳渲染的连续变化
     *
     * @param dayTime 游戏时间
     * @param stage 阶段
     * @return 进度值 0.0-1.0
     */
    public static float getPhaseProgress(long dayTime, SolarStage stage) {
        long[] timeRange = getPhaseTimeRange(stage);
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
     * 获取指定阶段的随机刻度等级
     * 用于影响方块随机更新频率
     *
     * @param stage 阶段
     * @return 随机刻度等级
     */
    public static int getRandomTickingLevel(SolarStage stage) {
        return SolarStageConfig.SOLAR_STAGE_VALUES.getRandomTickingLevel(stage);
    }
}
