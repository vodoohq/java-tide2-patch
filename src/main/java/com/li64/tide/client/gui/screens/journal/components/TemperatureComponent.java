package com.li64.tide.client.gui.screens.journal.components;

import com.li64.tide.Tide;
import com.li64.tide.client.gui.screens.journal.ProfileComponent;
import com.li64.tide.util.TideUtils;

import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import org.jetbrains.annotations.NotNull;

//? if >=26.2 {
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.util.ARGB;
import net.minecraft.resources.Identifier;
//?} else {
/*
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
*/
//?}

public class TemperatureComponent extends BarComponent {
        //? if >=26.2 {
    private static final Identifier TEMP_BAR = Tide.resource("textures/gui/journal/temp_bar.png");
        //?} else {
        /*
    private static final ResourceLocation TEMP_BAR = Tide.resource("textures/gui/journal/temp_bar.png");
        */
        //?}
    private static final Component TITLE = Component.translatable("journal.info.climate.title");

    private final float minTemp;
    private final float maxTemp;

    public TemperatureComponent(float minTemp, float maxTemp) {
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
    }

    @Override
    //? if >=26.2 {
    public void render(@NotNull GuiGraphicsExtractor graphics, Font font, int x, int y, int mouseX, int mouseY, float partialTick) {
    //?} else {
    /*
    public void render(@NotNull GuiGraphics graphics, Font font, int x, int y, int mouseX, int mouseY, float partialTick) {
    */
    //?}
        int center = x + ProfileComponent.AREA_WIDTH / 2;

        // render temperature bar
        //? if >=26.2 {
        graphics.text(font, TITLE, center - font.width(TITLE) / 2, y, ProfileComponent.TEXT_COLOR, false);
        graphics.blit(RenderPipelines.GUI_TEXTURED, EMPTY_BAR, x + BAR_X, y + BAR_Y, 0, 0, BAR_WIDTH, BAR_HEIGHT, BAR_WIDTH, BAR_HEIGHT, ARGB.white(1f));
        //?} else {
        /*
        graphics.text(font, TITLE, center - font.width(TITLE) / 2, y, ProfileComponent.TEXT_COLOR, false);
        graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, EMPTY_BAR, x + BAR_X, y + BAR_Y, 0, 0, BAR_WIDTH, BAR_HEIGHT, BAR_WIDTH, BAR_HEIGHT);
        */
        //?}
        renderFilledBar(TEMP_BAR, graphics, x + BAR_X, y + BAR_Y, minTemp / 2f + 0.5f, maxTemp / 2f + 0.5f);

        // render temperature mouse cursor
        if (mouseX >= x + BAR_X && mouseX <= x + BAR_X + BAR_WIDTH
                && mouseY >= y + BAR_Y && mouseY <= y + BAR_Y + BAR_HEIGHT) {
            float temp = Mth.clamp((float) (mouseX - (x + BAR_X)) / BAR_WIDTH, 0f, 1f) * 2f - 1f;
            //? if >=26.2 {
            graphics.setTooltipForNextFrame(font, Component.literal(
                    Math.round(TideUtils.mcTempToRealTemp(temp))
                            + (Tide.CLIENT_CONFIG.journal.useFahrenheit ? "°F" : "°C")),
                    mouseX, y + BAR_Y
            );
            graphics.blit(RenderPipelines.GUI_TEXTURED, CURSOR, mouseX - 1, y + BAR_Y, 0, 0, 3, 9, 3, 9, ARGB.white(1f));
            //?} else {
            /*
            graphics.renderTooltip(font, Component.literal(
                    Math.round(TideUtils.mcTempToRealTemp(temp))
                            + (Tide.CLIENT_CONFIG.journal.useFahrenheit ? "°F" : "°C")),
                    mouseX, y + BAR_Y
            );
            graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, CURSOR, mouseX - 1, y + BAR_Y, 0, 0, 3, 9, 3, 9);
            */
            //?}
        }
    }

    @Override
    public int getRequiredHeight() {
        return 24;
    }
}
