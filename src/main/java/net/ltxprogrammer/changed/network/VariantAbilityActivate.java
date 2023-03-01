package net.ltxprogrammer.changed.network;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.init.ChangedRegistry;
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
    public static final VariantAbilityActivate CONTROL_OPEN_RADIAL = new VariantAbilityActivate(-1);

    final int ability;

    public VariantAbilityActivate(int ability) {
        this.ability = ability;
    }

    public VariantAbilityActivate(FriendlyByteBuf buffer) {
        this.ability = buffer.readInt();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeInt(ability);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ProcessTransfur.ifPlayerLatex(context.getSender(), ((player, variant) -> {
                if (ability == CONTROL_OPEN_RADIAL.ability)
                    player.openMenu(new SimpleMenuProvider((id, inventory, givenPlayer) ->
                            new AbilityRadialMenu(id, inventory, null), AbilityRadialMenu.CONTAINER_TITLE));
                else
                    variant.activateAbility(player, ChangedRegistry.ABILITY.get().getValue(ability));
            }));
        });
        context.setPacketHandled(true);
    }
}
