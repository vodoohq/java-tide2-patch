package com.li64.tide.client.gui.screens.journal.components;

import com.li64.tide.Tide;
import com.li64.tide.client.gui.screens.journal.ProfileComponent;

//? if >=26.2 {
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
//?} else {
/*
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
*/
//?}

public abstract class BarComponent extends ProfileComponent {
        //? if >=26.2 {
    protected static final Identifier EMPTY_BAR = Tide.resource("textures/gui/journal/empty_bar.png");
    protected static final Identifier DIVIDER = Tide.resource("textures/gui/journal/bar_divider.png");
    protected static final Identifier CURSOR = Tide.resource("textures/gui/journal/bar_cursor.png");
        //?} else {
        /*
    protected static final ResourceLocation EMPTY_BAR = Tide.resource("textures/gui/journal/empty_bar.png");
    protected static final ResourceLocation DIVIDER = Tide.resource("textures/gui/journal/bar_divider.png");
    protected static final ResourceLocation CURSOR = Tide.resource("textures/gui/journal/bar_cursor.png");
        */
        //?}
    protected static final int BAR_WIDTH = 129;
    protected static final int BAR_HEIGHT = 9;
    protected static final int BAR_X = 22;
    protected static final int BAR_Y = 11;
    protected static final int DIV_WIDTH = 3;
    protected static final int DIV_HEIGHT = 11;

    //? if >=26.2 {
    protected void renderFilledBar(Identifier bar, GuiGraphicsExtractor graphics,
    //?} else {
    /*
    protected void renderFilledBar(ResourceLocation bar, GuiGraphics graphics,
    */
    //?}
                                   int x, int y, float min, float max) {
        int minX = Math.max((int)(min * BAR_WIDTH), 0);
        int maxX = Math.min((int)(max * BAR_WIDTH), BAR_WIDTH);
        int width = maxX - minX;
        //? if >=26.2 {
        graphics.blit(RenderPipelines.GUI_TEXTURED, bar, x + minX, y, minX, 0, width, BAR_HEIGHT, BAR_WIDTH, BAR_HEIGHT, ARGB.white(1f));
        //?} else {
        /*
        graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, bar, x + minX, y, minX, 0, width, BAR_HEIGHT, BAR_WIDTH, BAR_HEIGHT);
        */
        //?}

        // render dividers on time bar
        //? if >=26.2 {
        graphics.blit(RenderPipelines.GUI_TEXTURED, DIVIDER, x + minX - 1, y - 1, 0, 0, DIV_WIDTH, DIV_HEIGHT, DIV_WIDTH, DIV_HEIGHT, ARGB.white(1f));
        graphics.blit(RenderPipelines.GUI_TEXTURED, DIVIDER, x + maxX - 1, y - 1, 0, 0, DIV_WIDTH, DIV_HEIGHT, DIV_WIDTH, DIV_HEIGHT, ARGB.white(1f));
        //?} else {
        /*
        graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, DIVIDER, x + minX - 1, y - 1, 0, 0, DIV_WIDTH, DIV_HEIGHT, DIV_WIDTH, DIV_HEIGHT);
        graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, DIVIDER, x + maxX - 1, y - 1, 0, 0, DIV_WIDTH, DIV_HEIGHT, DIV_WIDTH, DIV_HEIGHT);
        */
        //?}
    }
}
