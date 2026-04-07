package com.supheria.solar_apocalypse_core.mixin;

import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Difficulty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldCreationUiState.class)
public abstract class WorldCreationUiStateMixin {
    private static final String SOLAR_APOCALYPSE_CORE$DEFAULT_WORLD_NAME_KEY = "generator.solar_apocalypse_core.default_world_name";

    @Inject(method = "<init>", at = @At("RETURN"))
    private void solarApocalypseCore$setDefaultWorldName(CallbackInfo ci) {
        WorldCreationUiState self = (WorldCreationUiState) (Object) this;
        String currentName = self.getName();
        String vanillaDefaultName = Component.translatable("selectWorld.newWorld").getString();
        if (currentName.equals(vanillaDefaultName)) {
            self.setName(Component.translatable(SOLAR_APOCALYPSE_CORE$DEFAULT_WORLD_NAME_KEY).getString());
        }
    }

    @Inject(method = "isAllowCheats", at = @At("HEAD"), cancellable = true)
    private void solarApocalypseCore$disableCheatsForSurvival(CallbackInfoReturnable<Boolean> cir) {
        WorldCreationUiState self = (WorldCreationUiState) (Object) this;
        if (self.getGameMode() == WorldCreationUiState.SelectedGameMode.SURVIVAL
                || self.getGameMode() == WorldCreationUiState.SelectedGameMode.HARDCORE) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "getDifficulty", at = @At("HEAD"), cancellable = true)
    private void solarApocalypseCore$lockDifficultyForSurvival(CallbackInfoReturnable<Difficulty> cir) {
        WorldCreationUiState self = (WorldCreationUiState) (Object) this;
        if (self.getGameMode() == WorldCreationUiState.SelectedGameMode.SURVIVAL
                || self.getGameMode() == WorldCreationUiState.SelectedGameMode.HARDCORE) {
            cir.setReturnValue(Difficulty.HARD);
        }
    }
}
