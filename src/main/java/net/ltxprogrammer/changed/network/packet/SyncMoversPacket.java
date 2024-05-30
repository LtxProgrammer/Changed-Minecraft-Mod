package net.ltxprogrammer.changed.network.packet;

import com.mojang.datafixers.util.Pair;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.PlayerDataExtension;
import net.ltxprogrammer.changed.entity.PlayerMoverInstance;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.UniversalDist;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

public class SyncMoversPacket implements ChangedPacket {
    record Listing(int mover, CompoundTag data) {
        static Listing fromStream(FriendlyByteBuf buf) {
            return new Listing(buf.readInt(), buf.readNbt());
        }

        void toStream(FriendlyByteBuf buf) {
            buf.writeInt(mover);
            buf.writeNbt(data);
        }
    }

    private final Map<UUID, Listing> movers;
    private static final int NO_FORM = -1;

    public SyncMoversPacket(@NotNull Map<UUID, Listing> movers) {
        this.movers = movers;
    }

    public SyncMoversPacket(FriendlyByteBuf buffer) {
        this.movers = new HashMap<>();
        buffer.readList(next ->
                new Pair<>(next.readUUID(), Listing.fromStream(next))).forEach(pair ->
                movers.put(pair.getFirst(), pair.getSecond()));
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeCollection(movers.entrySet(), (next, form) -> { next.writeUUID(form.getKey()); form.getValue().toStream(next); });
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        if (context.getDirection().getReceptionSide().isClient()) {
            ClientLevel level = Minecraft.getInstance().level;
            Objects.requireNonNull(level);
            movers.forEach((uuid, listing) -> {
                if (level.getPlayerByUUID(uuid) instanceof PlayerDataExtension ext) {
                    ext.setPlayerMoverType(listing.mover == NO_FORM ? null : ChangedRegistry.PLAYER_MOVER.get().getValue(listing.mover));
                    var mover = ext.getPlayerMover();
                    if (mover != null)
                        mover.readFrom(listing.data);
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
        private final Map<UUID, Listing> movers = new HashMap<>();

        public void addPlayer(Player player) {
            if (player instanceof PlayerDataExtension ext && ext.getPlayerMover() != null) {
                CompoundTag tag = new CompoundTag();
                ext.getPlayerMover().saveTo(tag);
                movers.put(player.getUUID(),
                        new Listing(ChangedRegistry.PLAYER_MOVER.get().getID(ext.getPlayerMover().parent), tag));
            } else {
                movers.put(player.getUUID(),
                        new Listing(-1, new CompoundTag()));
            }
        }

        public SyncMoversPacket build() {
            return new SyncMoversPacket(movers);
        }

        public static SyncMoversPacket of(Player player) {
            Builder builder = new Builder();
            builder.addPlayer(player);
            return builder.build();
        }
    }
}
