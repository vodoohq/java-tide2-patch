package com.li64.tide.registries.entities.misc.fishing;

import com.li64.tide.Tide;
import com.li64.tide.registries.TideItems;
import com.li64.tide.registries.items.FishingLineItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Map;

//? if >=26.2 {
import com.li64.tide.client.renderer.state.TideFishingHookRenderState;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;

//?} else {
/*
import com.li64.tide.registries.items.FishingHookItem;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;

*/
//?}

//? if >=26.2 {
public class TideFishingHookRenderer extends EntityRenderer<TideFishingHook, TideFishingHookRenderState> implements RenderLayerParent<TideFishingHookRenderState, TideFishingHookModel<TideFishingHookRenderState>> {

        private final TideFishingHookModel<TideFishingHookRenderState> model;
        private final TideFishingBobberLayer bobberLayer;
        
        private static final Identifier HOOK_TEX_LOCATION = Tide.resource("textures/entity/fishing_hook/fishing_hook.png");
        private static final RenderType HOOK_RENDER_TYPE = RenderTypes.entityCutout(HOOK_TEX_LOCATION);
        private static final RenderType LINE_RENDER_TYPE = RenderTypes.lines();
//?} else {
/*
public class TideFishingHookRenderer extends EntityRenderer<TideFishingHook> implements RenderLayerParent<TideFishingHook, TideFishingHookModel<TideFishingHook>> {
        private final TideFishingHookModel<TideFishingHook> model;
        private final TideFishingBobberLayer bobberLayer;
        private static final ResourceLocation HOOK_TEX_LOCATION = Tide.resource("textures/entity/fishing_hook/fishing_hook.png");
*/
//?}
        
        public static final Map<Item, Vec2> OFFSETS = Map.of(
                TideItems.STONE_FISHING_ROD, new Vec2(0.05f, -0.1f),
                TideItems.HONEYCOMB_FISHING_ROD, new Vec2(0.05f, -0.1f),
                TideItems.IRON_FISHING_ROD, new Vec2(0.05f, -0.02f),
                TideItems.PRISMARINE_FISHING_ROD, new Vec2(0f, -0.05f),
                TideItems.BLAZING_FISHING_ROD, new Vec2(0f, -0.1f),
                TideItems.SUNFLOWER_FISHING_ROD, new Vec2(0.05f, -0.1f)
        );
        
        public TideFishingHookRenderer(EntityRendererProvider.Context context) {
                super(context);
                this.model = new TideFishingHookModel<>(context.bakeLayer(TideFishingHookModel.MODEL_LOCATION));
                //? if >=26.2 {
                this.bobberLayer = new TideFishingBobberLayer(this, context.getModelSet());
                //?} else {
                /*
                this.bobberLayer = new TideFishingBobberLayer(this, context.getModelSet(), context.getItemRenderer());
                */
                //?}
                this.shadowRadius = 0.1f;
        }

        //? if >=26.2 {
        @Override 
        public TideFishingHookRenderState createRenderState() {
                return new TideFishingHookRenderState();
        }

        @Override 
        public void extractRenderState(TideFishingHook hookEntity, TideFishingHookRenderState state, float partialTick) {
                super.extractRenderState(hookEntity, state, partialTick);
                state.partialTick = partialTick;
                state.initialYaw = hookEntity.getInitialYaw();
                state.player = hookEntity.getPlayerOwner();
                state.hook = hookEntity.getHook();
                state.line = hookEntity.getLine();
                state.bobber = hookEntity.getBobber();
                state.position = hookEntity.getPosition(partialTick);
                state.id = hookEntity.getId();
                state.allowModifiers = allowModifiers();
        }

        @Override 
        public void submit(TideFishingHookRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
                super.submit(state, poseStack, collector, camera);
                if (state.player == null) return;

                poseStack.pushPose();
                poseStack.pushPose();

                poseStack.translate((1f / 16f) / 2f, 0, (1f / 16f) / 2f);
                poseStack.rotate(Axis.YP.rotationDegrees(180f - state.initialYaw));

                poseStack.scale(-1.0F, -1.0F, 1.0F);
                
                model.setupAnim(state);
                collector.submitModel(model, state, poseStack, HOOK_RENDER_TYPE, state.lightCoords, OverlayTexture.NO_OVERLAY, ARGB.color(255, 255, 255, 255), null, 0);

                bobberLayer.submit(poseStack, collector, state.lightCoords, state, 0f, 0f);

                poseStack.popPose();

                submitConnectingString(state, poseStack, collector);

                poseStack.popPose();
        }

