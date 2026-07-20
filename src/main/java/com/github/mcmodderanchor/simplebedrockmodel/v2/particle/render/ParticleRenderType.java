package com.github.mcmodderanchor.simplebedrockmodel.v2.particle.render;

import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class ParticleRenderType {
    private ParticleRenderType() {
    }

    public static RenderType additiveParticle(Identifier texture) {
        // 26.1 owns blend state in the render pipeline. The standard translucent
        // entity pipeline is the safe immediate-mode replacement for this helper.
        return RenderTypes.entityTranslucent(texture);
    }
}
