package com.supheria.solar_apocalypse_core.mixin;

import com.supheria.solar_apocalypse_core.thirst.SolarThirstHelper;
import dev.ghen.thirst.api.ThirstHelper;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ThirstHelper.class, remap = false)
public class ThirstHelperSolarMixin {

    @Inject(method = "getExhaustionMultiplier", at = @At("RETURN"), cancellable = true, remap = false)
    private static void onGetExhaustionMultiplier(Player player, CallbackInfoReturnable<Float> cir) {
        float solarMultiplier = SolarThirstHelper.getExhaustionMultiplier(player);
        if (solarMultiplier != 1.0f) {
            cir.setReturnValue(cir.getReturnValue() * solarMultiplier);
        }
    }
}
