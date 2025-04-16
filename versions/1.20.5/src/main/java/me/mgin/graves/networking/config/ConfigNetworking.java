package me.mgin.graves.networking.config;

import me.mgin.graves.Graves;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public class ConfigNetworking {
    // Keep the existing Identifiers for backward compatibility
    public static final Identifier SYNC_CONFIG_C2S_ID = new Identifier(Graves.MOD_ID, "sync_config_c2s");
    public static final Identifier STORE_CONFIG_C2S_ID = new Identifier(Graves.MOD_ID, "store_config_c2s");
    
    public static final CustomPayload.Id<SimplePayload> SYNC_CONFIG_C2S = CustomPayload.id(SYNC_CONFIG_C2S_ID.toString());
    public static final CustomPayload.Id<SimplePayload> STORE_CONFIG_C2S = CustomPayload.id(STORE_CONFIG_C2S_ID.toString());
    
    // Define S2C payload IDs using Identifier for now
    public static final Identifier REQUEST_CONFIG_S2C = new Identifier(Graves.MOD_ID, "request_config_s2c");
    public static final Identifier RELOAD_CONFIG_S2C = new Identifier(Graves.MOD_ID, "reload_config_s2c");
    public static final Identifier RESET_CONFIG_S2C = new Identifier(Graves.MOD_ID, "reset_config_s2c");
    public static final Identifier SET_CONFIG_S2C = new Identifier(Graves.MOD_ID, "set_config_s2c");
    public static final Identifier STORE_CONFIG_S2C = new Identifier(Graves.MOD_ID, "store_config_s2c");

    public static void registerC2SPackets() {
        // Define a simple packet codec for empty payloads
        PacketCodec<PacketByteBuf, SimplePayload> emptyCodec = PacketCodec.of(
            (payload, buf) -> {}, // No data to write
            buf -> new SimplePayload() // No data to read
        );
        
        // Register the client-to-server packets
        PayloadTypeRegistry.configurationC2S().register(SYNC_CONFIG_C2S, emptyCodec);
        PayloadTypeRegistry.configurationC2S().register(STORE_CONFIG_C2S, emptyCodec);
        
        // Register the packet handlers
        ServerConfigurationNetworking.registerGlobalReceiver(SYNC_CONFIG_C2S, (payload, context) -> {
            // Handle the sync config payload from client
            // Implementation will depend on your config system
        });
        
        ServerConfigurationNetworking.registerGlobalReceiver(STORE_CONFIG_C2S, (payload, context) -> {
            // Handle the store config payload from client
            // Implementation will depend on your config system
        });
    }

    public static void registerS2CPackets() {
        // No need to register S2C payload types here as they are registered on the client
        // This method is kept for compatibility
    }
    
    // A simple placeholder payload class until proper payloads are implemented
    public static class SimplePayload implements CustomPayload {
        private CustomPayload.Id<SimplePayload> id;
        
        public SimplePayload() {
            // Default constructor for decoding
        }
        
        public SimplePayload(CustomPayload.Id<SimplePayload> id) {
            this.id = id;
        }
        
        @Override
        public CustomPayload.Id<?> getId() {
            return id;
        }
    }
}