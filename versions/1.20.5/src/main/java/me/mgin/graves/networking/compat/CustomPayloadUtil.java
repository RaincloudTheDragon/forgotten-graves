package me.mgin.graves.networking.compat;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.network.codec.PacketCodec;
import io.netty.buffer.Unpooled;

/**
 * Utility class for working with the new CustomPayload system in Minecraft 1.20.5+
 * Provides bridge methods between old and new packet systems
 */
public class CustomPayloadUtil {
    
    /**
     * Creates a PacketByteBuf from a CustomPayload
     * @param payload The payload to convert
     * @return A PacketByteBuf containing the payload data
     */
    public static PacketByteBuf toByteBuf(CustomPayload payload) {
        // TODO: Implement when Fabric API for 1.20.5 is properly set up
        // For now, return an empty buffer
        return new PacketByteBuf(Unpooled.buffer());
    }
    
    /**
     * Creates a CustomPayload from an identifier and a PacketByteBuf
     * This is a bridge method between the old and new systems
     * 
     * @param identifier The packet identifier
     * @param buf The packet data
     * @return A CustomPayload object
     */
    public static CustomPayload fromByteBuf(Identifier identifier, PacketByteBuf buf) {
        // TODO: Implement when Fabric API for 1.20.5 is properly set up
        // For now, return null
        return null;
    }
    
    /**
     * Creates a PacketCodec for a specific payload type
     * @param <T> The payload type
     * @param encoderFunction Function to encode the payload to a buffer
     * @param decoderFunction Function to decode a buffer to a payload
     * @return A codec for the payload type
     */
    public static <T extends CustomPayload> PacketCodec<PacketByteBuf, T> createCodec(
            EncoderFunction<T> encoderFunction, 
            DecoderFunction<T> decoderFunction) {
        return PacketCodec.of(encoderFunction::encode, decoderFunction::decode);
    }
    
    /**
     * Encoder function interface for CustomPayload types
     */
    @FunctionalInterface
    public interface EncoderFunction<T extends CustomPayload> {
        void encode(T payload, PacketByteBuf buf);
    }
    
    /**
     * Decoder function interface for CustomPayload types
     */
    @FunctionalInterface
    public interface DecoderFunction<T extends CustomPayload> {
        T decode(PacketByteBuf buf);
    }
} 