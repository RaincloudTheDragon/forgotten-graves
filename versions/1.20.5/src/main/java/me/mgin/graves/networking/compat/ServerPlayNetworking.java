package me.mgin.graves.networking.compat;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.network.PacketByteBuf;

import java.util.HashMap;
import java.util.Map;

/**
 * Compatibility layer for ServerPlayNetworking in 1.20.5
 * This provides a functional implementation for the basic networking needed
 * to get the mod running in 1.20.5
 */
public class ServerPlayNetworking {
    // Store packet receivers for identifier-based approach
    private static final Map<Identifier, PacketReceiver> PACKET_RECEIVERS = new HashMap<>();
    
    // Store payload handlers for CustomPayload-based approach
    private static final Map<String, PayloadHandler<?>> PAYLOAD_HANDLERS = new HashMap<>();
    
    /**
     * Send a packet to a player using the old identifier+buffer approach
     */
    public static void send(ServerPlayerEntity player, Identifier identifier, PacketByteBuf buf) {
        if (player == null || player.networkHandler == null) return;
        
        try {
            // Use Fabric API's ServerPlayNetworking
            net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.send(player, identifier, buf);
        } catch (Exception e) {
            System.err.println("Failed to send packet to player: " + e.getMessage());
        }
    }
    
    /**
     * Send a payload to a player using the new CustomPayload approach
     * Not fully implemented yet but provided for API compatibility
     */
    public static void send(ServerPlayerEntity player, CustomPayload payload) {
        // Not fully implemented yet
        System.out.println("CustomPayload sending not fully implemented yet");
    }
    
    /**
     * Register a global receiver for packets from clients
     */
    public static void registerGlobalReceiver(Identifier identifier, PacketReceiver receiver) {
        PACKET_RECEIVERS.put(identifier, receiver);
    }
    
    /**
     * Register a global receiver for payloads from clients
     * Not fully implemented yet but provided for API compatibility
     */
    public static <T extends CustomPayload> void registerGlobalReceiver(CustomPayload.Id<T> id, PayloadHandler<T> handler) {
        // Store for later implementation
        PAYLOAD_HANDLERS.put(id.toString(), handler);
    }
    
    /**
     * Functional interface for handling packets from clients
     */
    @FunctionalInterface
    public interface PacketReceiver {
        void receive(ServerPlayerEntity player, Identifier identifier, PacketByteBuf buf, Context context);
    }
    
    /**
     * Functional interface for handling payloads from clients
     */
    @FunctionalInterface
    public interface PayloadHandler<T extends CustomPayload> {
        void receive(T payload, Context context);
    }
    
    /**
     * Context for packet handlers
     */
    public interface Context {
        ServerPlayerEntity player();
        void execute(Runnable runnable);
    }
} 