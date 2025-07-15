package xiaoyu.apotheosis_strip.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import xiaoyu.apotheosis_strip.DivineCreation;
import xiaoyu.apotheosis_strip.network.packet.EnchantmentClickPacket;

public class NetworkHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(DivineCreation.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );
    
    private static int id = 0;

    public static void init() {
        CHANNEL.registerMessage(id++, EnchantmentClickPacket.class, 
                EnchantmentClickPacket::encode, 
                EnchantmentClickPacket::decode, 
                EnchantmentClickPacket::handle);
    }

    public static void sendToServer(Object msg) {
        CHANNEL.sendToServer(msg);
    }

    public static void sendTo(Object msg, ServerPlayer player) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), msg);
    }
} 