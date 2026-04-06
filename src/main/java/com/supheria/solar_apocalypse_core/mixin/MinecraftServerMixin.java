package com.supheria.solar_apocalypse_core.mixin;

import com.supheria.solar_apocalypse_core.world.CheatLockManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.Difficulty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {

    @ModifyVariable(method = "setDifficulty", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private Difficulty solarApocalypseCore$lockDifficultyValue(Difficulty difficulty) {
        MinecraftServer self = (MinecraftServer) (Object) this;
        return CheatLockManager.isCheatLockEnabled(self) ? Difficulty.HARD : difficulty;
    }

    @ModifyArg(method = "setDifficultyLocked", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/WorldData;setDifficultyLocked(Z)V"), index = 0)
    private boolean solarApocalypseCore$keepDifficultyLocked(boolean locked) {
        MinecraftServer self = (MinecraftServer) (Object) this;
        return CheatLockManager.isCheatLockEnabled(self) || locked;
    }
}
