package me.mgin.graves.networking.config.payload;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import me.mgin.graves.Graves;

public record StoreConfigC2SPayload() implements CustomPayload {
    public static final CustomPayload.Id<StoreConfigC2SPayload> ID = 
        CustomPayload.id(new Identifier(Graves.MOD_ID, "store_config_c2s").toString());
    
    public static final PacketCodec<PacketByteBuf, StoreConfigC2SPayload> CODEC = 
        PacketCodec.of(
            (payload, buf) -> {
                // Encoding logic (if any) goes here
            },
            buf -> new StoreConfigC2SPayload() // Decoding logic
        );
    
    @Override
    public CustomPayload.Id<StoreConfigC2SPayload> getId() {
        return ID;
    }
} 