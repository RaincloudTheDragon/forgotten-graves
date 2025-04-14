package me.mgin.graves.networking.compat;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;

/**
 * Compatibility class for networking
 */
public class ServerPlayNetworking {
    
    /**
     * Sends a packet to a player
     */
    public static void send(ServerPlayerEntity player, Identifier channel, PacketByteBuf buf) {
        // Direct implementation using vanilla network code - will be different based on Minecraft version
        // Use direct vanilla methods for packet creation and sending
        try {
            // Create a vanilla packet directly
            Packet<?> packet = createPacket(channel, buf);
            
            // Send the packet using player's network handler
            if (packet != null) {
                player.networkHandler.sendPacket(packet);
            }
        } catch (Exception e) {
            System.err.println("Error sending packet: " + e.getMessage());
        }
    }
    
    /**
     * Creates a packet object appropriate for the current Minecraft version
     */
    private static Packet<?> createPacket(Identifier channel, PacketByteBuf buf) {
        try {
            // Try to use reflection to create the packet for the current version
            // For pre-1.20.5 this would be CustomPayloadS2CPacket
            Class<?> packetClass = Class.forName("net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket");
            return (Packet<?>)packetClass.getConstructor(Identifier.class, PacketByteBuf.class).newInstance(channel, buf);
        } catch (Exception e) {
            System.err.println("Could not create packet: " + e.getMessage());
            return null;
        }
    }
} 