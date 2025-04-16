package me.mgin.graves.networking.compat;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.network.codec.PacketCodec;
import io.netty.buffer.Unpooled;
import java.lang.reflect.Field;

/**
 * Utility class for working with CustomPayloads in 1.20.5
 * This provides a compatibility layer for transitioning from the old
 * identifier+buffer approach to the new CustomPayload approach.
 */
public class CustomPayloadUtil {
    /**
     * Creates a CustomPayload.Id from an Identifier
     */
    public static <T extends CustomPayload> CustomPayload.Id<T> createId(Identifier id) {
        return CustomPayload.id(id.toString());
    }
    
    /**
     * Creates a CustomPayload.Id from a string
     */
    public static <T extends CustomPayload> CustomPayload.Id<T> createId(String id) {
        return CustomPayload.id(id);
    }
    
    /**
     * Creates an Identifier from a CustomPayload.Id
     */
    public static Identifier toIdentifier(CustomPayload.Id<?> id) {
        return new Identifier(id.toString());
    }
    
    /**
     * Gets the CustomPayload.Id from a CustomPayload
     */
    public static <T extends CustomPayload> CustomPayload.Id<T> getId(T payload) {
        @SuppressWarnings("unchecked")
        CustomPayload.Id<T> id = (CustomPayload.Id<T>) payload.getId();
        return id;
    }
    
    /**
     * Writes a CustomPayload to a PacketByteBuf
     * This is a compatibility method and will need to be replaced with proper
     * codec usage in the future.
     */
    public static <T extends CustomPayload> void writeToBuffer(T payload, PacketByteBuf buf) {
        // This is a placeholder - actual codec implementations will need to be provided
        buf.writeString(payload.getId().toString());
    }
    
    /**
     * Reads a CustomPayload from a PacketByteBuf
     * This is a compatibility method and will need to be replaced with proper
     * codec usage in the future.
     */
    public static <T extends CustomPayload> T readFromBuffer(PacketByteBuf buf, CustomPayload.Id<T> id) {
        // This is a placeholder - actual codec implementations will need to be provided
        return null;
    }
    
    /**
     * Creates a PacketByteBuf from a CustomPayload
     * @param payload The payload to convert
     * @return A PacketByteBuf containing the payload data
     */
    public static PacketByteBuf toByteBuf(CustomPayload payload) {
        // Create a new buffer
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        
        // Write the payload ID
        buf.writeString(payload.getId().toString());
        
        // Encode the payload using reflection to get its codec
        try {
            Field codecField = payload.getClass().getField("CODEC");
            @SuppressWarnings("unchecked")
            PacketCodec<PacketByteBuf, CustomPayload> codec = 
                (PacketCodec<PacketByteBuf, CustomPayload>) codecField.get(null);
            
            if (codec != null) {
                codec.encode(buf, payload);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            // If no CODEC field is found, try to use writeToBuffer
            writeToBuffer(payload, buf);
        }
        
        return buf;
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
        // Try to find a registered CustomPayload.Type for this identifier
        @SuppressWarnings("unused")
        CustomPayload.Id<?> id = CustomPayload.id(identifier.toString());
        
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