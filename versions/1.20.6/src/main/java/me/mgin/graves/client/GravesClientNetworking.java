package me.mgin.graves.client;

import me.mgin.graves.networking.compat.ClientPlayNetworking;
import me.mgin.graves.networking.PacketIdentifiers;
import me.mgin.graves.networking.config.ConfigNetworking;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;

@Environment(EnvType.CLIENT)
public class GravesClientNetworking implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Register client-side packet handling for configuration networking
        registerConfigS2CReceivers();
        
        // Get the client grave manager instance
        ClientGraveManager graveManager = ClientGraveManager.getInstance();
        
        // When a grave is created on the server, this packet will be sent to all clients
        ClientPlayNetworking.registerGlobalReceiver(PacketIdentifiers.GRAVE_CREATED, (identifier, buf, context) -> {
            context.execute(() -> {
                // Handle the grave created packet using our manager
                graveManager.handleGraveCreated(buf);
                
                // Play a sound effect if needed
                MinecraftClient client = MinecraftClient.getInstance();
                if (client.player != null) {
                    // Play sound at player's location
                    Identifier soundId = new Identifier("minecraft", "entity.experience_orb.pickup");
                    SoundEvent soundEvent = SoundEvent.of(soundId);
                    client.getSoundManager().play(
                        PositionedSoundInstance.master(soundEvent, 0.5F)
                    );
                }
            });
        });
        
        // When a client requests grave data sync, this packet will be received with the data
        ClientPlayNetworking.registerGlobalReceiver(PacketIdentifiers.GRAVES_SYNC, (identifier, buf, context) -> {
            context.execute(() -> {
                // Handle the graves sync packet using our manager
                graveManager.handleGravesSync(buf);
            });
        });
        
        // Request an initial sync when client is initialized
        MinecraftClient.getInstance().execute(() -> {
            // Small delay to ensure connection is established
            try {
                Thread.sleep(1000);
                graveManager.requestGravesSync();
            } catch (InterruptedException e) {
                // Ignore
            }
        });
    }
    
    private void registerConfigS2CReceivers() {
        // Using SimplePayload as a placeholder
        // Define a simple packet codec for empty payloads
        PacketCodec<PacketByteBuf, ConfigNetworking.SimplePayload> emptyCodec = PacketCodec.of(
            (payload, buf) -> {}, // No data to write
            buf -> new ConfigNetworking.SimplePayload() // No data to read
        );
        
        // Register handlers for the config-related identifiers
        for (Identifier id : new Identifier[] {
            ConfigNetworking.REQUEST_CONFIG_S2C,
            ConfigNetworking.RELOAD_CONFIG_S2C,
            ConfigNetworking.RESET_CONFIG_S2C,
            ConfigNetworking.SET_CONFIG_S2C,
            ConfigNetworking.STORE_CONFIG_S2C
        }) {
            CustomPayload.Id<ConfigNetworking.SimplePayload> payloadId = CustomPayload.id(id.toString());
            
            // Register the payload type
            PayloadTypeRegistry.configurationS2C().register(payloadId, emptyCodec);
            
            // Register the handler
            ClientConfigurationNetworking.registerGlobalReceiver(payloadId, (payload, context) -> {
                // Handle the packet based on the ID
                if (id.equals(ConfigNetworking.REQUEST_CONFIG_S2C)) {
                    // Handle request config
                } else if (id.equals(ConfigNetworking.RELOAD_CONFIG_S2C)) {
                    // Handle reload config
                } else if (id.equals(ConfigNetworking.RESET_CONFIG_S2C)) {
                    // Handle reset config
                }
                // etc...
            });
        }
    }
} 