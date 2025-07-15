package xiaoyu.apotheosis_strip.ench.objects;

import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BookItem;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;

import xiaoyu.apotheosis_strip.ench.EnchModule;
import xiaoyu.apotheosis_strip.DivineCreation;
import net.minecraftforge.registries.ForgeRegistries;

public class TomeItem extends BookItem {

    final ItemStack rep;
    final EnchantmentCategory type;

    public TomeItem(Item rep, EnchantmentCategory type) {
        super(new Item.Properties());
        this.type = type;
        this.rep = new ItemStack(rep);
        EnchModule.TYPED_BOOKS.add(this);
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return stack.getCount() == 1;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        if (this.type == null) return EnchModule.TYPED_BOOKS.stream().filter(b -> b != this).allMatch(b -> !enchantment.canEnchant(new ItemStack(b)));
        return enchantment.canApplyAtEnchantingTable(this.rep);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(this);
        if (id != null) {
            tooltip.add(Component.translatable("info." + DivineCreation.MODID + "." + id.getPath()).withStyle(ChatFormatting.GRAY));
        }
        if (stack.isEnchanted()) {
            tooltip.add(Component.translatable("info." + DivineCreation.MODID + ".tome_error").withStyle(ChatFormatting.RED));
        }
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return !stack.isEnchanted() ? super.getRarity(stack) : Rarity.UNCOMMON;
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

    public ItemStack onEnchantment(ItemStack stack, List<EnchantmentInstance> enchantments) {
        stack = new ItemStack(Items.ENCHANTED_BOOK);
        for (EnchantmentInstance inst : enchantments) {
            EnchantedBookItem.addEnchantment(stack, inst);
        }
        return stack;
    }

    public boolean forciblyAllowsTableEnchantment(ItemStack stack, Enchantment enchantment) {
        return this.canApplyAtEnchantingTable(stack, enchantment);
    }
} 