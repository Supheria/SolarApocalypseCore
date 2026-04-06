package com.supheria.solar_apocalypse_core.mixin;

import com.supheria.solar_apocalypse_core.world.CheatLockManager;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.world.level.GameType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(IntegratedServer.class)
public abstract class IntegratedServerMixin {

    @ModifyVariable(method = "publishServer", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private GameType solarApocalypseCore$restrictPublishedGameType(GameType gameType) {
        IntegratedServer self = (IntegratedServer) (Object) this;
        if (!CheatLockManager.isCheatLockEnabled(self)) {
            return gameType;
        }

        return GameType.SURVIVAL;
    }

    @ModifyArg(method = "publishServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;setAllowCheatsForAllPlayers(Z)V"), index = 0)
    private boolean solarApocalypseCore$blockLanCheatsForLockedWorlds(boolean allowCheats) {
        IntegratedServer self = (IntegratedServer) (Object) this;
        return CheatLockManager.isCheatLockEnabled(self) ? false : allowCheats;
    }
}
