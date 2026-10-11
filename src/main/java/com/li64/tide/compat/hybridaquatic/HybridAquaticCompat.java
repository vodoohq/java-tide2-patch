package com.li64.tide.compat.hybridaquatic;

import com.li64.tide.registries.entities.misc.fishing.TideFishingHook;
import com.li64.tide.compat.OptionalModBridge;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

//? if <1.21 {
/*
import dev.hybridlabs.aquatic.entity.HAEntityTypes;
import dev.hybridlabs.aquatic.item.HAItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.phys.Vec3;
*/
//?}

public class HybridAquaticCompat {
        public static Entity convertEntity(ItemEntity itemEntity, Player player, TideFishingHook hook) {
                Entity newEntity = null;
                //? if <1.21 {
                /*
                ItemStack hookItem = hook.getHook();

                if (hookItem.is(HAItems.INSTANCE.getOMINOUS_HOOK().get())) {
                var karkinosType = HAEntityTypes.INSTANCE.getKARKINOS().get();
                newEntity = createAndLaunchEntityAtPlayer(karkinosType, player, hook.blockPosition());
                if (newEntity != null) hook.clearHookItem();
                }
                if (hookItem.is(HAItems.INSTANCE.getCREEPERMAGNET_HOOK().get())) {
                var creeperType = EntityType.CREEPER;
                newEntity = createAndLaunchEntityAtPlayer(creeperType, player, hook.blockPosition());
                if (newEntity != null) hook.clearHookItem();
                }
                */
                //?}

                return newEntity == null ? itemEntity : newEntity;
        }

        //? if <1.21 {
        /*
        private static Entity createAndLaunchEntityAtPlayer(EntityType<?> entityType, Player player, BlockPos pos) {
                if (player.level() instanceof ServerLevel serverLevel) {
                        Entity entity = entityType.spawn(serverLevel, pos, MobSpawnType.MOB_SUMMONED);
                        if (entity == null) return null;
                        double modifier = 0.15;
                        Vec3 vecBetween = player.position().subtract(Vec3.atCenterOf(pos));
                        Vec3 vecBetweenMod = vecBetween.multiply(modifier, modifier, modifier);
                        var yOffset = Math.sqrt(Math.sqrt(Math.pow(vecBetween.x, 2)
                                + Math.pow(vecBetween.y, 2)
                                + Math.pow(vecBetween.z, 2))) * 0.08;
                        entity.setDeltaMovement(
                                vecBetweenMod.x,
                                vecBetweenMod.y + yOffset,
                                vecBetweenMod.z
                        );
                        return entity;
                }
                return null;
        }
        */
        //?}

        // Im not sure how to change this appropriately but its probably not needed anymore -Mystic
        public static void applyVariant(Entity entity, ItemStack stack) {
                if (entity.getClass().getName().equals("dev.hybridlabs.aquatic.entity.fish.StingrayEntity")) {
                        String variant = BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath().contains("blue")
                                ? "BLUE_SPOTTED" : "SPOTTED_EAGLE";
                        Object value = OptionalModBridge.enumValue("dev.hybridlabs.aquatic.entity.fish.StingrayEntity$Companion$Type", variant);
                        if (value != null) OptionalModBridge.call(entity, "setVariant", value);
                }
        }
        
}
