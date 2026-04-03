package com.supheria.solar_apocalypse_core.client.hud;

import com.supheria.solar_apocalypse_core.world.SolarStageHelper;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.level.Level;

import com.supheria.solar_apocalypse_core.config.solar.SolarHudConfig;
import com.supheria.solar_apocalypse_core.network.SapModVariables;

/**
 * HUD 渲染器
 * 在游戏界面上渲染当前天数和太阳阶段信息
 * 支持精简模式和详细模式
 */
@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class SolarHudRenderer {

    private static final int TEXT_COLOR = 0xFFFFFF;         // 白色
    private static final int PROGRESS_BAR_BG = 0x88000000;   // 半透明黑色
    private static final int PROGRESS_BAR_FILL = 0xFF00AA00;  // 绿色
    private static final int PROGRESS_BAR_HEIGHT = 8;
    private static final int PROGRESS_BAR_WIDTH = 100;

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
        long today = dayTime / 24000;

        SapModVariables.MapVariables mapVars = SapModVariables.MapVariables.get(level);
        int currentPhase = mapVars.getCurrentStage();
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
    private static void renderCompactMode(GuiGraphics guiGraphics, int posX, int posY, long today, int phase) {
        String dayText = String.format("第%d天", today);
        String phaseText = getPhaseDisplayName(phase);
        String compactText = dayText + " · " + phaseText;

        guiGraphics.drawString(Minecraft.getInstance().font, compactText, posX, posY, TEXT_COLOR, false);
    }

    /**
     * 详细模式：显示多行信息 + 进度条
     * 格式：
     *   第27天
     *   危机期
     *   [████████░░] 78% → 终极期
     */
    private static void renderDetailedMode(GuiGraphics guiGraphics, int posX, int posY, long today, int phase, float progress) {
        Minecraft minecraft = Minecraft.getInstance();

        // 第一行：天数
        String dayText = String.format("第%d天", today);
        guiGraphics.drawString(minecraft.font, dayText, posX, posY, TEXT_COLOR, false);

        // 第二行：当前阶段
        String phaseText = getPhaseDisplayName(phase);
        guiGraphics.drawString(minecraft.font, phaseText, posX, posY + 11, TEXT_COLOR, false);

        // 第三行：进度条 + 信息
        int barY = posY + 22;

        // 进度条背景
        guiGraphics.fill(posX, barY, posX + PROGRESS_BAR_WIDTH, barY + PROGRESS_BAR_HEIGHT, PROGRESS_BAR_BG);

        // 进度条填充
        int fillWidth = (int) (PROGRESS_BAR_WIDTH * progress);
        if (fillWidth > 0) {
            guiGraphics.fill(posX, barY, posX + fillWidth, barY + PROGRESS_BAR_HEIGHT, PROGRESS_BAR_FILL);
        }

        // 进度条边框
        guiGraphics.fill(posX - 1, barY - 1, posX + PROGRESS_BAR_WIDTH + 1, barY, 0xFF888888);
        guiGraphics.fill(posX - 1, barY + PROGRESS_BAR_HEIGHT, posX + PROGRESS_BAR_WIDTH + 1, barY + PROGRESS_BAR_HEIGHT + 1, 0xFF888888);
        guiGraphics.fill(posX - 1, barY - 1, posX, barY + PROGRESS_BAR_HEIGHT + 1, 0xFF888888);
        guiGraphics.fill(posX + PROGRESS_BAR_WIDTH, barY - 1, posX + PROGRESS_BAR_WIDTH + 1, barY + PROGRESS_BAR_HEIGHT + 1, 0xFF888888);

        // 百分比文字
        String percentText = String.format("%.0f%%", progress * 100);
        guiGraphics.drawString(minecraft.font, percentText, posX + PROGRESS_BAR_WIDTH + 5, barY + 1, TEXT_COLOR, false);

        // 下一阶段信息
        int nextPhase = getNextPhase(phase);
        if (nextPhase != phase) {
            String arrowAndPhase = " → " + getPhaseDisplayName(nextPhase);
            guiGraphics.drawString(minecraft.font, arrowAndPhase, posX + PROGRESS_BAR_WIDTH + 40, barY + 1, 0xFFAAAAAA, false);
        }
    }

    /**
     * 获取阶段的显示名称（中文）
     */
    private static String getPhaseDisplayName(int phase) {
        return SolarStageHelper.getDisplayName(phase);
    }

    /**
     * 获取下一个阶段
     */
    private static int getNextPhase(int current) {
        if (current < SolarStageHelper.STAGE_6) {
            return current + 1;
        }
        return current;
    }
}
