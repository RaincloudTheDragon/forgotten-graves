package me.mgin.graves.client;

import me.mgin.graves.networking.PacketIdentifiers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.network.PacketByteBuf;

@Environment(EnvType.CLIENT)
public class GravesClientNetworking implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Get the client grave manager instance directly
        ClientGraveManager graveManager = ClientGraveManager.getInstance();
        
        // When a grave is created on the server, this packet will be sent to all clients
        ClientPlayNetworking.registerReceiver(PacketIdentifiers.GRAVE_CREATED, (client, handler, buf, responseSender) -> {
            // Use the client queue to execute on the main thread
            client.execute(() -> {
                // Handle the grave created packet using our manager
                graveManager.handleGraveCreated(buf);
                
                // Play a sound effect if needed
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
        ClientPlayNetworking.registerReceiver(PacketIdentifiers.GRAVES_SYNC, (client, handler, buf, responseSender) -> {
            // Use the client queue to execute on the main thread
            client.execute(() -> {
                // Handle the graves sync packet using our manager
                graveManager.handleGravesSync(buf);
            });
        });
        
        // Request an initial sync when client is initialized
        MinecraftClient.getInstance().execute(() -> {
            // Small delay to ensure connection is established
            try {
                Thread.sleep(1000);
                
                // Create an empty packet and send it to request graves
                PacketByteBuf requestBuf = ClientPlayNetworking.create();
                ClientPlayNetworking.send(PacketIdentifiers.REQUEST_GRAVES, requestBuf);
            } catch (InterruptedException e) {
                // Ignore
            }
        });
    }
} 