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

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class VariantAbilityKeybind {
    public VariantAbilityKeybind() {
    }

    public VariantAbilityKeybind(FriendlyByteBuf buffer) {
    }

    public static void buffer(VariantAbilityKeybind message, FriendlyByteBuf buffer) {
    }

    public static void handler(VariantAbilityKeybind message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer sender = context.getSender();
            if (sender == null)
                return;
            if (ProcessTransfur.isPlayerLatex(sender))
                ProcessTransfur.getPlayerLatexVariant(sender).activateAbility(sender);
        });
        context.setPacketHandled(true);
    }

    @SubscribeEvent
    public static void registerMessage(FMLCommonSetupEvent event) {
        Changed.addNetworkMessage(VariantAbilityKeybind.class, VariantAbilityKeybind::buffer, VariantAbilityKeybind::new,
                VariantAbilityKeybind::handler);
    }
}
