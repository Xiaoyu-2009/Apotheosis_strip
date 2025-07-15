package xiaoyu.apotheosis_strip.network.packet;

import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import xiaoyu.apotheosis_strip.ench.library.EnchLibraryContainer;

public class EnchantmentClickPacket {
    private final String enchantmentId;
    private final boolean isShiftDown;
    
    public EnchantmentClickPacket(String enchantmentId, boolean isShiftDown) {
        this.enchantmentId = enchantmentId;
        this.isShiftDown = isShiftDown;
    }
    
    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(enchantmentId);
        buf.writeBoolean(isShiftDown);
    }

    public static EnchantmentClickPacket decode(FriendlyByteBuf buf) {
        return new EnchantmentClickPacket(buf.readUtf(), buf.readBoolean());
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null && player.containerMenu instanceof EnchLibraryContainer container) {
                container.handleEnchantmentClick(enchantmentId, isShiftDown);
            }
        });
        ctx.get().setPacketHandled(true);
    }
} 