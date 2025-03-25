package me.mgin.graves.networking.config.payload;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import me.mgin.graves.Graves;

public record RequestConfigS2CPayload() implements CustomPayload {
    public static final CustomPayload.Id<RequestConfigS2CPayload> ID = 
        CustomPayload.id(new Identifier(Graves.MOD_ID, "request_config_s2c").toString());
    
    public static final PacketCodec<PacketByteBuf, RequestConfigS2CPayload> CODEC = 
        PacketCodec.of(
            (payload, buf) -> {
                // Encoding logic (if any) goes here
            },
            buf -> new RequestConfigS2CPayload() // Decoding logic
        );
    
    @Override
    public CustomPayload.Id<RequestConfigS2CPayload> getId() {
        return ID;
    }
} 