package net.ltxprogrammer.changed.network;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.network.packet.ChangedPacket;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.UniversalDist;
import net.ltxprogrammer.changed.world.inventory.AbilityRadialMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.UUID;
import java.util.function.Supplier;

public class VariantAbilityActivate implements ChangedPacket {
    final UUID uuid;
    final boolean keyState;
    final AbstractAbility<?> ability;

    public static VariantAbilityActivate openRadial(Player player) {
        return new VariantAbilityActivate(player, false);
    }

    public VariantAbilityActivate(Player player, boolean keyState, AbstractAbility<?> ability) {
        this.uuid = player.getUUID();
        this.keyState = keyState;
        this.ability = ability;
    }

    public VariantAbilityActivate(Player player, boolean keyState) {
        this.uuid = player.getUUID();
        this.keyState = keyState;
        this.ability = null;
    }

    public VariantAbilityActivate(FriendlyByteBuf buffer) {
        this.uuid = buffer.readUUID();
        this.keyState = buffer.readBoolean();
        this.ability = ChangedRegistry.ABILITY.get().getValue(buffer.readInt());
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeUUID(uuid);
        buffer.writeBoolean(keyState);
        buffer.writeInt(ChangedRegistry.ABILITY.get().getID(ability));
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            var sender = context.getSender();
            if (sender != null) { // Server
                if (!sender.getUUID().equals(this.uuid))
                    return;

                ProcessTransfur.ifPlayerTransfurred(sender, (variant) -> {
                    context.setPacketHandled(true);
                    if (variant.isTemporaryFromSuit())
                        return;

                    if (ability != null)
                        variant.setSelectedAbility(ability);

                    if (!keyState && ability == null) {
                        if (!sender.isUsingItem())
                            sender.openMenu(new SimpleMenuProvider((id, inventory, givenPlayer) ->
                                    new AbilityRadialMenu(id, inventory, null), AbilityRadialMenu.CONTAINER_TITLE));
                    }
                    else
                        variant.abilityKeyState = this.keyState;

                    Changed.PACKET_HANDLER.send(PacketDistributor.TRACKING_ENTITY.with(() -> sender), this);
                });
            } else { // Client
                ProcessTransfur.ifPlayerTransfurred(UniversalDist.getLevel().getPlayerByUUID(this.uuid), (player, variant) -> {
                    context.setPacketHandled(true);
                    if (variant.isTemporaryFromSuit())
                        return;

                    if (ability != null)
                        variant.setSelectedAbility(ability);

                    if (keyState || ability != null) {
                        variant.abilityKeyState = this.keyState;
                    }
                });
            }
        });
    }
}
