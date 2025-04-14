package me.mgin.graves.networking;

import net.minecraft.util.Identifier;
import me.mgin.graves.Graves;

/**
 * Defines all packet identifiers used for networking in the graves mod
 */
public class PacketIdentifiers {
    // S2C (Server to Client) packets
    public static final Identifier GRAVE_CREATED = new Identifier(Graves.MOD_ID, "grave_created");
    public static final Identifier GRAVES_SYNC = new Identifier(Graves.MOD_ID, "graves_sync");
    
    // C2S (Client to Server) packets
    public static final Identifier RECOVER_GRAVE = new Identifier(Graves.MOD_ID, "recover_grave");
    public static final Identifier REQUEST_GRAVES = new Identifier(Graves.MOD_ID, "request_graves");
} 