package net.ltxprogrammer.changed.network.packet;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import com.mojang.datafixers.util.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

public class SyncTransfurPacket implements ChangedPacket {
    record Listing(int form, CompoundTag data) {
        static Listing fromStream(FriendlyByteBuf buf) {
            return new Listing(buf.readInt(), buf.readNbt());
        }

        void toStream(FriendlyByteBuf buf) {
            buf.writeInt(form);
            buf.writeNbt(data);
        }
    }

    private final Map<UUID, Listing> changedForms;
    private static final int NO_FORM = -1;

    public SyncTransfurPacket(Map<UUID, Listing> changedForms) {
        this.changedForms = changedForms;
    }

    public SyncTransfurPacket(FriendlyByteBuf buffer) {
        this.changedForms = new HashMap<>();
        buffer.readList(next ->
                new Pair<>(next.readUUID(), Listing.fromStream(next))).forEach(pair ->
                    changedForms.put(pair.getFirst(), pair.getSecond()));
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
                    ProcessTransfur.setPlayerLatexVariant(player, ChangedRegistry.LATEX_VARIANT.get().getValue(listing.form));
                    ProcessTransfur.ifPlayerLatex(player, variant -> {
                        variant.loadAbilities(listing.data);
                    });
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
            ProcessTransfur.ifPlayerLatex(player, variant -> {
                changedForms.put(player.getUUID(),
                        new Listing(ChangedRegistry.LATEX_VARIANT.get().getID(variant.getParent()), variant.saveAbilities()));
            }, () -> {
                changedForms.put(player.getUUID(),
                        new Listing(NO_FORM, new CompoundTag()));
            });
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
