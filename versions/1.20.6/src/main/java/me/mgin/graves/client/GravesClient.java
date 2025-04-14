package me.mgin.graves.client;

import me.mgin.graves.block.GraveBlocks;
import me.mgin.graves.block.render.GraveBlockEntityRenderer;
import me.mgin.graves.config.GravesConfig;
import me.mgin.graves.networking.config.ConfigNetworking;
import me.mgin.graves.networking.config.event.ConfigNetworkingEvents;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class GravesClient implements ClientModInitializer {
    public static GravesConfig SERVER_CONFIG = null;

    // Access to the client grave manager
    public static ClientGraveManager GRAVE_MANAGER = ClientGraveManager.getInstance();

    @Override
    public void onInitializeClient() {
        // Register block entity renderer
        BlockEntityRendererFactories.register(GraveBlocks.GRAVE_BLOCK_ENTITY, GraveBlockEntityRenderer::new);
        
        // Register config networking
        ConfigNetworkingEvents.registerClientEvents();
        ConfigNetworking.registerS2CPackets();
    }
}
