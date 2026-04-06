package com.supheria.solar_apocalypse_core.mixin;

import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.world.Difficulty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldCreationUiState.class)
public abstract class WorldCreationUiStateMixin {

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
