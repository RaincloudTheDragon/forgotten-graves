package me.mgin.graves.networking.config.payload;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import me.mgin.graves.Graves;

public record StoreConfigPayload() implements CustomPayload {
    public static final CustomPayload.Id<StoreConfigPayload> ID = 
        CustomPayload.id(new Identifier(Graves.MOD_ID, "store_config_s2c").toString());
    
    public static final PacketCodec<PacketByteBuf, StoreConfigPayload> CODEC = 
        PacketCodec.of(
            (payload, buf) -> {
                // Encoding logic (if any) goes here
            },
            buf -> new StoreConfigPayload() // Decoding logic
        );
    
    @Override
    public CustomPayload.Id<StoreConfigPayload> getId() {
        return ID;
    }
}
