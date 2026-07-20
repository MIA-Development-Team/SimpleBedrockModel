package com.github.mcmodderanchor.simplebedrockmodel.v2.event;

import com.github.mcmodderanchor.simplebedrockmodel.v2.common.animation.BedrockAnimation;
import com.github.mcmodderanchor.simplebedrockmodel.v2.common.model.BedrockModel;
import com.github.mcmodderanchor.simplebedrockmodel.v2.common.resource.pojo.BedrockAnimationFile;
import com.github.mcmodderanchor.simplebedrockmodel.v2.resource.BedrockAnimationResourceProcessor;
import com.github.mcmodderanchor.simplebedrockmodel.v2.resource.RawResourceLoader;
import com.google.common.collect.Maps;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Used to register bedrock animations so that loaders can load them.
 */
public class RegisterBedrockAnimationEvent extends Event implements IModBusEvent {
    private final Map<Identifier, BedrockAnimationResourceProcessor> animationRegistry;
    private final Dist dist;

    public RegisterBedrockAnimationEvent(Dist dist) {
        this.animationRegistry = Maps.newHashMap();
        this.dist = dist;
    }

    public void register(Identifier animationLocation,
                         Identifier modelLocation,
                         RawResourceLoader loader,
                         BiFunction<BedrockAnimationFile, BedrockModel, List<BedrockAnimation>> converter) {
        animationRegistry.put(animationLocation, new BedrockAnimationResourceProcessor(loader, modelLocation, converter));
    }

    public void register(Identifier animationLocation,
                         Identifier modelLocation,
                         RawResourceLoader loader) {
        register(animationLocation, modelLocation, loader, BedrockAnimation::createAnimation);
    }


    public Dist getDist() {
        return dist;
    }

    public Map<Identifier, BedrockAnimationResourceProcessor> getAnimationRegistry() {
        return animationRegistry;
    }
}
