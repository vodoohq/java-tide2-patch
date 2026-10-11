package com.li64.tide.mixin;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.function.Consumer;

@Mixin(LootContextParamSets.class)
public abstract class LootContextParamSetsMixin {
    @Inject(method = "register(Ljava/lang/String;Ljava/util/function/Consumer;)Lnet/minecraft/util/context/ContextKeySet;",
            at = @At("HEAD"), cancellable = true)
    private static void tide$extendFishingContext(String name, Consumer<ContextKeySet.Builder> consumer,
                                                  CallbackInfoReturnable<ContextKeySet> cir) {
        if (!name.equals("fishing")) return;
        var builder = new ContextKeySet.Builder();
        consumer.accept(builder);
        builder.optional(LootContextParams.BLOCK_STATE);
        cir.setReturnValue(Registry.register(BuiltInRegistries.CONTEXT_KEY_SET, Identifier.withDefaultNamespace(name), builder.build()));
    }
}
