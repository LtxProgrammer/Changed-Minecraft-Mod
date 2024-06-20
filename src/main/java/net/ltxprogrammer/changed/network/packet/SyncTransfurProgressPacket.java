package net.ltxprogrammer.changed.network.packet;

import net.ltxprogrammer.changed.data.BiSignaler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class SyncTransfurProgressPacket implements ChangedPacket {
    public static final BiSignaler<UUID, Float> SIGNAL = new BiSignaler<>();
    private final UUID uuid;
    private final float progress;

    public SyncTransfurProgressPacket(UUID uuid, float progress) {
        this.uuid = uuid;
        this.progress = progress;
    }

    public SyncTransfurProgressPacket(FriendlyByteBuf buffer) {
        this.uuid = buffer.readUUID();
        this.progress = buffer.readFloat();
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeUUID(uuid);
        buffer.writeFloat(progress);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();

        if (context.getDirection().getReceptionSide().isClient()) {
            SIGNAL.invoke(uuid, progress);
            context.setPacketHandled(true);
        }
    }
}
