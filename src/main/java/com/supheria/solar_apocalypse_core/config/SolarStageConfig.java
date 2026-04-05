package com.supheria.solar_apocalypse_core.config;

import com.supheria.solar_apocalypse_core.world.SolarStage;

/**
 * 兼容旧包路径的阶段常量入口。
 */
public final class SolarStageConfig {

    public static int getStage5StartTime() {
        return com.supheria.solar_apocalypse_core.config.solar.SolarStageConfig.getStage5StartTime();
    }

    public static int getStage6StartTime() {
        return com.supheria.solar_apocalypse_core.config.solar.SolarStageConfig.getStage6StartTime();
    }

    public static int getRandomTickingLevel(SolarStage stage) {
        return com.supheria.solar_apocalypse_core.config.solar.SolarStageConfig.getRandomTickingLevel(stage);
    }

    private SolarStageConfig() {}
}
