package me.mgin.graves.networking;

import me.mgin.graves.Graves;
import me.mgin.graves.networking.config.ConfigNetworking;
import me.mgin.graves.networking.config.payload.*;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public class NetworkHelper {
    public static void sendToPlayer(ServerPlayerEntity player, CustomPayload payload) {
        // TODO: Implement when Fabric API for 1.20.5 is properly set up
        // For now, we'll leave this as a placeholder
        // In 1.20.5, we need to use ServerPlayNetworking.send(player, payload)
    }
    
    // Temporary compatibility method to support old code
    public static void sendToPlayer(ServerPlayerEntity player, Identifier identifier, PacketByteBuf buf) {
        // TODO: Implement when Fabric API for 1.20.5 is properly set up
        // This is a compatibility method for old code
    }
    
    // Temporary compatibility method for client-to-server packets
    public static void sendToServer(Object packetId, PacketByteBuf buf) {
        // TODO: Implement when Fabric API for 1.20.5 is properly set up
        // This is a compatibility method for old code
    }
}