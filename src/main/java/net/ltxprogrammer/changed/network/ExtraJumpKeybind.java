package net.ltxprogrammer.changed.network;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.world.inventory.ExtraHandsMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
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
            ServerPlayer sender = context.getSender();
            if (sender == null)
                return;
            if (ProcessTransfur.isPlayerLatex(sender) && ProcessTransfur.getPlayerLatexVariant(sender).canDoubleJump()) {
                if (ProcessTransfur.getPlayerLatexVariant(sender).getJumpCharges() > 0) {
                    ProcessTransfur.getPlayerLatexVariant(sender).decJumpCharges();
                    sender.jumpFromGround();
                }
            }
        });
        context.setPacketHandled(true);
    }
}
