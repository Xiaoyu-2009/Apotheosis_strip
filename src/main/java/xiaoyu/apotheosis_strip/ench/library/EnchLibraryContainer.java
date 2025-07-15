package xiaoyu.apotheosis_strip.ench.library;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistry;
import xiaoyu.apotheosis_strip.ModMenus;

public class EnchLibraryContainer extends AbstractContainerMenu {
    protected final EnchLibraryTile tile;
    protected final BlockPos pos;
    protected final Player player;
    protected final Level level;
    protected SimpleContainer ioInv = new SimpleContainer(3);
    protected Runnable notifier = null;

    public EnchLibraryContainer(int id, Inventory playerInv, EnchLibraryTile tile, BlockPos pos) {
        super(ModMenus.LIBRARY_CONTAINER.get(), id);
        this.tile = tile;
        this.pos = pos;
        this.player = playerInv.player;
        this.level = playerInv.player.level();
        this.initCommon(playerInv);
        
        if (tile != null) {
            tile.addListener(this);
        }
    }

    public EnchLibraryContainer(int id, Inventory playerInv, BlockPos pos) {
        super(ModMenus.LIBRARY_CONTAINER.get(), id);
        BlockEntity be = playerInv.player.level().getBlockEntity(pos);
        this.tile = be instanceof EnchLibraryTile ? (EnchLibraryTile) be : null;
        this.pos = pos;
        this.player = playerInv.player;
        this.level = playerInv.player.level();
        this.initCommon(playerInv);
        
        if (tile != null) {
            tile.addListener(this);
        }
    }
    