        private void submitConnectingString(TideFishingHookRenderState state, PoseStack poseStack, SubmitNodeCollector collector) {
                Player player = state.player;
                float partialTick = state.partialTick;
                float attackAnim = player.getSwingAnimation(partialTick);
                float attackAnimMod = Mth.sin(Mth.sqrt(attackAnim) * (float) Math.PI);
                Vec3 handPos = this.getPlayerHandPos(player, attackAnimMod, partialTick);
                Vec3 posDif = state.position.add(0.0, 0.25, 0.0);
                float posX = (float)(handPos.x - posDif.x);
                float posY = (float)(handPos.y - posDif.y);
                float posZ = (float)(handPos.z - posDif.z);

                collector.submitCustomGeometry(poseStack, LINE_RENDER_TYPE, (pose, vertexConsumer) -> {
                        for (int k = 0; k < 16; k++) {
                                stringVertex(posX, posY, posZ, vertexConsumer, pose, fraction(k), fraction(k + 1), player, FishingLineItem.getColor(state.line), partialTick);
                                stringVertex(posX, posY, posZ, vertexConsumer, pose, fraction(k + 1), fraction(k), player, FishingLineItem.getColor(state.line), partialTick);
                        }

                });
        }
        //?} elif >= 1.21 {
        /*
        @Override
        public void render(TideFishingHook hookEntity, float entityYaw, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {
                Player player = hookEntity.getPlayerOwner();
                if (player == null) return;
        
                poseStack.pushPose();
                poseStack.pushPose();
        
                poseStack.translate((1f / 16f) / 2f, 0, (1f / 16f) / 2f);
                poseStack.rotate(Axis.YP.rotationDegrees(180f - hookEntity.getInitialYaw()));
        
                poseStack.scale(-1.0F, -1.0F, 1.0F);
        
                model.setupAnim(hookEntity, partialTick, 0.0F, -0.1F, 0.0F, 0.0F);
                VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(getTextureLocation(hookEntity)));
        
                model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, FastColor.ARGB32.color(255, 255, 255, 255));
                bobberLayer.render(poseStack, buffer, packedLight, hookEntity, 0, 0, 0, 0, 0, 0);
        
                poseStack.popPose();
        
                renderConnectingString(hookEntity, partialTick, poseStack, buffer, player);
        
                poseStack.popPose();
        
                super.render(hookEntity, entityYaw, partialTick, poseStack, buffer, packedLight);
        }
        
        private void renderConnectingString(TideFishingHook hookEntity, float partialTick, PoseStack poseStack, MultiBufferSource buffer, Player player) {
                float f = player.getSwingAnimation(partialTick);
                float f1 = Mth.sin(Mth.sqrt(f) * (float) Math.PI);
                Vec3 vec3 = this.getPlayerHandPos(player, f1, partialTick);
                Vec3 vec31 = hookEntity.getPosition(partialTick).add(0.0, 0.25, 0.0);
                float f2 = (float)(vec3.x - vec31.x);
                float f3 = (float)(vec3.y - vec31.y);
                float f4 = (float)(vec3.z - vec31.z);
                VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.lineStrip());
                PoseStack.Pose pose = poseStack.last();
        
                for (int k = 0; k <= 16; k++) {
                        stringVertex(f2, f3, f4, vertexConsumer, pose, fraction(k),
                                fraction(k + 1), player, FishingLineItem.getColor(hookEntity.getLine()), partialTick);
                }
        
                // Iris's hacky fix for a bug where fishing lines connect
                // https://github.com/IrisShaders/Iris/blob/multiloader-new/common/src/main/java/net/irisshaders/batchedentityrendering/mixin/MixinFishingHookRenderer.java
                if (Tide.PLATFORM.isModLoaded("iris") || Tide.PLATFORM.isModLoaded("oculus")) {
                        vertexConsumer.addVertex(0, 0, 0)
                                .setColor(0, 0, 0, 255)
                                .setNormal(0, 0, 0);
                }
        }
        */
        //?} else {
        /*
        @Override
        public void render(TideFishingHook hookEntity, float entityYaw, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {
                Player player = hookEntity.getPlayerOwner();
                if (player == null) return;
        
                poseStack.pushPose();
                poseStack.pushPose();
        
                poseStack.translate((1f / 16f) / 2f, 0, (1f / 16f) / 2f);
                poseStack.rotate(Axis.YP.rotationDegrees(180f - hookEntity.getInitialYaw()));
        
                poseStack.scale(-1.0F, -1.0F, 1.0F);
        
                model.setupAnim(hookEntity, partialTick, 0.0F, -0.1F, 0.0F, 0.0F);
                VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(getTextureLocation(hookEntity)));
        
                model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);

                bobberLayer.render(poseStack, buffer, packedLight, hookEntity, 0, 0, 0, 0, 0, 0);
        
                poseStack.popPose();
        
                renderConnectingString(hookEntity, partialTick, poseStack, buffer, player);
        
                poseStack.popPose();
        
                super.render(hookEntity, entityYaw, partialTick, poseStack, buffer, packedLight);
        }
        
        private void renderConnectingString(TideFishingHook hookEntity, float partialTick, PoseStack poseStack, MultiBufferSource buffer, Player player) {
                float f = player.getSwingAnimation(partialTick);
                float f1 = Mth.sin(Mth.sqrt(f) * (float) Math.PI);
                Vec3 vec3 = this.getPlayerHandPos(player, f1, partialTick);
                Vec3 vec31 = hookEntity.getPosition(partialTick).add(0.0, 0.25, 0.0);
                float f2 = (float)(vec3.x - vec31.x);
                float f3 = (float)(vec3.y - vec31.y);
                float f4 = (float)(vec3.z - vec31.z);
                VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.lineStrip());
                PoseStack.Pose pose = poseStack.last();
        
                for (int k = 0; k <= 16; k++) {
                        stringVertex(f2, f3, f4, vertexConsumer, pose, fraction(k),
                                fraction(k + 1), player, FishingLineItem.getColor(hookEntity.getLine()), partialTick);
                }
        
                // Iris's hacky fix for a bug where fishing lines connect
                // https://github.com/IrisShaders/Iris/blob/multiloader-new/common/src/main/java/net/irisshaders/batchedentityrendering/mixin/MixinFishingHookRenderer.java
                if (Tide.PLATFORM.isModLoaded("iris") || Tide.PLATFORM.isModLoaded("oculus")) {
                        vertexConsumer.vertex(0, 0, 0)
                                .color(0, 0, 0, 255)
                                .normal(0, 0, 0);
                }
        }
        */
        //?}
        
