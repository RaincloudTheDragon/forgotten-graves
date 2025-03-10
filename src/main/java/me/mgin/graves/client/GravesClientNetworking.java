package me.mgin.graves.client;

import me.mgin.graves.networking.config.ConfigNetworking;
import me.mgin.graves.networking.config.payload.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;

@Environment(EnvType.CLIENT)
public class GravesClientNetworking implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Will register receivers when Fabric API for 1.20.5 is properly set up
    }

    private void registerS2CReceivers() {
        // TODO: Implement when Fabric API for 1.20.5 is properly set up
        // For now, we'll leave this as a placeholder
        // In 1.20.5, we need to use ClientPlayNetworking.registerGlobalReceiver
    }
} 