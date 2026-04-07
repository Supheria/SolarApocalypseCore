package com.supheria.solar_apocalypse_core.mixin;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Difficulty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.function.Consumer;

@Mixin(targets = "net.minecraft.client.gui.screens.worldselection.CreateWorldScreen$GameTab")
public abstract class CreateWorldScreenGameTabMixin {
    @ModifyArg(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/worldselection/WorldCreationUiState;addListener(Ljava/util/function/Consumer;)V",
                    ordinal = 1
            ),
            index = 0
    )
    private Consumer<WorldCreationUiState> solarApocalypseCore$wrapGameModeListener(Consumer<WorldCreationUiState> original) {
        return state -> {
            original.accept(state);
            CycleButton<WorldCreationUiState.SelectedGameMode> button = this.solarApocalypseCore$findCycleButton(original, WorldCreationUiState.SelectedGameMode.class);
            if (button == null) {
                return;
            }

            if (state.getGameMode() == WorldCreationUiState.SelectedGameMode.SURVIVAL) {
                button.setTooltip(Tooltip.create(Component.empty()
                        .append(Component.translatable("message.solar_apocalypse_core.create_world.survival.line1").withStyle(ChatFormatting.YELLOW))
                        .append(Component.literal("\n"))
                        .append(Component.translatable("message.solar_apocalypse_core.create_world.survival.line2").withStyle(ChatFormatting.GOLD))));
                return;
            }

            if (state.getGameMode() == WorldCreationUiState.SelectedGameMode.CREATIVE) {
                button.setTooltip(Tooltip.create(Component.translatable("message.solar_apocalypse_core.create_world.creative.tooltip").withStyle(ChatFormatting.GREEN)));
                return;
            }

            if (state.getGameMode() == WorldCreationUiState.SelectedGameMode.HARDCORE) {
                button.setTooltip(Tooltip.create(Component.translatable("message.solar_apocalypse_core.create_world.hardcore_locked").withStyle(ChatFormatting.RED)));
            }
        };
    }

    @ModifyArg(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/worldselection/WorldCreationUiState;addListener(Ljava/util/function/Consumer;)V",
                    ordinal = 2
            ),
            index = 0
    )
    private Consumer<WorldCreationUiState> solarApocalypseCore$wrapDifficultyListener(Consumer<WorldCreationUiState> original) {
        return state -> {
            original.accept(state);
            CycleButton<Difficulty> button = this.solarApocalypseCore$findCycleButton(original, Difficulty.class);
            if (button == null) {
                return;
            }

            boolean lockedDifficulty = state.getGameMode() == WorldCreationUiState.SelectedGameMode.SURVIVAL
                    || state.getGameMode() == WorldCreationUiState.SelectedGameMode.HARDCORE;
            if (lockedDifficulty) {
                button.setValue(Difficulty.HARD);
                button.setTooltip(Tooltip.create(Difficulty.HARD.getInfo()));
            }
            button.active = !lockedDifficulty;
        };
    }

    @ModifyArg(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/worldselection/WorldCreationUiState;addListener(Ljava/util/function/Consumer;)V",
                    ordinal = 3
            ),
            index = 0
    )
    private Consumer<WorldCreationUiState> solarApocalypseCore$wrapAllowCheatsListener(Consumer<WorldCreationUiState> original) {
        return state -> {
            original.accept(state);
            CycleButton<Boolean> button = this.solarApocalypseCore$findCycleButton(original, Boolean.class);
            if (button == null) {
                return;
            }

            if (state.getGameMode() == WorldCreationUiState.SelectedGameMode.SURVIVAL
                    || state.getGameMode() == WorldCreationUiState.SelectedGameMode.HARDCORE) {
                button.setValue(false);
            }
            button.active = !state.isDebug() && state.getGameMode() == WorldCreationUiState.SelectedGameMode.CREATIVE;
        };
    }

    @Unique
    @SuppressWarnings("unchecked")
    private <T> CycleButton<T> solarApocalypseCore$findCycleButton(Consumer<WorldCreationUiState> consumer, Class<T> valueClass) {
        for (var field : consumer.getClass().getDeclaredFields()) {
            if (!CycleButton.class.isAssignableFrom(field.getType())) {
                continue;
            }

            try {
                field.setAccessible(true);
                Object candidate = field.get(consumer);
                if (!(candidate instanceof CycleButton<?> rawButton)) {
                    continue;
                }

                Object value = rawButton.getValue();
                if (valueClass.isInstance(value)) {
                    return (CycleButton<T>) rawButton;
                }
            } catch (IllegalAccessException ignored) {
            }
        }

        return null;
    }
}
