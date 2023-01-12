package net.ltxprogrammer.changed.network.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

public class MountLatexPacket implements ChangedPacket {
    private final UUID entity;
    private final UUID mount;

    public MountLatexPacket(UUID entity, UUID mount) {
        this.entity = entity;
        this.mount = mount;
    }

    public MountLatexPacket(FriendlyByteBuf buffer) {
        this.entity = buffer.readUUID();
        this.mount = buffer.readUUID();
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeUUID(entity);
        buffer.writeUUID(mount);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        if (context.getDirection().getReceptionSide().isClient()) {
            ClientLevel level = Minecraft.getInstance().level;
            Objects.requireNonNull(level);

            if (entity.equals(mount))
                level.getPlayerByUUID(entity).stopRiding();
            else
                level.getPlayerByUUID(entity).startRiding(level.getPlayerByUUID(mount));
        }
    }
}
