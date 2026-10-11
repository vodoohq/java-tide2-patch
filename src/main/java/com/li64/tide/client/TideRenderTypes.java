package com.li64.tide.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.texture.TextureAtlas;

//? if >=26.2 {
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import com.mojang.renderpearl.api.pipeline.BlendFunction;
import com.mojang.renderpearl.api.pipeline.ColorTargetState;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;

import net.minecraft.client.renderer.BindGroupLayouts;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
//?} else {
/*
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.Util;

import java.util.function.Function;
*/
//?}

public abstract class TideRenderTypes {
        //? if >=26.2 {
        private static final RenderPipeline FULL_WHITE_ITEM_PIPELINE = RenderPipeline.builder()
                .withLocation(Identifier.fromNamespaceAndPath("tide", "full_white_item"))
                .withBindGroupLayout(BindGroupLayouts.DYNAMIC_TRANSFORMS)
                .withBindGroupLayout(BindGroupLayouts.PROJECTION)
                .withVertexShader(Identifier.fromNamespaceAndPath("minecraft", "core/position_tex_color"))
                .withFragmentShader(TideCoreShaders.FULL_WHITE)
                .withBindGroupLayout(BindGroupLayouts.SAMPLER0)
                .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
                .withVertexBinding(0, DefaultVertexFormat.POSITION_TEX_COLOR)
                .withPrimitiveTopology(PrimitiveTopology.QUADS)
                .build();
                
        @SuppressWarnings("deprecation")
        private static final RenderType FULL_WHITE_ITEM_RENDER_TYPE = RenderType.create(
                "tide_full_white_item", 
                RenderSetup.builder(FULL_WHITE_ITEM_PIPELINE)
                        .withTexture("Sampler0", TextureAtlas.LOCATION_BLOCKS)
                        .createRenderSetup());

        public static RenderType singleColorItem() {
                return FULL_WHITE_ITEM_RENDER_TYPE;
        }
        //?} else {
        /*
        private static final Function<ResourceLocation, RenderType> FULL_WHITE_RENDER_TYPE;
        private static final Function<ResourceLocation, RenderType> FULL_WHITE_ITEM_RENDER_TYPE;
        private static final RenderStateShard.ShaderStateShard FULL_WHITE_SHADER;
        private static final RenderStateShard.ShaderStateShard FULL_WHITE_ITEM_SHADER; 

        static {
                FULL_WHITE_SHADER = new RenderStateShard.ShaderStateShard(TideCoreShaders::fullWhite);
                FULL_WHITE_ITEM_SHADER = new RenderStateShard.ShaderStateShard(TideCoreShaders::fullWhiteItem);
        
                FULL_WHITE_RENDER_TYPE = Util.memoize(tex -> RenderType.create(
                        "full_white",
                        DefaultVertexFormat.POSITION_TEX_COLOR,
                        VertexFormat.Mode.QUADS,
                        786432,
                        RenderType.CompositeState.builder()
                                .setTextureState(new RenderStateShard.TextureStateShard(tex, false, false))
                                .setShaderState(FULL_WHITE_SHADER)
                                .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                                .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
                                .createCompositeState(false))
                );
                FULL_WHITE_ITEM_RENDER_TYPE = Util.memoize(tex -> RenderType.create(
                        "full_white_item",
                        DefaultVertexFormat.NEW_ENTITY,
                        VertexFormat.Mode.QUADS,
                        786432,
                        RenderType.CompositeState.builder()
                                .setTextureState(new RenderStateShard.TextureStateShard(tex, false, false))
                                .setShaderState(FULL_WHITE_ITEM_SHADER)
                                .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                                .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
                                .createCompositeState(false))
                );
        }

        public static RenderType singleColor(ResourceLocation location) {
                return FULL_WHITE_RENDER_TYPE.apply(location);
        }

        @SuppressWarnings("deprecation")
        public static RenderType singleColorItem() {
                return FULL_WHITE_ITEM_RENDER_TYPE.apply(TextureAtlas.LOCATION_BLOCKS);
        }
        */
        //?}
}