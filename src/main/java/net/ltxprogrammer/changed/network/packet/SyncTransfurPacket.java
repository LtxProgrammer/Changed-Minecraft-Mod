package net.ltxprogrammer.changed.network.packet;

import com.mojang.datafixers.util.Pair;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.TransfurCause;
import net.ltxprogrammer.changed.entity.TransfurContext;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class SyncTransfurPacket implements ChangedPacket {
    record Listing(int form,
                   TransfurCause cause,
                   float progress,
                   boolean temporaryFromSuit, CompoundTag data) {
        static Listing fromStream(FriendlyByteBuf buf) {
            return new Listing(
                    buf.readInt(),
                    buf.readEnum(TransfurCause.class),
                    buf.readFloat(),
                    buf.readBoolean(),
                    buf.readAnySizeNbt());
        }

        void toStream(FriendlyByteBuf buf) {
            buf.writeInt(form);
            buf.writeEnum(cause);
            buf.writeFloat(progress);
            buf.writeBoolean(temporaryFromSuit);
            buf.writeNbt(data);
        }
    }

    private final Map<Integer, Listing> changedForms;
    private static final int NO_FORM = -1;

    public SyncTransfurPacket(Map<Integer, Listing> changedForms) {
        this.changedForms = changedForms;
    }

    public SyncTransfurPacket(FriendlyByteBuf buffer) {
        this.changedForms = new HashMap<>();
        buffer.readList(next ->
                new Pair<>(next.readVarInt(), Listing.fromStream(next))).forEach(pair ->
                    changedForms.put(pair.getFirst(), pair.getSecond()));
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeCollection(changedForms.entrySet(), (next, form) -> { next.writeVarInt(form.getKey()); form.getValue().toStream(next); });
    }

    @Override
    public CompletableFuture<Void> handle(NetworkEvent.Context context, CompletableFuture<Level> levelFuture, Executor sidedExecutor) {
        if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
            context.setPacketHandled(true);
            return levelFuture.thenAccept(level -> {
                changedForms.forEach((id, listing) -> {
                    var entity = level.getEntity(id);
                    if (entity instanceof Player player) {
                        final var listingVariant = ChangedRegistry.TRANSFUR_VARIANT.getValue(listing.form);
                        final var variant = ProcessTransfur.setPlayerTransfurVariant(player,
                                listingVariant,
                                TransfurContext.hazard(listing.cause),
                                listing.progress,
                                listing.temporaryFromSuit);

                        if (variant != null)
                            variant.load(listing.data);
                    } else {
                        Changed.LOGGER.warn("Failed to find player specified in SyncTransfurPacket, {}", id);
                    }
                });
            });
        }

        else {
            ServerPlayer sender = context.getSender();
            if (sender != null) {
                final Listing senderListing = this.changedForms.get(sender.getId());

                if (senderListing != null)
                    Changed.PACKET_HANDLER.send(PacketDistributor.ALL.noArg(),
                            new SyncTransfurPacket(Map.of(sender.getId(), senderListing)));
            }
            context.setPacketHandled(true);
            return CompletableFuture.completedFuture(null);
        }
    }

    public static class Builder {
        private final Map<Integer, Listing> changedForms = new HashMap<>();

        public void addPlayer(Player player, boolean excludeNormal) {
            ProcessTransfur.ifPlayerTransfurred(player, variant -> {
                changedForms.put(player.getId(),
                        new Listing(ChangedRegistry.TRANSFUR_VARIANT.getID(variant.getParent()),
                                variant.transfurContext.cause(),
                                variant.transfurProgression,
                                variant.isTemporaryFromSuit(),
                                variant.save()));
            }, () -> {
                if (!excludeNormal)
                    changedForms.put(player.getId(),
                        new Listing(NO_FORM, TransfurCause.DEFAULT, 0f, false, new CompoundTag()));
            });
        }

        public boolean worthSending() {
            return !changedForms.isEmpty();
        }

        public SyncTransfurPacket build() {
            return new SyncTransfurPacket(changedForms);
        }

        public static SyncTransfurPacket of(Player player) {
            Builder builder = new Builder();
            builder.addPlayer(player, false);
            return builder.build();
        }
    }
}
