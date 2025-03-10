package me.mgin.graves.networking.config.payload;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import me.mgin.graves.Graves;

public record SyncConfigPayload() implements CustomPayload {
    public static final CustomPayload.Id<SyncConfigPayload> ID = 
        CustomPayload.id(new Identifier(Graves.MOD_ID, "sync_config_c2s").toString());
    
    public static final PacketCodec<PacketByteBuf, SyncConfigPayload> CODEC = 
        PacketCodec.of(
            (payload, buf) -> {
                // Encoding logic (if any) goes here
            },
            buf -> new SyncConfigPayload() // Decoding logic
        );
    
    @Override
    public CustomPayload.Id<SyncConfigPayload> getId() {
        return ID;
    }
}
