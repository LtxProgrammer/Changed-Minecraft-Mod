package net.ltxprogrammer.changed.network.packet;

import net.ltxprogrammer.changed.data.Signaler;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncTransfurProgressPacket implements ChangedPacket {
    public static final Signaler<ProcessTransfur.TransfurProgress> SIGNAL = new Signaler<>();
    private final ProcessTransfur.TransfurProgress progress;

    public SyncTransfurProgressPacket(ProcessTransfur.TransfurProgress progress) {
        this.progress = progress;
    }

    public SyncTransfurProgressPacket(FriendlyByteBuf buffer) {
        this.progress = new ProcessTransfur.TransfurProgress(buffer.readInt(), buffer.readResourceLocation());
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeInt(progress.ticks());
        buffer.writeResourceLocation(progress.type());
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();

        if (context.getDirection().getReceptionSide().isClient()) {
            SIGNAL.invoke(progress);
            context.setPacketHandled(true);
        }
    }
}
