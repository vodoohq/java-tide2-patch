package com.li64.tide.registries.entities.misc;

import com.li64.tide.Tide;
import com.li64.tide.registries.entities.misc.fishing.StarArrowModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;

//? if >=26.2 {
import com.li64.tide.client.renderer.state.StarArrowRenderState;

import net.minecraft.util.LightCoordsUtil;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.SubmitNodeCollector;
//?} elif >= 1.21 {
/*
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.jetbrains.annotations.NotNull;
*/
//?} else {
/*
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.jetbrains.annotations.NotNull;
*/
//?}

//? if >=26.2 {
public class StarArrowRenderer extends EntityRenderer<StarArrow, StarArrowRenderState> {
//?} else {
/*
public class StarArrowRenderer extends EntityRenderer<StarArrow> {
*/
//?}
        //? if >=26.2 {
        public static final Identifier STAR_ARROW_LOCATION = Tide.resource("textures/entity/projectiles/star_arrow.png");
        private static final RenderType RENDER_TYPE = RenderTypes.entityCutout(STAR_ARROW_LOCATION);
        //?} else {
        /*
        public static final ResourceLocation STAR_ARROW_LOCATION = Tide.resource("textures/entity/projectiles/star_arrow.png");
        */
        //?}
        
        private final StarArrowModel model;
        
        public StarArrowRenderer(EntityRendererProvider.Context context) {
                super(context);
                this.model = new StarArrowModel(context.bakeLayer(StarArrowModel.MODEL_LOCATION));
        }


        //? if >=26.2 {
        @Override 
        public StarArrowRenderState createRenderState() {
                return  new StarArrowRenderState();
        }

        @Override
        public void extractRenderState(StarArrow arrow, StarArrowRenderState state, float partialTick) {
                super.extractRenderState(arrow, state, partialTick);
                state.partialTick = partialTick;
                state.xRotO = arrow.xRotO;
                state.yRotO = arrow.yRotO;
                state.xRot = arrow.getXRot();
                state.yRot = arrow.getYRot();
        }
        
        @Override
        public void submit(StarArrowRenderState state, PoseStack poseStack, 
                        SubmitNodeCollector collector, CameraRenderState camera) {
                super.submit(state, poseStack, collector, camera);
                poseStack.pushPose();
                poseStack.translate(0, 0.25, 0);
                poseStack.rotate(Axis.YP.rotationDegrees(Mth.lerp(state.partialTick, state.yRotO, state.yRot) - 90f));
                poseStack.rotate(Axis.ZP.rotationDegrees(Mth.lerp(state.partialTick, state.xRotO, state.xRot)));
                poseStack.scale(1, -1, 1);
                poseStack.translate(0, -1.28, 0);
                collector.submitModel(model, state, poseStack, RENDER_TYPE, 
                        LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 
                        ARGB.color(255, 255, 255, 255), null, 0);
                poseStack.popPose();
        }
        //?} elif >= 1.21 {
        /*
        public @NotNull ResourceLocation getTextureLocation(StarArrow arrow) {
                return STAR_ARROW_LOCATION;
        }
        
        @Override
        public void render(StarArrow entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
                super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
                poseStack.pushPose();
                poseStack.translate(0, 0.25, 0);
                poseStack.rotate(Axis.YP.rotationDegrees(Mth.lerp(partialTick, entity.yRotO, entity.getYRot()) - 90f));
                poseStack.rotate(Axis.ZP.rotationDegrees(Mth.lerp(partialTick, entity.xRotO, entity.getXRot())));
                poseStack.scale(1, -1, 1);
                poseStack.translate(0, -1.28, 0);
                VertexConsumer buffer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(getTextureLocation(entity)));
                model.renderToBuffer(poseStack, buffer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, FastColor.ARGB32.color(255, 255, 255, 255));
                poseStack.popPose();
        }
        */
        //?} else {
        /*
        public @NotNull ResourceLocation getTextureLocation(StarArrow arrow) {
                return STAR_ARROW_LOCATION;
        }
        
        @Override
        public void render(StarArrow entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
                super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
                poseStack.pushPose();
                poseStack.translate(0, 0.25, 0);
                poseStack.rotate(Axis.YP.rotationDegrees(Mth.lerp(partialTick, entity.yRotO, entity.getYRot()) - 90f));
                poseStack.rotate(Axis.ZP.rotationDegrees(Mth.lerp(partialTick, entity.xRotO, entity.getXRot())));
                poseStack.scale(1, -1, 1);
                poseStack.translate(0, -1.28, 0);
                VertexConsumer buffer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(getTextureLocation(entity)));
                model.renderToBuffer(poseStack, buffer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
                poseStack.popPose();
        }
        */
        //?}
        
        @Override
        protected int getBlockLightLevel(StarArrow entity, BlockPos pos) {
                return 15;
        }
}