package org.lyxith.lyxithapi.mixin;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.GameModeArgument;
import net.minecraft.server.commands.GameModeCommand;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.Collections;

@Mixin(GameModeCommand.class)
public class GmCmdMixin {
    @Shadow
    private static int setMode(CommandContext<CommandSourceStack> commandContext, Collection<ServerPlayer> players, GameType gameType) {
        // 这个方法体不会被实际执行，Mixin会在运行时重定向到原版方法
        return 0;
    }
    @Inject(
            method = "register",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void registerWithCustomPermissions(CommandDispatcher<CommandSourceStack> dispatcher, CallbackInfo ci) {
        ci.cancel();

        dispatcher.register(
                Commands.literal("gamemode")
                        .requires(source -> true)
                        .then(Commands.argument("gamemode", GameModeArgument.gameMode())
                                .executes(commandContext -> {
                                    return setMode(commandContext,
                                            Collections.singleton(commandContext.getSource().getPlayerOrException()),
                                            GameModeArgument.getGameMode(commandContext, "gamemode"));
                                })
                                .then(Commands.argument("target", EntityArgument.players())
                                        .requires(Commands.hasPermission(2))
                                        .executes(commandContext ->
                                                setMode(commandContext,
                                                        EntityArgument.getPlayers(commandContext, "target"),
                                                        GameModeArgument.getGameMode(commandContext, "gamemode"))))
                        )
        );
    }
}
