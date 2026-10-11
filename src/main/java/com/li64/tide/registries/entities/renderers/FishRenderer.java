package com.li64.tide.registries.entities.renderers;

import com.li64.tide.Tide;
import com.li64.tide.registries.entities.models.FishModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

//? if >=26.2 {
import com.li64.tide.client.renderer.state.FishRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
//?} else {
/*
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
*/
//?}
// 
//? if >=26.2 {
public class FishRenderer<M extends FishModel> extends MobRenderer<Mob, FishRenderState, M> {
        private final Identifier texLocation;
        private final double xTiltScale;
        private final float swimAnimSpeed;
        private final float swimAnimScale;
        private final boolean renderTranslucent;
        
        public FishRenderer(String key, M model, EntityRendererProvider.Context context) {
                super(context, model, model.shadowRadius());
                this.texLocation = Tide.resource("textures/entity/fish/" + key + ".png");
                this.xTiltScale = model.xTiltScale();
                this.swimAnimSpeed = model.swimAnimSpeed();
                this.swimAnimScale = model.swimAnimScale();
                this.renderTranslucent = model.renderTranslucent();
        }

        @Override 
        public @NotNull FishRenderState createRenderState() {
                return  new FishRenderState();
        }

        @Override 
        public void extractRenderState(@NotNull Mob fish, @NotNull FishRenderState state, float partialTick) {
                super.extractRenderState(fish, state, partialTick);
                state.swimAnim = swimAnimScale * 4.3f * Mth.sin(swimAnimSpeed * (fish.tickCount + partialTick));
                state.deltaMovementY = (float) fish.getDeltaMovement().y();
                state.isInLava = fish.isInLava();
                state.xRotO = fish.xRotO;
                state.partialTick = partialTick;
        }
        
        @Override 
        public @NotNull Identifier getTextureLocation(@NotNull FishRenderState state) {
                return this.texLocation;
        }
        
        @Override
        protected void setupRotations(
                @NotNull FishRenderState state, 
                @NotNull PoseStack poseStack, 
                float bob, float yBodyRot
        ) {
                super.setupRotations(state, poseStack, bob, yBodyRot);
                poseStack.rotate(Axis.YP.rotationDegrees(state.swimAnim));
                if (!(state.isInWater || state.isInLava) && flipInAir()) {
                        poseStack.translate(0.1f, 0.1f, -0.1f);
                        poseStack.rotate(Axis.ZP.rotationDegrees(90f));
                } else {
                        // temp workaround for a bug with alex's caves
                        float tilt = Mth.clamp(state.deltaMovementY * 1.5f * (float) xTiltScale, -Mth.HALF_PI, Mth.HALF_PI);
                        if (Tide.PLATFORM.isModLoaded("alexscaves")) {
                                poseStack.rotate(Axis.XP.rotation(tilt));
                        } else {
                                poseStack.rotate(Axis.XP.rotation(Mth.lerp(state.partialTick, state.xRotO, tilt)));
                        }
                }
        }
        
        private boolean flipInAir() {
                return model.flipInAir();
        }
        
        @Override
        protected @Nullable RenderType getRenderType(@NotNull FishRenderState state, boolean isVisible, boolean renderTranslucent, boolean appearsGlowing) {
                if (this.renderTranslucent) return RenderTypes.entityTranslucent(this.getTextureLocation(state));
                return super.getRenderType(state, isVisible, renderTranslucent, appearsGlowing);
        }
        
        @Override
        protected int getBlockLightLevel(@NotNull Mob fish, @NotNull BlockPos pos) {
                return Math.max(super.getBlockLightLevel(fish, pos), model.brightnessOverride());
        }

