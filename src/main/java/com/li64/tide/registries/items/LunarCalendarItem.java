package com.li64.tide.registries.items;

import com.li64.tide.registries.TideBlocks;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.BlockItem;

import java.util.function.Consumer;

//? if >=26.2 {
import com.li64.tide.registries.TooltipRegistry;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.world.level.MoonPhase;
//?} elif <1.21 {
/* 
import net.minecraft.world.level.Level;
*/
//?} else {
/* 
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import java.util.List;
*/
//?}

        public class LunarCalendarItem extends BlockItem implements InformationalItem, TooltipItem {
        public LunarCalendarItem(Properties properties) {
                super(TideBlocks.LUNAR_CALENDAR, properties);
        
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
                Style gray = Component.empty().getStyle().withColor(ChatFormatting.GRAY);
                tooltip.accept(Component.translatable("item.tide.lunar_calendar.desc").setStyle(gray));
        }
        
        @Override
        public String getResult(ServerLevel level, ServerPlayer player) {
                //? if >=26.2 {
                MoonPhase phase = MoonPhase.values()[
                        (int) ((level.getGameTime() / MoonPhase.PHASE_LENGTH) % MoonPhase.COUNT)
                ];
                return Integer.toString(phase.index());
                //?} else {
                /*
                return Integer.toString(level.environmentAttributes().getValue(net.minecraft.world.attribute.EnvironmentAttributes.MOON_PHASE).index());
                */
                //?}
        }
        
        @Override
        public Component parseResult(String result) {
                return Component.translatable("journal.info.moon_phase." + Integer.parseInt(result));
        }
        
        @Override
        public int updatePeriod() {
                return 20;
        }
        
        //? if >=1.21.1 && <26.2 {
        /*
        @Override
        public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flag) {
                super.appendHoverText(stack, context, lines, flag);
                this.addTooltip(stack, lines::add);
        }
        */
        //?} else {
        /*
        @Override
        public void appendHoverText(ItemStack stack, Level level, List<Component> lines, TooltipFlag flag) {
                super.appendHoverText(stack, level, lines, flag);
                this.addTooltip(stack, lines::add);
        }
        */
        //?}
}
