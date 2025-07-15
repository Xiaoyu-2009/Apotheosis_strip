package xiaoyu.apotheosis_strip.ench.library;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ForgeRegistries;

import xiaoyu.apotheosis_strip.DivineCreation;
import xiaoyu.apotheosis_strip.ench.EnchModule;

public abstract class EnchLibraryTile extends BlockEntity implements MenuProvider {
    protected final Object2IntMap<Enchantment> points = new Object2IntOpenHashMap<>();
    protected final Object2IntMap<Enchantment> maxLevels = new Object2IntOpenHashMap<>();
    protected final Set<EnchLibraryContainer> activeContainers = new HashSet<>();
    protected final LazyOptional<IItemHandler> itemHandler = LazyOptional.of(EnchLibItemHandler::new);
    protected final int maxLevel;
    protected final int maxPoints;

    public EnchLibraryTile(BlockEntityType<?> type, BlockPos pos, BlockState state, int maxLevel) {
        super(type, pos, state);
        this.maxLevel = maxLevel;
        this.maxPoints = levelToPoints(maxLevel);
    }

    public void depositBook(ItemStack book) {
        storeBook(book);
    }

    public boolean storeBook(ItemStack book) {
        if (book.isEmpty() || !(book.getItem() instanceof EnchantedBookItem)) return false;
        
        Map<Enchantment, Integer> enchs = EnchantmentHelper.getEnchantments(book);
        boolean changed = false;
        
        for (Map.Entry<Enchantment, Integer> e : enchs.entrySet()) {
            Enchantment ench = e.getKey();
            int level = e.getValue();

            if (ench == null || level <= 0) continue;

            int newPoints = Math.min(this.maxPoints, this.points.getInt(ench) + levelToPoints(level));
            if (newPoints < 0) newPoints = this.maxPoints;

            this.points.put(ench, newPoints);
            this.maxLevels.put(ench, Math.min(this.maxLevel, Math.max(this.maxLevels.getInt(ench), level)));
            
            changed = true;
        }
        
        if (changed) {
            this.dispatchUpdate();
            this.setChanged();
        }
        
        return changed;
    }

    public boolean canExtract(Enchantment ench, int level, int currentLevel) {
        return this.maxLevels.getInt(ench) >= level && 
               this.points.getInt(ench) >= levelToPoints(level) - levelToPoints(currentLevel);
    }

    public void extractEnchant(ItemStack stack, Enchantment ench, int level) {
        if (stack.isEmpty() || stack.getItem() != Items.ENCHANTED_BOOK) return;
        
        int curLvl = EnchantmentHelper.getEnchantments(stack).getOrDefault(ench, 0);
        if (!this.canExtract(ench, level, curLvl) || level == curLvl) return;
        
        Map<Enchantment, Integer> enchs = EnchantmentHelper.getEnchantments(stack);
        enchs.put(ench, level);
        EnchantmentHelper.setEnchantments(enchs, stack);
        
        this.points.put(ench, Math.max(0, this.points.getInt(ench) - levelToPoints(level) + levelToPoints(curLvl))); // 安全检查，理论上不应该小于零

        if (this.level != null && !this.level.isClientSide) {
            this.dispatchUpdate();
        }
        this.setChanged();
    }

    public static int levelToPoints(int level) {
        return (int) Math.pow(2, level - 1);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);

        CompoundTag pointsTag = new CompoundTag();
        for (Object2IntMap.Entry<Enchantment> e : this.points.object2IntEntrySet()) {
            ResourceLocation id = ForgeRegistries.ENCHANTMENTS.getKey(e.getKey());
            if (id != null) {
                pointsTag.putInt(id.toString(), e.getIntValue());
            }
        }
        tag.put("Points", pointsTag);

        CompoundTag levelsTag = new CompoundTag();
        for (Object2IntMap.Entry<Enchantment> e : this.maxLevels.object2IntEntrySet()) {
            ResourceLocation id = ForgeRegistries.ENCHANTMENTS.getKey(e.getKey());
            if (id != null) {
                levelsTag.putInt(id.toString(), e.getIntValue());
            }
        }
        tag.put("Levels", levelsTag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        CompoundTag pointsTag = tag.getCompound("Points");
        for (String s : pointsTag.getAllKeys()) {
            Enchantment ench = ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(s));
            if (ench == null) continue;
            this.points.put(ench, pointsTag.getInt(s));
        }

