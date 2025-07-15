package xiaoyu.apotheosis_strip.util;

import java.util.function.BiFunction;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkHooks;

public class MenuUtil {
    public static InteractionResult openGui(Player player, BlockPos pos, BiFunction<Integer, Inventory, AbstractContainerMenu> factory, Component title) {
        if (player.level().isClientSide) return InteractionResult.SUCCESS;

        if (player instanceof ServerPlayer serverPlayer) {
            NetworkHooks.openScreen(serverPlayer, new MenuProvider() {
                @Override
                public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
                    return factory.apply(id, inventory);
                }
                
                @Override
                public Component getDisplayName() {
                    return title;
                }
            }, pos);
        }
        return InteractionResult.CONSUME;
    }
    public static InteractionResult openLibraryGui(Player player, BlockPos pos, BiFunction<Integer, Inventory, AbstractContainerMenu> factory) {
        return openGui(player, pos, factory, Component.translatable("block.apotheosis_strip.library"));
    }
}