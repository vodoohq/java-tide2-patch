package com.li64.tide.data.loaders;

import com.google.gson.JsonParser;
import com.li64.tide.Tide;
import com.li64.tide.data.ValidatableDataEntry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.FileToIdConverter;
//? if >=26.2 {
import net.minecraft.resources.Identifier;
//?} else {
/*
import net.minecraft.resources.ResourceLocation;
*/
//?}
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import java.io.Reader;
import java.util.function.Consumer;

public abstract class AbstractDataLoader<T, R> extends SimplePreparableReloadListener<R> {
    protected final String directory;
    protected R data;

    public AbstractDataLoader(String directory) {
        this.directory = directory;
    }

    public abstract void decode(FriendlyByteBuf buf);
    public abstract void encode(FriendlyByteBuf buf);

    protected abstract Codec<T> getEntryCodec();
    protected abstract int getEntryCount(R data);

    protected abstract R prepareData(ResourceManager manager, FileToIdConverter lister);

    @Override
    protected @NotNull R prepare(@NotNull ResourceManager manager, @NotNull ProfilerFiller profiler) {
        FileToIdConverter lister = FileToIdConverter.json(this.directory);
        return prepareData(manager, lister);
    }

    @Override
    protected void apply(@NotNull R data, @NotNull ResourceManager manager, @NotNull ProfilerFiller profiler) {
        this.setData(data);
        Tide.LOG.info("Loaded {} entries from '{}'", getEntryCount(data), getDirectory());
    }

    //? if >=26.2 {
    protected void parseOrLog(Identifier key, Reader reader, Consumer<T> onSuccess) {
    //?} else {
    /*
    protected void parseOrLog(ResourceLocation key, Reader reader, Consumer<T> onSuccess) {
    */
    //?}
        var json = JsonParser.parseReader(reader);
        // Optional integration entries can reference items that do not exist without their mod.
        // Skip those entries before registry codecs try to resolve the absent items.
        if (json.isJsonObject() && json.getAsJsonObject().has("associated_mods")
                && json.getAsJsonObject().get("associated_mods").isJsonArray()) {
            for (var association : json.getAsJsonObject().getAsJsonArray("associated_mods")) {
                if (!association.isJsonPrimitive() || !association.getAsJsonPrimitive().isString()) continue;
                String modId = association.getAsString();
                if (!Tide.PLATFORM.isModLoaded(modId) && !Tide.PLATFORM.isModLoaded(modId.replace("-", "_"))) return;
            }
        }
        getEntryCodec().parse(JsonOps.INSTANCE, json)
                .resultOrPartial(error -> {
                    if (Tide.SERVER_CONFIG.general.logDataErrors)
                        Tide.LOG.error("Skipping invalid data entry '{}' due to parsing error: {}", key, error);
                })
                .map(data -> {
                    if (data instanceof ValidatableDataEntry entry && !entry.isValid()) {
                        if (Tide.SERVER_CONFIG.general.logDataErrors)
                            Tide.LOG.error("Skipping invalid data entry '{}': {}", key, entry.invalidReason());
                        return null;
                    }
                    return data;
                })
                .ifPresent(onSuccess);
    }

    //? if >=26.2 {
    public Identifier getDirectory() {
    //?} else {
    /*
    public ResourceLocation getDirectory() {
    */
    //?}
        return Tide.resource(directory);
    }

    public R get() {
        return this.data;
    }

    public void setData(R data) {
        this.data = data;
        this.onDataUpdated();
    }

    public void onDataUpdated() {}
}
