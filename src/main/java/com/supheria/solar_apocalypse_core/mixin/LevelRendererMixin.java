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
        //1단계
        if (SapModVariables.MapVariables.get(world).LunarToday == 0 && SapModVariables.MapVariables.get(world).SolarFlare != 6){
            copy.scale(1.1F, 1.0F, 1.1F);
        }
        if (SapModVariables.MapVariables.get(world).LunarToday == 1 && SapModVariables.MapVariables.get(world).SolarFlare != 6){
            copy.scale(1.3F, 1.0F, 1.3F);
        }
        if (SapModVariables.MapVariables.get(world).LunarToday == 2 && SapModVariables.MapVariables.get(world).SolarFlare != 6){
            copy.scale(1.5F, 1.0F, 1.5F);
        }
        if (SapModVariables.MapVariables.get(world).LunarToday == 3 && SapModVariables.MapVariables.get(world).SolarFlare != 6){
            copy.scale(1.7F, 1.0F, 1.7F);
        }
        if (SapModVariables.MapVariables.get(world).LunarToday == 4 && SapModVariables.MapVariables.get(world).SolarFlare != 6){
            copy.scale(1.9F, 1.0F, 1.9F);
        }
        if (SapModVariables.MapVariables.get(world).LunarToday == 5 && SapModVariables.MapVariables.get(world).SolarFlare != 6){
            copy.scale(2.1F, 1.0F, 2.1F);
        }
        if (SapModVariables.MapVariables.get(world).LunarToday == 6 && SapModVariables.MapVariables.get(world).SolarFlare != 6){
            copy.scale(2.3F, 1.0F, 2.3F);
        }
        //2단계
        if (SapModVariables.MapVariables.get(world).LunarToday == 7 && SapModVariables.MapVariables.get(world).SolarFlare != 6){
            copy.scale(2.5F, 1.0F, 2.5F);
        }
        if (SapModVariables.MapVariables.get(world).LunarToday == 8 && SapModVariables.MapVariables.get(world).SolarFlare != 6){
            copy.scale(3.5F, 1.0F, 3.5F);
        }
        if (SapModVariables.MapVariables.get(world).LunarToday == 9 && SapModVariables.MapVariables.get(world).SolarFlare != 6){
            copy.scale(4.5F, 1.0F, 4.5F);
        }
        if (SapModVariables.MapVariables.get(world).LunarToday == 10 && SapModVariables.MapVariables.get(world).SolarFlare != 6){
            copy.scale(5.5F, 1.0F, 5.5F);
        }
        if (SapModVariables.MapVariables.get(world).LunarToday == 11 && SapModVariables.MapVariables.get(world).SolarFlare != 6){
            copy.scale(6.5F, 1.0F, 6.5F);
        }
        if (SapModVariables.MapVariables.get(world).LunarToday == 12 && SapModVariables.MapVariables.get(world).SolarFlare != 6){
            copy.scale(7.5F, 1.0F, 7.5F);
        }
        if (SapModVariables.MapVariables.get(world).LunarToday == 13 && SapModVariables.MapVariables.get(world).SolarFlare != 6){
            copy.scale(8.5F, 1.0F, 8.5F);
        }
        //3단계
        if (SapModVariables.MapVariables.get(world).LunarToday == 14 && SapModVariables.MapVariables.get(world).SolarFlare != 6){
            copy.scale(9.5F, 1.0F, 9.5F);
        }
        if (SapModVariables.MapVariables.get(world).LunarToday == 15 && SapModVariables.MapVariables.get(world).SolarFlare != 6){
            copy.scale(9.6F, 1.0F, 9.6F);
        }
        if (SapModVariables.MapVariables.get(world).LunarToday == 16 && SapModVariables.MapVariables.get(world).SolarFlare != 6){
            copy.scale(9.7F, 1.0F, 9.7F);
        }
        if (SapModVariables.MapVariables.get(world).LunarToday == 17 && SapModVariables.MapVariables.get(world).SolarFlare != 6){
            copy.scale(9.8F, 1.0F, 9.8F);
        }
        if (SapModVariables.MapVariables.get(world).LunarToday == 18 && SapModVariables.MapVariables.get(world).SolarFlare != 6){
            copy.scale(9.9F, 1.0F, 9.9F);
        }
        if (SapModVariables.MapVariables.get(world).LunarToday == 19 && SapModVariables.MapVariables.get(world).SolarFlare != 6){
            copy.scale(10.0F, 1.0F, 10.0F);
        }
        if (SapModVariables.MapVariables.get(world).LunarToday == 20 && SapModVariables.MapVariables.get(world).SolarFlare != 6){
            copy.scale(10.1F, 1.0F, 10.1F);
        }
        //4단계
        if (SapModVariables.MapVariables.get(world).LunarToday == 21 && SapModVariables.MapVariables.get(world).SolarFlare != 6){
            copy.scale(10.2F, 1.0F, 10.2F);
        }
        if (SapModVariables.MapVariables.get(world).LunarToday == 22 && SapModVariables.MapVariables.get(world).SolarFlare != 6){
            copy.scale(10.6F, 1.0F, 10.6F);
        }
        if (SapModVariables.MapVariables.get(world).LunarToday == 23 && SapModVariables.MapVariables.get(world).SolarFlare != 6){
            copy.scale(11.0F, 1.0F, 11.0F);
        }
        if (SapModVariables.MapVariables.get(world).LunarToday == 24 && SapModVariables.MapVariables.get(world).SolarFlare != 6){
            copy.scale(11.4F, 1.0F, 11.4F);
        }
        if (SapModVariables.MapVariables.get(world).LunarToday == 25 && SapModVariables.MapVariables.get(world).SolarFlare != 6){
            copy.scale(11.8F, 1.0F, 11.8F);
        }
        if (SapModVariables.MapVariables.get(world).LunarToday == 26 && SapModVariables.MapVariables.get(world).SolarFlare != 6){
            copy.scale(12.2F, 1.0F, 12.2F);
        }
        if (SapModVariables.MapVariables.get(world).LunarToday == 27 && SapModVariables.MapVariables.get(world).SolarFlare != 6){
            copy.scale(12.6F, 1.0F, 12.6F);
        }
        //5단계
        if (SapModVariables.MapVariables.get(world).LunarToday == 28 && SapModVariables.MapVariables.get(world).SolarFlare != 6){
            copy.scale(13.0F, 1.0F, 13.0F);
        }
        if (SapModVariables.MapVariables.get(world).SolarFlare == 5) {
            copy.scale(13.0F, 1.0F, 13.0F);
        }
        if (SapModVariables.MapVariables.get(world).SolarFlare == 6){
            copy.scale(1.0F, 1.0F, 1.0F);
        }
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
