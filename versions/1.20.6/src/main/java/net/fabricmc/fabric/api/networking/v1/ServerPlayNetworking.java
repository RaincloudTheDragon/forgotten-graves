package net.fabricmc.fabric.api.networking.v1;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

/**
 * Server networking compatibility class for 1.20.6
 * Delegates to our own internal implementation
 */
public class ServerPlayNetworking {
    /**
     * Send a packet to a player
     */
    public static void send(ServerPlayerEntity player, Identifier identifier, PacketByteBuf buf) {
        // Delegate to our own compatibility layer
        me.mgin.graves.networking.compat.ServerPlayNetworking.send(player, identifier, buf);
    }
    
    /**
     * Register a packet receiver
     */
    public static void registerReceiver(Identifier identifier, PlayChannelHandler handler) {
        // Delegate to our own compatibility layer
        me.mgin.graves.networking.compat.ServerPlayNetworking.registerGlobalReceiver(identifier, 
            (player, id, buf, context) -> {
                // Create a sender to pass to the original handler
                PacketSender sender = new PacketSender() {
                    @Override
                    public void sendPacket(Identifier channelId, PacketByteBuf data) {
                        ServerPlayNetworking.send(player, channelId, data);
                    }
                };
                
                // Call the original handler
                handler.receive(player, buf, sender);
            }
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
        void receive(ServerPlayerEntity player, PacketByteBuf buf, PacketSender responseSender);
    }
    
    /**
     * Interface for sending packets
     */
    public interface PacketSender {
        void sendPacket(Identifier id, PacketByteBuf buf);
    }
} 