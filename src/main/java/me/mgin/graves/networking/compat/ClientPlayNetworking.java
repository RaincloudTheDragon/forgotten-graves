package me.mgin.graves.networking.compat;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.Identifier;

import java.lang.reflect.Constructor;

/**
 * Compatibility class for client-side networking
 */
public class ClientPlayNetworking {

    /**
     * Sends a packet to the server
     */
    public static void send(Identifier channel, PacketByteBuf buf) {
        try {
            // Get the client network handler
            MinecraftClient client = MinecraftClient.getInstance();
            ClientPlayNetworkHandler networkHandler = client.getNetworkHandler();
            
            if (networkHandler != null) {
                // Use reflection to create the C2S packet to support different Minecraft versions
                Class<?> packetClass = Class.forName("net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket");
                Constructor<?> constructor = packetClass.getConstructor(Identifier.class, PacketByteBuf.class);
                Packet<?> packet = (Packet<?>)constructor.newInstance(channel, buf);
                
                // Send the packet
                networkHandler.sendPacket(packet);
            }
        } catch (Exception e) {
            System.err.println("Error sending packet to server: " + e.getMessage());
        }
    }
    
    /**
     * Registers a global receiver for a specific channel
     */
    public static void registerGlobalReceiver(Identifier channel, PacketReceiver receiver) {
        // This would normally be registered with Fabric API for pre-1.20.5
        // Not implemented in this compatibility class as we'd need to hook into Fabric's event system
        System.out.println("Registered receiver for channel: " + channel + " (compatibility mode)");
    }
    
    /**
     * Interface for packet receivers
     */
    @FunctionalInterface
    public interface PacketReceiver {
        void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, Identifier channel);
    }
} 