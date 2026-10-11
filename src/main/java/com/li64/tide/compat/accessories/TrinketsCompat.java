package com.li64.tide.compat.accessories;
import com.li64.tide.compat.OptionalModBridge;
import com.li64.tide.data.TideTags;
import com.li64.tide.data.informational.FishingInfoManager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class TrinketsCompat {
    public static void addInformationalItems(Player player, ArrayList<Item> items) {
        Object result = OptionalModBridge.callStatic("eu.pb4.trinkets.api.TrinketsApi", "getTrinketComponent", player);
        if (!(result instanceof Optional<?> component) || component.isEmpty()) return;
        Predicate<ItemStack> filter = stack -> stack.is(TideTags.Items.INFORMATIONAL);
        Object equipped = OptionalModBridge.call(component.get(), "equipped", filter, true);
        if (equipped instanceof Stream<?> stream) {
            try (stream) { stream.forEach(access -> add(items, access)); }
        } else if (equipped instanceof Iterable<?> entries) {
            entries.forEach(access -> add(items, access));
        }
    }
    private static void add(ArrayList<Item> items, Object access) {
        Object value = OptionalModBridge.call(access, "getStack");
        if (value instanceof ItemStack stack) FishingInfoManager.addInfoItem(items, stack.getItem());
    }
}
