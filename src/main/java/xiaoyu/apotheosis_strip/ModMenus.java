package xiaoyu.apotheosis_strip;

import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import xiaoyu.apotheosis_strip.ench.library.EnchLibraryContainer;

public class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, DivineCreation.MODID);
    public static final RegistryObject<MenuType<EnchLibraryContainer>> LIBRARY_CONTAINER = MENUS.register("library",
            () -> IForgeMenuType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                return new EnchLibraryContainer(windowId, inv, pos);
            }));
}