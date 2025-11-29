package org.lyxith.lyxithapi.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Arrays;
import java.util.List;

import static org.lyxith.lyxithapi.Lyxithapi.*;


public class EnderPearlCmd {
    public static LiteralArgumentBuilder<CommandSourceStack> enderPearlCmd = Commands.literal("enderpearl").executes(context -> {
        ServerPlayer player = context.getSource().getPlayerOrException();
        CompoundTag nbt =  NbtPredicate.getEntityTagToCompare(player);
        if (nbt.contains("UUID")) {
            String playerUUID = Arrays.toString(nbt.getIntArray("UUID").get());
            List<String> playerUUIDList = configNode.getNode("enderpearl").get().getList().get();
            if (playerUUIDList.contains(playerUUID)) {
                playerUUIDList.remove(playerUUID);
                context.getSource().sendSuccess(() ->Component.literal("Ender pearl fix disable"),true);
            } else {
                playerUUIDList.addLast(playerUUID);
                context.getSource().sendSuccess(() ->Component.literal("Ender pearl fix enable"),true);
            }
            configNode.getNode("enderpearl").get().set(playerUUIDList);
            configAPI.saveConfig(modId, configName, configNode);
        }
        return 0;
    });
}
