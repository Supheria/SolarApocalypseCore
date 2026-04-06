package com.supheria.solar_apocalypse_core.client.hud;

import com.supheria.solar_apocalypse_core.world.SolarStage;
import com.supheria.solar_apocalypse_core.world.SolarStageHelper;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.level.Level;

import com.supheria.solar_apocalypse_core.config.solar.SolarHudConfig;
import com.supheria.solar_apocalypse_core.network.SolarModVariables;

/**
 * HUD 渲染器
 * 在游戏界面上渲染当前天数和太阳阶段信息
 * 支持精简模式和详细模式
 */
@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class SolarHudRenderer {

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        if (!SolarHudConfig.isEnabled()) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        Level level = minecraft.level;

        if (level == null || !level.isClientSide()) {
            return;
        }

        GuiGraphics guiGraphics = event.getGuiGraphics();

        // 获取当前数据
        long dayTime = level.dayTime();
        long today = SolarStageHelper.getDayIndex(dayTime);

        SolarModVariables.MapVariables mapVars = SolarModVariables.MapVariables.get(level);
        SolarStage currentPhase = mapVars.getSolarStage();
        float progress = SolarStageHelper.getPhaseProgress(dayTime, currentPhase);

        // 获取配置信息
        int posX = SolarHudConfig.getHudX();
        int posY = SolarHudConfig.getHudY();
        boolean detailedMode = SolarHudConfig.isDetailedMode();

        if (detailedMode) {
            renderDetailedMode(guiGraphics, posX, posY, today, currentPhase, progress);
        } else {
            renderCompactMode(guiGraphics, posX, posY, today, currentPhase);
        }
    }

    /**
     * 精简模式：显示一行信息
     * 格式：第27天 · 危机期
     */
    private static void renderCompactMode(GuiGraphics guiGraphics, int posX, int posY, long today, SolarStage phase) {
        String dayText = String.format("第%d天", today);
        String phaseText = getPhaseDisplayName(phase);
        String compactText = dayText + " · " + phaseText;

        guiGraphics.drawString(Minecraft.getInstance().font, compactText, posX, posY, SolarHudConfig.getTextColor(), false);
    }

    /**
     * 详细模式：显示多行信息 + 进度条
     * 格式：
     *   第27天
     *   危机期
     *   [████████░░] 78% → 终极期
     */
    private static void renderDetailedMode(GuiGraphics guiGraphics, int posX, int posY, long today, SolarStage phase, float progress) {
        Minecraft minecraft = Minecraft.getInstance();

        // 第一行：天数
        String dayText = String.format("第%d天", today);
        guiGraphics.drawString(minecraft.font, dayText, posX, posY, SolarHudConfig.getTextColor(), false);

        // 第二行：当前阶段
        String phaseText = getPhaseDisplayName(phase);
        guiGraphics.drawString(minecraft.font, phaseText, posX, posY + SolarHudConfig.getHudLineHeight(), SolarHudConfig.getTextColor(), false);

        // 第三行：进度条 + 信息
        int barY = posY + SolarHudConfig.getHudDetailBarOffsetY();

        // 进度条背景
        guiGraphics.fill(posX, barY, posX + SolarHudConfig.getProgressBarWidth(), barY + SolarHudConfig.getProgressBarHeight(), SolarHudConfig.getProgressBarBackgroundColor());

        // 进度条填充
        int fillWidth = (int) (SolarHudConfig.getProgressBarWidth() * progress);
        if (fillWidth > 0) {
            guiGraphics.fill(posX, barY, posX + fillWidth, barY + SolarHudConfig.getProgressBarHeight(), SolarHudConfig.getProgressBarFillColor());
        }

        // 进度条边框
        int progressBarWidth = SolarHudConfig.getProgressBarWidth();
        int progressBarHeight = SolarHudConfig.getProgressBarHeight();
        int progressBarBorderColor = SolarHudConfig.getProgressBarBorderColor();
        guiGraphics.fill(posX - 1, barY - 1, posX + progressBarWidth + 1, barY, progressBarBorderColor);
        guiGraphics.fill(posX - 1, barY + progressBarHeight, posX + progressBarWidth + 1, barY + progressBarHeight + 1, progressBarBorderColor);
        guiGraphics.fill(posX - 1, barY - 1, posX, barY + progressBarHeight + 1, progressBarBorderColor);
        guiGraphics.fill(posX + progressBarWidth, barY - 1, posX + progressBarWidth + 1, barY + progressBarHeight + 1, progressBarBorderColor);

        // 百分比文字
        String percentText = String.format("%.0f%%", progress * 100);
        guiGraphics.drawString(minecraft.font, percentText, posX + progressBarWidth + SolarHudConfig.getHudPercentTextOffsetX(), barY + 1, SolarHudConfig.getTextColor(), false);

        // 下一阶段信息
        SolarStage nextPhase = getNextPhase(phase);
        if (nextPhase != phase) {
            String arrowAndPhase = " → " + getPhaseDisplayName(nextPhase);
            guiGraphics.drawString(minecraft.font, arrowAndPhase, posX + progressBarWidth + SolarHudConfig.getHudNextPhaseTextOffsetX(), barY + 1, SolarHudConfig.getMutedTextColor(), false);
        }
    }

    /**
     * 获取阶段的显示名称（中文）
     */
    private static String getPhaseDisplayName(SolarStage phase) {
        return phase.getDisplayName().getString();
    }

    /**
     * 获取下一个阶段
     */
    private static SolarStage getNextPhase(SolarStage current) {
        if (current != SolarStage.STAGE_6) {
            return SolarStage.values()[current.ordinal() + 1];
        }
        return current;
    }
}
