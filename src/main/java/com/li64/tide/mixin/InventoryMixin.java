package com.li64.tide.mixin;

import com.li64.tide.data.item.TideItemData;
import com.li64.tide.registries.items.FishSatchelItem;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Inventory.class)
public class InventoryMixin {
    @Shadow @Final private NonNullList<ItemStack> items;
    @Shadow @Final public Player player;

    @Inject(at = @At(value = "HEAD"), method = "add(ILnet/minecraft/world/item/ItemStack;)Z", cancellable = true)
    public void add(int slot, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (slot >= 0 || !FishSatchelItem.canPutInSatchel(stack)) return;
        for (ItemStack checked : this.items) {
            if (!(checked.getItem() instanceof FishSatchelItem satchelItem)) continue;
            if (!TideItemData.FISH_SATCHEL_OPENED.getOrDefault(checked, false)) continue;
            if (FishSatchelItem.getRemainingSlots(checked) < stack.getCount()) continue;
            int i = 0;
            boolean continueAdding = true;
            while (continueAdding && stack.getCount() > 0 && i < 64) {
                continueAdding = satchelItem.overrideOtherStackedOnMe(checked, stack, player, new SlotAccess() {
                    @Override public @NotNull ItemStack get() { return checked; }
                    @Override public boolean set(ItemStack carried) { return false; }
                }, i == 0);
                if (continueAdding) i++;
            }
            if (i > 0) {
                cir.setReturnValue(true);
                return;
            }
        }
    }
}
