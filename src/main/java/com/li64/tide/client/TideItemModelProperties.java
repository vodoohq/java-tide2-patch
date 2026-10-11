package com.li64.tide.client;
import com.li64.tide.Tide;
import com.li64.tide.data.item.TideItemData;
import com.li64.tide.registries.items.FishSatchelItem;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperties;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemStack;

public class TideItemModelProperties {
    public static void registerAll() {
        RangeSelectItemModelProperties.ID_MAPPER.put(Tide.resource("satchel_state"), SatchelState.MAP_CODEC);
    }
    public record SatchelState() implements RangeSelectItemModelProperty {
        public static final MapCodec<SatchelState> MAP_CODEC = MapCodec.unit(new SatchelState());
        @Override public float get(ItemStack stack, ClientLevel level, ItemOwner owner, int seed) {
            if (!TideItemData.FISH_SATCHEL_OPENED.getOrDefault(stack, false)) return 0f;
            int count = FishSatchelItem.getFishCount(stack);
            return count > 1 ? 1f : count == 1 ? 0.6f : 0.3f;
        }
        @Override public MapCodec<SatchelState> type() { return MAP_CODEC; }
    }
}
