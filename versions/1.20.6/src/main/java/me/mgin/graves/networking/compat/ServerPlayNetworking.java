package me.mgin.graves.networking.compat;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.network.PacketByteBuf;

import java.util.HashMap;
import java.util.Map;

/**
 * Compatibility layer for ServerPlayNetworking in 1.20.6
 * This provides placeholders that will be replaced with proper implementation
 * when the Fabric API is finalized
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
        // This is a placeholder that will be implemented when Fabric API is available
    }
    
    /**
     * Send a payload to a player using the new CustomPayload approach
     */
    public static void send(ServerPlayerEntity player, CustomPayload payload) {
        // This is a placeholder that will be implemented when Fabric API is available
    }
    
    /**
     * Register a global receiver for packets from clients
     */
    public static void registerGlobalReceiver(Identifier identifier, PacketReceiver receiver) {
        PACKET_RECEIVERS.put(identifier, receiver);
    }
    
    /**
     * Register a global receiver for payloads from clients
     */
    public static <T extends CustomPayload> void registerGlobalReceiver(CustomPayload.Id<T> id, PayloadHandler<T> handler) {
        // This is a placeholder that will be implemented when Fabric API is available
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