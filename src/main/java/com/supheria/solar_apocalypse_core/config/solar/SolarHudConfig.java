package com.supheria.solar_apocalypse_core.config.solar;

/**
 * HUD 显示配置常量。
 * 不再依赖 Forge Config / TOML，运行时仅保存在内存中。
 */
public final class SolarHudConfig {

    private static final int DEFAULT_HUD_X = 4;
    private static final int DEFAULT_HUD_Y = 4;
    private static final boolean DEFAULT_DETAILED_MODE = false;
    private static final boolean DEFAULT_ENABLED = true;

    private static final int TEXT_COLOR = 0xFFFFFF;
    private static final int MUTED_TEXT_COLOR = 0xFFAAAAAA;
    private static final int PROGRESS_BAR_BG = 0x88000000;
    private static final int PROGRESS_BAR_FILL = 0xFF00AA00;
    private static final int PROGRESS_BAR_BORDER = 0xFF888888;
    private static final int EDITOR_BG_COLOR = 0xAA1F1F1F;
    private static final int EDITOR_BORDER_COLOR = 0xFF00AAFF;
    private static final int EDITOR_OVERLAY_COLOR = 0xAA000000;

    private static final int PROGRESS_BAR_HEIGHT = 8;
    private static final int PROGRESS_BAR_WIDTH = 100;
    private static final int HUD_LINE_HEIGHT = 11;
    private static final int HUD_DETAIL_BAR_OFFSET_Y = 22;
    private static final int HUD_TEXT_PADDING = 5;
    private static final int HUD_PERCENT_TEXT_OFFSET_X = 5;
    private static final int HUD_NEXT_PHASE_TEXT_OFFSET_X = 40;
    private static final int EDITOR_WIDTH = 150;
    private static final int EDITOR_HEIGHT = 70;
    private static final int EDITOR_BUTTON_WIDTH = 150;
    private static final int EDITOR_BUTTON_HEIGHT = 20;
    private static final int EDITOR_BUTTON_OFFSET_Y = 50;
    private static final int HUD_PREVIEW_BAR_OFFSET_Y = 28;
    private static final long SAMPLE_DAY = 27L;
    private static final float SAMPLE_PROGRESS = 0.75F;

    private static int hudX = DEFAULT_HUD_X;
    private static int hudY = DEFAULT_HUD_Y;
    private static boolean detailedMode = DEFAULT_DETAILED_MODE;
    private static boolean enabled = DEFAULT_ENABLED;

    public static int getHudX() {
        return hudX;
    }

    public static void setHudX(int value) {
        hudX = Math.max(0, value);
    }

    public static int getHudY() {
        return hudY;
    }

    public static void setHudY(int value) {
        hudY = Math.max(0, value);
    }

    public static boolean isDetailedMode() {
        return detailedMode;
    }

    public static void setDetailedMode(boolean value) {
        detailedMode = value;
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static int getTextColor() {
        return TEXT_COLOR;
    }

    public static int getMutedTextColor() {
        return MUTED_TEXT_COLOR;
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

    public static int getEditorBackgroundColor() {
        return EDITOR_BG_COLOR;
    }

    public static int getEditorOverlayColor() {
        return EDITOR_OVERLAY_COLOR;
    }

    public static int getEditorBorderColor() {
        return EDITOR_BORDER_COLOR;
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

    public static int getHudTextPadding() {
        return HUD_TEXT_PADDING;
    }

    public static int getHudPercentTextOffsetX() {
        return HUD_PERCENT_TEXT_OFFSET_X;
    }

    public static int getHudNextPhaseTextOffsetX() {
        return HUD_NEXT_PHASE_TEXT_OFFSET_X;
    }

    public static int getEditorWidth() {
        return EDITOR_WIDTH;
    }

    public static int getEditorHeight() {
        return EDITOR_HEIGHT;
    }

    public static int getEditorButtonWidth() {
        return EDITOR_BUTTON_WIDTH;
    }

    public static int getEditorButtonHeight() {
        return EDITOR_BUTTON_HEIGHT;
    }

    public static int getEditorButtonOffsetY() {
        return EDITOR_BUTTON_OFFSET_Y;
    }

    public static int getHudPreviewBarOffsetY() {
        return HUD_PREVIEW_BAR_OFFSET_Y;
    }

    public static long getSampleDay() {
        return SAMPLE_DAY;
    }

    public static float getSampleProgress() {
        return SAMPLE_PROGRESS;
    }

    private SolarHudConfig() {}
}
