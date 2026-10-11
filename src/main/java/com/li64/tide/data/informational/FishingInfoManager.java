package com.li64.tide.data.informational;

import com.li64.tide.Tide;
import com.li64.tide.compat.CompatHelper;
import com.li64.tide.data.TideTags;
import com.li64.tide.network.messages.InfoDataMsg;
import com.li64.tide.registries.items.InfoItemContainer;
import com.li64.tide.registries.items.InformationalItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
//? if >=26.2 {
import net.minecraft.resources.Identifier;
//?} else {
/*
import net.minecraft.resources.ResourceLocation;
*/
//?}
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

public class FishingInfoManager {
        //? if >=26.2 {
    private static final Map<UUID, Map<Identifier, String>> CACHE = new HashMap<>();
    private static final Map<UUID, Map<Identifier, Integer>> TIMERS = new HashMap<>();
        //?} else {
        /*
    private static final Map<UUID, Map<ResourceLocation, String>> CACHE = new HashMap<>();
    private static final Map<UUID, Map<ResourceLocation, Integer>> TIMERS = new HashMap<>();
        */
        //?}
    private static final Map<UUID, Integer> PROXIMITY_SCAN_TIMER = new HashMap<>();
    private static final Map<UUID, Set<Item>> NEARBY_BLOCK_ITEMS = new HashMap<>();

    private static boolean hasNewData = false;

    public static void tick(ServerPlayer player) {
        UUID id = player.getUUID();

        CACHE.putIfAbsent(id, new HashMap<>());
        TIMERS.putIfAbsent(id, new HashMap<>());

        //? if >=26.2 {
        Map<Identifier, String> cache = CACHE.get(id);
        Map<Identifier, Integer> timers = TIMERS.get(id);
        Map<Identifier, String> results = new HashMap<>();
        //?} else {
        /*
        Map<ResourceLocation, String> cache = CACHE.get(id);
        Map<ResourceLocation, Integer> timers = TIMERS.get(id);
        Map<ResourceLocation, String> results = new HashMap<>();
        */
        //?}

        hasNewData = false;

        // gather data from active items
        List<Item> surveyItems = getActiveInformationalItems(player);
        for (Item item : surveyItems) {
            if (!(item instanceof InformationalItem surveyItem)) continue;
            //? if >=26.2 {
            Identifier key = BuiltInRegistries.ITEM.getKey(item);
            //?} else {
            /*
            ResourceLocation key = BuiltInRegistries.ITEM.getKey(item);
            */
            //?}

            int timer = timers.getOrDefault(key, 0);
            if (timer <= 0 || !cache.containsKey(key)) {
                hasNewData = true;
                String result = surveyItem.getResult(player.level(), player);
                results.put(key, result);
                cache.put(key, result);
                timers.put(key, surveyItem.updatePeriod());
            }
            else {
                if (cache.containsKey(key)) results.put(key, cache.get(key));
                timers.put(key, timer - 1);
            }
        }

        // remove stale entries
        Set.copyOf(cache.keySet()).forEach(key -> {
            Item item = BuiltInRegistries.ITEM.getValue(key);
            if (surveyItems.contains(item)) return;
            cache.remove(key);
            timers.remove(key);
            hasNewData = true;
        });

        // send new results to player
        if (!hasNewData) return;
        Tide.NETWORK.sendToPlayer(new InfoDataMsg(results), player);
    }

    private static List<Item> getActiveInformationalItems(ServerPlayer player) {
        ArrayList<Item> items = new ArrayList<>();

        Item mainhand = player.getMainHandItem().getItem();
        Item offhand = player.getOffhandItem().getItem();

        if (hasInfo(mainhand)) addInfoItem(items, mainhand);
        if (hasInfo(offhand)) addInfoItem(items, offhand);

        CompatHelper.addInformationItemsFromAccessories(player, items);
        addNearbyPlacedItems(player, items);

        return items;
    }

    @SuppressWarnings("deprecation")
    public static boolean hasInfo(Item item) {
        return item.builtInRegistryHolder().is(TideTags.Items.INFORMATIONAL);
    }

    public static void addInfoItem(ArrayList<Item> items, Item item) {
        if (item instanceof InfoItemContainer container) {
            container.getContainedInfoItems().forEach(c -> addInfoItem(items, c));
            return;
        }
        if (!items.contains(item)) items.add(item);
    }

    private static void addNearbyPlacedItems(ServerPlayer player, ArrayList<Item> items) {
        UUID id = player.getUUID();

        int timer = PROXIMITY_SCAN_TIMER.getOrDefault(id, 0);
        if (timer <= 0) {
            Set<Item> found = scanForNearbyInformationalBlocks(player);
            NEARBY_BLOCK_ITEMS.put(id, found);
            PROXIMITY_SCAN_TIMER.put(id, 20);
        }
        else PROXIMITY_SCAN_TIMER.put(id, timer - 1);

        Set<Item> nearby = NEARBY_BLOCK_ITEMS.get(id);
        if (nearby == null || nearby.isEmpty()) return;
        for (Item item : nearby) addInfoItem(items, item);
    }

    private static Set<Item> scanForNearbyInformationalBlocks(ServerPlayer player) {
        Set<Item> found = new HashSet<>();

        int radiusXZ = 6;
        int radiusY = 3;
        BlockPos center = player.blockPosition();

        for (BlockPos pos : BlockPos.betweenClosed(
                center.offset(-radiusXZ, -radiusY, -radiusXZ),
                center.offset(radiusXZ, radiusY, radiusXZ))) {

            BlockState state = player.level().getBlockState(pos);
            if (state.is(TideTags.Blocks.INFORMATIONAL)) found.add(state.getBlock().asItem());
        }

        return found;
    }
}