package com.supheria.solar_apocalypse_core.client;

import com.supheria.solar_apocalypse_core.world.SolarStage;
import com.supheria.solar_apocalypse_core.world.SolarStageHelper;
import net.minecraft.resources.ResourceLocation;

/**
 * 太阳渲染辅助类
 * 根据阶段和时间计算太阳的渲染属性
 * 包括大小、纹理、透明度等
 */
public class SunRenderHelper {

    /**
     * 获取当前阶段在其时间范围内的进度 (0.0 - 1.0)
     * 用于太阳大小、颜色等属性的平滑变化
     */
    public static float getSunSizeProgress(long dayTime, SolarStage phase) {
        return SolarStageHelper.getPhaseProgress(dayTime, phase);
    }

    /**
     * 根据阶段获取对应的太阳纹理
     */
    public static ResourceLocation getSunTexture(SolarStage phase) {
        return switch (phase) {
            case STAGE_1 -> new ResourceLocation("solar_apocalypse_core", "textures/sun/stage1.png");
            case STAGE_2 -> new ResourceLocation("solar_apocalypse_core", "textures/sun/stage2.png");
            case STAGE_3 -> new ResourceLocation("solar_apocalypse_core", "textures/sun/stage3.png");
            case STAGE_4 -> new ResourceLocation("solar_apocalypse_core", "textures/sun/stage4.png");
            case STAGE_5 -> new ResourceLocation("solar_apocalypse_core", "textures/sun/stage5.png");
            case STAGE_6 -> new ResourceLocation("solar_apocalypse_core", "textures/sun/stage6.png");
            default -> new ResourceLocation("minecraft", "textures/sky/sun.png");
        };
    }

    /**
     * 根据阶段和进度计算太阳的大小倍数
     * 阶段越高，太阳越大
     * 进度代表在该阶段内的变化 (0.0 - 1.0)
     */
    public static float getSunSize(SolarStage phase, float progress) {
        // 基础大小 1.0
        float baseSize = 1.0f;

        // 根据阶段增长 1.0, 1.5, 2.0, 2.5, 3.0, 3.5
        int level = phase.getEruptionLevel();
        float stageSizeMultiplier = 1.0f + (level - 1) * 0.5f;

        // 在阶段内逐渐增长（可选，如果需要额外的进度变化）
        // float progressBonus = progress * 0.2f;  // 最多增加 0.2 的倍数

        return baseSize * stageSizeMultiplier;
    }

    /**
     * 根据阶段获取太阳的颜色和透明度
     * 用于实现从黄色到红色到完全黑暗的色彩变化
     */
    public static float[] getSunColor(SolarStage phase) {
        // RGBA 格式
        return switch (phase) {
            case STAGE_1 -> new float[]{1.0f, 1.0f, 0.6f, 1.0f};      // 黄色
            case STAGE_2 -> new float[]{1.0f, 0.8f, 0.3f, 1.0f}; // 橙黄
            case STAGE_3 -> new float[]{1.0f, 0.5f, 0.0f, 1.0f};         // 橙色
            case STAGE_4 -> new float[]{1.0f, 0.2f, 0.0f, 1.0f};     // 红色
            case STAGE_5 -> new float[]{0.8f, 0.0f, 0.0f, 1.0f};     // 深红
            case STAGE_6 -> new float[]{0.2f, 0.2f, 0.2f, 0.8f};     // 暗灰色，半透明
            default -> new float[]{1.0f, 1.0f, 1.0f, 1.0f};           // 白色
        };
    }

    /**
     * 获取太阳的光照亮度
     * 阶段越高，光照越强
     */
    public static float getSunBrightness(SolarStage phase) {
        int level = phase.getEruptionLevel();
        return switch (level) {
            case 1 -> 0.8f;
            case 2 -> 1.0f;
            case 3 -> 1.2f;
            case 4 -> 1.4f;
            case 5 -> 1.6f;
            default -> 0.5f; // COLLAPSE
        };
    }

    /**
     * 获取第六阶段（坍缩）的白天亮度倍数
     * 白天返回配置的亮度倍数（默认0.3），夜晚返回1.0（正常亮度）
     * 用于LevelRendererMixin在渲染天空时调整亮度
     */
    public static float getCollapseDayBrightness(long dayTime, float brightnessFactor) {
        // 白天时间范围：0-12000 ticks（昼夜周期24000）
        long timeOfDay = dayTime % 24000;
        boolean isDaytime = timeOfDay < 12000;

        return isDaytime ? (float) brightnessFactor : 1.0f;
    }
}
