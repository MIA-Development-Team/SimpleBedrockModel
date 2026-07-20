package example.init;

import com.github.mcmodderanchor.simplebedrockmodel.SimpleBedrockModel;
import example.block.TestBlock;
import example.block.blockentity.TestBlockEntity;
import example.entity.Zti;
import example.item.DeagleItem;
import example.item.ExampleArmorItem;
import example.item.TreeDeagleItem;
import example.item.ZtiSpawnerItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.ArmorMaterials;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

/** 仅用于开发环境的完整渲染测试注册表；发布 JAR 会排除 example。 */
@EventBusSubscriber(modid = SimpleBedrockModel.MOD_ID)
public final class ExampleModRegister {
    public static final String MOD_ID = "example";

    public static Item DEAGLE_ITEM;
    public static Item TREE_DEAGLE_ITEM;
    public static Block TEST_BLOCK;
    public static Item TEST_BLOCK_ITEM;
    public static BlockEntityType<TestBlockEntity> TEST_BLOCK_ENTITY_TYPE;
    public static EntityType<Zti> ZTI_ENTITY_TYPE;
    public static Item ZTI_SPAWNER_ITEM;
    public static ExampleArmorItem DEFENDER_ARMOR_HELMET;
    public static ExampleArmorItem DEFENDER_ARMOR_CHESTPLATE;
    public static ExampleArmorItem DEFENDER_ARMOR_LEGGINGS;
    public static ExampleArmorItem DEFENDER_ARMOR_BOOTS;
    public static CreativeModeTab TEST_TAB;

    private ExampleModRegister() {
    }

    @SubscribeEvent
    public static void register(RegisterEvent event) {
        if (event.getRegistryKey().equals(BuiltInRegistries.BLOCK.key())) {
            Identifier id = modLoc("test_block");
            ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, id);
            TEST_BLOCK = new TestBlock(BlockBehaviour.Properties.of()
                    .setId(key).strength(2.0F).sound(SoundType.WOOD).noOcclusion());
            event.register(BuiltInRegistries.BLOCK.key(), id, () -> TEST_BLOCK);
        }

        if (event.getRegistryKey().equals(BuiltInRegistries.BLOCK_ENTITY_TYPE.key())) {
            TEST_BLOCK_ENTITY_TYPE = new BlockEntityType<>(TestBlockEntity::new, TEST_BLOCK);
            event.register(BuiltInRegistries.BLOCK_ENTITY_TYPE.key(), modLoc("test_block"), () -> TEST_BLOCK_ENTITY_TYPE);
        }

        if (event.getRegistryKey().equals(BuiltInRegistries.ENTITY_TYPE.key())) {
            Identifier id = modLoc("zti");
            ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, id);
            ZTI_ENTITY_TYPE = EntityType.Builder.of(Zti::new, MobCategory.CREATURE)
                    .sized(1.8F, 3.5F).clientTrackingRange(10).build(key);
            event.register(BuiltInRegistries.ENTITY_TYPE.key(), id, () -> ZTI_ENTITY_TYPE);
        }

        if (event.getRegistryKey().equals(BuiltInRegistries.ITEM.key())) {
            DEAGLE_ITEM = registerItem(event, "deagle", p -> new DeagleItem(p.stacksTo(1)));
            TREE_DEAGLE_ITEM = registerItem(event, "tree_deagle", p -> new TreeDeagleItem(p.stacksTo(1)));
            TEST_BLOCK_ITEM = registerItem(event, "test_block", p -> new BlockItem(TEST_BLOCK, p));
            ZTI_SPAWNER_ITEM = registerItem(event, "zti_spawner", p -> new ZtiSpawnerItem(p.stacksTo(1)));
            DEFENDER_ARMOR_HELMET = registerArmor(event, "defender_helmet", ArmorType.HELMET);
            DEFENDER_ARMOR_CHESTPLATE = registerArmor(event, "defender_chestplate", ArmorType.CHESTPLATE);
            DEFENDER_ARMOR_LEGGINGS = registerArmor(event, "defender_leggings", ArmorType.LEGGINGS);
            DEFENDER_ARMOR_BOOTS = registerArmor(event, "defender_boots", ArmorType.BOOTS);
        }

        if (event.getRegistryKey().equals(Registries.CREATIVE_MODE_TAB)) {
            TEST_TAB = CreativeModeTab.builder()
                    .title(Component.literal("SimpleBedrockModel Render Tests"))
                    .icon(() -> DEAGLE_ITEM.getDefaultInstance())
                    .displayItems((parameters, output) -> testItems().forEach(output::accept))
                    .build();
            event.register(Registries.CREATIVE_MODE_TAB, modLoc("render_test"), () -> TEST_TAB);
        }
    }

    private static ExampleArmorItem registerArmor(RegisterEvent event, String name, ArmorType type) {
        return (ExampleArmorItem) registerItem(event, name,
                p -> new ExampleArmorItem(type, p.humanoidArmor(ArmorMaterials.DIAMOND, type)));
    }

    private static Item registerItem(RegisterEvent event, String name, java.util.function.Function<Item.Properties, Item> factory) {
        Identifier id = modLoc(name);
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);
        Item item = factory.apply(new Item.Properties().setId(key));
        event.register(BuiltInRegistries.ITEM.key(), id, () -> item);
        return item;
    }

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ZTI_ENTITY_TYPE, Zti.createAttributes().build());
    }

    @SubscribeEvent
    public static void giveRenderTestItems(PlayerEvent.PlayerLoggedInEvent event) {
        for (Item item : testItems()) {
            ItemStack stack = new ItemStack(item);
            if (!event.getEntity().getInventory().contains(stack)) {
                event.getEntity().addItem(stack);
            }
        }
    }

    private static java.util.List<Item> testItems() {
        return java.util.List.of(DEAGLE_ITEM, TREE_DEAGLE_ITEM, TEST_BLOCK_ITEM, ZTI_SPAWNER_ITEM,
                DEFENDER_ARMOR_HELMET, DEFENDER_ARMOR_CHESTPLATE,
                DEFENDER_ARMOR_LEGGINGS, DEFENDER_ARMOR_BOOTS);
    }

    public static Identifier modLoc(String name) {
        return Identifier.fromNamespaceAndPath(MOD_ID, name);
    }
}
