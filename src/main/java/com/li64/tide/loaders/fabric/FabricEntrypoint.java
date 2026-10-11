//? if fabric {
package com.li64.tide.loaders.fabric;

import com.li64.tide.Tide;
import com.li64.tide.client.gui.TideMenuTypes;
import com.li64.tide.data.TideCriteriaTriggers;
import com.li64.tide.data.TideData;
import com.li64.tide.data.TideRecipeSerializers;
import com.li64.tide.network.TideMessages;
import com.li64.tide.registries.*;
import com.li64.tide.registries.entities.TideSpawnConfigs;
import com.li64.tide.registries.particles.TideParticleTypes;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.levelgen.Heightmap;

//? if >=26.2 {
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CookingFuel;
import net.minecraft.world.level.storage.loot.providers.number.ints.ResolvableInt;
import net.minecraft.world.level.storage.loot.providers.number.floats.ResolvableFloat;
import net.minecraft.world.level.storage.loot.providers.number.floats.ContextFloatProviders;
//?} else {
/*
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
*/
//?}


/*? if >=1.21*/import com.li64.tide.data.item.TideDataComponents;


public class FabricEntrypoint implements ModInitializer {
    @Override
    public void onInitialize() {
        Tide.initialize();
        TideMessages.init(Tide.NETWORK);
        FabricEventHandler.init();

        TideRegistries.init();
        TideItems.init();
        TideBlocks.init();
        TideBlockEntities.init();
        TideEntityTypes.init();
        TideMenuTypes.init();
        TideSoundEvents.init();
        TideParticleTypes.init();
        TideLootFunctions.init();
        TideCriteriaTriggers.init();
        TideRecipeSerializers.init();
        TideArmorMaterials.init();
        /*? if >=1.21*/TideDataComponents.init();

        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, Tide.resource("tide"),
                //? if >=26.2 {
                TideItems.getCreativeTab(FabricCreativeModeTab.builder()).build());
                //?} else {
                /*
                TideItems.getCreativeTab(FabricItemGroup.builder()).build());
                */
                //?}

        TideEntityAttributes.REGISTRY.forEach(FabricEntrypoint::registerAttributes);

        //? if >=26.2 {
        DefaultItemComponentEvents.MODIFY.register(context -> {
                context.modify(TideItems.WOODEN_CRATE, builder -> builder.set(DataComponents.COOKING_FUEL, new CookingFuel(new ResolvableInt.Constant(300), ResolvableFloat.fromKey(ContextFloatProviders.COOKING_DEFAULT_SPEED_MULTIPLIER))));
                context.modify(TideItems.ANGLING_TABLE, builder -> builder.set(DataComponents.COOKING_FUEL, new CookingFuel(new ResolvableInt.Constant(300), ResolvableFloat.fromKey(ContextFloatProviders.COOKING_DEFAULT_SPEED_MULTIPLIER))));
                context.modify(TideItems.FISH_DISPLAY, builder -> builder.set(DataComponents.COOKING_FUEL, new CookingFuel(new ResolvableInt.Constant(300), ResolvableFloat.fromKey(ContextFloatProviders.COOKING_DEFAULT_SPEED_MULTIPLIER))));
                context.modify(TideItems.FISHY_NOTE, builder -> builder.set(DataComponents.COOKING_FUEL, new CookingFuel(new ResolvableInt.Constant(200), ResolvableFloat.fromKey(ContextFloatProviders.COOKING_DEFAULT_SPEED_MULTIPLIER))));
                context.modify(TideFish.INFERNO_GUPPY, builder -> builder.set(DataComponents.COOKING_FUEL, new CookingFuel(new ResolvableInt.Constant(3200), ResolvableFloat.fromKey(ContextFloatProviders.COOKING_DEFAULT_SPEED_MULTIPLIER))));
        });
        //?} else {
        /*
        FuelRegistry.INSTANCE.add(TideItems.WOODEN_CRATE, 300);
        FuelRegistry.INSTANCE.add(TideItems.ANGLING_TABLE, 300);
        FuelRegistry.INSTANCE.add(TideItems.FISH_DISPLAY, 300);
        FuelRegistry.INSTANCE.add(TideItems.FISHY_NOTE, 200);
        FuelRegistry.INSTANCE.add(TideFish.INFERNO_GUPPY, 3200);
        */
        //?}

        //? if >=26.2 {
        TideData.onRegisterReloadListeners((id, listener) ->
                ResourceLoader.get(PackType.SERVER_DATA).registerReloadListener(id, listener));
        //?} else {
        /*
        TideData.onRegisterReloadListeners((id, listener) -> ResourceManagerHelper.get(PackType.SERVER_DATA)
            .registerReloadListener(new IdentifiableResourceReloadListener() {
                @Override
                public ResourceLocation getFabricId() {
                    return id;
                }
                
                @Override
                public @NotNull CompletableFuture<Void> reload(PreparationBarrier synchronizer, ResourceManager manager, ProfilerFiller prepareProfiler, ProfilerFiller applyProfiler, Executor prepareExecutor, Executor applyExecutor) {
                    return listener.reload(synchronizer, manager, prepareProfiler, applyProfiler, prepareExecutor, applyExecutor);
                }
        }));
        */
        //?}

        TideSpawnConfigs.onRegisterSpawnConfigs(config -> {
            BiomeModifications.addSpawn(
                    BiomeSelectors.tag(config.biomes()),
                    config.mobCategory(), config.entityType(),
                    config.weight(), config.minGroup(), config.maxGroup()
            );
            SpawnPlacements.register(
                    config.entityType(),
                    config.placementType(),
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    config.spawnPredicate()
            );
        });
    }

    public static <T extends LivingEntity> void registerAttributes(TideEntityAttributes.Registration<T> reg) {
        FabricDefaultAttributeRegistry.register(reg.entityType(), reg.attributes());
    }
}
//?}