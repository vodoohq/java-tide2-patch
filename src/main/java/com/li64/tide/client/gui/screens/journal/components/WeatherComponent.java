package com.li64.tide.client.gui.screens.journal.components;

import com.li64.tide.Tide;
import com.li64.tide.client.gui.screens.journal.ProfileComponent;
import com.li64.tide.data.fishing.conditions.types.WeatherType;
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

public class WeatherComponent extends ProfileComponent {
        //? if >=26.2 {
    private static final Identifier ICONS = Tide.resource("textures/gui/journal/weather.png");
        //?} else {
        /*
    private static final ResourceLocation ICONS = Tide.resource("textures/gui/journal/weather.png");
        */
        //?}
    private static final Component TITLE = Component.translatable("journal.info.weather.title");

    public List<WeatherType> weatherTypes;

    public WeatherComponent(List<WeatherType> weatherTypes) {
        this.weatherTypes = weatherTypes;
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

        int count = weatherTypes.size();
        int spriteY = y + 12;
        int padding = 4;

        for (int i = 0; i < count; i++) {
            int cellSize = 10 + padding;
            int spriteX = center - ((count - 1) * cellSize / 2) + (i * cellSize) - 4;

            int offset = getOffset(weatherTypes.get(i)) * 10;
            //? if >=26.2 {
            graphics.blit(RenderPipelines.GUI_TEXTURED, ICONS, spriteX, spriteY, offset, 0, 10, 10, 30, 10, ARGB.white(1f));
            //?} else {
            /*
            graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, ICONS, spriteX, spriteY, offset, 0, 10, 10, 30, 10);
            */
            //?}

            if (mouseX >= spriteX && mouseX <= spriteX + 10 && mouseY >= spriteY && mouseY <= spriteY + 10)
            //? if >=26.2 {
            graphics.setTooltipForNextFrame(font, Component.translatable("journal.info.weather." + weatherTypes.get(i).getSerializedName()), mouseX, mouseY);
            //?} else {
            /*
            graphics.renderTooltip(font, Component.translatable("journal.info.weather." + weatherTypes.get(i).getSerializedName()), mouseX, mouseY);
            */
            //?}
        }
    }

    public static int getOffset(WeatherType weatherType) {
        return switch (weatherType) {
            case CLEAR -> 0;
            case RAIN -> 1;
            case STORM -> 2;
        };
    }

    @Override
    public int getRequiredHeight() {
        return 26;
    }
}
