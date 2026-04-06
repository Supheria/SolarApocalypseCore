package com.supheria.solar_apocalypse_core.world;

import com.supheria.solar_apocalypse_core.network.SolarModVariables;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public final class CheatLockManager {
    private static Boolean pendingCheatLock;

    private CheatLockManager() {
    }

    public static void queuePendingWorldLock(GameType gameType, boolean hardcore) {
        pendingCheatLock = hardcore || gameType == GameType.SURVIVAL;
    }

    public static boolean isCheatLockEnabled(MinecraftServer server) {
        if (server == null || server.getLevel(Level.OVERWORLD) == null) {
            return false;
        }

        return SolarModVariables.MapVariables.get(server.getLevel(Level.OVERWORLD)).isCheatsPermanentlyLocked();
    }

    public static void enforceLockedWorldRules(MinecraftServer server) {
        if (!isCheatLockEnabled(server)) {
            return;
        }

        server.setDifficulty(Difficulty.HARD, true);
        server.setDifficultyLocked(true);
    }

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        if (event.getServer().getLevel(Level.OVERWORLD) == null) {
            return;
        }

        if (pendingCheatLock != null) {
            SolarModVariables.MapVariables mapVariables = SolarModVariables.MapVariables.get(event.getServer().getLevel(Level.OVERWORLD));
            mapVariables.setCheatLockConfigured(true);
            mapVariables.setCheatsPermanentlyLocked(pendingCheatLock);
            mapVariables.syncData(event.getServer().getLevel(Level.OVERWORLD));
            pendingCheatLock = null;
        }

        enforceLockedWorldRules(event.getServer());
    }

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        pendingCheatLock = null;
    }
}
