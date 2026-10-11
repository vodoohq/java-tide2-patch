package com.li64.tide.client.gui.overlays;

import com.li64.tide.Tide;
import net.minecraft.util.Mth;
//? if >=26.2 {
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.util.ARGB;
import net.minecraft.resources.Identifier;
//?} else {
/*
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
*/
//?}

public class CastBarOverlay {
        //? if >=26.2 {
    private static final Identifier BAR_EMPTY_TEX = Tide.resource("textures/gui/fishing/cast_bar_empty.png");
    private static final Identifier BAR_FILLED_TEX = Tide.resource("textures/gui/fishing/cast_bar_filled.png");
        //?} else {
        /*
    private static final ResourceLocation BAR_EMPTY_TEX = Tide.resource("textures/gui/fishing/cast_bar_empty.png");
    private static final ResourceLocation BAR_FILLED_TEX = Tide.resource("textures/gui/fishing/cast_bar_filled.png");
        */
        //?}

    private static float rodChargePercent = 0f;
    private static float timer = 20f;

    //? if >=26.2 {
    public static void render(GuiGraphicsExtractor extractor, float deltaTicks) {
        if (timer >= 20f) return;
        timer += deltaTicks;

        float alpha = -Mth.clamp((timer - 10f) / 10f, 0f, 1f) + 1f;

        int texWidth = 24;
        int texHeight = 16;

        int x = (extractor.guiWidth() - texWidth) / 2;
        int y = extractor.guiHeight() / 2 - texHeight - 6;

        int fillWidth = (int) Math.ceil(rodChargePercent * texWidth);

        int color = ARGB.white(alpha);
        extractor.blit(RenderPipelines.GUI_TEXTURED, BAR_EMPTY_TEX, x, y, 0, 0, texWidth, texHeight, texWidth, texHeight, color);
        extractor.blit(RenderPipelines.GUI_TEXTURED, BAR_FILLED_TEX, x, y, 0, 0, fillWidth, texHeight, texWidth, texHeight, color);
    }
    //?} else {
    /*
    public static void render(GuiGraphics graphics, float deltaTicks) {
        if (timer >= 20f) return;
        timer += deltaTicks;

        float alpha = -Mth.clamp((timer - 10f) / 10f, 0f, 1f) + 1f;

        int texWidth = 24;
        int texHeight = 16;

        int x = (graphics.guiWidth() - texWidth) / 2;
        int y = graphics.guiHeight() / 2 - texHeight - 6;

        int fillWidth = (int) Math.ceil(rodChargePercent * texWidth);

        RenderSystem.enableBlend();
        graphics.setColor(1f, 1f, 1f, alpha);
        graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, BAR_EMPTY_TEX, x, y, 0, 0, texWidth, texHeight, texWidth, texHeight);
        graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, BAR_FILLED_TEX, x, y, 0, 0, fillWidth, texHeight, texWidth, texHeight);
        RenderSystem.disableBlend();
        graphics.setColor(1f, 1f, 1f, 1f);
    }
    */
    //?}

    public static void rodChargeTick(float percent) {
        rodChargePercent = percent;
        timer = 0;
    }
}