        private Vec3 getPlayerHandPos(Player player, float anim, float partialTick) {
                int i = player.getMainArm() == HumanoidArm.RIGHT ? 1 : -1;
                ItemStack stack = player.getMainHandItem();
                if (!(stack.getItem() instanceof FishingRodItem)) i = -i;
                Vec2 stringOffset = OFFSETS.getOrDefault(stack.getItem(), new Vec2(0f, 0f));
        
                if (!Tide.PLATFORM.isModLoaded("firstperson") && this.entityRenderDispatcher.options.getCameraType().isFirstPerson()&& player == Minecraft.getInstance().player) {
                        double fovOption = this.entityRenderDispatcher.options.fov().get().doubleValue();
                        double d4 = 960.0 / (fovOption);
                        double fovScalar = /*? if >=26.2 {*/
                                        (this.entityRenderDispatcher.camera.getFov() / fovOption - 1.0) * 2.5 + 1.0
                                        /*?} else {*//*(Minecraft.getInstance().gameRenderer.getFov(this.entityRenderDispatcher.camera, partialTick, true) / fovOption - 1.0) * 2.5 + 1.0*//*?}*/;
                        Vec3 vec3 = this.entityRenderDispatcher.camera
                                .getNearPlane(/*? if >=26.2 {*/partialTick/*?}*/)
                                .getPointOnPlane(
                                        i * (0.525F + stringOffset.x) * (float) fovScalar,
                                        (-0.1F + stringOffset.y) * (float) fovScalar)
                                .scale(d4)
                                .yRot(anim * 0.5F)
                                .xRot(-anim * 0.7F);
                        return player.getEyePosition(partialTick).add(vec3);
                } else {
                        float f = Mth.lerp(partialTick, player.yBodyRotO, player.yBodyRot) * (float) (Math.PI / 180.0);
                        double d0 = Mth.sin(f);
                        double d1 = Mth.cos(f);
                        float f1 = player.getScale();
                        double d2 = (double)i * 0.35 * (double)f1;
                        double d3 = 0.8 * (double)f1;
                        float f2 = player.isCrouching() ? -0.1875F : 0.0F;
                        return player.getEyePosition(partialTick).add(-d1 * d2 - d0 * d3, (double)f2 - 0.45 * (double)f1, -d0 * d2 + d1 * d3);
                }
        }
        
