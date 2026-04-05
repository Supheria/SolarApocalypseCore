package com.supheria.solar_apocalypse_core.client.hud;

import com.supheria.solar_apocalypse_core.world.SolarStage;
import com.supheria.solar_apocalypse_core.world.SolarStageHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import com.supheria.solar_apocalypse_core.config.solar.SolarHudConfig;

/**
 * HUD 编辑界面
 * 允许玩家通过拖拽调整 HUD 位置，Esc 或点击界面外保存
 */
public class SolarHudEditScreen extends Screen {

    private int hudX;
    private int hudY;
    private boolean isDragging = false;
    private int dragOffsetX = 0;
    private int dragOffsetY = 0;
    private boolean isDetailedMode;

    private long sampleDay = SolarHudConfig.getSampleDay();
    private SolarStage samplePhase = SolarStage.STAGE_4;
    private float sampleProgress = SolarHudConfig.getSampleProgress();

    private Button toggleModeButton;

    public SolarHudEditScreen() {
        super(Component.literal("HUD 编辑模式"));
        this.hudX = SolarHudConfig.getHudX();
        this.hudY = SolarHudConfig.getHudY();
        this.isDetailedMode = SolarHudConfig.isDetailedMode();
    }

    @Override
    protected void init() {
        super.init();

        // 添加模式切换按钮
        String modeText = isDetailedMode ? "详细模式 ON" : "精简模式 ON";
        toggleModeButton = this.addRenderableWidget(new Button.Builder(Component.literal(modeText), button -> {
            isDetailedMode = !isDetailedMode;
            String newText = isDetailedMode ? "详细模式 ON" : "精简模式 ON";
            button.setMessage(Component.literal(newText));
            SolarHudConfig.setDetailedMode(isDetailedMode);
        })
        .pos(this.width / 2 - SolarHudConfig.getEditorButtonWidth() / 2, SolarHudConfig.getEditorButtonOffsetY())
        .size(SolarHudConfig.getEditorButtonWidth(), SolarHudConfig.getEditorButtonHeight())
        .build());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // 绘制背景（半透明黑色）
        guiGraphics.fill(0, 0, this.width, this.height, SolarHudConfig.getEditorOverlayColor());

        // 绘制 HUD 元素和边框
        renderHudElement(guiGraphics);

        // 绘制提示文字
        String hint = "[拖动] 调整位置  |  [Esc] 保存退出";
        int hintWidth = this.font.width(hint);
        int hintX = (this.width - hintWidth) / 2;
        guiGraphics.drawString(this.font, hint, hintX, 10, SolarHudConfig.getTextColor(), false);

        // 绘制坐标显示
        String coordText = String.format("位置: X=%d Y=%d", hudX, hudY);
        guiGraphics.drawString(this.font, coordText, 10, 25, SolarHudConfig.getMutedTextColor(), false);

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    /**
     * 绘制 HUD 预览元素
     */
    private void renderHudElement(GuiGraphics guiGraphics) {
        // 背景
        int editorWidth = SolarHudConfig.getEditorWidth();
        int editorHeight = SolarHudConfig.getEditorHeight();
        guiGraphics.fill(hudX - 1, hudY - 1, hudX + editorWidth + 1, hudY + editorHeight + 1, SolarHudConfig.getEditorBackgroundColor());

        // 蓝色虚线边框（表示可拖动）
        // 上边
        int editorBorderColor = SolarHudConfig.getEditorBorderColor();
        guiGraphics.fill(hudX, hudY, hudX + editorWidth, hudY + 1, editorBorderColor);
        // 下边
        guiGraphics.fill(hudX, hudY + editorHeight, hudX + editorWidth, hudY + editorHeight + 1, editorBorderColor);
        // 左边
        guiGraphics.fill(hudX, hudY, hudX + 1, hudY + editorHeight, editorBorderColor);
        // 右边
        guiGraphics.fill(hudX + editorWidth, hudY, hudX + editorWidth + 1, hudY + editorHeight, editorBorderColor);

        // 绘制示例 HUD 内容
        String dayText = String.format("第%d天", sampleDay);
        String phaseText = getPhaseDisplayName(samplePhase);

        if (isDetailedMode) {
            // 详细模式
            int textPadding = SolarHudConfig.getHudTextPadding();
            int lineHeight = SolarHudConfig.getHudLineHeight();
            int progressBarWidth = SolarHudConfig.getProgressBarWidth();
            int progressBarHeight = SolarHudConfig.getProgressBarHeight();
            int progressBarBorderColor = SolarHudConfig.getProgressBarBorderColor();
            guiGraphics.drawString(this.font, dayText, hudX + textPadding, hudY + textPadding, SolarHudConfig.getTextColor(), false);
            guiGraphics.drawString(this.font, phaseText, hudX + textPadding, hudY + textPadding + lineHeight, SolarHudConfig.getTextColor(), false);

            // 进度条背景
            int barX = hudX + textPadding;
            int barY = hudY + SolarHudConfig.getHudPreviewBarOffsetY();
            guiGraphics.fill(barX, barY, barX + progressBarWidth, barY + progressBarHeight, SolarHudConfig.getProgressBarBackgroundColor());

            // 进度条填充
            int fillWidth = (int) (progressBarWidth * sampleProgress);
            guiGraphics.fill(barX, barY, barX + fillWidth, barY + progressBarHeight, SolarHudConfig.getProgressBarFillColor());

            // 进度条边框
            guiGraphics.fill(barX - 1, barY - 1, barX + progressBarWidth + 1, barY, progressBarBorderColor);
            guiGraphics.fill(barX - 1, barY + progressBarHeight, barX + progressBarWidth + 1, barY + progressBarHeight + 1, progressBarBorderColor);
            guiGraphics.fill(barX - 1, barY - 1, barX, barY + progressBarHeight + 1, progressBarBorderColor);
            guiGraphics.fill(barX + progressBarWidth, barY - 1, barX + progressBarWidth + 1, barY + progressBarHeight + 1, progressBarBorderColor);

            // 百分比
            String percentText = String.format("%.0f%%", sampleProgress * 100);
            guiGraphics.drawString(this.font, percentText, barX + progressBarWidth + SolarHudConfig.getHudPercentTextOffsetX(), barY + 1, SolarHudConfig.getTextColor(), false);
        } else {
            // 精简模式
            String compactText = dayText + " · " + phaseText;
            guiGraphics.drawString(this.font, compactText, hudX + SolarHudConfig.getHudTextPadding(), hudY + SolarHudConfig.getHudTextPadding(), SolarHudConfig.getTextColor(), false);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double pDelta) {

        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) { // 左键
            // 检查是否点击在 HUD 元素范围内
            if (mouseX >= hudX && mouseX <= hudX + SolarHudConfig.getEditorWidth() && mouseY >= hudY && mouseY <= hudY + SolarHudConfig.getEditorHeight()) {
                isDragging = true;
                dragOffsetX = (int) (mouseX - hudX);
                dragOffsetY = (int) (mouseY - hudY);
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            isDragging = false;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (isDragging) {
            hudX = (int) (mouseX - dragOffsetX);
            hudY = (int) (mouseY - dragOffsetY);

            // 限制范围，避免 HUD 超出屏幕
            hudX = Math.max(0, Math.min(hudX, this.width - SolarHudConfig.getEditorWidth()));
            hudY = Math.max(0, Math.min(hudY, this.height - SolarHudConfig.getEditorHeight()));

            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public void onClose() {
        // 保存坐标到配置
        savePositionToConfig();
        super.onClose();
    }

    /**
     * 保存位置到配置文件
     */
    private void savePositionToConfig() {
        SolarHudConfig.setHudX(hudX);
        SolarHudConfig.setHudY(hudY);
    }

    /**
     * 获取阶段的显示名称（中文）
     */
    private String getPhaseDisplayName(SolarStage phase) {
        return phase.getDisplayName().getString();
    }

    @Override
    public boolean isPauseScreen() {
        return false; // 不暂停游戏
    }
}
