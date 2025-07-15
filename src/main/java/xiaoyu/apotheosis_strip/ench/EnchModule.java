package xiaoyu.apotheosis_strip.ench;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import xiaoyu.apotheosis_strip.DivineCreation;
import xiaoyu.apotheosis_strip.ench.library.EnchLibraryBlock;
import xiaoyu.apotheosis_strip.ench.library.EnchLibraryTile.BasicLibraryTile;
import xiaoyu.apotheosis_strip.ench.library.EnchLibraryTile.EnderLibraryTile;
import xiaoyu.apotheosis_strip.ench.objects.ExtractionTomeItem;
import xiaoyu.apotheosis_strip.ench.objects.ImprovedScrappingTomeItem;
import xiaoyu.apotheosis_strip.ench.objects.ScrappingTomeItem;
import xiaoyu.apotheosis_strip.ench.objects.TomeItem;

@Mod.EventBusSubscriber(modid = DivineCreation.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EnchModule {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, DivineCreation.MODID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, DivineCreation.MODID);
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, DivineCreation.MODID);
    private static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, DivineCreation.MODID);
    
    public static final List<TomeItem> TYPED_BOOKS = new ArrayList<>();

    public static final RegistryObject<Block> LIBRARY = BLOCKS.register("library",
            () -> new EnchLibraryBlock(BasicLibraryTile::new, 16));
            
    public static final RegistryObject<Block> ENDER_LIBRARY = BLOCKS.register("ender_library",
            () -> new EnchLibraryBlock(EnderLibraryTile::new, 31));

    public static final RegistryObject<BlockItem> LIBRARY_ITEM = ITEMS.register("library",
            () -> new BlockItem(LIBRARY.get(), new Item.Properties()));
            
    public static final RegistryObject<BlockItem> ENDER_LIBRARY_ITEM = ITEMS.register("ender_library",
            () -> new BlockItem(ENDER_LIBRARY.get(), new Item.Properties()));

    public static final RegistryObject<TomeItem> HELMET_TOME = ITEMS.register("helmet_tome",
            () -> new TomeItem(Items.DIAMOND_HELMET, EnchantmentCategory.ARMOR_HEAD));

    public static final RegistryObject<TomeItem> CHESTPLATE_TOME = ITEMS.register("chestplate_tome",
            () -> new TomeItem(Items.DIAMOND_CHESTPLATE, EnchantmentCategory.ARMOR_CHEST));

    public static final RegistryObject<TomeItem> LEGGINGS_TOME = ITEMS.register("leggings_tome",
            () -> new TomeItem(Items.DIAMOND_LEGGINGS, EnchantmentCategory.ARMOR_LEGS));

    public static final RegistryObject<TomeItem> BOOTS_TOME = ITEMS.register("boots_tome",
            () -> new TomeItem(Items.DIAMOND_BOOTS, EnchantmentCategory.ARMOR_FEET));

    public static final RegistryObject<TomeItem> WEAPON_TOME = ITEMS.register("weapon_tome",
            () -> new TomeItem(Items.DIAMOND_SWORD, EnchantmentCategory.WEAPON));

    public static final RegistryObject<TomeItem> BOW_TOME = ITEMS.register("bow_tome",
            () -> new TomeItem(Items.BOW, EnchantmentCategory.BOW));

    public static final RegistryObject<TomeItem> PICKAXE_TOME = ITEMS.register("pickaxe_tome",
            () -> new TomeItem(Items.DIAMOND_PICKAXE, EnchantmentCategory.DIGGER));

    public static final RegistryObject<TomeItem> FISHING_TOME = ITEMS.register("fishing_tome",
            () -> new TomeItem(Items.FISHING_ROD, EnchantmentCategory.FISHING_ROD));

    public static final RegistryObject<TomeItem> OTHER_TOME = ITEMS.register("other_tome",
            () -> new TomeItem(Items.BOOK, null));

    public static final RegistryObject<ScrappingTomeItem> SCRAP_TOME = ITEMS.register("scrap_tome",
            ScrappingTomeItem::new);

    public static final RegistryObject<ImprovedScrappingTomeItem> IMPROVED_SCRAP_TOME = ITEMS.register("improved_scrap_tome",
            ImprovedScrappingTomeItem::new);

    public static final RegistryObject<ExtractionTomeItem> EXTRACTION_TOME = ITEMS.register("extraction_tome",
            ExtractionTomeItem::new);

    public static final RegistryObject<BlockEntityType<BasicLibraryTile>> LIBRARY_TILE = BLOCK_ENTITIES.register("library",
            () -> BlockEntityType.Builder.of(BasicLibraryTile::new, LIBRARY.get()).build(null));
            
    public static final RegistryObject<BlockEntityType<EnderLibraryTile>> ENDER_LIBRARY_TILE = BLOCK_ENTITIES.register("ender_library",
            () -> BlockEntityType.Builder.of(EnderLibraryTile::new, ENDER_LIBRARY.get()).build(null));

    public static final RegistryObject<CreativeModeTab> ENCHANTMENT_TAB = CREATIVE_MODE_TABS.register("tab",
            () -> CreativeModeTab.builder()
                    .icon(() -> ENDER_LIBRARY_ITEM.get().getDefaultInstance())
                    .title(Component.translatable("itemGroup." + DivineCreation.MODID))
                    .displayItems((parameters, output) -> {
                        output.accept(LIBRARY_ITEM.get());
                        output.accept(ENDER_LIBRARY_ITEM.get());
                        output.accept(HELMET_TOME.get());
                        output.accept(CHESTPLATE_TOME.get());
                        output.accept(LEGGINGS_TOME.get());
                        output.accept(BOOTS_TOME.get());
                        output.accept(WEAPON_TOME.get());
                        output.accept(BOW_TOME.get());
                        output.accept(PICKAXE_TOME.get());
                        output.accept(FISHING_TOME.get());
                        output.accept(OTHER_TOME.get());
                        output.accept(SCRAP_TOME.get());
                        output.accept(IMPROVED_SCRAP_TOME.get());
                        output.accept(EXTRACTION_TOME.get());
                    })
                    .build());

    public static void registerModule(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
        BLOCK_ENTITIES.register(eventBus);
        CREATIVE_MODE_TABS.register(eventBus);

        MinecraftForge.EVENT_BUS.addListener(EnchModule::onAnvilUpdate);
        MinecraftForge.EVENT_BUS.addListener(EnchModule::onAnvilRepair);
    }

    private static void onAnvilUpdate(AnvilUpdateEvent event) {
        if (handleScrappingTome(event)) return;
        if (handleImprovedScrappingTome(event)) return;
        if (handleExtractionTome(event)) return;
    }

    private static void onAnvilRepair(AnvilRepairEvent event) {
        handleExtractionTomeRepair(event);
    }

    private static boolean handleScrappingTome(AnvilUpdateEvent event) {
        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();

        if (!(right.getItem() instanceof ScrappingTomeItem) || right.isEnchanted() || !left.isEnchanted()) {
            return false;
        }

        ScrappingTomeItem tome = (ScrappingTomeItem) right.getItem();
        ItemStack output = tome.extractEnchantments(right, left);
        
        event.setOutput(output);
        event.setCost(Math.max(1, EnchantmentHelper.getEnchantments(output).size() * 6));
        event.setMaterialCost(1);
        return true;
    }
    
    private static boolean handleImprovedScrappingTome(AnvilUpdateEvent event) {
        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();

        if (!(right.getItem() instanceof ImprovedScrappingTomeItem) || right.isEnchanted() || !left.isEnchanted()) {
            return false;
        }

        ImprovedScrappingTomeItem tome = (ImprovedScrappingTomeItem) right.getItem();
        ItemStack output = tome.extractEnchantments(right, left);
        
        event.setOutput(output);
        event.setCost(Math.max(1, EnchantmentHelper.getEnchantments(output).size() * 10));
        event.setMaterialCost(1);
        return true;
    }

    private static boolean handleExtractionTome(AnvilUpdateEvent event) {
        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();

        if (!(right.getItem() instanceof ExtractionTomeItem) || right.isEnchanted() || !left.isEnchanted()) {
            return false;
        }

        ItemStack output = new ItemStack(Items.ENCHANTED_BOOK);
        EnchantmentHelper.setEnchantments(EnchantmentHelper.getEnchantments(left), output);
        
        event.setOutput(output);
        event.setCost(Math.max(1, EnchantmentHelper.getEnchantments(output).size() * 16));
        event.setMaterialCost(1);
        return true;
    }

    private static void handleExtractionTomeRepair(AnvilRepairEvent event) {
        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();

        if (!(right.getItem() instanceof ExtractionTomeItem) || right.isEnchanted() || !left.isEnchanted()) {
            return;
        }

        ItemStack cleanItem = left.copy();
        EnchantmentHelper.setEnchantments(java.util.Map.of(), cleanItem);

        event.getEntity().getInventory().placeItemBackInInventory(cleanItem);
    }
}