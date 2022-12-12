package net.ltxprogrammer.changed.network.packet;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
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

import java.util.*;
import java.util.function.Supplier;

public class QueryTransfurPacket implements ChangedPacket {
    private final List<UUID> changedForms;
    private static final ResourceLocation NO_FORM = Changed.modResource("no_form");

    public QueryTransfurPacket(List<UUID> changedForms) {
        this.changedForms = changedForms;
    }

    public QueryTransfurPacket(FriendlyByteBuf buffer) {
        this.changedForms = buffer.readList(FriendlyByteBuf::readUUID);
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeCollection(changedForms, FriendlyByteBuf::writeUUID);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        if (context.getDirection().getReceptionSide().isServer()) { // Mirror packet
            ServerPlayer sender = context.getSender();
            if (sender != null) {
                SyncTransfurPacket.Builder builder = new SyncTransfurPacket.Builder();
                changedForms.forEach(uuid -> {
                    Player player = sender.level.getPlayerByUUID(uuid);
                    if (player != null)
                        builder.addPlayer(player);
                });
                Changed.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(context::getSender), builder.build());
            }
            context.setPacketHandled(true);
        }
    }

    public static class Builder {
        private final List<UUID> changedForms = new ArrayList<>();

        public void addPlayer(Player player) {
            changedForms.add(player.getUUID());
        }

        public QueryTransfurPacket build() {
            return new QueryTransfurPacket(changedForms);
        }

        public static QueryTransfurPacket of(Player player) {
            Builder builder = new Builder();
            builder.addPlayer(player);
            return builder.build();
        }
    }
}
