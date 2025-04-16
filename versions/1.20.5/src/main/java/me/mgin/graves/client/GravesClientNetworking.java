package me.mgin.graves.client;

import me.mgin.graves.networking.PacketIdentifiers;
import me.mgin.graves.networking.config.event.ConfigNetworkingEvents;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Handles client-side networking initialization for 1.20.5
 */
@Environment(EnvType.CLIENT)
public class GravesClientNetworking implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Register networking event handlers
        ConfigNetworkingEvents.registerClientEvents();
        
        System.out.println("Graves client networking initialized for 1.20.5!");
    }
} 