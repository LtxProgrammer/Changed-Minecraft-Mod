package net.ltxprogrammer.changed.network.packet;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.TagUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import oshi.util.tuples.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

public class SyncTransfurPacket implements ChangedPacket {
    record Listing(ResourceLocation form, CompoundTag data) {
        static Listing fromStream(FriendlyByteBuf buf) {
            return new Listing(buf.readResourceLocation(), buf.readNbt());
        }

        void toStream(FriendlyByteBuf buf) {
            buf.writeResourceLocation(form);
            buf.writeNbt(data);
        }
    }

    private final Map<UUID, Listing> changedForms;
    private static final ResourceLocation NO_FORM = Changed.modResource("no_form");

    public SyncTransfurPacket(Map<UUID, Listing> changedForms) {
        this.changedForms = changedForms;
    }

    public SyncTransfurPacket(FriendlyByteBuf buffer) {
        this.changedForms = new HashMap<>();
        buffer.readList(next ->
                new Pair<>(next.readUUID(), Listing.fromStream(next))).forEach(pair ->
                    changedForms.put(pair.getA(), pair.getB()));
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeCollection(changedForms.entrySet(), (next, form) -> { next.writeUUID(form.getKey()); form.getValue().toStream(next); });
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        if (context.getDirection().getReceptionSide().isClient()) {
            ClientLevel level = Minecraft.getInstance().level;
            Objects.requireNonNull(level);
            changedForms.forEach((uuid, listing) -> {
                Player player = level.getPlayerByUUID(uuid);
                if (player != null) {
                    ProcessTransfur.setPlayerLatexVariantNamed(player, listing.form);
                    TagUtil.replace(listing.data, player.getPersistentData());
                }
            });
            context.setPacketHandled(true);
        }
        else if (context.getDirection().getReceptionSide().isServer()) { // Mirror packet
            ServerPlayer sender = context.getSender();
            if (sender != null) {
                Changed.PACKET_HANDLER.send(PacketDistributor.ALL.noArg(), this);
            }
            context.setPacketHandled(true);
        }
    }

    public static class Builder {
        private final Map<UUID, Listing> changedForms = new HashMap<>();

        public void addPlayer(Player player) {
            var variant = ProcessTransfur.getPlayerLatexVariant(player);
            changedForms.put(player.getUUID(),
                    new Listing(variant != null ? variant.getFormId() : NO_FORM,
                            player.getPersistentData()));
        }

        public SyncTransfurPacket build() {
            return new SyncTransfurPacket(changedForms);
        }

        public static SyncTransfurPacket of(Player player) {
            Builder builder = new Builder();
            builder.addPlayer(player);
            return builder.build();
        }
    }
}
