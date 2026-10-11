package com.li64.tide.registries.entities.misc.fishing;

import com.li64.tide.Tide;
import com.li64.tide.registries.items.FishingBobberItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;

import org.jetbrains.annotations.NotNull;

//? if >=26.2 {
import com.li64.tide.client.renderer.state.TideFishingHookRenderState;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
//?} elif >= 1.21 {
/*
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
*/
//?} else {
/*
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
*/
//?}

//? if >=26.2 {
public class TideFishingBobberLayer extends RenderLayer<TideFishingHookRenderState, TideFishingHookModel<TideFishingHookRenderState>> {
    public static final ModelLayerLocation MODEL_LOCATION = new ModelLayerLocation(Tide.resource("fishing_hook"), "bobber");
    private final TideFishingHookModel<TideFishingHookRenderState> model;

    public TideFishingBobberLayer(RenderLayerParent<TideFishingHookRenderState, TideFishingHookModel<TideFishingHookRenderState>> parent, EntityModelSet modelSet) {
        super(parent);
        this.model = new TideFishingHookModel<>(modelSet.bakeLayer(MODEL_LOCATION));
    }

    @Override
    public void submit(@NotNull PoseStack poseStack, @NotNull SubmitNodeCollector collector, int packedLight, @NotNull TideFishingHookRenderState state, float yRot, float xRot) {
            this.model.setupAnim(state);

            //Render bobber
            if (FishingBobberItem.renderItemModel(state.bobber)) {
                    poseStack.pushPose();
                    poseStack.scale(0.5f, 0.5f, 0.5f);
                    poseStack.translate(0.03f, -0.22f, 0.0f);
                    poseStack.rotate(Axis.ZP.rotationDegrees(180.0f));

                    ItemStackRenderState itemState = new ItemStackRenderState();
                    Minecraft.getInstance().getItemModelResolver().updateForTopItem(itemState, state.bobber, ItemDisplayContext.FIXED, null, null, state.id);
                    itemState.submit(poseStack, collector, packedLight, OverlayTexture.NO_OVERLAY, 0);

                    poseStack.popPose();
            } else {
                    Identifier textureLocation = FishingBobberItem.getTexture(state.bobber);
                    RenderType renderType = RenderTypes.entityCutout(textureLocation);
                    collector.submitModel(this.model, state, poseStack, renderType, packedLight, OverlayTexture.NO_OVERLAY, ARGB.color(255, 255, 255, 255), null, 0);
            }
    }
}
//?} elif >= 1.21 {
/*
public class TideFishingBobberLayer extends RenderLayer<TideFishingHook, TideFishingHookModel<TideFishingHook>> {
    public static final ModelLayerLocation MODEL_LOCATION = new ModelLayerLocation(Tide.resource("fishing_hook"), "bobber");
    private final TideFishingHookModel<TideFishingHook> model;
    private final ItemRenderer itemRenderer;

    public TideFishingBobberLayer(RenderLayerParent<TideFishingHook, TideFishingHookModel<TideFishingHook>> parent, EntityModelSet modelSet, ItemRenderer itemRenderer) {
        super(parent);
        this.itemRenderer = itemRenderer;
        this.model = new TideFishingHookModel<>(modelSet.bakeLayer(MODEL_LOCATION));
    }

    @Override
    public void render(@NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight, @NotNull TideFishingHook hookEntity, float limbSwing, float limbSwingAmount, float var1, float ageInTicks, float netHeadYaw, float netHeadPitch) {
        this.getParentModel().copyPropertiesTo(this.model);
        this.model.setupAnim(hookEntity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, netHeadPitch);

        // Render bobber
        if (FishingBobberItem.renderItemModel(hookEntity.getBobber())) {
            poseStack.pushPose();
            poseStack.scale(0.5f, 0.5f, 0.5f);
            poseStack.translate(0.03f, -0.22f, 0.0f);
            poseStack.rotate(Axis.ZP.rotationDegrees(180.0f));
            this.itemRenderer.renderStatic(hookEntity.getBobber(),
                    ItemDisplayContext.FIXED, packedLight, OverlayTexture.NO_OVERLAY,
                    poseStack, buffer, hookEntity.level(), hookEntity.getId());
            poseStack.popPose();
        } else {
                ResourceLocation textureLocation = FishingBobberItem.getTexture(hookEntity.getBobber());
                VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(textureLocation));
                this.model.renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY, FastColor.ARGB32.color(255, 255, 255 ,255));
        }
    }
}
*/
//?} else {
/*
public class TideFishingBobberLayer extends RenderLayer<TideFishingHook, TideFishingHookModel<TideFishingHook>> {
    public static final ModelLayerLocation MODEL_LOCATION = new ModelLayerLocation(Tide.resource("fishing_hook"), "bobber");
    private final TideFishingHookModel<TideFishingHook> model;
    private final ItemRenderer itemRenderer;

    public TideFishingBobberLayer(RenderLayerParent<TideFishingHook, TideFishingHookModel<TideFishingHook>> parent, EntityModelSet modelSet, ItemRenderer itemRenderer) {
        super(parent);
        this.itemRenderer = itemRenderer;
        this.model = new TideFishingHookModel<>(modelSet.bakeLayer(MODEL_LOCATION));
    }

    @Override
    public void render(@NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight, @NotNull TideFishingHook hookEntity, float limbSwing, float limbSwingAmount, float var1, float ageInTicks, float netHeadYaw, float netHeadPitch) {
        this.getParentModel().copyPropertiesTo(this.model);
        this.model.setupAnim(hookEntity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, netHeadPitch);

        // Render bobber
        if (FishingBobberItem.renderItemModel(hookEntity.getBobber())) {
            poseStack.pushPose();
            poseStack.scale(0.5f, 0.5f, 0.5f);
            poseStack.translate(0.03f, -0.22f, 0.0f);
            poseStack.rotate(Axis.ZP.rotationDegrees(180.0f));
            this.itemRenderer.renderStatic(hookEntity.getBobber(),
                    ItemDisplayContext.FIXED, packedLight, OverlayTexture.NO_OVERLAY,
                    poseStack, buffer, hookEntity.level(), hookEntity.getId());
            poseStack.popPose();
        } else {
                ResourceLocation textureLocation = FishingBobberItem.getTexture(hookEntity.getBobber());
                VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(textureLocation));
                this.model.renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY, 255, 255, 255 ,255);
        }
    }
}
*/
//?}