package com.li64.tide.client.gui.overlays;

import com.li64.tide.registries.entities.misc.fishing.HookAccessor;
import com.li64.tide.util.TideUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
//? if >=26.2 {
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
//?} else {
/*
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
*/
//?}
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class SonarOverlay {
        //? if >=26.2 {
    private static final Identifier SLOT_BACKGROUND_SPRITE = TideUtils.sprite("bait/slot_background");
        //?} else {
        /*
    private static final ResourceLocation SLOT_BACKGROUND_SPRITE = TideUtils.sprite("bait/slot_background");
        */
        //?}
    private static final float MAX_TIMEOUT = 20f;
    private static final float MAX_FADEOUT = 20f;

    private static ItemStack stack;
    private static float timeout;
    private static float fadeout;

    @SuppressWarnings("DataFlowIssue")
    public static void render(/*? if >=26.2 {*/GuiGraphicsExtractor/*?} else {*//*GuiGraphics*//*?}*/ graphics, float dt) {
        if (stack == null) return;
        Player player = Minecraft.getInstance().player;
        boolean hooked = !HookAccessor.bobberRemoved(player) && HookAccessor.getHook(player).isFishHooked();

        if (timeout > 0f) timeout -= dt;
        else if (!hooked) fadeout -= dt;
        if (fadeout < 0f) {
            stack = null;
            return;
        }

        int x = graphics.guiWidth() / 2 - 8;
        int y = graphics.guiHeight() / 2 + 12;
        float alpha = fadeout / MAX_FADEOUT;
        /*? if >=1.21 {*/graphics.blitSprite(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, SLOT_BACKGROUND_SPRITE, x - 4, y - 4, 24, 24, net.minecraft.util.ARGB.white(alpha));
        /*?} else {*//*graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, SLOT_BACKGROUND_SPRITE, x - 4, y - 4, 0, 0, 24, 24, 24, 24);*//*?}*/
        graphics.item(stack, x, y);
    }

    public static void show(ItemStack stack) {
        SonarOverlay.stack = stack;
        SonarOverlay.timeout = MAX_TIMEOUT;
        SonarOverlay.fadeout = MAX_FADEOUT;
    }
}
