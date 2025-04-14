package me.mgin.graves.networking;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import me.mgin.graves.networking.compat.ServerPlayNetworking;
import me.mgin.graves.networking.compat.ClientPlayNetworking;

public class NetworkHelper {
    /**
     * Sends a packet to a player using the identifier and buffer approach
     */
    public static void sendToPlayer(ServerPlayerEntity player, Identifier channel, PacketByteBuf buf) {
        // Delegate to our compatibility layer
        ServerPlayNetworking.send(player, channel, buf);
    }
    
    /**
     * Sends a packet to the server using the identifier and buffer approach
     */
    public static void sendToServer(Identifier channel, PacketByteBuf buf) {
        // Delegate to our compatibility layer
        ClientPlayNetworking.send(channel, buf);
    }
    
    /**
     * Generic method to handle object packet ID
     */
    public static void sendToServer(Object packetId, PacketByteBuf buf) {
        if (packetId instanceof Identifier) {
            sendToServer((Identifier)packetId, buf);
        }
    }
}