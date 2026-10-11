package com.li64.tide.client.gui.overlays;

import com.li64.tide.Tide;
import com.li64.tide.compat.CompatHelper;
import com.li64.tide.data.fishing.MinigameBehavior;
import com.li64.tide.network.messages.MinigameServerMsg;
import com.li64.tide.registries.entities.misc.fishing.HookAccessor;
import com.li64.tide.util.TideUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
//? if >=26.2 {
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
//?} else {
/*
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
*/
//?}
import net.minecraft.util.Mth;

import java.util.Random;

public class CatchMinigameOverlay {
    private static final int INIT_DELAY_MILLIS = 200;
    //? if >=26.2 {
    private static final Identifier BAR_BG = Tide.resource("textures/gui/fishing/minigame_bg.png");
    private static final Identifier FILL_WATER = TideUtils.sprite("fishing/minigame_fill");
    private static final Identifier FILL_LAVA = TideUtils.sprite("fishing/minigame_fill_lava");
    private static final Identifier FILL_VOID = TideUtils.sprite("fishing/minigame_fill_void");
    private static final Identifier BAR_OVERLAY = Tide.resource("textures/gui/fishing/minigame_overlay.png");

    private static final Identifier MARKER = Tide.resource("textures/gui/fishing/marker.png");
    private static final Identifier SELECT = Tide.resource("textures/gui/fishing/marker_select.png");
    //?} else {
    /*
    private static final ResourceLocation BAR_BG = Tide.resource("textures/gui/fishing/minigame_bg.png");
    private static final ResourceLocation FILL_WATER = TideUtils.sprite("fishing/minigame_fill");
    private static final ResourceLocation FILL_LAVA = TideUtils.sprite("fishing/minigame_fill_lava");
    private static final ResourceLocation FILL_VOID = TideUtils.sprite("fishing/minigame_fill_void");
    private static final ResourceLocation BAR_OVERLAY = Tide.resource("textures/gui/fishing/minigame_overlay.png");

    private static final ResourceLocation MARKER = Tide.resource("textures/gui/fishing/marker.png");
    private static final ResourceLocation SELECT = Tide.resource("textures/gui/fishing/marker_select.png");
    */
    //?}

    private static float timeLeft = 100f;
    private static float animProgress = 0f;
    private static float timer = 20f;
    private static boolean isActive = false;
    private static MutableComponent accuracyText = null;
    private static ChatFormatting textColor = ChatFormatting.WHITE;
    private static byte type;
    private static float area;
    private static float speed;
    private static MinigameBehavior behavior;
    private static long delayTime;

    public static void start(byte type, byte behavior, float area, float speed) {
        if (isActive) return;

        CatchMinigameOverlay.type = type;
        CatchMinigameOverlay.behavior = MinigameBehavior.values()[behavior];
        CatchMinigameOverlay.area = area;
        CatchMinigameOverlay.speed = speed;

        accuracyText = null;
        animProgress = new Random().nextFloat() * 100f;

        timeLeft = 0;
        timer = 0;

        isActive = true;
        delayTime = System.currentTimeMillis() + INIT_DELAY_MILLIS;
    }

    public static void interact() {
        if (!isActive || System.currentTimeMillis() < delayTime) return;

        // Calculate accuracy
        float accuracy = Math.abs(getMinigamePosition());

        if (accuracy < area) {
            if (accuracy < 0.1f) {
                accuracyText = Component.translatable("minigame.perfect");
                textColor = ChatFormatting.GOLD;
                Tide.NETWORK.sendToServer(new MinigameServerMsg((byte) 3));
            }
            else {
                accuracyText = Component.translatable("minigame.catch");
                textColor = ChatFormatting.GREEN;
                Tide.NETWORK.sendToServer(new MinigameServerMsg((byte) 2));
            }
        }
        else {
            accuracyText = Component.translatable("minigame.miss");
            textColor = ChatFormatting.RED;
            Tide.NETWORK.sendToServer(new MinigameServerMsg((byte) 1));
        }

        close();
    }

    public static void close() {
        isActive = false;
        timer = 0f;
    }

    public static void render(/*? if >=26.2 {*/GuiGraphicsExtractor/*?} else {*//*GuiGraphics*//*?}*/ graphics, float dt) {
        if (CompatHelper.useStarcatcherMinigame()) return;
        if (timer >= 20f) return;
        if (!isActive) timer += dt;
        else {
            timeLeft += dt;
            animProgress += dt;

            if (timeLeft >= 80f || HookAccessor.bobberRemoved(Minecraft.getInstance().player)) {
                // Timeout
                close();
                Tide.NETWORK.sendToServer(new MinigameServerMsg((byte) 0));
            }
        }

        float alpha = -Mth.clamp((timer - 10f) / 10f, 0f, 1f) + 1f;

        int texWidth = 60;
        int texHeight = 7;

        int offset = Math.round((texWidth / 2f) * (1 - area));

        int x = (graphics.guiWidth() - texWidth) / 2;
        int y = graphics.guiHeight() / 2 - 18;
        //? if >=26.2 {
        Identifier fillSprite = type == 1 ? FILL_LAVA : (type == 2 ? FILL_VOID : FILL_WATER);
        //?} else {
        /*
        ResourceLocation fillSprite = type == 1 ? FILL_LAVA : (type == 2 ? FILL_VOID : FILL_WATER);
        */
        //?}

        graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, BAR_BG, x, y, 0, 0, texWidth, texHeight, texWidth, texHeight, net.minecraft.util.ARGB.white(alpha));
        /*? if >=1.21 {*/graphics.blitSprite(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, fillSprite, x + offset, y, texWidth - offset * 2, texHeight, net.minecraft.util.ARGB.white(alpha));
        //?} else {
        /*TideUtils.blitNineSliced(graphics, fillSprite, x + offset, y,
                texWidth - offset * 2, texHeight, 2, 2, 2, 4,
                6, 7, 0, 0, 6, 7);
        *///?}
        graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, BAR_OVERLAY, x, y, 0, 0, texWidth, texHeight, texWidth, texHeight, net.minecraft.util.ARGB.white(alpha));

        int markerX = Math.round((x + texWidth / 2f - 2) + getMinigamePosition() * (texWidth / 2f - 2));
        //? if >=26.2 {
        Identifier markerTexture = timer % 4 < 2 ? MARKER : SELECT;
        //?} else {
        /*
        ResourceLocation markerTexture = timer % 4 < 2 ? MARKER : SELECT;
        */
        //?}
        graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, markerTexture, markerX, y - 1, 0, 0, 4, 9, 4, 9, net.minecraft.util.ARGB.white(alpha));

        if (Tide.CLIENT_CONFIG.minigame.doFeedback && accuracyText != null) {
            Font font = Minecraft.getInstance().font;
            if (alpha >= 0.03f) {
                graphics.text(font,
                        accuracyText.withStyle(accuracyText.getStyle().withColor(textColor)),
                        (graphics.guiWidth() - font.width(accuracyText)) / 2, y - 10,
                        net.minecraft.util.ARGB.white(alpha), false);
            }
        }
    }

    private static float getMinigamePosition() {
        return behavior.apply(animProgress * speed);
    }

    public static boolean isActive() {
        return isActive;
    }
}
