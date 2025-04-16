package me.mgin.graves.networking.config.payload;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import me.mgin.graves.Graves;

public record ResetConfigS2CPayload() implements CustomPayload {
    public static final CustomPayload.Id<ResetConfigS2CPayload> ID = 
        CustomPayload.id(new Identifier(Graves.MOD_ID, "reset_config_s2c").toString());
    
    public static final PacketCodec<PacketByteBuf, ResetConfigS2CPayload> CODEC = 
        PacketCodec.of(
            (payload, buf) -> {
                // Encoding logic (if any) goes here
            },
            buf -> new ResetConfigS2CPayload() // Decoding logic
        );
    
    @Override
    public CustomPayload.Id<ResetConfigS2CPayload> getId() {
        return ID;
    }
} 