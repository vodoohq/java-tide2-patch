package com.li64.tide.client.gui.screens.journal.components;

import com.li64.tide.Tide;
import com.li64.tide.client.gui.screens.journal.ProfileComponent;
import com.li64.tide.compat.seasons.Season;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
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
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SeasonsComponent extends ProfileComponent {
        //? if >=26.2 {
    private static final Identifier SEASONS = Tide.resource("textures/gui/journal/seasons.png");
        //?} else {
        /*
    private static final ResourceLocation SEASONS = Tide.resource("textures/gui/journal/seasons.png");
        */
        //?}
    private static final Component TITLE = Component.translatable("journal.info.seasons.title");

    public List<Season> seasons;

    public SeasonsComponent(List<Season> seasons) {
        this.seasons = seasons;
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
        //? if >=26.2 {
        graphics.text(font, TITLE, center - font.width(TITLE) / 2, y, TEXT_COLOR, false);
        //?} else {
        /*
        graphics.text(font, TITLE, center - font.width(TITLE) / 2, y, TEXT_COLOR, false);
        */
        //?}

        int seasonCount = seasons.size();
        int seasonY = y + 12;
        int padding = 4;

        for (int i = 0; i < seasonCount; i++) {
            int cellSize = 8 + padding;
            int seasonX = center - ((seasonCount - 1) * cellSize / 2) + (i * cellSize) - 4;

            int offset = switch (seasons.get(i)) {
                case SPRING -> 0;
                case SUMMER -> 8;
                case FALL -> 16;
                case WINTER -> 24;
            };
            //? if >=26.2 {
            graphics.blit(RenderPipelines.GUI_TEXTURED, SEASONS, seasonX, seasonY, offset, 0, 8, 8, 32, 8, ARGB.white(1f));
            //?} else {
            /*
            graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, SEASONS, seasonX, seasonY, offset, 0, 8, 8, 32, 8);
            */
            //?}

            if (mouseX >= seasonX && mouseX <= seasonX + 8 && mouseY >= seasonY && mouseY <= seasonY + 8)
            //? if >=26.2 {
            graphics.setTooltipForNextFrame(font, Component.translatable("journal.info.seasons." + seasons.get(i).getSerializedName()), mouseX, mouseY);
            //?} else {
            /*
            graphics.renderTooltip(font, Component.translatable("journal.info.seasons." + seasons.get(i).getSerializedName()), mouseX, mouseY);
            */
            //?}
        }
    }

    @Override
    public int getRequiredHeight() {
        return 24;
    }
}
