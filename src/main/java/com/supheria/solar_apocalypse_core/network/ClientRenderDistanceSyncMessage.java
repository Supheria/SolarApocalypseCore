package com.supheria.solar_apocalypse_core.network;

import com.supheria.solar_apocalypse_core.handlers.EnvironmentalTransformScheduler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientRenderDistanceSyncMessage {
    private final int renderDistance;

    public ClientRenderDistanceSyncMessage(int renderDistance) {
        this.renderDistance = renderDistance;
    }

    public ClientRenderDistanceSyncMessage(FriendlyByteBuf buffer) {
        this.renderDistance = buffer.readVarInt();
    }

    public static void buffer(ClientRenderDistanceSyncMessage message, FriendlyByteBuf buffer) {
        buffer.writeVarInt(message.renderDistance);
    }

    public static void handler(ClientRenderDistanceSyncMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer sender = context.getSender();
            if (sender != null) {
                EnvironmentalTransformScheduler.updateClientRenderDistance(sender, message.renderDistance);
            }
        });
        context.setPacketHandled(true);
    }
}
