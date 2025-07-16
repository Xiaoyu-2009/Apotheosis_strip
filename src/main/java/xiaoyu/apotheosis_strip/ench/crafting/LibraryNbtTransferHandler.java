package xiaoyu.apotheosis_strip.ench.crafting;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent.ItemCraftedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xiaoyu.apotheosis_strip.DivineCreation;
import xiaoyu.apotheosis_strip.ench.EnchModule;

@Mod.EventBusSubscriber(modid = DivineCreation.MODID)
public class LibraryNbtTransferHandler {
    @SubscribeEvent
    public static void onItemCrafted(ItemCraftedEvent event) {
        ItemStack result = event.getCrafting();
        if (result.getItem() == EnchModule.ENDER_LIBRARY_ITEM.get()) {
            for (int i = 0; i < event.getInventory().getContainerSize(); i++) {
                ItemStack ingredient = event.getInventory().getItem(i);
                if (ingredient.getItem() == EnchModule.LIBRARY_ITEM.get()) {
                    CompoundTag tag = ingredient.getTagElement("BlockEntityTag");
                    if (tag != null) {
                        result.getOrCreateTag().put("BlockEntityTag", tag.copy());
                    }
                    break;
                }
            }
        }
    }
} 