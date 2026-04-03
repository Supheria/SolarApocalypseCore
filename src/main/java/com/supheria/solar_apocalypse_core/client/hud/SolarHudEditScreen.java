package com.supheria.solar_apocalypse_core.client.hud;

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

    private static final int HUD_WIDTH = 150;
    private static final int HUD_HEIGHT = 70;
    private static final int ELEMENT_BG_COLOR = 0xAA1F1F1F;
    private static final int ELEMENT_BORDER_COLOR = 0xFF00AAFF;
    private static final int TEXT_COLOR = 0xFFFFFF;

    private int hudX;
    private int hudY;
    private boolean isDragging = false;
    private int dragOffsetX = 0;
    private int dragOffsetY = 0;
    private boolean isDetailedMode;

    private long sampleDay = 27;
    private int samplePhase = SolarStageHelper.STAGE_4;
    private float sampleProgress = 0.75f;

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
            SolarHudConfig.HUD_VALUES.detailedMode.set(isDetailedMode);
        })
        .pos(this.width / 2 - 75, 50)
        .size(150, 20)
        .build());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // 绘制背景（半透明黑色）
        guiGraphics.fill(0, 0, this.width, this.height, 0xAA000000);

        // 绘制 HUD 元素和边框
        renderHudElement(guiGraphics);

        // 绘制提示文字
        String hint = "[拖动] 调整位置  |  [Esc] 保存退出";
        int hintWidth = this.font.width(hint);
        int hintX = (this.width - hintWidth) / 2;
        guiGraphics.drawString(this.font, hint, hintX, 10, TEXT_COLOR, false);

        // 绘制坐标显示
        String coordText = String.format("位置: X=%d Y=%d", hudX, hudY);
        guiGraphics.drawString(this.font, coordText, 10, 25, 0xFFAAAAAA, false);

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    /**
     * 绘制 HUD 预览元素
     */
    private void renderHudElement(GuiGraphics guiGraphics) {
        // 背景
        guiGraphics.fill(hudX - 1, hudY - 1, hudX + HUD_WIDTH + 1, hudY + HUD_HEIGHT + 1, ELEMENT_BG_COLOR);

        // 蓝色虚线边框（表示可拖动）
        // 上边
        guiGraphics.fill(hudX, hudY, hudX + HUD_WIDTH, hudY + 1, ELEMENT_BORDER_COLOR);
        // 下边
        guiGraphics.fill(hudX, hudY + HUD_HEIGHT, hudX + HUD_WIDTH, hudY + HUD_HEIGHT + 1, ELEMENT_BORDER_COLOR);
        // 左边
        guiGraphics.fill(hudX, hudY, hudX + 1, hudY + HUD_HEIGHT, ELEMENT_BORDER_COLOR);
        // 右边
        guiGraphics.fill(hudX + HUD_WIDTH, hudY, hudX + HUD_WIDTH + 1, hudY + HUD_HEIGHT, ELEMENT_BORDER_COLOR);

        // 绘制示例 HUD 内容
        String dayText = String.format("第%d天", sampleDay);
        String phaseText = getPhaseDisplayName(samplePhase);

        if (isDetailedMode) {
            // 详细模式
            guiGraphics.drawString(this.font, dayText, hudX + 5, hudY + 5, TEXT_COLOR, false);
            guiGraphics.drawString(this.font, phaseText, hudX + 5, hudY + 16, TEXT_COLOR, false);

            // 进度条背景
            int barX = hudX + 5;
            int barY = hudY + 28;
            int barWidth = 100;
            guiGraphics.fill(barX, barY, barX + barWidth, barY + 8, 0x88000000);

            // 进度条填充
            int fillWidth = (int) (barWidth * sampleProgress);
            guiGraphics.fill(barX, barY, barX + fillWidth, barY + 8, 0xFF00AA00);

            // 进度条边框
            guiGraphics.fill(barX - 1, barY - 1, barX + barWidth + 1, barY, 0xFF888888);
            guiGraphics.fill(barX - 1, barY + 8, barX + barWidth + 1, barY + 9, 0xFF888888);
            guiGraphics.fill(barX - 1, barY - 1, barX, barY + 9, 0xFF888888);
            guiGraphics.fill(barX + barWidth, barY - 1, barX + barWidth + 1, barY + 9, 0xFF888888);

            // 百分比
            String percentText = String.format("%.0f%%", sampleProgress * 100);
            guiGraphics.drawString(this.font, percentText, barX + barWidth + 5, barY + 1, TEXT_COLOR, false);
        } else {
            // 精简模式
            String compactText = dayText + " · " + phaseText;
            guiGraphics.drawString(this.font, compactText, hudX + 5, hudY + 5, TEXT_COLOR, false);
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
            if (mouseX >= hudX && mouseX <= hudX + HUD_WIDTH && mouseY >= hudY && mouseY <= hudY + HUD_HEIGHT) {
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
            hudX = Math.max(0, Math.min(hudX, this.width - HUD_WIDTH));
            hudY = Math.max(0, Math.min(hudY, this.height - HUD_HEIGHT));

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
        SolarHudConfig.HUD_VALUES.posX.set(hudX);
        SolarHudConfig.HUD_VALUES.posY.set(hudY);
    }

    /**
     * 获取阶段的显示名称（中文）
     */
    private String getPhaseDisplayName(int phase) {
        return SolarStageHelper.getDisplayName(phase);
    }

    @Override
    public boolean isPauseScreen() {
        return false; // 不暂停游戏
    }
}
