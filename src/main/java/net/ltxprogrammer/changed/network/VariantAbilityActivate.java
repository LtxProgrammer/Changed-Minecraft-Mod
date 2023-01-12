package net.ltxprogrammer.changed.network;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.network.packet.ChangedPacket;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.world.inventory.AbilityRadialMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class VariantAbilityActivate implements ChangedPacket {
    public static final VariantAbilityActivate CONTROL_OPEN_RADIAL = new VariantAbilityActivate(Changed.modResource("open_radial"));

    final ResourceLocation ability;

    public VariantAbilityActivate(ResourceLocation ability) {
        this.ability = ability;
    }

    public VariantAbilityActivate(FriendlyByteBuf buffer) {
        this.ability = buffer.readResourceLocation();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeResourceLocation(ability);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer sender = context.getSender();
            if (sender == null)
                return;
            if (ProcessTransfur.isPlayerLatex(sender)) {
                if (ability.equals(CONTROL_OPEN_RADIAL.ability))
                    sender.openMenu(new SimpleMenuProvider((p_52229_, p_52230_, p_52231_) ->
                            new AbilityRadialMenu(p_52229_, p_52230_, null), AbilityRadialMenu.CONTAINER_TITLE));
                else
                    ProcessTransfur.getPlayerLatexVariant(sender).activateAbility(sender, ability);
            }
        });
        context.setPacketHandled(true);
    }
}
