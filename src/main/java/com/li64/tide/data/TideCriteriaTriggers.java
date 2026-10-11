package com.li64.tide.data;

import com.li64.tide.data.triggers.TideSimpleTrigger;
import com.li64.tide.Tide;
import net.minecraft.core.registries.BuiltInRegistries;
import java.util.Optional;
//? if >= 26.2 {
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.advancements.triggers.SimpleCriterionTrigger;
import net.minecraft.advancements.triggers.CriterionTrigger;
//?} elif >=1.21 {
/*
import com.li64.tide.Tide;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import java.util.Optional;
*/
//?} else {
/*import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
*///?}

import java.util.HashMap;
import java.util.function.Function;

public class TideCriteriaTriggers {
    public static final HashMap<String, CriterionTrigger<?>> CRITERIA_TRIGGERS = new HashMap<>();

    public static TideSimpleTrigger PULLED_CRATE = (TideSimpleTrigger) register("pull_crate", TideSimpleTrigger::new);
    public static TideSimpleTrigger FISHED_IN_LAVA = (TideSimpleTrigger) register("fish_in_lava", TideSimpleTrigger::new);
    public static TideSimpleTrigger FISHED_IN_VOID = (TideSimpleTrigger) register("fish_in_void", TideSimpleTrigger::new);
    public static TideSimpleTrigger RELEASED_PUPFISH = (TideSimpleTrigger) register("released_pupfish", TideSimpleTrigger::new);
    public static TideSimpleTrigger FROZE_COELACANTH = (TideSimpleTrigger) register("froze_coelacanth", TideSimpleTrigger::new);
    public static TideSimpleTrigger JOURNAL_HALFWAY_COMPLETE = (TideSimpleTrigger) register("journal_halfway_complete", TideSimpleTrigger::new);
    public static TideSimpleTrigger JOURNAL_COMPLETE = (TideSimpleTrigger) register("journal_complete", TideSimpleTrigger::new);

    public static /*? if >=1.21 {*/<T extends SimpleCriterionTrigger.SimpleInstance>/*?} else {*//*<T extends AbstractCriterionTriggerInstance>*//*?}*/
            SimpleCriterionTrigger<T> register(String key, Function<String, SimpleCriterionTrigger<T>> triggerFunc) {
        SimpleCriterionTrigger<T> trigger = triggerFunc.apply(key);
        CRITERIA_TRIGGERS.put(key, trigger);
        /*? if >=1.21 {*/return Tide.PLATFORM.register(BuiltInRegistries.TRIGGER_TYPES, Tide.resource(key), trigger);
        /*?} else {*//*return CriteriaTriggers.register(trigger);*//*?}*/
    }

    public static void init() {}

    //? if >=1.21 {
    public static Criterion<TideSimpleTrigger.TriggerInstance> simpleInstance(TideSimpleTrigger trigger) {
        return trigger.createCriterion(new TideSimpleTrigger.TriggerInstance(Optional.empty()));
    }
    //?} else {
    /*public static TideSimpleTrigger.TriggerInstance simpleInstance(TideSimpleTrigger trigger) {
        return new TideSimpleTrigger.TriggerInstance(trigger.getId(), ContextAwarePredicate.ANY);
    }
    *///?}
}
