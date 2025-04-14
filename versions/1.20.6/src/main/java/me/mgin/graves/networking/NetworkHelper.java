package me.mgin.graves.networking;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import me.mgin.graves.networking.compat.ServerPlayNetworking;
import me.mgin.graves.networking.compat.ClientPlayNetworking;

/**
 * NetworkHelper provides utility methods for sending packets between server and client
 * This implementation includes compatibility with both 1.20.5+ and pre-1.20.5 systems.
 * 
 * This is a temporary implementation until the Fabric API is fully stabilized for 1.20.5/1.20.6.
 * The compatibility layer in the 'compat' package provides placeholder implementations
 * that will be replaced with the actual Fabric API when available.
 */
public class NetworkHelper {
    /**
     * Send a payload to a player using the new CustomPayload system
     */
    public static void sendToPlayer(ServerPlayerEntity player, CustomPayload payload) {
        // Delegate to ServerPlayNetworking compatibility layer
        ServerPlayNetworking.send(player, payload);
    }
    
    /**
     * Temporary compatibility method to support old code
     * Converts old identifier+buffer pattern to new payload system when possible
     */
    public static void sendToPlayer(ServerPlayerEntity player, Identifier identifier, PacketByteBuf buf) {
        // Delegate to ServerPlayNetworking compatibility layer
        ServerPlayNetworking.send(player, identifier, buf);
    }
    
    /**
     * Send a payload to the server using the new CustomPayload system
     */
    public static void sendToServer(CustomPayload payload) {
        // Delegate to ClientPlayNetworking compatibility layer
        ClientPlayNetworking.send(payload);
    }
    
    /**
     * Temporary compatibility method for client-to-server packets
     * Converts old identifier+buffer pattern to new payload system when possible
     */
    public static void sendToServer(Object packetId, PacketByteBuf buf) {
        if (packetId instanceof Identifier) {
            // Delegate to ClientPlayNetworking compatibility layer
            ClientPlayNetworking.send((Identifier)packetId, buf);
        } else if (packetId instanceof CustomPayload.Id<?>) {
            // Future support for CustomPayload.Id
            // Will be implemented when the Fabric API for 1.20.6 is stabilized
        }
    }
}