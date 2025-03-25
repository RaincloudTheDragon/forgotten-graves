package me.mgin.graves.networking.config;

import me.mgin.graves.Graves;
import me.mgin.graves.networking.config.payload.*;

import net.minecraft.util.Identifier;
import net.minecraft.network.packet.CustomPayload;

public class ConfigNetworking {
    // Keep the existing Identifiers for backward compatibility
    public static final CustomPayload.Id<SyncConfigPayload> SYNC_CONFIG_C2S = SyncConfigPayload.ID;
    public static final CustomPayload.Id<StoreConfigC2SPayload> STORE_CONFIG_C2S = StoreConfigC2SPayload.ID;
    
    // Define S2C payload IDs using Identifier for now
    public static final Identifier REQUEST_CONFIG_S2C = new Identifier(Graves.MOD_ID, "request_config_s2c");
    public static final Identifier RELOAD_CONFIG_S2C = new Identifier(Graves.MOD_ID, "reload_config_s2c");
    public static final Identifier RESET_CONFIG_S2C = new Identifier(Graves.MOD_ID, "reset_config_s2c");
    public static final Identifier SET_CONFIG_S2C = new Identifier(Graves.MOD_ID, "set_config_s2c");
    public static final Identifier STORE_CONFIG_S2C = new Identifier(Graves.MOD_ID, "store_config_s2c");

    public static void registerC2SPackets() {
        // TODO: Implement when Fabric API for 1.20.5 is properly set up
        // For now, we'll leave this as a placeholder
        // In 1.20.5, we need to use PayloadTypeRegistry and ServerPlayNetworking
    }

    public static void registerS2CPackets() {
        // TODO: Implement when Fabric API for 1.20.5 is properly set up
        // For now, we'll leave this as a placeholder
        // In 1.20.5, we need to use PayloadTypeRegistry
    }
}