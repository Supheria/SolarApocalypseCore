package com.supheria.solar_apocalypse_core.config.solar;

/**
 * 太阳 HUD 的内存态配置与样式常量。
 *
 * <p>这里同时承载三类信息：默认显示状态、运行时可变的位置/模式开关，以及 HUD/编辑器绘制时使用的固定尺寸与颜色。
 * 当前实现不再依赖 Forge Config 或 TOML，设置仅在本次运行期间保存在内存中。
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

    /** 当前 HUD 左上角锚点 X，允许在编辑界面中调整。 */
    private static int hudX = DEFAULT_HUD_X;
    /** 当前 HUD 左上角锚点 Y，允许在编辑界面中调整。 */
    private static int hudY = DEFAULT_HUD_Y;
    /** 是否显示带进度条与更多文本的详细模式。 */
    private static boolean detailedMode = DEFAULT_DETAILED_MODE;
    /** HUD 总开关。当前实现默认启用，且没有持久化到外部配置。 */
    private static boolean enabled = DEFAULT_ENABLED;

    public static int getHudX() {
        return hudX;
    }

    /**
     * 更新 HUD 横向位置，并钳制为非负值，避免拖出屏幕左侧。
     */
    public static void setHudX(int value) {
        hudX = Math.max(0, value);
    }

    public static int getHudY() {
        return hudY;
    }

    /**
     * 更新 HUD 纵向位置，并钳制为非负值，避免拖出屏幕顶部。
     */
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

    /**
     * 编辑/预览界面使用的示例天数，不代表真实世界进度。
     */
    public static long getSampleDay() {
        return SAMPLE_DAY;
    }

    /**
     * 编辑/预览界面使用的示例阶段进度，用于稳定展示进度条效果。
     */
    public static float getSampleProgress() {
        return SAMPLE_PROGRESS;
    }

    private SolarHudConfig() {}
}
