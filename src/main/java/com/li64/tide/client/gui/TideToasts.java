package com.li64.tide.client.gui;

import com.li64.tide.Tide;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

//? if >=26.2 {
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
//?} else {
/*
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.resources.ResourceLocation;
*/
//?}

public class TideToasts {
    // TODO: maybe make some custom art for this?
    //? if >=26.2 {
    private static final Identifier TEXTURE = Tide.resource("minecraft", "toast/recipe");
    //?} else {
    /*
    private static final ResourceLocation TEXTURE = Tide.resource("minecraft", "toast/recipe");
    */
    //?}

    public static void display(Toast toast) {
        Minecraft.getInstance().gui.toastManager().addToast(toast);
    }

    public static class NewPageToast implements Toast {
            private final Component title;
            private final Component description;
            private final ItemStack displayedItem;
            private long time;
            private double notificationMultiplier = 1.0D;
            
            public NewPageToast(Component title, Component description, ItemStack displayedItem) {
                    this.title = title;
                    this.description = description;
                    this.displayedItem = displayedItem;
                    if (description == null) Tide.LOG.error("Component cannot be null");
            }
    
            //? if >=26.2 {
            @Override
            public @NotNull Visibility getWantedVisibility() {
                    return (double) this.time >= 5000.0D * this.notificationMultiplier ? Visibility.HIDE : Visibility.SHOW;
            }
    
            @Override 
            public void update(ToastManager manager, long time) {
                    this.time = time;
                    this.notificationMultiplier = manager.getNotificationDisplayTimeMultiplier();
            }
    
            @Override
            public void extractRenderState(GuiGraphicsExtractor extractor, Font font, long time) {
                    extractor.blitSprite(RenderPipelines.GUI_TEXT, TEXTURE, 0, 0, this.width(), this.height());
                    extractor.text(font, title, 30, 7, -11534256, false);
                    extractor.text(font, description, 30, 18, -16777216, false);
                    extractor.fakeItem(displayedItem, 8, 8);
            }
            //?} else {
            /*
            @Override
            public @NotNull Visibility render(GuiGraphics graphics, ToastComponent component, long time) {
            //? if >=1.21 {
            graphics.blitSprite(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, TEXTURE, 0, 0, this.width(), this.height());
            //?} else {
            graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, TEXTURE, 0, 0, 0, 32, this.width(), this.height());
            //?}
            graphics.text(component.getMinecraft().font, title, 30, 7, -11534256, false);
            graphics.text(component.getMinecraft().font, description, 30, 18, -16777216, false);
            graphics.renderFakeItem(displayedItem, 8, 8);
            return (double) (time) >= 5000.0D * component.getNotificationDisplayTimeMultiplier() ? Visibility.HIDE : Visibility.SHOW;
            }
            */
            //?}
    }
}