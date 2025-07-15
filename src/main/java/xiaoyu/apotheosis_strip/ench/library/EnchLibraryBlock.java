package xiaoyu.apotheosis_strip.ench.library;

import java.util.Arrays;
import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import xiaoyu.apotheosis_strip.DivineCreation;
import xiaoyu.apotheosis_strip.util.MenuUtil;

public class EnchLibraryBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public static final Component NAME = Component.translatable("block." + DivineCreation.MODID + ".library");
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    protected final BlockEntitySupplier<? extends EnchLibraryTile> tileSupplier;
    protected final int maxLevel;

    public EnchLibraryBlock(BlockEntitySupplier<? extends EnchLibraryTile> tileSupplier, int maxLevel) {
        super(Properties.of().mapColor(MapColor.COLOR_RED).strength(5.0F, 1200.0F));
        this.tileSupplier = tileSupplier;
        this.maxLevel = maxLevel;
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        return MenuUtil.openLibraryGui(player, pos, (id, inv) -> new EnchLibraryContainer(id, inv, pos));
    }

    @Override
    public MenuProvider getMenuProvider(BlockState state, Level world, BlockPos pos) {
        return new MenuProvider() {
            @Override
            public Component getDisplayName() {
                return NAME;
            }
            
            @Override
            public AbstractContainerMenu createMenu(int id, net.minecraft.world.entity.player.Inventory inventory, Player player) {
                return new EnchLibraryContainer(id, inventory, pos);
            }
        };
    }

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return this.tileSupplier.create(pPos, pState);
    }
    
    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
        ItemStack s = new ItemStack(this);
        BlockEntity te = world.getBlockEntity(pos);
        if (te != null) s.getOrCreateTag().put("BlockEntityTag", te.saveWithoutMetadata());
        return s;
    }
    
    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        BlockEntity te = world.getBlockEntity(pos);
        if (te != null) {
            te.load(stack.getOrCreateTagElement("BlockEntityTag"));
        }
    }
    
    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        ItemStack s = new ItemStack(this);
        BlockEntity te = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (te != null) s.getOrCreateTag().put("BlockEntityTag", te.saveWithoutMetadata());
        return List.of(s);
    }
    
    @Override
    public void appendHoverText(ItemStack stack, BlockGetter world, List<Component> list, TooltipFlag advanced) {
        list.add(Component.translatable("tooltip." + DivineCreation.MODID + ".enchlib.capacity", Component.translatable("enchantment.level." + this.maxLevel)).withStyle(ChatFormatting.GOLD));
        CompoundTag tag = stack.getTagElement("BlockEntityTag");
        if (tag != null && tag.contains("Points")) {
            list.add(Component.translatable("tooltip." + DivineCreation.MODID + ".enchlib.item", tag.getCompound("Points").size()).withStyle(ChatFormatting.GOLD));
        }
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (newState.getBlock() != this) {
            world.removeBlockEntity(pos);
        }
    }
}