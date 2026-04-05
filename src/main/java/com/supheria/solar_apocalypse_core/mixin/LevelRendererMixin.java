package com.supheria.solar_apocalypse_core.mixin;

import com.supheria.solar_apocalypse_core.config.solar.SolarStageConfig;
import com.supheria.solar_apocalypse_core.network.SolarModVariables;
import com.supheria.solar_apocalypse_core.world.SolarStage;
import com.supheria.solar_apocalypse_core.world.SolarStageHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelAccessor;
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
            new ResourceLocation("solar_apocalypse_core:textures/environment/sun_step1.png"),
            new ResourceLocation("solar_apocalypse_core:textures/environment/sun_step2.png"),
            new ResourceLocation("solar_apocalypse_core:textures/environment/sun_step3.png"),
            new ResourceLocation("solar_apocalypse_core:textures/environment/sun_step4.png"),
            new ResourceLocation("solar_apocalypse_core:textures/environment/sun_step5.png"),
            new ResourceLocation("solar_apocalypse_core:textures/environment/sun_step6.png")
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
    private static final int FINAL_LUNAR_DAY = 28;
    @Unique
    private static final float FINAL_STAGE_SUN_SCALE = 13.0F;
    public LevelAccessor world;
    @Unique
    private Matrix4f originalCelestialMatrix;
    @Unique
    private static final java.util.Map<Integer, Float> LUNAR_SCALE_MAP = new java.util.HashMap<>() {{
        put(0,  1.1F);
        put(1,  1.3F);
        put(2,  1.5F);
        put(3,  1.7F);
        put(4,  1.9F);
        put(5,  2.1F);
        put(6,  2.3F);
        put(7,  2.5F);
        put(8,  3.5F);
        put(9,  4.5F);
        put(10, 5.5F);
        put(11, 6.5F);
        put(12, 7.5F);
        put(13, 8.5F);
        put(14, 9.5F);
        put(15, 9.6F);
        put(16, 9.7F);
        put(17, 9.8F);
        put(18, 9.9F);
        put(19, 10.0F);
        put(20, 10.1F);
        put(21, 10.2F);
        put(22, 10.6F);
        put(23, 11.0F);
        put(24, 11.4F);
        put(25, 11.8F);
        put(26, 12.2F);
        put(27, 12.6F);
    }};

    @Inject(method="renderSky", at = @At("HEAD"))
    private void onRendersky(PoseStack p_202424_, Matrix4f p_254034_, float p_202426_, Camera p_202427_, boolean p_202428_, Runnable p_202429_, CallbackInfo ci){
        SolarStage solarFlare = SolarModVariables.MapVariables.get(world).getSolarStage();
        SolarStage currentPhase = SolarModVariables.MapVariables.get(world).getCurrentStage();

        SUN_LOCATION = getSunTexture(solarFlare);

        // 第六阶段白天降低光照
        if (currentPhase == SolarStage.STAGE_6) {
            if (world instanceof net.minecraft.server.level.ServerLevel serverLevel
                    && SolarStageHelper.isDaytime(serverLevel.dayTime())) {
                // 降低白天的光照亮度 - 通过修改着色器颜色和雾效果
                com.mojang.blaze3d.systems.RenderSystem.setShaderFogColor(COLLAPSE_FOG_RED, COLLAPSE_FOG_GREEN, COLLAPSE_FOG_BLUE, FULL_ALPHA);
                // 降低渲染的整体颜色强度（通过降低颜色乘数）
                float brightnessFactor = getCollapseDayBrightnessFactor();
                com.mojang.blaze3d.systems.RenderSystem.setShaderColor(brightnessFactor, brightnessFactor, brightnessFactor, FULL_ALPHA);
            }
        }
    }

    @ModifyVariable(method = "renderSky(Lcom/mojang/blaze3d/vertex/PoseStack;Lorg/joml/Matrix4f;FLnet/minecraft/client/Camera;ZLjava/lang/Runnable;)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/resources/ResourceLocation;)V", ordinal = 0), ordinal = 1)
    private Matrix4f celesteConfig$scaleSun(Matrix4f in) {
        originalCelestialMatrix = new Matrix4f(in);
        Matrix4f copy = new Matrix4f(in);

        SolarStage flare = SolarModVariables.MapVariables.get(world).getSolarStage();
        int lunar = (int) SolarModVariables.MapVariables.get(world).currentLunarDay;

        float scale;
        if (flare == SolarStage.STAGE_6) {
            scale = 1.0F;
        } else if (flare == SolarStage.STAGE_5 || lunar >= FINAL_LUNAR_DAY) {
            scale = FINAL_STAGE_SUN_SCALE;
        } else {
            scale = LUNAR_SCALE_MAP.getOrDefault(lunar, 1.0F);
        }

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
        int stageIndex = solarFlare.ordinal();
        if (stageIndex < 0 || stageIndex >= SUN_TEXTURES.length) {
            return SUN_TEXTURES[0];
        }
        return SUN_TEXTURES[stageIndex];
    }

    /**
     * 获取第六阶段白天的亮度因子
     * 用于降低白天的光照等级，使僵尸能在白天生成
     */
    private float getCollapseDayBrightnessFactor() {
        if (world == null) return 1.0f;

        SolarStage currentPhase = SolarModVariables.MapVariables.get(world).getCurrentStage();
        if (currentPhase != SolarStage.STAGE_6) {
            return 1.0f;
        }

        if (world instanceof net.minecraft.server.level.ServerLevel serverLevel
                && SolarStageHelper.isDaytime(serverLevel.dayTime())) {
            return SolarStageConfig.getCollapseDayBrightnessFactor();
        }
        return 1.0f;
    }
}
