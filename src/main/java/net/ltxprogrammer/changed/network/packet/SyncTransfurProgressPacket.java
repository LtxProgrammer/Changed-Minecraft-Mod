package net.ltxprogrammer.changed.network.packet;

import net.ltxprogrammer.changed.data.BiSignaler;
import net.ltxprogrammer.changed.data.Signaler;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class SyncTransfurProgressPacket implements ChangedPacket {
    public static final BiSignaler<UUID, ProcessTransfur.TransfurProgress> SIGNAL = new BiSignaler<>();
    private final UUID uuid;
    private final ProcessTransfur.TransfurProgress progress;

    public SyncTransfurProgressPacket(UUID uuid, ProcessTransfur.TransfurProgress progress) {
        this.uuid = uuid;
        this.progress = progress;
    }

    public SyncTransfurProgressPacket(FriendlyByteBuf buffer) {
        this.uuid = buffer.readUUID();
        this.progress = new ProcessTransfur.TransfurProgress(buffer.readInt(), buffer.readResourceLocation());
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeUUID(uuid);
        buffer.writeInt(progress.ticks());
        buffer.writeResourceLocation(progress.type());
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();

        if (context.getDirection().getReceptionSide().isClient()) {
            SIGNAL.invoke(uuid, progress);
            context.setPacketHandled(true);
        }
    }
}
