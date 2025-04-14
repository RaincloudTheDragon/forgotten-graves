package me.mgin.graves.client;

import me.mgin.graves.networking.PacketIdentifiers;
import me.mgin.graves.networking.compat.ClientPlayNetworking;
import me.mgin.graves.networking.compat.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages client-side grave data
 */
public class ClientGraveManager {
    private static final ClientGraveManager INSTANCE = new ClientGraveManager();
    
    // Map of player UUID to their grave positions
    private final Map<UUID, Map<BlockPos, GraveInfo>> playerGraves = new HashMap<>();
    // The most recently created grave, used for visual effects
    private GraveInfo lastCreatedGrave = null;
    // Whether the client has performed initial sync
    private boolean hasInitialized = false;
    
    private ClientGraveManager() {
        // Private constructor for singleton
    }
    
    /**
     * Get the singleton instance
     */
    public static ClientGraveManager getInstance() {
        return INSTANCE;
    }
    
    /**
     * Request a sync of all graves from the server
     */
    public void requestGravesSync() {
        PacketByteBuf buf = PacketByteBufs.create();
        ClientPlayNetworking.send(PacketIdentifiers.REQUEST_GRAVES, buf);
    }
    
    /**
     * Handle a grave created packet
     */
    public void handleGraveCreated(PacketByteBuf buf) {
        try {
            // Read data from the buffer
            UUID playerId = buf.readUuid();
            BlockPos pos = buf.readBlockPos();
            long timestamp = buf.readLong();
            String playerName = buf.readString();
            
            // Create grave info and store it
            GraveInfo graveInfo = new GraveInfo(playerId, pos, timestamp, playerName);
            
            // Create player entry if it doesn't exist
            playerGraves.computeIfAbsent(playerId, k -> new HashMap<>());
            // Store the grave
            playerGraves.get(playerId).put(pos, graveInfo);
            
            // Set as last created grave for visual effects
            lastCreatedGrave = graveInfo;
            
            // Display notification
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player != null && client.player.getUuid().equals(playerId)) {
                // If it's the player's own grave, show more prominent notification
                client.inGameHud.setOverlayMessage(
                    net.minecraft.text.Text.translatable("notification.graves.grave_created", 
                    pos.getX(), pos.getY(), pos.getZ()), false);
            }
            
            System.out.println("Client received grave created at " + pos.toShortString() + " for player " + playerName);
        } catch (Exception e) {
            System.err.println("Error handling grave created packet: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Handle a graves sync packet
     */
    public void handleGravesSync(PacketByteBuf buf) {
        try {
            // Clear existing graves
            playerGraves.clear();
            
            // Read number of graves
            int graveCount = buf.readVarInt();
            
            // Read each grave
            for (int i = 0; i < graveCount; i++) {
                UUID playerId = buf.readUuid();
                BlockPos pos = buf.readBlockPos();
                long timestamp = buf.readLong();
                String playerName = buf.readString();
                
                // Create grave info
                GraveInfo graveInfo = new GraveInfo(playerId, pos, timestamp, playerName);
                
                // Create player entry if it doesn't exist
                playerGraves.computeIfAbsent(playerId, k -> new HashMap<>());
                // Store the grave
                playerGraves.get(playerId).put(pos, graveInfo);
            }
            
            // Set initialized flag
            hasInitialized = true;
            
            System.out.println("Client received graves sync with " + graveCount + " graves");
        } catch (Exception e) {
            System.err.println("Error handling graves sync packet: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Get all graves for a specific player
     */
    public Map<BlockPos, GraveInfo> getGravesForPlayer(UUID playerId) {
        return playerGraves.getOrDefault(playerId, new HashMap<>());
    }
    
    /**
     * Get the last created grave
     */
    public GraveInfo getLastCreatedGrave() {
        return lastCreatedGrave;
    }
    
    /**
     * Check if the client has performed initial sync
     */
    public boolean hasInitialized() {
        return hasInitialized;
    }
    
    /**
     * Class to store grave information
     */
    public static class GraveInfo {
        private final UUID playerId;
        private final BlockPos position;
        private final long timestamp;
        private final String playerName;
        
        public GraveInfo(UUID playerId, BlockPos position, long timestamp, String playerName) {
            this.playerId = playerId;
            this.position = position;
            this.timestamp = timestamp;
            this.playerName = playerName;
        }
        
        public UUID getPlayerId() {
            return playerId;
        }
        
        public BlockPos getPosition() {
            return position;
        }
        
        public long getTimestamp() {
            return timestamp;
        }
        
        public String getPlayerName() {
            return playerName;
        }
    }
} 