        private static float fraction(int pNumerator) {
                return (float)pNumerator / (float) 16;
        }
        
        private static void stringVertex(float x, float y, float z, VertexConsumer vertexConsumer, PoseStack.Pose pose, float frac1, float frac2, Player player, String colorHex, float partialTick) {
                float f = x * frac1;
                float f1 = y * (frac1 * frac1 + frac1) * 0.5F + 0.25F;
                float f2 = z * frac1;
                float f3 = x * frac2 - f;
                float f4 = y * (frac2 * frac2 + frac2) * 0.5F + 0.25F - f1;
                float f5 = z * frac2 - f2;
                float f6 = Mth.sqrt(f3 * f3 + f4 * f4 + f5 * f5);
        
                f3 /= f6;
                f4 /= f6;
                f5 /= f6;
        
                Color color = Color.decode(colorHex);
                BlockPos samplePos = player.blockPosition().above();
        
                float skyDarken = (1 - ((ClientLevel) player.level()).getSkyDarken(/*? if <26.2 {*//*partialTick*//*?}*/)) * 15;
                float blockBrightness = player.level().getBrightness(LightLayer.BLOCK, samplePos);
                float skyBrightness = player.level().getBrightness(LightLayer.SKY, samplePos) - skyDarken + 1;
        
                float colorBrightness = Tide.CLIENT_CONFIG.general.defaultLineColor ? 0.0f : Mth.clamp(
                        Math.max(blockBrightness, skyBrightness) / 15f,
                        player.level().dimensionType().ambientLight(), 1f);
        
                int r = (int) (color.getRed() * colorBrightness);
                int g = (int) (color.getGreen() * colorBrightness);
                int b = (int) (color.getBlue() * colorBrightness);
                //? if >= 26.2 {
                vertexConsumer.addVertex(pose.pose(), f, f1, f2)
                        .setColor(ARGB.color(255, r, g, b))
                        .setNormal(pose, f3, f4, f5)
                        .setLineWidth(3.0F);
                //?} elif >=1.21 {
                /*
                vertexConsumer.addVertex(pose.pose(), f, f1, f2)
                        .setColor(FastColor.ARGB32.color(255, r, g, b))
                        .setNormal(pose, f3, f4, f5);
                */
                //?} else {
                /*
                vertexConsumer.vertex(pose.pose(), f, f1, f2)
                        .color(FastColor.ARGB32.color(255, r, g, b))
                        .normal(pose.normal(), f3, f4, f5)
                        .endVertex();
                */
                //?}
        }

        //? if >=26.2 {
        @Override
        public @NotNull TideFishingHookModel<TideFishingHookRenderState> getModel() {
                return this.model;
        }
        //?} else {
        /*
        @Override
        public @NotNull TideFishingHookModel<TideFishingHook> getModel() {
                return this.model;
        }
        
        @Override
        public @NotNull ResourceLocation getTextureLocation(@NotNull TideFishingHook hookEntity) {
                if (!allowModifiers()) return HOOK_TEX_LOCATION;
                return FishingHookItem.getTexture(hookEntity.getHook());
        }
        */
        //?}
        
        /** Override this to disable bobber, hook, and line modifiers */
        protected boolean allowModifiers() { return true; }
}

