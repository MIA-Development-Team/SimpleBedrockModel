package example.item;

import com.github.mcmodderanchor.simplebedrockmodel.SimpleBedrockModel;
import com.github.mcmodderanchor.simplebedrockmodel.v2.client.renderer.GeoArmorRendererV2;
import com.github.mcmodderanchor.simplebedrockmodel.v2.common.model.tree.TreeBedrockModel;
import com.github.mcmodderanchor.simplebedrockmodel.v2.resource.BedrockModelResources;
import example.init.ExampleModRegister;
import example.resource.KnownResources;
import net.minecraft.client.model.Model;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.ArmorType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

import java.util.IdentityHashMap;
import java.util.Map;

/** 26.1 数据组件盔甲物品及 Tree 盔甲渲染示例。 */
public final class ExampleArmorItem extends Item {
    private final ArmorType armorType;

    public ExampleArmorItem(ArmorType armorType, Properties properties) {
        super(properties);
        this.armorType = armorType;
    }

    public ArmorType armorType() {
        return armorType;
    }

    @EventBusSubscriber(modid = SimpleBedrockModel.MOD_ID, value = Dist.CLIENT)
    public static final class ClientRegistration {
        private static final Map<Item, GeoArmorRendererV2> RENDERERS = new IdentityHashMap<>();

        private ClientRegistration() {
        }

        @SubscribeEvent
        public static void register(RegisterClientExtensionsEvent event) {
            for (ExampleArmorItem item : new ExampleArmorItem[]{
                    ExampleModRegister.DEFENDER_ARMOR_HELMET,
                    ExampleModRegister.DEFENDER_ARMOR_CHESTPLATE,
                    ExampleModRegister.DEFENDER_ARMOR_LEGGINGS,
                    ExampleModRegister.DEFENDER_ARMOR_BOOTS
            }) {
                event.registerItem(new IClientItemExtensions() {
                    @Override
                    public Model getHumanoidArmorModel(ItemStack stack, EquipmentClientInfo.LayerType layerType, Model original) {
                        TreeBedrockModel model = BedrockModelResources.getInstance().getTreeModel(KnownResources.DEFENDER);
                        if (model == null) {
                            return original;
                        }
                        return RENDERERS.computeIfAbsent(item, ignored -> new GeoArmorRendererV2(
                                model, item.armorType().getSlot(), KnownResources.DEFENDER_TEXTURE));
                    }

                    @Override
                    public Identifier getArmorTexture(ItemStack stack, EquipmentClientInfo.LayerType type,
                                                      EquipmentClientInfo.Layer layer, Identifier defaultTexture) {
                        return KnownResources.DEFENDER_TEXTURE;
                    }
                }, item);
            }
        }
    }
}
