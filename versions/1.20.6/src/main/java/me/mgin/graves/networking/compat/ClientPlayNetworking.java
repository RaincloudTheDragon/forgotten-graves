package me.mgin.graves.networking.compat;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of ClientPlayNetworking for Minecraft 1.20.6
 * This handles client-side network packet registration and sending
 */
public class ClientPlayNetworking {
    // Store packet receivers for identifier-based (legacy) approach
    private static final Map<Identifier, PacketReceiver> PACKET_RECEIVERS = new HashMap<>();
    
    // Store payload handlers for CustomPayload-based (new) approach
    private static final Map<String, PayloadHandler<?>> PAYLOAD_HANDLERS = new HashMap<>();
    
    /**
     * Send a packet to the server using the legacy identifier+buffer approach
     */
    public static void send(Identifier identifier, PacketByteBuf buf) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null && client.getNetworkHandler() != null) {
            try {
                // Use Fabric API's net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
                // to send the packet instead of using CustomPayloadC2SPacket directly
                net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.send(identifier, buf);
            } catch (Exception e) {
                System.err.println("Failed to send packet to server: " + e.getMessage());
            }
        }
    }
    
    /**
     * Send a payload to the server using the new CustomPayload system
     * Not fully implemented yet but provided for API compatibility
     */
    public static void send(CustomPayload payload) {
        // Not fully implemented yet
        System.out.println("CustomPayload sending not fully implemented yet");
    }
    
    /**
     * Register a receiver for server-to-client packets using identifier+buffer approach
     */
    public static void registerGlobalReceiver(Identifier identifier, PacketReceiver receiver) {
        PACKET_RECEIVERS.put(identifier, receiver);
    }
    
    /**
     * Register a receiver for a specific payload type
     * Not fully implemented yet but provided for API compatibility
     */
    public static <T extends CustomPayload> void registerGlobalReceiver(CustomPayload.Id<T> id, PayloadHandler<T> handler) {
        PAYLOAD_HANDLERS.put(id.toString(), handler);
    }
    
    /**
     * Handle an incoming packet - to be called from a mixin into ClientPlayNetworkHandler
     */
    public static boolean handlePacket(Identifier identifier, PacketByteBuf buf) {
        PacketReceiver receiver = PACKET_RECEIVERS.get(identifier);
        if (receiver != null) {
            MinecraftClient client = MinecraftClient.getInstance();
            Context context = new SimpleContext(client);
            receiver.receive(identifier, buf, context);
            return true;
        }
        return false;
    }
    
    /**
     * Handle an incoming payload - to be called from a mixin into ClientPlayNetworkHandler
     */
    @SuppressWarnings("unchecked") // Required for type safety with CustomPayload.Id
    public static <T extends CustomPayload> boolean handlePayload(T payload) {
        CustomPayload.Id<T> id = (CustomPayload.Id<T>) payload.getId();
        PayloadHandler<T> handler = (PayloadHandler<T>) PAYLOAD_HANDLERS.get(id.toString());
        if (handler != null) {
            MinecraftClient client = MinecraftClient.getInstance();
            Context context = new SimpleContext(client);
            handler.receive(payload, context);
            return true;
        }
        return false;
    }
    
    /**
     * Functional interface for handling packets in pre-1.20.5
     */
    @FunctionalInterface
    public interface PacketReceiver {
        void receive(Identifier identifier, PacketByteBuf buf, Context context);
    }
    
    /**
     * Functional interface for handling payloads in 1.20.5
     */
    @FunctionalInterface
    public interface PayloadHandler<T extends CustomPayload> {
        void receive(T payload, Context context);
    }
    
    /**
     * Context for the payload handler
     */
    public interface Context {
        MinecraftClient client();
        void execute(Runnable runnable);
    }
    
    /**
     * Simple implementation of Context
     */
    private static class SimpleContext implements Context {
        private final MinecraftClient client;
        
        public SimpleContext(MinecraftClient client) {
            this.client = client;
        }
        
        @Override
        public MinecraftClient client() {
            return client;
        }
        
        @Override
        public void execute(Runnable runnable) {
            client.execute(runnable);
        }
    }
} 