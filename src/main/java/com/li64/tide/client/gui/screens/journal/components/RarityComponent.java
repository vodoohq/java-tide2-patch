package com.li64.tide.client.gui.screens.journal.components;

import com.li64.tide.Tide;
import com.li64.tide.client.gui.screens.journal.ProfileComponent;
import com.li64.tide.data.journal.FishRarity;
import net.minecraft.client.gui.Font;

import org.jetbrains.annotations.NotNull;

//? if >=26.2 {
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
//?} else {
/*
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
*/
//?}

public class RarityComponent extends ProfileComponent {
        //? if >=26.2 {
    private static final Identifier STAR = Tide.resource("textures/gui/journal/star_note.png");
        //?} else {
        /*
    private static final ResourceLocation STAR = Tide.resource("textures/gui/journal/star_note.png");
        */
        //?}

    public int numStars;

    public RarityComponent(FishRarity rarity) {
        this.numStars = rarity.getNumStars();
    }

    @Override
    //? if >=26.2 {
    public void render(@NotNull GuiGraphicsExtractor graphics, Font font, int x, int y, int mouseX, int mouseY, float partialTick) {
    //?} else {
    /*
    public void render(@NotNull GuiGraphics graphics, Font font, int x, int y, int mouseX, int mouseY, float partialTick) {
    */
    //?}
        int center = x + AREA_WIDTH / 2;
        int padding = 2;
        for (int i = 0; i < numStars; i++) {
            int cellSize = 7 + padding;
            //? if >=26.2 {
            graphics.blit(RenderPipelines.GUI_TEXTURED, STAR, center - ((numStars - 1) * cellSize / 2) + (i * cellSize) - 4, y, 0, 0, 7, 6, 7, 6, ARGB.white(1f));
            //?} else {
            /*
            graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, STAR, center - ((numStars - 1) * cellSize / 2) + (i * cellSize) - 4, y, 0, 0, 7, 6, 7, 6);
            */
            //?}
        }
    }

    @Override
    public int getRequiredHeight() {
        return 7;
    }
}
