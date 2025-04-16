package me.mgin.graves.networking.compat;

import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;

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
    
    /**
     * Copies the contents of a PacketByteBuf
     */
    public static PacketByteBuf copy(PacketByteBuf buf) {
        PacketByteBuf copy = create();
        copy.writeBytes(buf.slice());
        return copy;
    }
} 