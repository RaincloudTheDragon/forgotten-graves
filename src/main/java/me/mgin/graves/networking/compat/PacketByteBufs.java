package me.mgin.graves.networking.compat;

import net.minecraft.network.PacketByteBuf;
import io.netty.buffer.Unpooled;

/**
 * Temporary compatibility class to handle compilation with 1.20.5
 * This will be replaced with the actual Fabric API implementation when available
 */
public class PacketByteBufs {
    
    /**
     * Creates an empty PacketByteBuf
     */
    public static PacketByteBuf create() {
        // This is a temporary implementation
        return new PacketByteBuf(Unpooled.buffer());
    }
} 