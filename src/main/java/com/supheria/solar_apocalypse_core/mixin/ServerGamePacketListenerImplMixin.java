package com.supheria.solar_apocalypse_core.mixin;

import com.supheria.solar_apocalypse_core.world.CheatLockManager;
import net.minecraft.network.protocol.game.ServerboundChangeDifficultyPacket;
import net.minecraft.network.protocol.game.ServerboundLockDifficultyPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin {
    @Shadow
    @Final
    private MinecraftServer server;

    @Inject(method = "handleChangeDifficulty", at = @At("HEAD"), cancellable = true)
    private void solarApocalypseCore$blockDifficultyChangePacket(ServerboundChangeDifficultyPacket packet, CallbackInfo ci) {
        if (CheatLockManager.isCheatLockEnabled(this.server)) {
            ci.cancel();
        }
    }

    @Inject(method = "handleLockDifficulty", at = @At("HEAD"), cancellable = true)
    private void solarApocalypseCore$blockDifficultyLockPacket(ServerboundLockDifficultyPacket packet, CallbackInfo ci) {
        if (CheatLockManager.isCheatLockEnabled(this.server)) {
            ci.cancel();
        }
    }
}
