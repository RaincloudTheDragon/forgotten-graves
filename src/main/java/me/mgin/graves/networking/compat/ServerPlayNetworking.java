package me.mgin.graves.networking.compat;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.network.PacketByteBuf;

/**
 * Temporary compatibility class to handle compilation with 1.20.5
 * This will be replaced with the actual Fabric API implementation when available
 */
public class ServerPlayNetworking {
    
    /**
     * Temporary method to support old code
     */
    public static void send(ServerPlayerEntity player, Identifier identifier, PacketByteBuf buf) {
        // TODO: Implement when Fabric API for 1.20.5 is properly set up
        // This is a compatibility method for old code
    }
    
    /**
     * New method for 1.20.5
     */
    public static void send(ServerPlayerEntity player, CustomPayload payload) {
        // TODO: Implement when Fabric API for 1.20.5 is properly set up
    }
} 