//? if fabric {
package com.li64.tide.loaders.fabric;

import com.li64.tide.data.TidePlayer;
import com.li64.tide.data.commands.FishingCommand;
import com.li64.tide.data.commands.JournalCommand;
import com.li64.tide.data.fishing.FishData;
import com.li64.tide.events.TideEventHandler;
import com.li64.tide.registries.TideItems;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;



//? if >= 26.2 {
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.world.entity.EntityTypes;
//?} elif >=1.21 {
/*
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
*/
//?} else {
/*
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
*/
//?}

public class FabricEventHandler {
    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            new JournalCommand(dispatcher, registryAccess);
            new FishingCommand(dispatcher, registryAccess);
        });

        ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> {
            CompoundTag data = ((TidePlayer) oldPlayer).tide$getTidePlayerData();
            ((TidePlayer) newPlayer).tide$setTidePlayerData(data);
        });

        ServerLifecycleEvents.SERVER_STARTED.register(server -> TideEventHandler.serverStarted());

        ServerTickEvents.END_SERVER_TICK.register(TideEventHandler::endServerTick);

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
                TideEventHandler.onPlayerJoinWorld(handler.getPlayer()));

        /*? if >=1.21 {*/LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
        /*?} else {*//*LootTableEvents.MODIFY.register((resourceManager, lootDataManager, key, tableBuilder, source) -> {*//*?}*/
            if (key == BuiltInLootTables.FISHING_JUNK) {
                tableBuilder.modifyPools(builder -> builder
                        .add(LootItem.lootTableItem(TideItems.FISH_BONE).setWeight(8)));
            }

            if (key == BuiltInLootTables.UNDERWATER_RUIN_BIG || key == BuiltInLootTables.UNDERWATER_RUIN_SMALL) {
                tableBuilder.pool(new LootPool.Builder().setRolls(ContextIntProviders.between(1, 2))
                        .add(LootItem.lootTableItem(TideItems.BAIT).setWeight(20)
                                .apply(SetItemCountFunction.setCount(ContextIntProviders.exactly(4))))
                        .build()
                );
            }

            if (key == BuiltInLootTables.SHIPWRECK_SUPPLY) {
                tableBuilder.pool(new LootPool.Builder().setRolls(ContextIntProviders.between(1, 2))
                        .add(LootItem.lootTableItem(TideItems.BAIT).setWeight(10)
                                .apply(SetItemCountFunction.setCount(ContextIntProviders.exactly(4))))
                        .add(LootItem.lootTableItem(TideItems.LUCKY_BAIT).setWeight(10)
                                .apply(SetItemCountFunction.setCount(ContextIntProviders.exactly(4))))
                        .build()
                );
            }

            if (key == BuiltInLootTables.BURIED_TREASURE) {
                tableBuilder.pool(new LootPool.Builder().setRolls(ContextIntProviders.exactly(2))
                        .add(LootItem.lootTableItem(TideItems.LUCKY_BAIT).setWeight(5)
                                .apply(SetItemCountFunction.setCount(ContextIntProviders.exactly(4))))
                        .add(LootItem.lootTableItem(TideItems.MAGNETIC_BAIT).setWeight(5)
                                .apply(SetItemCountFunction.setCount(ContextIntProviders.exactly(6))))
                        .build()
                );
            }

            if (key == BuiltInLootTables.ANCIENT_CITY) {
                tableBuilder.pool(new LootPool.Builder().setRolls(ContextIntProviders.exactly(1))
                        .add(LootItem.lootTableItem(TideItems.ECHO_FISHING_ROD))
                                .when(LootItemRandomChanceCondition.randomChance(0.1f))
                        .build()
                );
            }

            if (key == BuiltInLootTables.NETHER_BRIDGE) {
                tableBuilder.pool(new LootPool.Builder().setRolls(ContextIntProviders.exactly(1))
                        .add(LootItem.lootTableItem(TideItems.BLAZING_FISHING_ROD))
                        .when(LootItemRandomChanceCondition.randomChance(0.3f))
                        .build()
                );
            }

            //? if >=26.2 {
            if (key == EntityTypes.ELDER_GUARDIAN.getDefaultLootTable().orElse(null)) {
            //?} else {
            /*
            if (key == EntityType.ELDER_GUARDIAN.getDefaultLootTable()) {
            */
            //?}
                tableBuilder.pool(new LootPool.Builder().setRolls(ContextIntProviders.exactly(1))
                        .add(LootItem.lootTableItem(TideItems.PRISMARINE_FISHING_ROD))
                        .when(LootItemRandomChanceCondition.randomChance(0.5f))
                        .build()
                );
            }
        });

        //?if >=1.21 <26.2 {
        /*
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.FISHERMAN, 4, (factories) -> {
            factories.add((entity, random) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 15),
                    new ItemStack(TideItems.VILLAGE_FISHING_ROD, 1),
                    1, 15, 0.05f
            ));
        });
        */
        //?} else {
        /*
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.FISHERMAN, 4, (factories) -> {
            factories.add((entity, random) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 15),
                    new ItemStack(TideItems.VILLAGE_FISHING_ROD, 1),
                    1, 15, 0.05f
            ));
        });
        */
        //?}

        //? if >=26.2 {
        LootTableEvents.ALL_LOADED.register((resourceManager, registry) ->
                FishData.VANILLA_FISH_TABLE = registry.get(BuiltInLootTables.FISHING_FISH.identifier()).map(net.minecraft.core.Holder.Reference::value).orElse(null));
        //?} elif >=1.21 {
        /*LootTableEvents.ALL_LOADED.register((resourceManager, registry) ->
                FishData.VANILLA_FISH_TABLE = registry.get(BuiltInLootTables.FISHING_FISH.location()));*/
        //?} else {
        /*LootTableEvents.ALL_LOADED.register((resourceManager, lootDataManager) ->
                FishData.VANILLA_FISH_TABLE = lootDataManager.getLootTable(BuiltInLootTables.FISHING_FISH));*/
        //?}
    }
}
//?}
