package com.supheria.solar_apocalypse_core.mixin;

import com.supheria.solar_apocalypse_core.client.CollapsePhaseState;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@OnlyIn(Dist.CLIENT)
@Pseudo
@Mixin(targets = "weather2.ClientWeatherProxy", remap = false)
public abstract class Weather2ClientWeatherProxyMixin {

    @Inject(method = "getRainAmount", at = @At("HEAD"), cancellable = true, remap = false)
    private void solarApocalypseCore$forceCollapseRainAmount(CallbackInfoReturnable<Float> cir) {
        if (solarApocalypseCore$shouldForceCollapsePrecipitation()) {
            cir.setReturnValue(1.0F);
        }
    }

    @Inject(method = "getVanillaRainAmount", at = @At("HEAD"), cancellable = true, remap = false)
    private void solarApocalypseCore$forceCollapseVanillaRainAmount(CallbackInfoReturnable<Float> cir) {
        if (solarApocalypseCore$shouldForceCollapsePrecipitation()) {
            cir.setReturnValue(1.0F);
        }
    }

    @Inject(method = "hasWeather", at = @At("HEAD"), cancellable = true, remap = false)
    private void solarApocalypseCore$forceCollapseHasWeather(CallbackInfoReturnable<Boolean> cir) {
        if (solarApocalypseCore$shouldForceCollapsePrecipitation()) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "getPrecipitationType", at = @At("HEAD"), cancellable = true, remap = false)
    private void solarApocalypseCore$useCollapsePrecipitationType(Biome biome, CallbackInfoReturnable<Object> cir) {
        if (!CollapsePhaseState.isInCollapseOverworld() || biome == null) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null) {
            return;
        }

        BlockPos pos = minecraft.player.blockPosition();
        Biome.Precipitation precipitation = biome.getPrecipitationAt(pos);
        if (precipitation == Biome.Precipitation.SNOW) {
            cir.setReturnValue(solarApocalypseCore$getWeather2PrecipitationType("SNOW"));
        } else if (precipitation == Biome.Precipitation.RAIN) {
            cir.setReturnValue(solarApocalypseCore$getWeather2PrecipitationType("NORMAL"));
        } else {
            cir.setReturnValue(null);
        }
    }

    @Unique
    private static boolean solarApocalypseCore$shouldForceCollapsePrecipitation() {
        if (!CollapsePhaseState.isInCollapseOverworld()) {
            return false;
        }

        Minecraft minecraft = Minecraft.getInstance();
        return minecraft.level != null && minecraft.player != null;
    }

    @Unique
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Object solarApocalypseCore$getWeather2PrecipitationType(String name) {
        try {
            Class<? extends Enum> precipitationTypeClass =
                    (Class<? extends Enum>) Class.forName("weather2.datatypes.PrecipitationType");
            return Enum.valueOf(precipitationTypeClass, name);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}
