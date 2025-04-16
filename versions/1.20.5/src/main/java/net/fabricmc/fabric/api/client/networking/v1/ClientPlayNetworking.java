package net.fabricmc.fabric.api.client.networking.v1;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

/**
 * Client networking compatibility class for 1.20.5
 * Delegates to our own internal implementation
 */
public class ClientPlayNetworking {
    /**
     * Send a packet to the server
     */
    public static void send(Identifier id, PacketByteBuf buf) {
        // Delegate to our own compatibility layer
        me.mgin.graves.networking.compat.ClientPlayNetworking.send(id, buf);
    }
    
    /**
     * Send a custom payload to the server
     */
    public static <T extends CustomPayload> void send(T payload) {
        // Delegate to our own compatibility layer
        me.mgin.graves.networking.compat.ClientPlayNetworking.send(payload);
    }
    
    /**
     * Register a packet receiver
     */
    public static void registerReceiver(Identifier id, PlayChannelHandler handler) {
        // Delegate to our own compatibility layer
        me.mgin.graves.networking.compat.ClientPlayNetworking.registerGlobalReceiver(id, 
            (identifier, buf, context) -> handler.receive(MinecraftClient.getInstance(), 
                new ClientPlayNetworkHandler() {}, buf, new PacketSender() {
                    @Override
                    public void sendPacket(Identifier packetId, PacketByteBuf buf) {
                        ClientPlayNetworking.send(packetId, buf);
                    }
                }
            )
        );
    }
    
    /**
     * Create an empty PacketByteBuf
     */
    public static PacketByteBuf create() {
        return me.mgin.graves.networking.compat.PacketByteBufs.create();
    }
    
    /**
     * Interface for handling packets
     */
    @FunctionalInterface
    public interface PlayChannelHandler {
        void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender);
    }
    
    /**
     * Interface for the network handler
     */
    public interface ClientPlayNetworkHandler {
        // Empty compatibility interface
    }
    
    /**
     * Interface for sending packets
     */
    public interface PacketSender {
        void sendPacket(Identifier id, PacketByteBuf buf);
    }
} 