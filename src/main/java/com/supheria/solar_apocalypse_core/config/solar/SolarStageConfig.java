package com.supheria.solar_apocalypse_core.config.solar;

import com.supheria.solar_apocalypse_core.world.SolarStage;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import org.apache.commons.lang3.tuple.Pair;

/**
 * 太阳爆发阶段配置类
 * 使用 Forge Config API，自动生成 TOML 配置文件
 * 位置：${GAME_DIR}/config/solar/solar_apocalypse_core-common.toml
 *
 * 配置策略：
 * - 玩家仅需配置两个时间点：stage5StartTime 和 stage6StartTime
 * - 前4个阶段自动平均分配到 stage5StartTime 之前
 * - 随机刻度等级是固定的，随阶段递增
 */
public class SolarStageConfig {

    public static final ForgeConfigSpec SPEC;
    public static final SolarStageValues SOLAR_STAGE_VALUES;

    static {
        Pair<SolarStageValues, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder()
                .configure(SolarStageValues::new);
        SPEC = pair.getRight();
        SOLAR_STAGE_VALUES = pair.getLeft();
    }

    public static class SolarStageValues {
        // 阶段转换时间配置
        public final IntValue stage5StartTime;
        public final IntValue stage6StartTime;

        // 第六阶段特性配置
        public final IntValue collapseMaxSnowLayer;
        public final IntValue collapseSnowAccumulationRate;
        public final DoubleValue collapseDayBrightnessFactor;
        public final IntValue collapseBlockTransformRate;

        // 固定的随机刻度等级（随阶段递增）
        public static final int[] RANDOM_TICKING_LEVELS = {4, 5, 8, 9, 10, 10};

        public SolarStageValues(ForgeConfigSpec.Builder builder) {
            builder.comment("===============================");
            builder.comment("Solar Apocalypse Core - Stage Configuration");
            builder.comment("===============================");
            builder.comment("");
            builder.comment("Strategy: Only configure two time points");
            builder.comment("- Stage 1-4: Automatically distributed before stage5StartTime");
            builder.comment("- Stage 5: From stage5StartTime to stage6StartTime");
            builder.comment("- Stage 6: From stage6StartTime onwards");
            builder.comment("");

            builder.push("timing");
            stage5StartTime = builder
                    .comment("Stage 5 (Ultimate) start time in ticks")
                    .comment("Default: 648000 = 27 Minecraft days")
                    .defineInRange("stage5StartTime", 648000, 1, Integer.MAX_VALUE);

            stage6StartTime = builder
                    .comment("Stage 6 (Collapse/Eternal Winter) start time in ticks")
                    .comment("Default: 960000 = 40 Minecraft days")
                    .defineInRange("stage6StartTime", 960000, 1, Integer.MAX_VALUE);
            builder.pop();

            builder.push("collapse_effects");
            collapseMaxSnowLayer = builder
                    .comment("Maximum snow layer depth in Stage 6 (1-8 layers)")
                    .comment("Each layer = 1/8 block height")
                    .defineInRange("maxSnowLayer", 6, 1, 8);

            collapseSnowAccumulationRate = builder
                    .comment("Snow accumulation tick interval in Stage 6")
                    .comment("Lower = faster accumulation, 100 = default")
                    .defineInRange("snowAccumulationRate", 100, 1, 1000);

            collapseDayBrightnessFactor = builder
                    .comment("Daytime brightness factor in Stage 6 (0.0-1.0)")
                    .comment("0.3 = 30% brightness, allows mob spawning")
                    .defineInRange("dayBrightnessFactor", 0.3, 0.0, 1.0);

            collapseBlockTransformRate = builder
                    .comment("Block transformation speed multiplier in Stage 6")
                    .comment("Higher = faster transformation, 2 = default")
                    .defineInRange("blockTransformRate", 2, 1, 10);
            builder.pop();
        }

        /**
         * 获取指定阶段（SolarStage）的随机刻度等级
         */
        public int getRandomTickingLevel(SolarStage stage) {
            if (stage == null) {
                return RANDOM_TICKING_LEVELS[0];
            }
            return RANDOM_TICKING_LEVELS[stage.ordinal() - 1];
        }
    }

    /**
     * 获取指定阶段（SolarStage）的随机刻度等级
     */
    public static int getRandomTickingLevel(SolarStage stage) {
        return SOLAR_STAGE_VALUES.getRandomTickingLevel(stage);
    }
}
