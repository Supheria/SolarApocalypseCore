package com.supheria.solar_apocalypse_core.config.solar;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import org.apache.commons.lang3.tuple.Pair;

/**
 * HUD 显示配置类（CLIENT 类型）
 * 使用 Forge Config API，自动生成 TOML 配置文件
 * 位置：${GAME_DIR}/config/solar_apocalypse_core-client.toml
 *
 * 配置项：
 * - posX / posY：HUD 在屏幕上的像素坐标（可通过游戏内拖拽调整）
 * - detailedMode：精简/详细模式切换
 * - enabled：是否启用 HUD 显示
 */
public class SolarHudConfig {

    public static final ForgeConfigSpec SPEC;
    public static final HudValues HUD_VALUES;

    static {
        Pair<HudValues, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder()
                .configure(HudValues::new);
        SPEC = pair.getRight();
        HUD_VALUES = pair.getLeft();
    }

    public static class HudValues {
        public final IntValue posX;
        public final IntValue posY;
        public final BooleanValue detailedMode;
        public final BooleanValue enabled;

        public HudValues(ForgeConfigSpec.Builder builder) {
            builder.comment("===============================");
            builder.comment("Solar Apocalypse Core - HUD Configuration");
            builder.comment("===============================");
            builder.comment("");
            builder.comment("Configure HUD display position and mode");
            builder.comment("");

            builder.push("hud");
            posX = builder
                    .comment("HUD X coordinate (pixels from left)")
                    .comment("Default: 4")
                    .defineInRange("posX", 4, 0, 10000);

            posY = builder
                    .comment("HUD Y coordinate (pixels from top)")
                    .comment("Default: 4")
                    .defineInRange("posY", 4, 0, 10000);

            detailedMode = builder
                    .comment("Detailed mode: true shows progress bar and phase name, false shows compact info only")
                    .comment("Default: false")
                    .define("detailedMode", false);

            enabled = builder
                    .comment("Enable HUD display")
                    .comment("Default: true")
                    .define("enabled", true);

            builder.pop();
        }
    }

    /**
     * 获取 HUD X 坐标
     */
    public static int getHudX() {
        return HUD_VALUES.posX.get();
    }

    /**
     * 获取 HUD Y 坐标
     */
    public static int getHudY() {
        return HUD_VALUES.posY.get();
    }

    /**
     * 是否启用详细模式
     */
    public static boolean isDetailedMode() {
        return HUD_VALUES.detailedMode.get();
    }

    /**
     * 是否启用 HUD
     */
    public static boolean isEnabled() {
        return HUD_VALUES.enabled.get();
    }
}