    private void initCommon(Inventory inv) {
        this.addSlot(new Slot(this.ioInv, 0, 142, 77) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() == Items.ENCHANTED_BOOK;
            }

            @Override
            public int getMaxStackSize() {
                return 1;
            }

            @Override
            public void setChanged() {
                super.setChanged();
                if (!EnchLibraryContainer.this.level.isClientSide && !this.getItem().isEmpty()) {
                    if (EnchLibraryContainer.this.tile != null) {
                        EnchLibraryContainer.this.tile.depositBook(this.getItem());
                    }
                }
                if (!this.getItem().isEmpty() && EnchLibraryContainer.this.level.isClientSide)
                    inv.player.level().playSound(inv.player, EnchLibraryContainer.this.pos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.NEUTRAL, 0.5F, 0.7F);
                
                EnchLibraryContainer.this.ioInv.setItem(0, ItemStack.EMPTY);
            }
        });

        this.addSlot(new Slot(this.ioInv, 1, 142, 106) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() == Items.ENCHANTED_BOOK;
            }

            @Override
            public int getMaxStackSize() {
                return 1;
            }
        });

        this.addSlot(new Slot(this.ioInv, 2, 142, 18) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return true;
            }

            @Override
            public int getMaxStackSize() {
                return 1;
            }

            @Override
            public void setChanged() {
                EnchLibraryContainer.this.onChanged();
            }
        });
        this.addPlayerInventory(inv);
    }

    private void addPlayerInventory(Inventory playerInv) {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 148 + i * 18));
            }
        }
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInv, i, 8 + i * 18, 206));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return player.distanceToSqr(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D && 
        this.tile != null && !this.tile.isRemoved();
    }
    
    @Override
    public void removed(Player player) {
        super.removed(player);
        if (!this.level.isClientSide && this.tile != null) this.tile.removeListener(this);
        this.clearContainer(player, this.ioInv);
    }

    public ItemStack getItem(int index) {
        return this.ioInv.getItem(index);
    }

    public void setNotifier(Runnable r) {
        this.notifier = r;
    }

    public void onChanged() {
        if (this.notifier != null) this.notifier.run();
    }

    public Set<Enchantment> getAvailableEnchants() {
        if (tile == null) return new HashSet<>();
        return tile.getAvailableEnchants();
    }

    public int getAvailablePoints(Enchantment ench) {
        if (tile == null) return 0;
        return tile.getAvailablePoints(ench);
    }

    public int getMaxLevel(Enchantment ench) {
        if (tile == null) return 0;
        return tile.getMaxLevel();
    }

    public int getPointCap() {
        if (tile == null) return 0;
        return tile.getPointCap();
    }

    public List<Object2IntMap.Entry<Enchantment>> getPointsForDisplay() {
        List<Object2IntMap.Entry<Enchantment>> list = new ArrayList<>();
        if (tile != null) {
            for (Enchantment ench : this.getAvailableEnchants()) {
                int points = this.getAvailablePoints(ench);
                if (points > 0) {
                    list.add(new SimpleEntry(ench, points));
                }
            }
        }
        return list;
    }

    public int getNumStoredEnchants() {
        if (tile == null) return 0;
        int count = 0;
        for (Enchantment ench : this.getAvailableEnchants()) {
            if (this.getAvailablePoints(ench) > 0) {
                count++;
            }
        }
        return count;
    }

    public void onButtonClick(int id) {
        boolean shift = (id & 0x80000000) == 0x80000000;
        if (shift) id = id & 0x7FFFFFFF;
        
        Enchantment ench = ((ForgeRegistry<Enchantment>) ForgeRegistries.ENCHANTMENTS).getValue(id);
        if (ench == null) return;

        ItemStack outSlot = this.ioInv.getItem(1);
        int curLvl = EnchantmentHelper.getEnchantments(outSlot).getOrDefault(ench, 0);
        int targetLevel = shift ? Math.min(this.getMaxLevel(ench), 1 + (int) (Math.log(this.getAvailablePoints(ench) + EnchLibraryTile.levelToPoints(curLvl)) / Math.log(2))) : curLvl + 1;
        
        if (this.tile != null && this.tile.canExtract(ench, targetLevel, curLvl)) {
            if (outSlot.isEmpty()) outSlot = new ItemStack(Items.ENCHANTED_BOOK);

            this.tile.extractEnchant(outSlot, ench, targetLevel);
            this.ioInv.setItem(1, outSlot);

            if (this.player != null && !this.level.isClientSide) {
                this.level.playSound(null, this.pos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 0.5F, 0.7F);
            }

            this.broadcastChanges();
        }
    }

    public void handleEnchantmentClick(String enchId, boolean isShiftDown) {
        if (tile == null || this.level.isClientSide) return;
        
        try {
            int id;
            if (enchId.contains(":")) {
                Enchantment ench = ForgeRegistries.ENCHANTMENTS.getValue(new net.minecraft.resources.ResourceLocation(enchId));
                if (ench == null) return;
                id = ((ForgeRegistry<Enchantment>) ForgeRegistries.ENCHANTMENTS).getID(ench);
            } else {
                id = Integer.parseInt(enchId);
            }
            
            if (isShiftDown) id |= 0x80000000;
            this.onButtonClick(id);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        
        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            itemstack = slotStack.copy();
            
            if (index < 3) {
                if (!this.moveItemStackTo(slotStack, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (slotStack.getItem() == Items.ENCHANTED_BOOK) {
                    if (!this.moveItemStackTo(slotStack, 0, 1, false)) {
                        if (!this.moveItemStackTo(slotStack, 1, 2, false)) {
                            return ItemStack.EMPTY;
                        }
                    }
                } else {
                    if (!this.moveItemStackTo(slotStack, 2, 3, false)) {
                        if (index < 30) {
                            if (!this.moveItemStackTo(slotStack, 30, 39, false)) {
                                return ItemStack.EMPTY;
                            }
                        } else if (!this.moveItemStackTo(slotStack, 3, 30, false)) {
                            return ItemStack.EMPTY;
                        }
                    }
                }
            }
            
            if (slotStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            
            if (slotStack.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }
            
            slot.onTake(player, slotStack);
        }
        
        return itemstack;
    }

    protected void clearContainer(Player player, SimpleContainer container) {
        if (!player.isAlive() || player instanceof net.minecraft.server.level.ServerPlayer && ((net.minecraft.server.level.ServerPlayer)player).hasDisconnected()) {
            for(int i = 0; i < container.getContainerSize(); ++i) {
                player.drop(container.removeItemNoUpdate(i), false);
            }
        } else {
            for(int i = 0; i < container.getContainerSize(); ++i) {
                player.getInventory().placeItemBackInInventory(container.removeItemNoUpdate(i));
            }
        }
    }

    private static class SimpleEntry implements Object2IntMap.Entry<Enchantment> {
        private final Enchantment key;
        private int value;
        
        public SimpleEntry(Enchantment key, int value) {
            this.key = key;
            this.value = value;
        }
        
        @Override
        public Enchantment getKey() {
            return key;
        }

        @Override
        public int getIntValue() {
            return value;
        }

        @Override
        public int setValue(int value) {
            int old = this.value;
            this.value = value;
            return old;
        }
    }
}