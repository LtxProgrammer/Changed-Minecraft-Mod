package net.ltxprogrammer.changed.network.packet;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import oshi.util.tuples.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

public class SyncTransfurPacket implements ChangedPacket {
    private final Map<UUID, ResourceLocation> changedForms;
    private static final ResourceLocation NO_FORM = Changed.modResource("no_form");

    public SyncTransfurPacket(Map<UUID, ResourceLocation> changedForms) {
        this.changedForms = changedForms;
    }

    public SyncTransfurPacket(FriendlyByteBuf buffer) {
        this.changedForms = new HashMap<>();
        buffer.readList(next ->
                new Pair<>(next.readUUID(), next.readResourceLocation())).forEach(pair ->
                    changedForms.put(pair.getA(), pair.getB()));
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeCollection(changedForms.entrySet(), (next, form) -> { next.writeUUID(form.getKey()); next.writeResourceLocation(form.getValue()); });
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        if (context.getDirection().getReceptionSide().isClient()) {
            ClientLevel level = Minecraft.getInstance().level;
            Objects.requireNonNull(level);
            changedForms.forEach((uuid, form) -> {
                Player player = level.getPlayerByUUID(uuid);
                ProcessTransfur.setPlayerLatexVariantNamed(player, form);
                player.refreshDimensions();
            });
            context.setPacketHandled(true);
        }
    }

    public static class Builder {
        private final Map<UUID, ResourceLocation> changedForms = new HashMap<>();

        public void addPlayer(Player player) {
            var variant = ProcessTransfur.getPlayerLatexVariant(player);
            changedForms.put(player.getUUID(), variant != null ? variant.getFormId() : NO_FORM);
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
