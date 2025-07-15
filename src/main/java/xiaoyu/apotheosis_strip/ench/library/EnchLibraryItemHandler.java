package xiaoyu.apotheosis_strip.ench.library;

import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class EnchLibraryItemHandler implements Container {
    private final EnchLibraryTile tile;
    private final NonNullList<ItemStack> inventory;

    public EnchLibraryItemHandler(EnchLibraryTile tile) {
        this.tile = tile;
        this.inventory = NonNullList.withSize(1, ItemStack.EMPTY);
    }

    @Override
    public void clearContent() {
        this.inventory.clear();
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return this.inventory.get(0).isEmpty();
    }

    @Override
    public ItemStack getItem(int index) {
        return this.inventory.get(0);
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        return ContainerHelper.removeItem(this.inventory, 0, count);
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        return ContainerHelper.takeItem(this.inventory, 0);
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        this.inventory.set(0, stack);
        this.setChanged();
    }

    @Override
    public void setChanged() {
        if (this.tile != null) {
            this.tile.setChanged();
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
} 