package com.li64.tide.registries;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;

import java.util.List;

public class TideFoods {
    public static final FoodDefinition RAW_FISH = new FoodDefinition(2, 0.1f);
    public static final FoodDefinition TINY_RAW_FISH = new FoodDefinition(1, 0.05f, true);
    public static final FoodDefinition BIG_RAW_FISH = new FoodDefinition(6, 0.3f);

    public static final FoodDefinition FIERY_FISH = new FoodDefinition(2, 0.1f, false, true,
            List.of(
                    new FoodEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 200, 0), 1.0f),
                    new FoodEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 200, 0), 1.0f)
            ));
    public static final FoodDefinition TINY_FIERY_FISH = new FoodDefinition(1, 0.05f, true, true,
            List.of(
                    new FoodEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 100, 0), 1.0f),
                    new FoodEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 100, 0), 1.0f)
            ));
    public static final FoodDefinition BIG_FIERY_FISH = new FoodDefinition(6, 0.3f, false, true,
            List.of(
                    new FoodEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 600, 0), 1.0f),
                    new FoodEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 600, 0), 1.0f)
            ));

    public static Item.Properties apply(Item.Properties properties, FoodDefinition def) {
        return properties.food(def.toProperties(), def.toConsumable());
    }

    public record FoodDefinition(int nutrition, float saturation, boolean fast, boolean alwaysEdible, List<FoodEffect> effects) {
        public FoodDefinition(int nutrition, float saturation) {
            this(nutrition, saturation, false);
        }

        public FoodDefinition(int nutrition, float saturation, boolean fast) {
            this(nutrition, saturation, fast, false, List.of());
        }

        public FoodDefinition withAlwaysEdible() {
            return new FoodDefinition(nutrition, saturation, fast, true, effects);
        }

        public FoodDefinition withEffects(List<FoodEffect> effects) {
            return new FoodDefinition(nutrition, saturation, fast, true, effects);
        }

        public net.minecraft.world.item.component.Consumable toConsumable() {
            var builder = net.minecraft.world.item.component.Consumable.builder()
                    .consumeSeconds(fast() ? 0.8F : 1.6F);
            for (FoodEffect effect : effects()) {
                builder.onConsume(new net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect(effect.effect(), effect.chance()));
            }
            return builder.build();
        }

        public FoodProperties toProperties() {
            FoodProperties.Builder builder = new FoodProperties.Builder()
                    .nutrition(nutrition())
                    /*? if >=1.21 {*/.saturationModifier(saturation());
                    /*?} else {*//*.saturationMod(saturation());*//*?}*/


            if (alwaysEdible()) builder/*? if >=1.21 {*/.alwaysEdible()/*?} else {*//*.alwaysEat()*//*?}*/;



            return builder.build();
        }
    }
    public record FoodEffect(MobEffectInstance effect, float chance) {}

    public static FoodDefinition COOKED_FISH = new FoodDefinition(6, 0.6f);
    public static FoodDefinition TINY_COOKED_FISH = new FoodDefinition(4, 0.4f, true);
    public static FoodDefinition BIG_COOKED_FISH = new FoodDefinition(10, 1.2f);
    public static FoodDefinition GRILLED_TUNA = new FoodDefinition(8, 0.7f);
    public static FoodDefinition FISH_SLICE = new FoodDefinition(1, 0.1f, true);
    public static FoodDefinition COOKED_FISH_SLICE = new FoodDefinition(3, 0.5f, true);
}
