package org.lyxith.lyxithapi;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class Lyxithapi implements ModInitializer {

    @Override
    public void onInitialize() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            server.execute(() -> {
                sendPermissionPacket(handler.getPlayer());
            });
        });
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            sendPermissionPacket(newPlayer);
        });
        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register(((player, serverLevel, serverLevel1) -> {
            sendPermissionPacket(player);
        }));
    }
    private static void sendPermissionPacket(ServerPlayer player) {
        if (player != null && player.hasPermissions(2)) {
            return; // 已经是OP且有权限，不需要发送
        }
        ClientboundEntityEventPacket packet = new ClientboundEntityEventPacket(player, (byte)26);
        player.connection.send(packet);
    }
    public static void refreshAllPlayers(MinecraftServer server) {
        for(ServerPlayer player : server.getPlayerList().getPlayers()) {
            sendPermissionPacket(player);
        }
    }
}
