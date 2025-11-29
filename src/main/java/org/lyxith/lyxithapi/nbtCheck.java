package org.lyxith.lyxithapi;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.component.TypedEntityData;

import static org.lyxith.lyxithapi.Lyxithapi.LOGGER;
import static org.lyxith.lyxithapi.Lyxithapi.configNode;

import java.util.Arrays;
import java.util.List;

public class nbtCheck {
    public static boolean checkEntityNbt(TypedEntityData<EntityType<?>> loadingEntityData) {
        CompoundTag nbt = loadingEntityData.copyTagWithoutId();
        if (nbt.getIntArray("Owner").isPresent()) {
            String ownerUUIDString = Arrays.toString(nbt.getIntArray("Owner").get());
            List<String> playerUUIDList = configNode.getNode("enderpearl").get().getList().get();
            return playerUUIDList.contains(ownerUUIDString);
        }
        LOGGER.warn("Passed");
        return false;
    }
}
