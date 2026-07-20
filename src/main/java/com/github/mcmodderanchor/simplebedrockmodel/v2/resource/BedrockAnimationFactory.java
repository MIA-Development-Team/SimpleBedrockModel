package com.github.mcmodderanchor.simplebedrockmodel.v2.resource;

import com.github.mcmodderanchor.simplebedrockmodel.v2.common.BoneIndexProvider;
import com.github.mcmodderanchor.simplebedrockmodel.v2.common.animation.BedrockAnimation;
import com.github.mcmodderanchor.simplebedrockmodel.v2.common.resource.pojo.BedrockAnimationFile;

import java.util.List;

@FunctionalInterface
public interface BedrockAnimationFactory {
    List<BedrockAnimation> create(BedrockAnimationFile animationFile, BoneIndexProvider model);
}
