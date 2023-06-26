package net.ltxprogrammer.changed.network;

import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.network.packet.ChangedPacket;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.world.inventory.AbilityRadialMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class VariantAbilityActivate implements ChangedPacket {
    public static final VariantAbilityActivate CONTROL_OPEN_RADIAL = new VariantAbilityActivate(false);
    final boolean keyState;
    final AbstractAbility<?> ability;

    public VariantAbilityActivate(boolean keyState, AbstractAbility<?> ability) {
        this.keyState = keyState;
        this.ability = ability;
    }

    public VariantAbilityActivate(boolean keyState) {
        this.keyState = keyState;
        this.ability = null;
    }

    public VariantAbilityActivate(FriendlyByteBuf buffer) {
        this.keyState = buffer.readBoolean();
        this.ability = ChangedRegistry.ABILITY.get().getValue(buffer.readInt());
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeBoolean(keyState);
        buffer.writeInt(ChangedRegistry.ABILITY.get().getID(ability));
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ProcessTransfur.ifPlayerLatex(context.getSender(), (player, variant) -> {
                if (ability != null)
                    variant.setSelectedAbility(ability);

                if (keyState == CONTROL_OPEN_RADIAL.keyState && ability == CONTROL_OPEN_RADIAL.ability) {
                    if (!player.isUsingItem())
                        player.openMenu(new SimpleMenuProvider((id, inventory, givenPlayer) ->
                                new AbilityRadialMenu(id, inventory, null), AbilityRadialMenu.CONTAINER_TITLE));
                }
                else
                    variant.abilityKeyState = this.keyState;
            });
        });
        context.setPacketHandled(true);
    }
}
