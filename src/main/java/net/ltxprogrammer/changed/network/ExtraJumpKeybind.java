package net.ltxprogrammer.changed.network;

import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ExtraJumpKeybind {
    private static final Component CONTAINER_TITLE = new TranslatableComponent("container.changed.extra_hands");

    public ExtraJumpKeybind() {
    }

    public ExtraJumpKeybind(FriendlyByteBuf buffer) {
    }

    public static void buffer(ExtraJumpKeybind message, FriendlyByteBuf buffer) {
    }

    public static void handler(ExtraJumpKeybind message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ProcessTransfur.ifPlayerLatex(context.getSender(), (player, variant) -> {
                if (variant.getJumpCharges() > 0) {
                    variant.decJumpCharges();
                    player.jumpFromGround();
                }
            });
        });
        context.setPacketHandled(true);
    }
}
