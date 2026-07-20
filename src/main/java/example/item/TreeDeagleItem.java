package example.item;

import com.github.mcmodderanchor.simplebedrockmodel.SimpleBedrockModel;
import com.github.mcmodderanchor.simplebedrockmodel.v2.client.renderer.IFPGeoItemRenderer;
import example.client.render.item.TreeDeagleRenderer;
import example.init.ExampleModRegister;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

/** 使用 AbstractGeoItemRendererV2 与 TreeModelInstance 的动画物品示例。 */
public final class TreeDeagleItem extends Item {
    public TreeDeagleItem(Properties properties) {
        super(properties);
    }

    @EventBusSubscriber(modid = SimpleBedrockModel.MOD_ID, value = Dist.CLIENT)
    public static final class ClientRegistration {
        private ClientRegistration() {
        }

        @SubscribeEvent
        public static void register(FMLClientSetupEvent event) {
            event.enqueueWork(() -> IFPGeoItemRenderer.register(
                    ExampleModRegister.TREE_DEAGLE_ITEM, new TreeDeagleRenderer()));
        }
    }
}
