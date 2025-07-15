package xiaoyu.apotheosis_strip.ench.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BookItem;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;

import xiaoyu.apotheosis_strip.DivineCreation;

public class ScrappingTomeItem extends BookItem {

    protected static final Random RAND = new Random();

    public ScrappingTomeItem() {
        super(new Item.Properties());
    }

    @Override
    public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("info." + DivineCreation.MODID + ".scrap_tome").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("info." + DivineCreation.MODID + ".scrap_tome2").withStyle(ChatFormatting.RED));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.isEnchanted()) {
            ItemStack book = new ItemStack(Items.ENCHANTED_BOOK, stack.getCount());
            EnchantmentHelper.setEnchantments(EnchantmentHelper.getEnchantments(stack), book);
            return InteractionResultHolder.consume(book);
        }
        return InteractionResultHolder.pass(stack);
    }

    public ItemStack extractEnchantments(ItemStack stack, ItemStack input) {
        Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(input);
        if (map.isEmpty()) return stack;

        int remove = Math.max(1, map.size() / 2);
        Set<Enchantment> s = map.keySet();
        List<Enchantment> l = new ArrayList<>(s);
        while (l.size() > remove) {
            l.remove(RAND.nextInt(l.size()));
        }

        ItemStack out = new ItemStack(Items.ENCHANTED_BOOK);
        for (Enchantment e : l) {
            EnchantedBookItem.addEnchantment(out, new EnchantmentInstance(e, map.get(e)));
        }
        return out;
    }
}