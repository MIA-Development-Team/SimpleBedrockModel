package com.github.mcmodderanchor.simplebedrockmodel.v2.common;

import com.maydaymemory.mae.basic.BaseKeyframe;
import net.minecraft.resources.Identifier;

public class ResourceLocationKeyframe extends BaseKeyframe<Identifier> {
    private final Identifier resourceLocation;

    public ResourceLocationKeyframe(float timeS, Identifier resourceLocation) {
        super(timeS);
        this.resourceLocation = resourceLocation;
    }

    @Override
    public Identifier getValue() {
        return resourceLocation;
    }
}
