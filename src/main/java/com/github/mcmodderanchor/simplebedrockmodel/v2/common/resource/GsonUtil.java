package com.github.mcmodderanchor.simplebedrockmodel.v2.common.resource;

import com.github.mcmodderanchor.simplebedrockmodel.v2.common.resource.exclusion.NullAdapter;
import com.github.mcmodderanchor.simplebedrockmodel.v2.common.resource.exclusion.ServerExclusionStrategyForRootMotion;
import com.github.mcmodderanchor.simplebedrockmodel.v2.common.resource.exclusion.ServerNormalExclusionStrategy;
import com.github.mcmodderanchor.simplebedrockmodel.v2.common.resource.pojo.AnimationKeyframes;
import com.github.mcmodderanchor.simplebedrockmodel.v2.common.resource.pojo.BedrockModelPOJO;
import com.github.mcmodderanchor.simplebedrockmodel.v2.common.resource.pojo.CubesItem;
import com.github.mcmodderanchor.simplebedrockmodel.v2.common.resource.pojo.ParticleEffectKeyframes;
import com.github.mcmodderanchor.simplebedrockmodel.v2.common.resource.pojo.SoundEffectKeyframes;
import com.github.mcmodderanchor.simplebedrockmodel.v2.common.resource.pojo.TimelineKeyframes;
import com.github.mcmodderanchor.simplebedrockmodel.v2.common.resource.serialize.AnimationKeyframesSerializer;
import com.github.mcmodderanchor.simplebedrockmodel.v2.common.resource.serialize.ParticleEffectKeyframesSerializer;
import com.github.mcmodderanchor.simplebedrockmodel.v2.common.resource.serialize.SoundEffectKeyframesSerializer;
import com.github.mcmodderanchor.simplebedrockmodel.v2.common.resource.serialize.TimelineKeyframesSerializer;
import com.github.mcmodderanchor.simplebedrockmodel.v2.common.resource.serialize.Vector3fSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.minecraft.resources.Identifier;
import org.joml.Vector3f;

import java.lang.reflect.Type;

public class GsonUtil {
    private static final IdentifierAdapter IDENTIFIER_ADAPTER = new IdentifierAdapter();

    public static final Gson CLIENT_GSON = new GsonBuilder()
            .registerTypeAdapter(Identifier.class, IDENTIFIER_ADAPTER)
            .registerTypeAdapter(CubesItem.class, new CubesItem.Deserializer())
            .registerTypeAdapter(Vector3f.class, new Vector3fSerializer())
            .registerTypeAdapter(AnimationKeyframes.class, new AnimationKeyframesSerializer())
            .registerTypeAdapter(SoundEffectKeyframes.class, new SoundEffectKeyframesSerializer())
            .registerTypeAdapter(ParticleEffectKeyframes.class, new ParticleEffectKeyframesSerializer())
            .registerTypeAdapter(TimelineKeyframes.class, new TimelineKeyframesSerializer())
            .create();

    public static final Gson SERVER_NORMAL_GSON = new GsonBuilder()
            .addDeserializationExclusionStrategy(new ServerNormalExclusionStrategy())
            .registerTypeAdapter(BedrockModelPOJO.class, new NullAdapter<>())
            .registerTypeAdapter(Identifier.class, IDENTIFIER_ADAPTER)
            .registerTypeAdapter(SoundEffectKeyframes.class, new SoundEffectKeyframesSerializer())
            .registerTypeAdapter(ParticleEffectKeyframes.class, new ParticleEffectKeyframesSerializer())
            .registerTypeAdapter(TimelineKeyframes.class, new TimelineKeyframesSerializer())
            .create();

    public static final Gson SERVER_GSON_FOR_ROOT_MOTION = new GsonBuilder()
            .addDeserializationExclusionStrategy(new ServerExclusionStrategyForRootMotion())
            .registerTypeAdapter(Identifier.class, IDENTIFIER_ADAPTER)
            .registerTypeAdapter(Vector3f.class, new Vector3fSerializer())
            .registerTypeAdapter(AnimationKeyframes.class, new AnimationKeyframesSerializer())
            .registerTypeAdapter(SoundEffectKeyframes.class, new SoundEffectKeyframesSerializer())
            .registerTypeAdapter(ParticleEffectKeyframes.class, new ParticleEffectKeyframesSerializer())
            .registerTypeAdapter(TimelineKeyframes.class, new TimelineKeyframesSerializer())
            .create();

    private static final class IdentifierAdapter implements JsonSerializer<Identifier>, JsonDeserializer<Identifier> {
        @Override
        public JsonElement serialize(Identifier src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.toString());
        }

        @Override
        public Identifier deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return Identifier.parse(json.getAsString());
        }
    }
}
