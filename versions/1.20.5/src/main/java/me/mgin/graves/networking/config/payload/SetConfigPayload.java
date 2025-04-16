package me.mgin.graves.networking.config.payload;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import me.mgin.graves.Graves;

public record SetConfigPayload() implements CustomPayload {
    public static final CustomPayload.Id<SetConfigPayload> ID = 
        CustomPayload.id(new Identifier(Graves.MOD_ID, "set_config_s2c").toString());
    
    public static final PacketCodec<PacketByteBuf, SetConfigPayload> CODEC = 
        PacketCodec.of(
            (payload, buf) -> {
                // Encoding logic (if any) goes here
            },
            buf -> new SetConfigPayload() // Decoding logic
        );
    
    @Override
    public CustomPayload.Id<SetConfigPayload> getId() {
        return ID;
    }
}
