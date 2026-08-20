package net.ltxprogrammer.changed.network.packet;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.ability.AbstractAbilityInstance;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.UniversalDist;
import net.ltxprogrammer.changed.world.LatexCoverState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
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
        this.entityId = buffer.readVarInt();
        this.ability = ChangedRegistry.ABILITY.readRegistryObject(buffer);
        this.tag = buffer.readAnySizeNbt();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeVarInt(entityId);
        ChangedRegistry.ABILITY.writeRegistryObject(buffer, ability);
        buffer.writeNbt(tag);
    }

    @Override
    public CompletableFuture<Void> handle(NetworkEvent.Context context, CompletableFuture<Level> levelFuture, Executor sidedExecutor) {
        if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
            context.setPacketHandled(true);
            return levelFuture.thenAccept(level -> {
                var entity = level.getEntity(entityId);
                AbstractAbilityInstance abilityInstance = null;
                if (entity instanceof ChangedEntity changedEntity) {
                    abilityInstance = changedEntity.getAbilityInstance(ability);
                } else if (entity instanceof Player player) {
                    final var variant = ProcessTransfur.getPlayerTransfurVariant(player);
                    if (variant != null)
                        abilityInstance = variant.getAbilityInstance(ability);
                }

                if (abilityInstance == null)
                    throw new IllegalStateException("Ability instance not present on entity: " + entity);

                abilityInstance.acceptPayload(tag);
            });
        }

        else {
            ServerPlayer sender = context.getSender();
            context.setPacketHandled(true);
            return levelFuture.thenAccept(level -> {
                var entity = level.getEntity(entityId);

                AbstractAbilityInstance abilityInstance = null;
                if (entity instanceof Player player) {
                    final var variant = ProcessTransfur.getPlayerTransfurVariant(player);
                    if (variant != null)
                        abilityInstance = variant.getAbilityInstance(ability);
                }

                if (abilityInstance == null)
                    throw new IllegalStateException("Ability instance not present on entity: " + entity);

                if (entity != sender)
                    abilityInstance.acceptPayloadFromNonHost(tag, sender);
                else {
                    abilityInstance.acceptPayload(tag);
                    Changed.PACKET_HANDLER.send(PacketDistributor.TRACKING_ENTITY.with(context::getSender), this);
                }
            });
        }
    }
}
