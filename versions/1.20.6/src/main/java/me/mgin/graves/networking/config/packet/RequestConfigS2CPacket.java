package me.mgin.graves.networking.config.packet;

import me.mgin.graves.config.GravesConfig;
import me.mgin.graves.networking.config.ConfigNetworking;
import me.mgin.graves.networking.compat.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import me.mgin.graves.networking.NetworkHelper;

public class RequestConfigS2CPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf _buf,
                               PacketSender sender) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(GravesConfig.getConfig().serialize());
        NetworkHelper.sendToServer(ConfigNetworking.SYNC_CONFIG_C2S, buf);
    }
}
