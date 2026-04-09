package com.supheria.solar_apocalypse_core.client;

import com.supheria.solar_apocalypse_core.SolarApocalypseCoreMod;
import com.supheria.solar_apocalypse_core.network.ClientRenderDistanceSyncMessage;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SolarApocalypseCoreMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class ClientRenderDistanceReporter {
    private static int lastReportedRenderDistance = -1;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.getConnection() == null) {
            return;
        }

        int renderDistance = minecraft.options.renderDistance().get();
        if (renderDistance == lastReportedRenderDistance) {
            return;
        }

        lastReportedRenderDistance = renderDistance;
        SolarApocalypseCoreMod.PACKET_HANDLER.sendToServer(new ClientRenderDistanceSyncMessage(renderDistance));
    }

    @SubscribeEvent
    public static void onClientLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        lastReportedRenderDistance = -1;
    }

    private ClientRenderDistanceReporter() {}
}