        CompoundTag levelsTag = tag.getCompound("Levels");
        for (String s : levelsTag.getAllKeys()) {
            Enchantment ench = ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(s));
            if (ench == null) continue;
            this.maxLevels.put(ench, levelsTag.getInt(s));
        }
    }
    
    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        CompoundTag tag = pkt.getTag();

        CompoundTag pointsTag = tag.getCompound("Points");
        for (String s : pointsTag.getAllKeys()) {
            Enchantment ench = ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(s));
            if (ench == null) continue;
            this.points.put(ench, pointsTag.getInt(s));
        }

        CompoundTag levelsTag = tag.getCompound("Levels");
        for (String s : levelsTag.getAllKeys()) {
            Enchantment ench = ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(s));
            if (ench == null) continue;
            this.maxLevels.put(ench, levelsTag.getInt(s));
        }

        this.activeContainers.forEach(EnchLibraryContainer::onChanged);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();

        CompoundTag pointsTag = new CompoundTag();
        for (Object2IntMap.Entry<Enchantment> e : this.points.object2IntEntrySet()) {
            ResourceLocation id = ForgeRegistries.ENCHANTMENTS.getKey(e.getKey());
            if (id != null) {
                pointsTag.putInt(id.toString(), e.getIntValue());
            }
        }
        tag.put("Points", pointsTag);

        CompoundTag levelsTag = new CompoundTag();
        for (Object2IntMap.Entry<Enchantment> e : this.maxLevels.object2IntEntrySet()) {
            ResourceLocation id = ForgeRegistries.ENCHANTMENTS.getKey(e.getKey());
            if (id != null) {
                levelsTag.putInt(id.toString(), e.getIntValue());
            }
        }
        tag.put("Levels", levelsTag);
        
        return tag;
    }

    protected void dispatchUpdate() {
        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }

    public Set<Enchantment> getAvailableEnchants() {
        return new HashSet<>(this.points.keySet());
    }

    public int getAvailablePoints(Enchantment ench) {
        return this.points.getInt(ench);
    }

    public int getMax(Enchantment ench) {
        return Math.min(this.maxLevel, this.maxLevels.getInt(ench));
    }

    public int getPointCap() {
        return this.maxPoints;
    }

    public int getMaxLevel() {
        return this.maxLevel;
    }
    
    public void addListener(EnchLibraryContainer ctr) {
        this.activeContainers.add(ctr);
    }

    public void removeListener(EnchLibraryContainer ctr) {
        this.activeContainers.remove(ctr);
    }

    public boolean isRemoved() {
        return this.remove;
    }
    
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) return this.itemHandler.cast();
        return super.getCapability(cap, side);
    }

    private class EnchLibItemHandler implements IItemHandler {
        
        @Override
        public int getSlots() {
            return 1;
        }
        
        @Override
        public ItemStack getStackInSlot(int slot) {
            return ItemStack.EMPTY;
        }
        
        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (stack.getItem() != Items.ENCHANTED_BOOK || stack.getCount() > 1) return stack;
            if (!simulate) {
                EnchLibraryTile.this.depositBook(stack);
            }
            return ItemStack.EMPTY;
        }
        
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return ItemStack.EMPTY;
        }
        
        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }
        
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return slot == 0 && stack.getItem() == Items.ENCHANTED_BOOK;
        }
    }

    public static class BasicLibraryTile extends EnchLibraryTile {
        
        public BasicLibraryTile(BlockPos pos, BlockState state) {
            super(EnchModule.LIBRARY_TILE.get(), pos, state, 16);
        }
        
        @Override
        public Component getDisplayName() {
            return Component.translatable("block." + DivineCreation.MODID + ".library");
        }
        
        @Override
        public AbstractContainerMenu createMenu(int id, Inventory playerInv, Player player) {
            return new EnchLibraryContainer(id, playerInv, this, this.worldPosition);
        }
    }

    public static class EnderLibraryTile extends EnchLibraryTile {
        
        public EnderLibraryTile(BlockPos pos, BlockState state) {
            super(EnchModule.ENDER_LIBRARY_TILE.get(), pos, state, 31);
        }
        
        @Override
        public Component getDisplayName() {
            return Component.translatable("block." + DivineCreation.MODID + ".ender_library");
        }
        
        @Override
        public AbstractContainerMenu createMenu(int id, Inventory playerInv, Player player) {
            return new EnchLibraryContainer(id, playerInv, this, this.worldPosition);
        }
    }
}