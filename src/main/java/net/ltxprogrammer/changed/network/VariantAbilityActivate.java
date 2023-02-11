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
            ProcessTransfur.ifPlayerLatex(context.getSender(), ((player, variant) -> {
                if (ability.equals(CONTROL_OPEN_RADIAL.ability))
                    player.openMenu(new SimpleMenuProvider((id, inventory, givenPlayer) ->
                            new AbilityRadialMenu(id, inventory, null), AbilityRadialMenu.CONTAINER_TITLE));
                else
                    variant.activateAbility(player, ability);
            }));
        });
        context.setPacketHandled(true);
    }
}
