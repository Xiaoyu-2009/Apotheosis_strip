package xiaoyu.apotheosis_strip.ench.objects;

import java.util.List;
import java.util.Map;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;

import xiaoyu.apotheosis_strip.DivineCreation;

public class ExtractionTomeItem extends ImprovedScrappingTomeItem {

    public ExtractionTomeItem() {
        super();
    }

    @Override
    public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("info." + DivineCreation.MODID + ".extraction_tome").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("info." + DivineCreation.MODID + ".extraction_tome2").withStyle(ChatFormatting.GREEN));
    }

    public ItemStack[] extractAndPreserve(ItemStack stack, ItemStack input) {
        Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(input);
        if (map.isEmpty()) return new ItemStack[] { stack };

        ItemStack out = new ItemStack(Items.ENCHANTED_BOOK);
        for (Map.Entry<Enchantment, Integer> e : map.entrySet()) {
            EnchantedBookItem.addEnchantment(out, new EnchantmentInstance(e.getKey(), e.getValue()));
        }

        ItemStack result = input.copy();
        EnchantmentHelper.setEnchantments(Map.of(), result);
        return new ItemStack[] { result, out };
    }
}