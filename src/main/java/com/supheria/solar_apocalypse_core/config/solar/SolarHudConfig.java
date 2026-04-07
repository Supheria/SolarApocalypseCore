package com.supheria.solar_apocalypse_core.config.solar;

/**
 * 太阳 HUD 的样式常量。
 */
public final class SolarHudConfig {

    private static final boolean DEFAULT_ENABLED = true;

    private static final int TEXT_COLOR = 0xFFFFFF;
    private static final int PROGRESS_BAR_BG = 0x88000000;
    private static final int PROGRESS_BAR_FILL = 0xFF00AA00;
    private static final int PROGRESS_BAR_BORDER = 0xFF888888;

    private static final int PROGRESS_BAR_HEIGHT = 8;
    private static final int PROGRESS_BAR_WIDTH = 100;
    private static final int HUD_LINE_HEIGHT = 11;
    private static final int HUD_DETAIL_BAR_OFFSET_Y = 22;
    private static final int HUD_MARGIN_LEFT = 4;
    private static final int HUD_MARGIN_BOTTOM = 4;
    private static final int HUD_PERCENT_TEXT_OFFSET_X = 5;
    private static boolean enabled = DEFAULT_ENABLED;

    public static boolean isEnabled() {
        return enabled;
    }

    public static int getTextColor() {
        return TEXT_COLOR;
    }

    public static int getProgressBarBackgroundColor() {
        return PROGRESS_BAR_BG;
    }

    public static int getProgressBarFillColor() {
        return PROGRESS_BAR_FILL;
    }

    public static int getProgressBarBorderColor() {
        return PROGRESS_BAR_BORDER;
    }

    public static int getProgressBarHeight() {
        return PROGRESS_BAR_HEIGHT;
    }

    public static int getProgressBarWidth() {
        return PROGRESS_BAR_WIDTH;
    }

    public static int getHudLineHeight() {
        return HUD_LINE_HEIGHT;
    }

    public static int getHudDetailBarOffsetY() {
        return HUD_DETAIL_BAR_OFFSET_Y;
    }

    public static int getHudMarginLeft() {
        return HUD_MARGIN_LEFT;
    }

    public static int getHudMarginBottom() {
        return HUD_MARGIN_BOTTOM;
    }

    public static int getHudPercentTextOffsetX() {
        return HUD_PERCENT_TEXT_OFFSET_X;
    }

    private SolarHudConfig() {}
}
