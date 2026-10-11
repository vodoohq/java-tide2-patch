package com.li64.tide.mixin;

import com.li64.tide.data.FishLengthHolder;
import com.li64.tide.data.fishing.FishData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//? if >= 26.2 {
import net.minecraft.world.entity.animal.fish.AbstractFish;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
//?} elif >=1.21 {
/*
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
*/
//?} else {
/*
import net.minecraft.world.entity.animal.AbstractFish;
*/
//?}

@Mixin(AbstractFish.class)
public abstract class AbstractFishMixin implements FishLengthHolder {
    @Unique public double tide$length = 0.0;

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput tag, CallbackInfo ci) {
        FishData data = FishData.get((Entity)(Object)this).orElse(null);
        if (data == null) return;

        this.tide$length = tag.getDoubleOr(tide$LENGTH_KEY, this.tide$getLength());
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput compound, CallbackInfo ci) {
        if (FishData.get((Entity)(Object)this).isEmpty()) return;
        compound.putDouble(tide$LENGTH_KEY, this.tide$getLength());
    }

    @Inject(method = "saveToBucketTag", at = @At("TAIL"))
    private void saveToBucketTag(ItemStack stack, CallbackInfo ci) {
        if (FishData.get((Entity)(Object)this).isEmpty()) return;
        //? if >=1.21 {
        CustomData.update(DataComponents.BUCKET_ENTITY_DATA, stack, tag ->
                tag.putDouble(tide$LENGTH_KEY, this.tide$getLength()));
        //?} else {
        /*CompoundTag tag = stack.getOrCreateTag();
        tag.putDouble(tide$LENGTH_KEY, this.tide$getLength());
        *///?}
    }

    @Inject(method = "loadFromBucketTag", at = @At("TAIL"))
    private void loadFromBucketTag(CompoundTag tag, CallbackInfo ci) {
        FishData data = FishData.get((Entity)(Object)this).orElse(null);
        if (data == null) return;

        this.tide$length = tag.contains(tide$LENGTH_KEY) ? tag.getDoubleOr(tide$LENGTH_KEY, this.tide$getLength()) : this.tide$getLength();
    }

    @Unique
    private void tide$randomizeLength() {
        Entity self = (Entity)(Object)this;
        this.tide$length = FishData.get(self)
                /*? if >=1.21.1 {*/.map(data -> data.getRandomLength(self.getRandom()))
                /*?} else {*//*.map(data -> data.getRandomLength(self.random))*//*?}*/
                .orElse(0.0);
    }

    @Override
    public double tide$getLength() {
        if (this.tide$length == 0.0) this.tide$randomizeLength();
        return this.tide$length;
    }

    @Override
    public void tide$setLength(double length) {
        this.tide$length = length;
    }
}