        @Override 
        protected int getModelTint(@NotNull FishRenderState state) {
                return this.model.tint();
        }
}
//?} elif >= 1.21 {
/*
public class FishRenderer<M extends FishModel> extends MobRenderer<Mob, FishModel> {
        private final ResourceLocation texLocation;
        private final double xTiltScale;
        private final float swimAnimSpeed;
        private final float swimAnimScale;
        private final boolean renderTranslucent;
        
        public FishRenderer(String key, M model, EntityRendererProvider.Context context) {
                super(context, model, model.shadowRadius());
                this.texLocation = Tide.resource("textures/entity/fish/" + key + ".png");
                this.xTiltScale = model.xTiltScale();
                this.swimAnimSpeed = model.swimAnimSpeed();
                this.swimAnimScale = model.swimAnimScale();
                this.renderTranslucent = model.renderTranslucent();
        }

        public @NotNull ResourceLocation getTextureLocation() {
                return this.texLocation;
        }
        
        @Override
        public @NotNull ResourceLocation getTextureLocation(@NotNull Mob entity) {
                return this.getTextureLocation();
        }
        
        @Override
        protected void setupRotations(Mob fish, PoseStack poseStack, float bob, float yBodyRot, float partialTick, float scale) {
                super.setupRotations(fish, poseStack, bob, yBodyRot, partialTick, scale);
                float f = swimAnimScale * 4.3f * Mth.sin(swimAnimSpeed * (fish.tickCount + partialTick));
                poseStack.rotate(Axis.YP.rotationDegrees(f));
                if (!(fish.isInWaterOrBubble() || fish.isInLava()) && flipInAir()) {
                poseStack.translate(0.1f, 0.1f, -0.1f);
                poseStack.rotate(Axis.ZP.rotationDegrees(90f));
                }
                else {
                // temp workaround for a bug with alex's caves
                float tilt = Mth.clamp((float) (fish.getDeltaMovement().y() * 1.5 * xTiltScale), -Mth.HALF_PI, Mth.HALF_PI);
                if (Tide.PLATFORM.isModLoaded("alexscaves")) poseStack.rotate(Axis.XP.rotation(tilt));
                else {
                        fish.setXRot(tilt);
                        poseStack.rotate(Axis.XP.rotation(Mth.lerp(partialTick, fish.xRotO, fish.getXRot())));
                }
                }
        }
        
        private boolean flipInAir() {
                return model.flipInAir();
        }
        
        @Override
        protected @Nullable RenderType getRenderType(@NotNull Mob fish, boolean isVisible, boolean renderTranslucent, boolean appearsGlowing) {
                ResourceLocation texture = this.getTextureLocation(fish);
                if (this.renderTranslucent) return RenderType.entityTranslucent(texture);
                return super.getRenderType(fish, isVisible, renderTranslucent, appearsGlowing);
        }
        
        @Override
        protected int getBlockLightLevel(@NotNull Mob fish, @NotNull BlockPos pos) {
                return Math.max(super.getBlockLightLevel(fish, pos), model.brightnessOverride());
        }
}
*/
//?} else {
/*
public class FishRenderer<M extends FishModel> extends MobRenderer<Mob, FishModel> {
        private final ResourceLocation texLocation;
        private final double xTiltScale;
        private final float swimAnimSpeed;
        private final float swimAnimScale;
        private final boolean renderTranslucent;
        
        public FishRenderer(String key, M model, EntityRendererProvider.Context context) {
                super(context, model, model.shadowRadius());
                this.texLocation = Tide.resource("textures/entity/fish/" + key + ".png");
                this.xTiltScale = model.xTiltScale();
                this.swimAnimSpeed = model.swimAnimSpeed();
                this.swimAnimScale = model.swimAnimScale();
                this.renderTranslucent = model.renderTranslucent();
        }

        public @NotNull ResourceLocation getTextureLocation() {
                return this.texLocation;
        }
        
        @Override
        public @NotNull ResourceLocation getTextureLocation(@NotNull Mob entity) {
                return this.getTextureLocation();
        }
        
        @Override
        protected void setupRotations(Mob fish, PoseStack poseStack, float bob, float yBodyRot, float partialTick) {
                super.setupRotations(fish, poseStack, bob, yBodyRot, partialTick);
                float f = swimAnimScale * 4.3f * Mth.sin(swimAnimSpeed * (fish.tickCount + partialTick));
                poseStack.rotate(Axis.YP.rotationDegrees(f));
                if (!(fish.isInWaterOrBubble() || fish.isInLava()) && flipInAir()) {
                poseStack.translate(0.1f, 0.1f, -0.1f);
                poseStack.rotate(Axis.ZP.rotationDegrees(90f));
                }
                else {
                // temp workaround for a bug with alex's caves
                float tilt = Mth.clamp((float) (fish.getDeltaMovement().y() * 1.5 * xTiltScale), -Mth.HALF_PI, Mth.HALF_PI);
                if (Tide.PLATFORM.isModLoaded("alexscaves")) poseStack.rotate(Axis.XP.rotation(tilt));
                else {
                        fish.setXRot(tilt);
                        poseStack.rotate(Axis.XP.rotation(Mth.lerp(partialTick, fish.xRotO, fish.getXRot())));
                }
                }
        }
        
        private boolean flipInAir() {
                return model.flipInAir();
        }
        
        @Override
        protected @Nullable RenderType getRenderType(@NotNull Mob fish, boolean isVisible, boolean renderTranslucent, boolean appearsGlowing) {
                ResourceLocation texture = this.getTextureLocation(fish);
                if (this.renderTranslucent) return RenderType.entityTranslucent(texture);
                return super.getRenderType(fish, isVisible, renderTranslucent, appearsGlowing);
        }
        
        @Override
        protected int getBlockLightLevel(@NotNull Mob fish, @NotNull BlockPos pos) {
                return Math.max(super.getBlockLightLevel(fish, pos), model.brightnessOverride());
        }
}
*/
//?}
