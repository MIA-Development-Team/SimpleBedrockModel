package example.resource;

import com.github.mcmodderanchor.simplebedrockmodel.v2.common.animation.BedrockAnimation;
import com.github.mcmodderanchor.simplebedrockmodel.v2.event.RegisterBedrockAnimationEvent;
import com.github.mcmodderanchor.simplebedrockmodel.v2.event.RegisterBedrockModelEvent;
import com.github.mcmodderanchor.simplebedrockmodel.v2.molang.MochaEngine;
import com.github.mcmodderanchor.simplebedrockmodel.v2.resource.RawResourceLoaders;
import com.github.mcmodderanchor.simplebedrockmodel.v2.event.RegisterV2BedrockResourcesEvent;
import example.animation.MolangTestAnimationContext;
import example.init.ExampleModRegister;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.ArrayList;

@EventBusSubscriber
public class KnownResources {
    public static final ArrayList<Identifier> ANIMATION_AND_MODEL = new ArrayList<>();
    public static final ArrayList<Identifier> MODEL = new ArrayList<>();

    public static final Identifier TEST = Identifier.fromNamespaceAndPath(ExampleModRegister.MOD_ID, "test");
    public static final Identifier DEAGLE = registerAnimationAndModel(Identifier.fromNamespaceAndPath(ExampleModRegister.MOD_ID, "deagle"));
    public static final Identifier POLY_MESH_TEST = registerModel(Identifier.fromNamespaceAndPath(ExampleModRegister.MOD_ID, "vct.geo"));
    public static final Identifier ZTI_MODEL = Identifier.fromNamespaceAndPath(ExampleModRegister.MOD_ID, "zti.geo");
    public static final Identifier ZTI_ANIMATION = Identifier.fromNamespaceAndPath(ExampleModRegister.MOD_ID, "zti.animation");
    // Molang 测试动画，复用 TEST 的模型
    public static final Identifier MOLANG_TEST = Identifier.fromNamespaceAndPath(ExampleModRegister.MOD_ID, "molang_test");

    private static Identifier registerAnimationAndModel(Identifier location) {
        ANIMATION_AND_MODEL.add(location);
        return location;
    }

    private static Identifier registerModel(Identifier location) {
        MODEL.add(location);
        return location;
    }

    @SubscribeEvent
    public static void onAnimationRegister(RegisterBedrockAnimationEvent event) {
        for (Identifier resourceLocation : ANIMATION_AND_MODEL) {
            event.register(resourceLocation, resourceLocation, RawResourceLoaders.COMMON_LOADER);
        }
        // Molang 测试动画：使用自定义 converter 传入 MochaEngine
        MochaEngine<?> engine = MolangTestAnimationContext.getSharedEngine();
        event.register(MOLANG_TEST, TEST, RawResourceLoaders.COMMON_LOADER,
                (file, model) -> BedrockAnimation.createAnimation(file, model, engine));
    }

    @SubscribeEvent
    public static void onModelRegister(RegisterBedrockModelEvent event) {
        for (Identifier resourceLocation : MODEL) {
            event.register(resourceLocation, RawResourceLoaders.COMMON_LOADER);
        }
        for (Identifier resourceLocation : ANIMATION_AND_MODEL) {
            event.register(resourceLocation, RawResourceLoaders.COMMON_LOADER);
        }
    }

    @SubscribeEvent
    public static void onV2ResourceRegister(RegisterV2BedrockResourcesEvent event) {
        event.treeModel(TEST)
                .animation(TEST)
                .animation(MOLANG_TEST, (file, model) -> BedrockAnimation.createAnimation(file, model, MolangTestAnimationContext.getSharedEngine()))
                .register();
        event.treeModel(POLY_MESH_TEST).register();
        event.bakedModel(ZTI_MODEL)
                .animation(ZTI_ANIMATION)
                .register();
        event.treeModel(InnerResourceLoader.DEFENDER).register();
        for (Identifier resourceLocation : ANIMATION_AND_MODEL) {
            event.bakedModel(resourceLocation)
                    .animation(resourceLocation)
                    .register();
        }
    }
}
