package me.mgin.graves.networking.config.payload;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload; // Ensure this is the correct import
import net.minecraft.util.Identifier; // Use Identifier instead of Id

import me.mgin.graves.Graves;

public record ResetConfigPayload() implements CustomPayload {
    public static final CustomPayload.Id<ResetConfigPayload> ID = 
        CustomPayload.id(new Identifier(Graves.MOD_ID, "reset_config_s2c").toString());
    
    // Define the codec for encoding and decoding the payload
    public static final PacketCodec<PacketByteBuf, ResetConfigPayload> CODEC = 
        PacketCodec.of(
            (payload, buf) -> {
                // Encoding logic (if any) goes here
                // For example, if you had data to write:
                // buf.writeString(payload.someData);
            },
            buf -> new ResetConfigPayload() // Decoding logic
        );
    
    @Override
        public CustomPayload.Id<ResetConfigPayload> getId() {
        return ID;
    }
}