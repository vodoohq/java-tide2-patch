package com.li64.tide.mixin;

import com.li64.tide.Tide;
import com.li64.tide.registries.items.TideFishingRodItem;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import java.util.function.Function;

@Mixin(Items.class)
public abstract class ItemsMixin {
    @Redirect(method = "registerItem(Lnet/minecraft/resources/ResourceKey;Ljava/util/function/Function;Lnet/minecraft/world/item/Item$Properties;)Lnet/minecraft/world/item/Item;",
            at = @At(value = "INVOKE", target = "Ljava/util/function/Function;apply(Ljava/lang/Object;)Ljava/lang/Object;"))
    private static Object tide$createItem(Function<Item.Properties, Item> factory, Object properties,
                                          ResourceKey<Item> id, Function<Item.Properties, Item> originalFactory, Item.Properties originalProperties) {
        if (id.identifier().equals(net.minecraft.resources.Identifier.withDefaultNamespace("fishing_rod"))) {
            if (Tide.SERVER_CONFIG == null) Tide.setupConfigs();
            if (Tide.SERVER_CONFIG.general.overrideVanillaRod) return new TideFishingRodItem(1, 32, (Item.Properties) properties);
        }
        return factory.apply((Item.Properties) properties);
    }
}
