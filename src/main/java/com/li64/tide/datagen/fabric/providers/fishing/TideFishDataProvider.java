//? if fabric {
package com.li64.tide.datagen.fabric.providers.fishing;

import com.li64.tide.compat.fishofthieves.FishOfThievesFishData;
import com.li64.tide.compat.hybridaquatic.HybridAquaticFishData;
import com.li64.tide.compat.netherdepths.NetherDepthsFishData;
import com.li64.tide.compat.seasons.Season;
import com.li64.tide.compat.stardewfishing.StardewFishingFishData;
import com.li64.tide.data.fishing.FishData;
import com.li64.tide.data.journal.FishRarity;
import com.li64.tide.data.journal.JournalGroup;
import com.li64.tide.datagen.fabric.providers.SimpleDataOutput;
import com.li64.tide.datagen.fabric.providers.SimpleDataProvider;
import com.li64.tide.registries.TideFish;
import com.mojang.serialization.Codec;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
//? if >=26.2 {
import net.minecraft.resources.Identifier;
//?} else {
/*
import net.minecraft.resources.ResourceLocation;
*/
//?}
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class TideFishDataProvider extends SimpleDataProvider<FishData> {
    public TideFishDataProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super("fishing/fish", output, registries);
    }

    @Override
    protected Codec<FishData> dataCodec() {
        return FishData.CODEC;
    }

    @Override
    protected void generate(SimpleDataOutput<FishData> output) {
        // generate fish data from TideFish class

        TideFish.DATA_BUILDERS.forEach((item, constructor) -> {
            FishData.Builder builder = FishData.builder();
            constructor.accept(builder.fish(item));
            //? if >=26.2 {
            Identifier itemKey = BuiltInRegistries.ITEM.getKey(item);
            //?} else {
            /*
            ResourceLocation itemKey = BuiltInRegistries.ITEM.getKey(item);
            */
            //?}
            if (TideFish.ENTITY_DATA.containsKey(itemKey.getPath())) {
                // add bucket item
                builder.bucket(BuiltInRegistries.ITEM.getValue(itemKey.withSuffix("_bucket")));
            }
            builder.build(output);
        });

        // vanilla fish data

        FishData.builder().fish(Items.SALMON).bucket(Items.SALMON_BUCKET)
                .size(55.0, 95.0, 145.0)
                .strength(0.4f).speed(0.9f)
                .selectionWeight(40)
                .temperature(-0.6f, 1.1f)
                .seasons(Season.SUMMER, Season.FALL, Season.WINTER)
                .overworld().water().surface().freshwater()
                .journalLocation("journal.info.location.freshwater")
                .journalGroup(JournalGroup.FRESHWATER)
                .displayData(display -> display
                        .offsets(0.1f, -0.1f, 0f)
                        .rotation(0f, 0f, 0f))
                .build(output);

        FishData.builder().fish(Items.COD).bucket(Items.COD_BUCKET)
                .size(65.0, 115.0, 200.0)
                .strength(0.5f).speed(1.0f)
                .selectionWeight(55)
                .temperature(-1.0f, 1.3f)
                .overworld().water().surface().saltwater()
                .journalLocation("journal.info.location.saltwater")
                .journalGroup(JournalGroup.SALTWATER)
                .displayData(display -> display
                        .offsets(0.1f, 0.0f, 0f)
                        .rotation(0f, 0f, 0f))
                .build(output);

        FishData.builder().fish(Items.PUFFERFISH).bucket(Items.PUFFERFISH_BUCKET)
                .size(12.0, 22.0, 75.0)
                .strength(0.6f).speed(1.25f)
                .selectionWeight(25)
                .temperature(1.0f, 0.8f)
                .overworld().water().surface().saltwater()
                .journalLocation("journal.info.location.saltwater")
                .journalGroup(JournalGroup.SALTWATER)
                .journalRarity(FishRarity.UNCOMMON)
                .displayData(display -> display
                        .offsets(0.05f, 0.0f, -0.1f)
                        .rotation(0f, 0f, 0f))
                .build(output);

        FishData.builder().fish(Items.TROPICAL_FISH).bucket(Items.TROPICAL_FISH_BUCKET)
                .size(10.0, 35.0, 100.0)
                .strength(0.4f).speed(1.1f)
                .selectionWeight(50)
                .temperature(1.0f, 0.8f)
                .overworld().water().surface().saltwater()
                .journalLocation("journal.info.location.saltwater")
                .journalGroup(JournalGroup.SALTWATER)
                .displayData(display -> display
                        .offsets(0.05f, -0.08f, 0f)
                        .rotation(0f, 0f, 0f))
                .build(output);

        // modded fish data

        NetherDepthsFishData.generate(output);
        HybridAquaticFishData.generate(output);
        StardewFishingFishData.generate(output);
        FishOfThievesFishData.generate(output);
    }

    @Override
    public @NotNull String getName() {
        return "Fish Data";
    }
}
//?}