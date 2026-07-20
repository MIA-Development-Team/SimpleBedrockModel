package com.github.mcmodderanchor.simplebedrockmodel.v2.particle.world;

import com.github.mcmodderanchor.simplebedrockmodel.v2.particle.data.ParticleDescription;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Creates the render-state layer used by SnowStorm particles on 26.1. */
public final class MolangWorldParticleRenderType {
    private static final Map<String, SingleQuadParticle.Layer> CACHE = new ConcurrentHashMap<>();

    private MolangWorldParticleRenderType() {
    }

    public static SingleQuadParticle.Layer get(ParticleDescription.Material material, Identifier texture) {
        String key = material.name() + ':' + texture;
        return CACHE.computeIfAbsent(key, ignored -> new SingleQuadParticle.Layer(
                material != ParticleDescription.Material.PARTICLES_OPAQUE,
                texture,
                material == ParticleDescription.Material.PARTICLES_OPAQUE
                        ? RenderPipelines.OPAQUE_PARTICLE
                        : RenderPipelines.TRANSLUCENT_PARTICLE));
    }

    public static void clearCache() {
        CACHE.clear();
    }
}
