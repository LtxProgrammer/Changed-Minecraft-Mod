package net.ltxprogrammer.changed.network.packet;

import com.mojang.datafixers.util.Pair;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.BasicPlayerInfo;
import net.ltxprogrammer.changed.entity.PlayerDataExtension;
import net.ltxprogrammer.changed.util.UniversalDist;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

public class BasicPlayerInfoPacket implements ChangedPacket {
    record Listing(BasicPlayerInfo info) {
        static Listing fromStream(FriendlyByteBuf buf) {
            return new Listing(new BasicPlayerInfo(buf.readNbt()));
        }

        void toStream(FriendlyByteBuf buf) {
            var tag = new CompoundTag();
            info.save(tag);
            buf.writeNbt(tag);
        }
    }

    private final Map<UUID, Listing> playerInfos;
    public BasicPlayerInfoPacket(Map<UUID, Listing> playerInfos) {
        this.playerInfos = playerInfos;
    }

    public BasicPlayerInfoPacket(FriendlyByteBuf buffer) {
        this.playerInfos = new HashMap<>();
        buffer.readList(next ->
                new Pair<>(next.readUUID(), Listing.fromStream(next))).forEach(pair ->
                playerInfos.put(pair.getFirst(), pair.getSecond()));
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeCollection(playerInfos.entrySet(), (next, form) -> { next.writeUUID(form.getKey()); form.getValue().toStream(next); });
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        if (context.getDirection().getReceptionSide().isClient()) {
            Level level = UniversalDist.getLevel();

            if (!playerInfos.isEmpty()) {
                Objects.requireNonNull(level);
                playerInfos.forEach((uuid, listing) -> {
                    var player = level.getPlayerByUUID(uuid);
                    if (player instanceof PlayerDataExtension ext && !UniversalDist.isLocalPlayer(player)) {
                        ext.getBasicPlayerInfo().copyFrom(listing.info);
                    }
                });
                context.setPacketHandled(true);
            }

            else {
                Changed.PACKET_HANDLER.sendToServer(BasicPlayerInfoPacket.Builder.of(UniversalDist.getLocalPlayer()));
                context.setPacketHandled(true);
            }
        }
        else if (context.getDirection().getReceptionSide().isServer()) { // Mirror packet
            ServerPlayer sender = context.getSender();
            if (sender != null) {
                if (sender instanceof PlayerDataExtension ext && playerInfos.containsKey(sender.getUUID()))
                    ext.getBasicPlayerInfo().copyFrom(playerInfos.get(sender.getUUID()).info); // Keep player info state
                Changed.PACKET_HANDLER.send(PacketDistributor.ALL.noArg(), this);
            }
            context.setPacketHandled(true);
        }
    }

    public static class Builder {
        private final Map<UUID, Listing> playerInfos = new HashMap<>();

        public void addPlayer(Player player) {
            if (player instanceof PlayerDataExtension ext) {
                playerInfos.put(player.getUUID(), new Listing(ext.getBasicPlayerInfo()));
            }
        }

        public BasicPlayerInfoPacket build() {
            return new BasicPlayerInfoPacket(playerInfos);
        }

        public static BasicPlayerInfoPacket of(Player player) {
            Builder builder = new Builder();
            builder.addPlayer(player);
            return builder.build();
        }
    }
}
