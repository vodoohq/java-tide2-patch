package com.li64.tide.client;

import com.google.gson.JsonParser;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

/** Flat fish icons are rendered during GUI extraction, with their texture alpha preserved. */
public final class TideSilhouetteRenderer {
    public static final RenderPipeline PIPELINE = RenderPipeline.builder(RenderPipelines.GUI_TEXTURED_SNIPPET)
            .withLocation(Identifier.fromNamespaceAndPath("tide", "pipeline/gui_silhouette"))
            .withFragmentShader(Identifier.fromNamespaceAndPath("tide", "core/gui_silhouette"))
            .build();

    private TideSilhouetteRenderer() {}

    public static void item(GuiGraphicsExtractor graphics, ItemStack stack, int x, int y, int color) {
        Identifier id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        Identifier texture = id.withPath(path -> "textures/item/" + path + ".png");
        Identifier model = id.withPath(path -> "models/item/" + path + ".json");
        var resource = Minecraft.getInstance().getResourceManager().getResource(model);
        if (resource.isPresent()) {
            try (var reader = resource.get().openAsReader()) {
                var json = JsonParser.parseReader(reader).getAsJsonObject();
                if (json.has("textures") && json.getAsJsonObject("textures").has("layer0")) {
                    var value = json.getAsJsonObject("textures").get("layer0").getAsString();
                    if (!value.startsWith("#")) texture = Identifier.parse(value).withPath(path -> "textures/" + path + ".png");
                }
            } catch (java.io.IOException ignored) {
                // The conventional item texture remains a valid fallback for generated fish models.
            }
        }
        graphics.blit(PIPELINE, texture, x, y, 0, 0, 16, 16, 16, 16, color);
    }
}
