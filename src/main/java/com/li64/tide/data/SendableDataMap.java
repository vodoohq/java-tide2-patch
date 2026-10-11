package com.li64.tide.data;

import com.google.common.collect.ImmutableMap;
import com.li64.tide.Tide;
import com.li64.tide.data.fishing.FishData;
import com.mojang.serialization.Codec;
import net.minecraft.network.FriendlyByteBuf;
//? if >=26.2 {
import net.minecraft.resources.Identifier;
//?} else {
/*
import net.minecraft.resources.ResourceLocation;
*/
//?}

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

public class SendableDataMap<T> {
        //? if >=26.2 {
    public Map<Identifier, T> data;
        //?} else {
        /*
    public Map<ResourceLocation, T> data;
        */
        //?}
    public Codec<T> codec;
    //? if >=26.2 {
    public SendableDataMap(Map<Identifier, T> data, Codec<T> codec) {
    //?} else {
    /*
    public SendableDataMap(Map<ResourceLocation, T> data, Codec<T> codec) {
    */
    //?}
        this.data = ImmutableMap.copyOf(data);
        this.codec = codec;
    }
    //? if >=26.2 {
    public T getEntry(Identifier location) {
    //?} else {
    /*
    public T getEntry(ResourceLocation location) {
    */
    //?}
        return data.get(location);
    }

    //? if >=26.2 {
    public Optional<T> getEntryOptional(Identifier location) {
    //?} else {
    /*
    public Optional<T> getEntryOptional(ResourceLocation location) {
    */
    //?}
        return Optional.ofNullable(getEntry(location));
    }
    
    //? if >=26.2 {
    public Stream<Map.Entry<Identifier, T>> stream() {
    //?} else {
    /*
    public Stream<Map.Entry<ResourceLocation, T>> stream() {
    */
    //?}
        return data.entrySet().parallelStream();
    }

    public Stream<T> valueStream() {
        return data.values().parallelStream();
    }

    public List<T> values() {
        return valueStream().toList();
    }

    //? if >=26.2 {
    public Set<Identifier> getKeys() {
    //?} else {
    /*
    public Set<ResourceLocation> getKeys() {
    */
    //?}
        return data.keySet();
    }

    public int count() {
        return data.size();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(data.size());
        //? if >=26.2 {
        data.keySet().forEach(buf::writeIdentifier);
        //?} else {
        /*
        data.keySet().forEach(buf::writeResourceLocation);
        */
        //?}
        data.values().forEach(value -> buf.writeJsonWithCodec(codec, value));
    }

    public static <T> SendableDataMap<T> decode(FriendlyByteBuf buf, Codec<T> codec) {
        int size = buf.readInt();

        //? if >=26.2 {
        ArrayList<Identifier> keys = new ArrayList<>(size);
        //?} else {
        /*
        ArrayList<ResourceLocation> keys = new ArrayList<>(size);
        */
        //?}
        ArrayList<T> values = new ArrayList<>(size);

        //? if >=26.2 {
        for (int i = 0; i < size; i++) keys.add(buf.readIdentifier());
        for (int i = 0; i < size; i++) values.add(buf.readLenientJsonWithCodec(codec));

        Map<Identifier, T> map = new HashMap<>();
        //?} else {
        /*
        for (int i = 0; i < size; i++) keys.add(buf.readResourceLocation());
        for (int i = 0; i < size; i++) values.add(buf.readLenientJsonWithCodec(codec));

        Map<ResourceLocation, T> map = new HashMap<>();
        */
        //?}
        for (int i = 0; i < size; i++) map.put(keys.get(i), values.get(i));

        return new SendableDataMap<>(map, codec);
    }

    //? if >=26.2 {
    public static <T> SendableDataMap<T> merge(SendableDataMap<T> map, Map<Identifier, T> other) {
        Map<Identifier, T> merged = new HashMap<>(map.data);
    //?} else {
    /*
    public static <T> SendableDataMap<T> merge(SendableDataMap<T> map, Map<ResourceLocation, T> other) {
        Map<ResourceLocation, T> merged = new HashMap<>(map.data);
    */
    //?}
        merged.putAll(other);
        return new SendableDataMap<>(merged, map.codec);
    }

    //? if >=26.2 {
    public void debugLogContents(BiConsumer<Identifier, T> logger) {
    //?} else {
    /*
    public void debugLogContents(BiConsumer<ResourceLocation, T> logger) {
    */
    //?}
        Tide.LOG.info("Logging sendable data map contents:");
        data.forEach(logger);
    }
}