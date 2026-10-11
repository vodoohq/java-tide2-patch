package com.li64.tide.compat.fishingreal;

import com.li64.tide.compat.OptionalModBridge;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class FishingRealCompat {
    public static Entity convertItemStack(ItemStack stack, Player player, Vec3 pos) {
        Object result = OptionalModBridge.callStatic("koala.fishingreal.FishingReal", "convertItemStack", stack, player, pos);
        Entity entity = result instanceof Entity value ? value : null;
        if (entity != null && player.fishing != null)
            OptionalModBridge.callStatic("koala.fishingreal.FishingReal", "fishUpEntity", entity, player.fishing, stack, player);
        return entity;
    }
}
