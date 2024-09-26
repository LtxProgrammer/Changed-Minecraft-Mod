package net.ltxprogrammer.changed.network.packet;

import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.ability.AbstractAbilityInstance;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.UniversalDist;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Packet intended to sync NPC abilities with clients
 */
public class AbilityPayloadPacket implements ChangedPacket {
    private final int entityId;
    private final AbstractAbility<?> ability;
    private final CompoundTag tag;

    public AbilityPayloadPacket(int entityId, AbstractAbility<?> ability, CompoundTag tag) {
        this.entityId = entityId;
        this.ability = ability;
        this.tag = tag;
    }

    public AbilityPayloadPacket(FriendlyByteBuf buffer) {
        this.entityId = buffer.readInt();
        this.ability = ChangedRegistry.ABILITY.get().getValue(buffer.readInt());
        this.tag = buffer.readAnySizeNbt();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeInt(entityId);
        buffer.writeInt(ChangedRegistry.ABILITY.get().getID(ability));
        buffer.writeNbt(tag);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        final var context = contextSupplier.get();

        if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
            var entity = UniversalDist.getLevel().getEntity(entityId);
            AbstractAbilityInstance abilityInstance = null;
            if (entity instanceof ChangedEntity changedEntity) {
                abilityInstance = changedEntity.getAbilityInstance(ability);
            } else if (entity instanceof Player player) {
                final var variant = ProcessTransfur.getPlayerTransfurVariant(player);
                if (variant != null)
                    abilityInstance = variant.getAbilityInstance(ability);
            }

            if (abilityInstance == null)
                return; // Not handled

            abilityInstance.acceptPayload(tag);

            context.setPacketHandled(true);
        }
    }
}
