package com.li64.tide.client;

import com.li64.tide.compat.CompatHelper;
import com.li64.tide.data.fishing.DisplayData;
import com.li64.tide.registries.blocks.FishDisplayBlock;
import com.li64.tide.registries.blocks.entities.FishDisplayBlockEntity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import org.jetbrains.annotations.NotNull;

//? if >=26.2 {
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.entity.EntityProcessor;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.phys.Vec3;

//?} else {
/*
import net.minecraft.client.renderer.MultiBufferSource;
*/
//?}

//? if >=26.2 {
public record FishDisplayRenderer(
        EntityRenderDispatcher entityRenderer
) implements
        BlockEntityRenderer<
                FishDisplayBlockEntity,
                FishDisplayRenderer.FishDisplayRenderState
        >
{
        public FishDisplayRenderer(
                BlockEntityRendererProvider.Context context
        ) {
                this(context.entityRenderer());
        }

        @Override
        public FishDisplayRenderState createRenderState() {
                return new FishDisplayRenderState();
        }

        @Override
        public void extractRenderState(
                @NotNull FishDisplayBlockEntity display,
                @NotNull FishDisplayRenderState state,
                float partialTick,
                @NotNull Vec3 cameraPos,
                ModelFeatureRenderer.CrumblingOverlay crumblingOverlay
        ) {
                BlockEntityRenderer.super.extractRenderState(
                        display,
                        state,
                        partialTick,
                        cameraPos,
                        crumblingOverlay
                );
                if (display.isEmpty()) return;

                DisplayData displayData = display.getDisplayData();
                state.displayData = displayData;
                state.facing = display
                        .getBlockState()
                        .getValue(FishDisplayBlock.FACING);

                EntityType<?> entityType = displayData.entityType();
                if (entityType == null) return;

                Entity entity = display.getRenderedEntity();
                if (
                        entity == null ||
                        (entity.getType() != entityType &&
                                Minecraft.getInstance().level != null)
                ) {
                        if (displayData.nbt().isPresent()) {
                                entity = EntityType.loadEntityRecursive(
                                        entityType,
                                        displayData.nbt().get(),
                                        Minecraft.getInstance().level,
                                        EntitySpawnReason.LOAD,
                                        EntityProcessor.NOP
                                );
                        } else {
                                entity = entityType.create(
                                        Minecraft.getInstance().level,
                                        EntitySpawnReason.LOAD
                                );
                        }
                        if (entity != null) {
                                //?if <26.2 {
                                /*
                                if (
                                        CompatHelper.isHybridAquaticLoaded()
                                ) CompatHelper.hybridAquaticApplyVariant(
                                        entity,
                                        display.getDisplayStack()
                                );
                                */
                                //?}
                                entity.xRotO = entity.getXRot();
                                entity.yRotO = entity.getYRot();
                        }
                        display.setRenderedEntity(entity);
                }

                if (entity != null) {
                        state.entityRenderState = entityRenderer.extractEntity(
                                entity,
                                partialTick
                        );
                        double lengthCm = display.getFishLength();
                        double baseLengthCm = display.getBaseLength();
                        state.scale = Mth.sqrt(
                                (float) (lengthCm / baseLengthCm)
                        );
                }
        }

        @Override
        public void submit(
                @NotNull FishDisplayRenderState state,
                @NotNull PoseStack poseStack,
                @NotNull SubmitNodeCollector collector,
                @NotNull CameraRenderState cameraState
        ) {
                if (state.displayData == null) return;

                poseStack.pushPose();

                poseStack.translate(0.5f, 0.5f, 0.5f);
                poseStack.rotate(
                        Axis.YP.rotationDegrees(-state.facing.toYRot() + 90)
                );
                poseStack.translate(0.5f, 0.0f, 0.0f);
                poseStack.pushPose();

                poseStack.translate(
                        -state.displayData.z(),
                        state.displayData.y(),
                        state.displayData.x()
                );

                poseStack.rotate(
                        Axis.ZP.rotationDegrees(state.displayData.roll() + 90f)
                );
                poseStack.rotate(
                        Axis.XP.rotationDegrees(state.displayData.pitch())
                );
                poseStack.rotate(
                        Axis.YP.rotationDegrees(state.displayData.yaw())
                );

                if (state.entityRenderState != null) {
                        poseStack.pushPose();
                        poseStack.scale(state.scale, state.scale, state.scale);
                        entityRenderer.submit(
                                state.entityRenderState,
                                cameraState,
                                0,
                                0,
                                0,
                                poseStack,
                                collector
                        );
                        poseStack.popPose();
                }

                poseStack.popPose();
                poseStack.popPose();
        }

        public static class FishDisplayRenderState
                extends BlockEntityRenderState
        {

                public DisplayData displayData;
                public Direction facing;
                public EntityRenderState entityRenderState;
                public float scale;
        }
}
//?} else {
/*
public record FishDisplayRenderer(EntityRenderDispatcher entityRenderer) implements BlockEntityRenderer<FishDisplayBlockEntity> {
    public FishDisplayRenderer(BlockEntityRendererProvider.Context entityRenderer) {
        this(entityRenderer.getEntityRenderer());
    }

    @Override
    public void render(@NotNull FishDisplayBlockEntity display, float partialTick, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {
        if (display.isEmpty()) return;
        DisplayData displayData = display.getDisplayData();
        poseStack.pushPose();

        Direction dir = display.getBlockState().getValue(FishDisplayBlock.FACING);
        poseStack.translate(0.5f, 0.5f, 0.5f); // center rotation
        poseStack.rotate(Axis.YP.rotationDegrees(-dir.toYRot() + 90)); // rotate based on facing direction
        poseStack.translate(0.5f, 0.0f, 0.0f); // move towards display
        poseStack.pushPose();

        // apply translations
        poseStack.translate(-displayData.z(), displayData.y(), displayData.x());
        // apply rotations
        poseStack.rotate(Axis.ZP.rotationDegrees(displayData.roll() + 90f));
        poseStack.rotate(Axis.XP.rotationDegrees(displayData.pitch()));
        poseStack.rotate(Axis.YP.rotationDegrees(displayData.yaw()));

        EntityType<?> entityType = displayData.entityType();
        if (entityType == null) {
            poseStack.popPose();
            poseStack.popPose();
            return;
        }
        Entity entity = display.getRenderedEntity();
        if (entity == null || entity.getType() != entityType
                && Minecraft.getInstance().level != null) {
            entity = entityType.create(Minecraft.getInstance().level);
            if (entity != null) {
                if (displayData.nbt().isPresent()) entity.load(displayData.nbt().get());
                if (CompatHelper.isHybridAquaticLoaded()) CompatHelper.hybridAquaticApplyVariant(entity, display.getDisplayStack());
                entity.xRotO = entity.getXRot();
                entity.yRotO = entity.getYRot();
            }
            display.setRenderedEntity(entity);
        }

        if (entity != null) {
            poseStack.pushPose();

            double lengthCm = display.getFishLength();
            double baseLengthCm = display.getBaseLength();

            float scale = Mth.sqrt((float)(lengthCm / baseLengthCm));
            poseStack.scale(scale, scale, scale);

            this.entityRenderer.render(entity,
                    0, 0, 0,
                    0, 0,
                    poseStack, buffer,
                    packedLight
            );
            poseStack.popPose();
        }

        poseStack.popPose();
        poseStack.popPose();
    }
}
*/
//?}
