package com.li64.tide.mixin;

import com.li64.tide.data.TidePlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class TidePlayerMixin implements TidePlayer {
    //? if fabric {
    @Unique public CompoundTag tide$playerData = new CompoundTag();
    @Unique public String tide$dataKey = "TidePersistentData";

    @Inject(method = "readAdditionalSaveData", at = @At("HEAD"))
    private void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input, CallbackInfo ci) {
        tide$playerData = input.read(tide$dataKey, CompoundTag.CODEC).orElseGet(CompoundTag::new);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("HEAD"))
    private void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output, CallbackInfo ci) {
        output.store(tide$dataKey, CompoundTag.CODEC, tide$playerData);
    }

    @Override
    public CompoundTag tide$getTidePlayerData() {
        return tide$playerData;
    }

    @Override
    public void tide$setTidePlayerData(CompoundTag tag) {
        this.tide$playerData = tag;
    }
    //?}
}
