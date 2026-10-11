package com.li64.tide.client.gui.overlays;

import com.li64.tide.Tide;
import com.li64.tide.config.TideClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
//? if >=26.2 {
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
//?} else {
/*
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
*/
//?}

import java.util.Map;

public class FishingInfoOverlay {
        //? if >=26.2 {
    public static Map<Identifier, Component> CLIENT_INFO_DATA = Map.of();
        //?} else {
        /*
    public static Map<ResourceLocation, Component> CLIENT_INFO_DATA = Map.of();
        */
        //?}

    private static final int PADDING = 5;
    private static final int LINE_SPACING = 2;

    public static void render(/*? if >=26.2 {*/GuiGraphicsExtractor/*?} else {*//*GuiGraphics*//*?}*/ graphics, float dt) {
        Font font = Minecraft.getInstance().font;
        TideClientConfig.General.InfoPlacement placement = Tide.CLIENT_CONFIG.general.infoPlacement;
        if (placement == null) return;
        int offsetX = Tide.CLIENT_CONFIG.general.infoOffsetX;
        int offsetY = Tide.CLIENT_CONFIG.general.infoOffsetY;

        int i = 0;
        //? if >=26.2 {
        for (Map.Entry<Identifier, Component> entry : CLIENT_INFO_DATA.entrySet()) {
        //?} else {
        /*
        for (Map.Entry<ResourceLocation, Component> entry : CLIENT_INFO_DATA.entrySet()) {
        */
        //?}
            graphics.blit(
                    Tide.resource("textures/gui/info_icons/" + entry.getKey().getPath() + ".png"),
                    placement.isLeft()
                            ? PADDING + offsetX
                            : graphics.guiWidth() - PADDING - 9 + offsetX,
                    placement.isTop()
                            ? PADDING + (font.lineHeight + LINE_SPACING) * i + offsetY
                            : graphics.guiHeight() - PADDING - (font.lineHeight + LINE_SPACING) * i - 9 - offsetY,
                    0, 0, 9, 9, 9, 9
            );
            Component text = entry.getValue();
            graphics.text(
                    font, text,
                    placement.isLeft()
                            ? PADDING + 12 + offsetX
                            : graphics.guiWidth() - PADDING - 12 - font.width(text) - offsetX,
                    placement.isTop()
                            ? PADDING + (font.lineHeight + LINE_SPACING) * i + 1 + offsetY
                            : graphics.guiHeight() - PADDING - (font.lineHeight + LINE_SPACING) * i - font.lineHeight + 1 - offsetY,
                    0xffffff
            );
            i++;
        }
    }
}
