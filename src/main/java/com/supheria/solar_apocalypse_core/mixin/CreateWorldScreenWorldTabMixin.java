package com.supheria.solar_apocalypse_core.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.function.BooleanSupplier;

@Mixin(targets = "net.minecraft.client.gui.screens.worldselection.CreateWorldScreen$WorldTab")
public abstract class CreateWorldScreenWorldTabMixin {
    @ModifyArg(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/worldselection/SwitchGrid$SwitchBuilder;withIsActiveCondition(Ljava/util/function/BooleanSupplier;)Lnet/minecraft/client/gui/screens/worldselection/SwitchGrid$SwitchBuilder;",
                    ordinal = 1
            ),
            index = 0
    )
    private BooleanSupplier solarApocalypseCore$disableBonusChestSwitch(BooleanSupplier original) {
        return () -> false;
    }
}
