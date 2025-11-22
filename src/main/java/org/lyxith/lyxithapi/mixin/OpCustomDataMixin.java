package org.lyxith.lyxithapi.mixin;

import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

import static net.minecraft.world.entity.EntityType.*;

@Mixin(EntityType.class)
public abstract class OpCustomDataMixin {
    @Mutable
    @Shadow @Final
    private static Set<EntityType<?>> OP_ONLY_CUSTOM_DATA;
    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void onStaticBlockEnd(CallbackInfo ci) {
        OP_ONLY_CUSTOM_DATA = Set.of(
                FALLING_BLOCK,
                COMMAND_BLOCK_MINECART,
                SPAWNER_MINECART,
                ENDER_PEARL
        );
    }
}
