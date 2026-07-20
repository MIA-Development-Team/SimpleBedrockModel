package com.github.mcmodderanchor.simplebedrockmodel.v2.client.renderer;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public final class BedrockModelRenderTypes {
    private static final RenderPipeline POLY_MESH_CUTOUT_PIPELINE = RenderPipeline.builder(RenderPipelines.ENTITY_SNIPPET)
            .withLocation("pipeline/simplebedrockmodel_poly_mesh_cutout")
            .withVertexFormat(DefaultVertexFormat.ENTITY, VertexFormat.Mode.TRIANGLES)
            .withShaderDefine("ALPHA_CUTOUT", 0.1F)
            .withShaderDefine("PER_FACE_LIGHTING")
            .withSampler("Sampler1")
            .withCull(false)
            .build();
    private static final Function<Identifier, RenderType> POLY_MESH_CUTOUT = Util.memoize(BedrockModelRenderTypes::createPolyMeshCutout);

    private BedrockModelRenderTypes() {
    }

    public static RenderType polyMeshCutout(Identifier texture) {
        return POLY_MESH_CUTOUT.apply(texture);
    }

    private static RenderType createPolyMeshCutout(Identifier texture) {
        RenderSetup setup = RenderSetup.builder(POLY_MESH_CUTOUT_PIPELINE)
                .withTexture("Sampler0", texture)
                .useLightmap()
                .useOverlay()
                .affectsCrumbling()
                .setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE)
                .createRenderSetup();
        return RenderType.create("simplebedrockmodel_poly_mesh_cutout", setup);
    }
}
