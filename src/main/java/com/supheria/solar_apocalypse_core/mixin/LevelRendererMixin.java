package com.supheria.solar_apocalypse_core.mixin;

import com.supheria.solar_apocalypse_core.network.SapModVariables;
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

        if (SapModVariables.MapVariables.get(world).SolarFlare == 1){
            SUN_LOCATION = new ResourceLocation("solar_apocalypse_core:textures/environment/sun_step1.png");
        }
        if (SapModVariables.MapVariables.get(world).SolarFlare == 2){
            SUN_LOCATION = new ResourceLocation("solar_apocalypse_core:textures/environment/sun_step2.png");
        }
        if (SapModVariables.MapVariables.get(world).SolarFlare == 3){
            SUN_LOCATION = new ResourceLocation("solar_apocalypse_core:textures/environment/sun_step3.png");
        }
        if (SapModVariables.MapVariables.get(world).SolarFlare == 4){
            SUN_LOCATION = new ResourceLocation("solar_apocalypse_core:textures/environment/sun_step4.png");
        }
        if (SapModVariables.MapVariables.get(world).SolarFlare == 5){
            SUN_LOCATION = new ResourceLocation("solar_apocalypse_core:textures/environment/sun_step5.png");
        }
        if (SapModVariables.MapVariables.get(world).SolarFlare == 6){
            SUN_LOCATION = new ResourceLocation("solar_apocalypse_core:textures/environment/sun_step6.png");
        }
    }

    @ModifyVariable(method = "renderSky(Lcom/mojang/blaze3d/vertex/PoseStack;Lorg/joml/Matrix4f;FLnet/minecraft/client/Camera;ZLjava/lang/Runnable;)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/resources/ResourceLocation;)V", ordinal = 0), ordinal = 1)
    private Matrix4f celesteConfig$scaleSun(Matrix4f in) {
        originalCelestialMatrix = new Matrix4f(in);
        Matrix4f copy = new Matrix4f(in);

        int flare = (int) SapModVariables.MapVariables.get(world).SolarFlare;
        int lunar = (int) SapModVariables.MapVariables.get(world).LunarToday;

        float scale;
        if (flare == 6) {
            scale = 1.0F;
        } else if (flare == 5 || lunar >= 28) {
            scale = 13.0F;
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
}
