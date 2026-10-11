package com.li64.tide.loaders;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
//? if >=26.2 {
import net.minecraft.resources.Identifier;
//?} else {
/*
import net.minecraft.resources.ResourceLocation;
*/
//?}
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

public interface LoaderPlatform {
    String getPlatformName();

    default String getMCVersion() {
            //? if >=26.2 {
            return net.minecraft.SharedConstants.getCurrentVersion().name();
            //?} elif >=1.21 {
            /*return "1.21.1";*/
            //?} else {
            /*return "1.20.1";*/
            //?}
    }

    boolean isModLoaded(String modId);

    boolean isDevEnvironment();

    CompoundTag getPlayerData(ServerPlayer player);

    /*? if !forge*/<T> Registry<T> createRegistry(ResourceKey<Registry<T>> registryKey);

    //? if >=26.2 {
    <T> T register(Registry<? super T> registry, Identifier id, T toRegister);
    <T> Holder<T> registerForHolder(Registry<T> registry, Identifier id, T toRegister);
    //?} elif >=1.21 {
    /*
    <T> T register(Registry<? super T> registry, ResourceLocation id, T toRegister);
    <T> Holder<T> registerForHolder(Registry<T> registry, ResourceLocation id, T toRegister);
    */
    //?} else {
    /*
    <T> T register(Registry<? super T> registry, ResourceLocation id, T toRegister);
    */
    //?}
    <T> T register(Registry<? super T> registry, ResourceKey<T> id, T toRegister);

    <T extends AbstractContainerMenu> MenuType<T> createMenuType(MenuType.MenuSupplier<T> menuSupplier, FeatureFlagSet flags);
}
