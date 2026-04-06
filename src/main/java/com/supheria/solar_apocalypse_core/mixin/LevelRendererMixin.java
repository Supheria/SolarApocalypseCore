package com.supheria.solar_apocalypse_core.mixin;

import com.supheria.solar_apocalypse_core.config.solar.SolarStageConfig;
import com.supheria.solar_apocalypse_core.network.SolarModVariables;
import com.supheria.solar_apocalypse_core.world.SolarStage;
import com.supheria.solar_apocalypse_core.world.SolarStageHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {
    @Shadow
    @Final
    @Mutable
    public static ResourceLocation SUN_LOCATION;
    @Unique
    private static final ResourceLocation[] SUN_TEXTURES = new ResourceLocation[] {
            new ResourceLocation("solar_apocalypse_core:textures/environment/sun_stage1.png"),
            new ResourceLocation("solar_apocalypse_core:textures/environment/sun_stage2.png"),
            new ResourceLocation("solar_apocalypse_core:textures/environment/sun_stage3.png"),
            new ResourceLocation("solar_apocalypse_core:textures/environment/sun_stage4.png"),
            new ResourceLocation("solar_apocalypse_core:textures/environment/sun_stage5.png"),
            new ResourceLocation("solar_apocalypse_core:textures/environment/sun_stage6.png")
    };
    @Unique
    private static final float COLLAPSE_FOG_RED = 0.15F;
    @Unique
    private static final float COLLAPSE_FOG_GREEN = 0.15F;
    @Unique
    private static final float COLLAPSE_FOG_BLUE = 0.15F;
    @Unique
    private static final float FULL_ALPHA = 1.0F;
    @Unique
    private Matrix4f originalCelestialMatrix;
    @Unique
    private static final float STAGE_1_START_SCALE = 1.1F;
    @Unique
    private static final float STAGE_2_SCALE = 2.1F;
    @Unique
    private static final float STAGE_3_SCALE = 4.5F;
    @Unique
    private static final float STAGE_4_SCALE = 7.5F;
    @Unique
    private static final float STAGE_5_SCALE = 10.6F;
    @Unique
    private static final float STAGE_6_SCALE = 13.0F;

    @Inject(method="renderSky", at = @At("HEAD"))
    private void onRendersky(PoseStack p_202424_, Matrix4f p_254034_, float p_202426_, Camera p_202427_, boolean p_202428_, Runnable p_202429_, CallbackInfo ci){
        Level level = getClientLevel();
        if (level == null) {
            return;
        }

        SolarStage solarFlare = SolarStageHelper.getPhaseByDayTime(level.dayTime());
        SolarStage currentPhase = solarFlare;

        SUN_LOCATION = getSunTexture(solarFlare);

        // 第六阶段白天降低光照
        if (currentPhase == SolarStage.STAGE_6 && SolarStageHelper.isDaytime(level.dayTime())) {
                // 降低白天的光照亮度 - 通过修改着色器颜色和雾效果
                com.mojang.blaze3d.systems.RenderSystem.setShaderFogColor(COLLAPSE_FOG_RED, COLLAPSE_FOG_GREEN, COLLAPSE_FOG_BLUE, FULL_ALPHA);
                // 降低渲染的整体颜色强度（通过降低颜色乘数）
                float brightnessFactor = SolarStageConfig.getCollapseDayBrightnessFactor();
                com.mojang.blaze3d.systems.RenderSystem.setShaderColor(brightnessFactor, brightnessFactor, brightnessFactor, FULL_ALPHA);
        }
    }

    @ModifyVariable(method = "renderSky(Lcom/mojang/blaze3d/vertex/PoseStack;Lorg/joml/Matrix4f;FLnet/minecraft/client/Camera;ZLjava/lang/Runnable;)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/resources/ResourceLocation;)V", ordinal = 0), ordinal = 1)
    private Matrix4f celesteConfig$scaleSun(Matrix4f in) {
        originalCelestialMatrix = new Matrix4f(in);
        Matrix4f copy = new Matrix4f(in);

        float scale = getCurrentSunScale();

        copy.scale(scale, 1.0F, scale);
        return copy;
    }

    @ModifyVariable(method = "renderSky(Lcom/mojang/blaze3d/vertex/PoseStack;Lorg/joml/Matrix4f;FLnet/minecraft/client/Camera;ZLjava/lang/Runnable;)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/resources/ResourceLocation;)V", ordinal = 1), ordinal = 1)
    private Matrix4f celesteConfig$scaleMoon(Matrix4f in) {
        Matrix4f copy = new Matrix4f(originalCelestialMatrix);
        copy.scale(1.0F, 1.0F, 1.0F);
        originalCelestialMatrix = null;
        return copy;
    }

    @Unique
    private static ResourceLocation getSunTexture(SolarStage solarFlare) {
        if (solarFlare == null || !solarFlare.isAtLeast(SolarStage.STAGE_1)) {
            return SUN_TEXTURES[0];
        }

        int stageIndex = solarFlare.ordinal() - SolarStage.STAGE_1.ordinal();
        if (stageIndex < 0 || stageIndex >= SUN_TEXTURES.length) {
            return SUN_TEXTURES[SUN_TEXTURES.length - 1];
        }
        return SUN_TEXTURES[stageIndex];
    }

    @Unique
    private static Level getClientLevel() {
        return Minecraft.getInstance().level;
    }

    @Unique
    private static float getCurrentSunScale() {
        Level level = getClientLevel();
        if (level == null) {
            return STAGE_1_START_SCALE;
        }

        long dayTime = level.dayTime();
        SolarStage currentStage = SolarStageHelper.getPhaseByDayTime(dayTime);

        return switch (currentStage) {
            case STAGE_1 -> STAGE_1_START_SCALE;
            case STAGE_2 -> STAGE_2_SCALE;
            case STAGE_3 -> STAGE_3_SCALE;
            case STAGE_4 -> STAGE_4_SCALE;
            case STAGE_5 -> STAGE_5_SCALE;
            case STAGE_6 -> STAGE_6_SCALE;
            case NONE -> STAGE_1_START_SCALE;
        };
    }
}
