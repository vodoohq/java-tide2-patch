package com.li64.tide.registries.items;

import com.li64.tide.registries.entities.misc.StarArrow;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

//? if >= 26.2 {
import com.li64.tide.registries.TooltipRegistry;
import org.jetbrains.annotations.Nullable;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
//?} elif >=1.21 {
/*
import org.jetbrains.annotations.Nullable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.server.level.ServerLevel;
*/
//?} else {
/*
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Player;
*/
//?}

import java.util.List;
import java.util.function.Consumer;

public class StarlightBowItem extends BowItem implements TooltipItem {
    public StarlightBowItem(Properties properties) {
        super(properties);

        //?if >=26.2 {
        TooltipRegistry.register(this, this);
        //?}
    }

    @Override
    //? if >=26.2 {
    public void addTooltip(DataComponentGetter getter, Consumer<Component> tooltip) {
    //?} else {
    /*
    public void addTooltip(ItemStack stack, Consumer<Component> tooltip) {
    */
    //?}
        Style gold = Component.empty().getStyle().withColor(ChatFormatting.GOLD);
        tooltip.accept(Component.translatable("item.tide.starlight_bow.desc_0").setStyle(gold));
        tooltip.accept(Component.translatable("item.tide.starlight_bow.desc_1").setStyle(gold));
    }

    protected @NotNull Projectile createProjectile(@NotNull Level level, @NotNull LivingEntity shooter, @NotNull ItemStack weapon, @NotNull ItemStack ammo, boolean isCrit) {
        AbstractArrow projectile = new StarArrow(level, shooter, ammo, weapon);
        if (isCrit) projectile.setCritArrow(true);
        return projectile;
    }

    //? if >=1.21 {
    @Override
    protected void shoot(@NotNull ServerLevel level, @NotNull LivingEntity shooter, @NotNull InteractionHand hand, @NotNull ItemStack weapon, @NotNull List<ItemStack> projectileItems, float velocity, float inaccuracy, boolean isCrit, @Nullable LivingEntity target) {
        super.shoot(level, shooter, hand, weapon, projectileItems, velocity, inaccuracy, isCrit, target);
        //? if >=26.2 {
        level.playSound(null, shooter.blockPosition(), SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.PLAYERS, 1.5f, 1.0f - (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.1f);
        //?} else {
        /*
        level.playSound(null, shooter.blockPosition(), SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.PLAYERS, 1.5f, 1.0f - (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.1f);
        */
        //?}
    }
    //?}
    
    //? if >=1.21 && <26.2 {
    /* 
    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> lines, TooltipFlag flag) {
        super.appendHoverText(stack, context, lines, flag);
        this.addTooltip(stack, lines::add);
    }
    */
    //?} else {
    /*
    @Override
    public void releaseUsing(ItemStack weapon, Level level, LivingEntity shooter, int i) {
        if (shooter instanceof Player player) {
            boolean hasInfinity = player.getAbilities().instabuild || EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, weapon) > 0;
            ItemStack ammo = player.getProjectile(weapon);
            if (!ammo.isEmpty() || hasInfinity) {
                if (ammo.isEmpty()) ammo = new ItemStack(Items.ARROW);

                int j = this.getUseDuration(weapon) - i;
                float power = getPowerForTime(j);
                if (!(power < 0.1)) {
                    boolean skipAmmoConsume = (hasInfinity && ammo.is(Items.ARROW)) || shooter.getRandom().nextFloat() < 0.6f;
                    if (!level.isClientSide()) {
                        AbstractArrow arrow = (AbstractArrow) createProjectile(level, shooter, weapon, ammo, power == 1f);
                        arrow.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, power * 3.0F, 1.0F);

                        int powerBonus = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, weapon);
                        if (powerBonus > 0) arrow.setBaseDamage(arrow.getBaseDamage() + powerBonus * 0.5 + 0.5);

                        int kbBonus = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PUNCH_ARROWS, weapon);
                        if (kbBonus > 0) arrow.setKnockback(kbBonus);

                        if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FLAMING_ARROWS, weapon) > 0) arrow.setSecondsOnFire(100);

                        weapon.hurtAndBreak(1, player, player2 -> player2.broadcastBreakEvent(player.getUsedItemHand()));
                        if (skipAmmoConsume || player.getAbilities().instabuild && (ammo.is(Items.SPECTRAL_ARROW) || ammo.is(Items.TIPPED_ARROW))) arrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;

                        level.addFreshEntity(arrow);
                    }

                    level.playSound(
                            null,
                            player.getX(),
                            player.getY(),
                            player.getZ(),
                            SoundEvents.ARROW_SHOOT,
                            SoundSource.PLAYERS,
                            1.0F,
                            1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + power * 0.5F
                    );
                    level.playSound(null, shooter.blockPosition(), SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.PLAYERS, 1.5f, 1.0f - (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.1f);
                    if (!skipAmmoConsume && !player.getAbilities().instabuild) {
                        ammo.shrink(1);
                        if (ammo.isEmpty()) player.getInventory().removeItem(ammo);
                    }

                    player.awardStat(Stats.ITEM_USED.get(this));
                }
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> lines, TooltipFlag flag) {
        super.appendHoverText(stack, level, lines, flag);
        this.addTooltip(stack, lines::add);
    }
    */
    //?}
}
