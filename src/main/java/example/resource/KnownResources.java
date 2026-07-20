package example.resource;

import com.github.mcmodderanchor.simplebedrockmodel.SimpleBedrockModel;
import com.github.mcmodderanchor.simplebedrockmodel.v2.event.RegisterBedrockAnimationEvent;
import com.github.mcmodderanchor.simplebedrockmodel.v2.event.RegisterBedrockModelEvent;
import com.github.mcmodderanchor.simplebedrockmodel.v2.event.RegisterV2BedrockResourcesEvent;
import com.github.mcmodderanchor.simplebedrockmodel.v2.resource.RawResourceLoaders;
import example.init.ExampleModRegister;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

/** 所有开发环境渲染示例使用的模型、动画和纹理标识。 */
@EventBusSubscriber(modid = SimpleBedrockModel.MOD_ID)
public final class KnownResources {
    public static final Identifier DEAGLE = ExampleModRegister.modLoc("deagle");
    public static final Identifier TREE_DEAGLE = ExampleModRegister.modLoc("tree_deagle");
    public static final Identifier TEST = ExampleModRegister.modLoc("test");
    public static final Identifier ZTI_MODEL = ExampleModRegister.modLoc("zti.geo");
    public static final Identifier ZTI_ANIMATION = ExampleModRegister.modLoc("zti.animation");
    public static final Identifier DEFENDER = ExampleModRegister.modLoc("defender.geo");

    public static final Identifier DEAGLE_TEXTURE = ExampleModRegister.modLoc("textures/item/deagle.png");
    public static final Identifier TEST_TEXTURE = ExampleModRegister.modLoc("textures/block/test.png");
    public static final Identifier ZTI_TEXTURE = ExampleModRegister.modLoc("textures/entity/zti.png");
    public static final Identifier DEFENDER_TEXTURE = ExampleModRegister.modLoc("textures/armor/defender.png");

    private KnownResources() {
    }

    /** 旧对象模型路径，用于覆盖 AbstractGeoItemRenderer。 */
    @SubscribeEvent
    public static void registerLegacyModel(RegisterBedrockModelEvent event) {
        event.register(DEAGLE, RawResourceLoaders.COMMON_LOADER);
    }

    @SubscribeEvent
    public static void registerLegacyAnimation(RegisterBedrockAnimationEvent event) {
        event.register(DEAGLE, DEAGLE, RawResourceLoaders.COMMON_LOADER);
    }

    /** Tree/Baked 资源路径，用于其余渲染器和动画示例。 */
    @SubscribeEvent
    public static void registerV2Resources(RegisterV2BedrockResourcesEvent event) {
        event.treeModel(TREE_DEAGLE, DEAGLE).animation(DEAGLE).register();
        event.treeModel(TEST).animation(TEST).register();
        event.bakedModel(ZTI_MODEL).animation(ZTI_ANIMATION).register();
        event.treeModel(DEFENDER).register();
    }
}
