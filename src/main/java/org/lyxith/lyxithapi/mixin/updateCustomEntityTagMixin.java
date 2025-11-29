package org.lyxith.lyxithapi.mixin;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static org.lyxith.lyxithapi.Lyxithapi.LOGGER;
import static org.lyxith.lyxithapi.Lyxithapi.sendAdminBroadcast;
import static org.lyxith.lyxithapi.nbtCheck.checkEntityNbt;

@Mixin(EntityType.class)
public class updateCustomEntityTagMixin {
    @Inject(method = "updateCustomEntityTag", at = @At("HEAD"), cancellable = true)
    private static void updateCustomEntityTagModified(Level level, LivingEntity livingEntity, Entity entity, TypedEntityData<EntityType<?>> typedEntityData, CallbackInfo ci) {
        ci.cancel();
        MinecraftServer minecraftServer = level.getServer();
        if (minecraftServer != null && entity != null) {
            if (entity.getType() == typedEntityData.type()) {
                if (!level.isClientSide() && entity.getType().onlyOpCanSetNbt() && checkEntityNbt(typedEntityData)) {
                    if (!(livingEntity instanceof Player player)) {
                        sendAdminBroadcast("Removed some nbt,caused by block, pos : "+ entity.getOnPos().toShortString());
                        return;
                    }

                    if (!minecraftServer.getPlayerList().isOp(player.nameAndId())) {
                        sendAdminBroadcast("Removed some nbt,caused by player : "+player.getName().getString()+",pos : "+player.getOnPos().toShortString());
                        return;
                    }
                }
                typedEntityData.loadInto(entity);
            }
        }
    }
}
