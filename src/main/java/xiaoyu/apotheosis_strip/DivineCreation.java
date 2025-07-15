package xiaoyu.apotheosis_strip;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import xiaoyu.apotheosis_strip.ench.EnchModule;
import xiaoyu.apotheosis_strip.network.NetworkHandler;

@Mod("apotheosis_strip")
public class DivineCreation {
    public static final String MODID = "apotheosis_strip";
    
    public DivineCreation() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModMenus.MENUS.register(modEventBus);
        EnchModule.registerModule(modEventBus);
        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);
    }
    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            NetworkHandler.init();
        });
    }
} 