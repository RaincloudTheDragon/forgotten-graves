package me.mgin.graves.compat;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Compatibility layer for Fabric networking API changes in 1.20.5
 */
public class NetworkingCompat {

    public static void send(ServerPlayerEntity player, Identifier channel, PacketByteBuf buf) {
        try {
            // Try legacy send(Identifier, PacketByteBuf) for <1.20.5
            Method send = Class.forName("net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking")
                .getMethod("send", ServerPlayerEntity.class, Identifier.class, PacketByteBuf.class);
            send.invoke(null, player, channel, buf);
            return;
        } catch (NoSuchMethodException e) {
            // 1.20.5+ uses CustomPayload - config sync disabled for now
            // TODO: Implement CustomPayload-based config sync for 1.20.5
        } catch (Exception e) {
            System.err.println("Failed to send config packet: " + e.getMessage());
        }
    }
}
