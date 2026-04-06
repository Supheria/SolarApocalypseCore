package com.supheria.solar_apocalypse_core.mixin;

import com.supheria.solar_apocalypse_core.world.CheatLockManager;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CreateWorldScreen.class)
public abstract class CreateWorldScreenMixin {

    @Inject(method = "createLevelSettings", at = @At("RETURN"), cancellable = true)
    private void solarApocalypseCore$recordCheatLock(boolean debugWorld, CallbackInfoReturnable<LevelSettings> cir) {
        LevelSettings original = cir.getReturnValue();
        boolean lockCheats = original.hardcore() || original.gameType() == GameType.SURVIVAL;
        Difficulty targetDifficulty = lockCheats ? Difficulty.HARD : original.difficulty();

        CheatLockManager.queuePendingWorldLock(original.gameType(), original.hardcore());

        if (!lockCheats && (!debugWorld || !original.allowCommands()) && targetDifficulty == original.difficulty()) {
            return;
        }

        cir.setReturnValue(new LevelSettings(
                original.levelName(),
                original.gameType(),
                original.hardcore(),
                targetDifficulty,
                lockCheats ? false : original.allowCommands(),
                original.gameRules(),
                original.getDataConfiguration()));
    }
}
