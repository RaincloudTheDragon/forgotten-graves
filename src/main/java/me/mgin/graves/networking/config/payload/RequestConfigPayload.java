package me.mgin.graves.networking.config.payload;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload; // Ensure this is the correct import
import net.minecraft.util.Identifier; // Use Identifier instead of Id
import me.mgin.graves.Graves;

public record RequestConfigPayload() implements CustomPayload {
    public static final CustomPayload.Id<RequestConfigPayload> ID = 
        CustomPayload.id(new Identifier(Graves.MOD_ID, "request_config_s2c").toString()); // Updated usage
    
    // Define the codec for encoding and decoding the payload
    public static final PacketCodec<PacketByteBuf, RequestConfigPayload> CODEC = 
        PacketCodec.of(
            (payload, buf) -> {
                // Encoding logic (if any) goes here
            },
            buf -> new RequestConfigPayload() // Decoding logic
        );
    
    @Override
    public CustomPayload.Id<RequestConfigPayload> getId() {
        return ID; // Updated return type
    }
}