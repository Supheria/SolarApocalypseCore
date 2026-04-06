package com.supheria.solar_apocalypse_core.mixin;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Difficulty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.client.gui.screens.worldselection.CreateWorldScreen$GameTab")
public abstract class CreateWorldScreenGameTabMixin {
    @Inject(method = "lambda$new$8", at = @At("TAIL"), remap = false)
    private void solarApocalypseCore$lockAllowCheatsButton(CycleButton<Boolean> button, WorldCreationUiState state, CallbackInfo ci) {
        button.active = !state.isDebug() && state.getGameMode() == WorldCreationUiState.SelectedGameMode.CREATIVE;
    }

    @Inject(method = "lambda$new$5", at = @At("TAIL"), remap = false)
    private void solarApocalypseCore$lockDifficultyButton(CycleButton<Difficulty> button, WorldCreationUiState state, CallbackInfo ci) {
        boolean lockedDifficulty = state.getGameMode() == WorldCreationUiState.SelectedGameMode.SURVIVAL
                || state.getGameMode() == WorldCreationUiState.SelectedGameMode.HARDCORE;
        if (lockedDifficulty) {
            button.setValue(Difficulty.HARD);
            button.setTooltip(Tooltip.create(Difficulty.HARD.getInfo()));
        }
        button.active = !lockedDifficulty;
    }

    @Inject(method = "lambda$new$3", at = @At("TAIL"), remap = false)
    private static void solarApocalypseCore$setHardcoreTooltip(CycleButton<WorldCreationUiState.SelectedGameMode> button, WorldCreationUiState state, CallbackInfo ci) {
        if (state.getGameMode() == WorldCreationUiState.SelectedGameMode.HARDCORE) {
            button.setTooltip(Tooltip.create(Component.translatable("message.solar_apocalypse_core.create_world.hardcore_locked").withStyle(ChatFormatting.RED)));
        }
    }
}
