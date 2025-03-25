package me.mgin.graves.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class GravesClientNetworking implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // TODO: Implement when Fabric API for 1.20.5 is properly set up
        // Will register S2C receivers using ClientPlayNetworking.registerGlobalReceiver
    }
} 