package net.ltxprogrammer.changed.network.packet;

import net.ltxprogrammer.changed.entity.Emote;
import net.ltxprogrammer.changed.process.ProcessEmote;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

public class EmotePacket implements ChangedPacket {
    private final UUID entity;
    private final Emote emote;

    public EmotePacket(UUID entity, Emote emote) {
        this.entity = entity;
        this.emote = emote;
    }

    public EmotePacket(FriendlyByteBuf buffer) {
        this.entity = buffer.readUUID();
        this.emote = Emote.values()[buffer.readInt()];
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeUUID(entity);
        buffer.writeInt(emote.ordinal());
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        if (context.getDirection().getReceptionSide().isClient()) {
            ClientLevel level = Minecraft.getInstance().level;
            Objects.requireNonNull(level);

            ProcessEmote.playerEmote(level.getPlayerByUUID(entity), emote);
            context.setPacketHandled(true);
        }
    }
}
