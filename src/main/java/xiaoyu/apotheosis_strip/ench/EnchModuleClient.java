package xiaoyu.apotheosis_strip.ench;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import xiaoyu.apotheosis_strip.DivineCreation;
import xiaoyu.apotheosis_strip.ModMenus;
import xiaoyu.apotheosis_strip.ench.library.EnchLibraryScreen;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = DivineCreation.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class EnchModuleClient {
    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(ModMenus.LIBRARY_CONTAINER.get(), EnchLibraryScreen::new);
        });
    }
}