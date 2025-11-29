package org.lyxith.lyxithapi;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.lyxith.lyxithconfig.api.LyXithConfigAPI;
import org.lyxith.lyxithconfig.api.LyXithConfigNodeImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.lyxith.lyxithapi.commands.EnderPearlCmd.*;

public class Lyxithapi implements ModInitializer {
    private static final String defaultHelpInfo = "test help information";
    public static final String modId = "LyXithAPI";
    public static final String configName = "LyxithAPI";
    public static LyXithConfigNodeImpl configNode = new LyXithConfigNodeImpl();
    public static final Logger LOGGER = LoggerFactory.getLogger(modId);
    public static MinecraftServer server;
    public static LyXithConfigAPI configAPI;
    @Override
    public void onInitialize() {
        List<LyXithConfigAPI> apiInstances = FabricLoader.getInstance()
                .getEntrypoints("lyxithconfig-api", LyXithConfigAPI.class);

        if (apiInstances.isEmpty()) {
            System.err.println("LyXithConfig API 入口点未找到，可能是版本不兼容");
        } else if (apiInstances.size() == 1) {
            configAPI = apiInstances.getFirst();
        }
        initConfig();
        ServerLifecycleEvents.SERVER_STARTING.register(minecraftServer -> {
            server = minecraftServer;
        });
        CommandRegistrationCallback.EVENT.register(((dispatcher, commandBuildContext, commandSelection) -> {
            dispatcher.register(enderPearlCmd);
        }));
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
    public static void sendAdminBroadcast(String message) {
        Component broadcastMsg = Component.literal(message)
                .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC);

        // 遍历所有在线玩家
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            // 检查玩家是否为 OP（管理员）
            if (server.getPlayerList().isOp(player.nameAndId())) {
                player.sendSystemMessage(broadcastMsg);
            }
        }

        // 同时发送到服务器控制台
        server.sendSystemMessage(broadcastMsg);
    }
    private void initConfig() {
        if (!configAPI.modConfigDirExist(modId)) {
            configAPI.createModConfigDir(modId);
        }
        if (!configAPI.modConfigExist(modId, configName)) {
            configAPI.createModConfig(modId, configName);
        }
        configAPI.loadConfig(modId, configName);
        configNode = configAPI.getConfigRootNode(modId, configName).getRoot();
        if (configNode.getNode("enderpearl").isEmpty()) {
            configNode.initNode("enderpearl", false, new ArrayList<String>());
        }

        List<String> playerUUIDList = (List<String>) configNode.getNode("enderpearl").get().getList().get();
        configNode.initNode("enderpearl", false, playerUUIDList);
        configNode.initNode("helpInfo", false, defaultHelpInfo);
        configAPI.saveConfig(modId, configName, configNode);
    }
}
