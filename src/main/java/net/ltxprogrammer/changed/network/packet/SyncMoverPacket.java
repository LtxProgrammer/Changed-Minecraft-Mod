package net.ltxprogrammer.changed.network.packet;

import net.ltxprogrammer.changed.entity.PlayerDataExtension;
import net.ltxprogrammer.changed.entity.PlayerMoverInstance;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.util.UniversalDist;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.UUID;
import java.util.function.Supplier;

public class SyncMoverPacket implements ChangedPacket {
    private final UUID playerUUID;
    @Nullable private final PlayerMoverInstance<?> mover;

    public SyncMoverPacket(@NotNull Player player) {
        this.playerUUID = player.getUUID();
        if (!(player instanceof PlayerDataExtension extension))
            throw new RuntimeException("Player not extended");
        this.mover = extension.getPlayerMover();
    }

    public SyncMoverPacket(@NotNull Player player, @Nullable PlayerMoverInstance<?> mover) {
        this.playerUUID = player.getUUID();
        this.mover = mover;
    }

    public SyncMoverPacket(FriendlyByteBuf buffer) {
        this.playerUUID = buffer.readUUID();
        var parent = ChangedRegistry.PLAYER_MOVER.get().getValue(buffer.readInt());
        if (parent == null) {
            this.mover = null;
            return;
        }

        this.mover = parent.createInstance();
        if (this.mover != null) {
            this.mover.readFrom(buffer.readNbt());
        }
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeUUID(playerUUID);
        if (mover != null) {
            buffer.writeInt(ChangedRegistry.PLAYER_MOVER.get().getID(mover.parent));
            CompoundTag data = new CompoundTag();
            mover.saveTo(data);
            buffer.writeNbt(data);
        }

        else {
            buffer.writeInt(-1);
        }
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        if (context.getDirection().getReceptionSide().isClient()) {
            Player player = UniversalDist.getLevel().getPlayerByUUID(playerUUID);
            if (!(player instanceof PlayerDataExtension extension))
                return;

            extension.setPlayerMover(mover);
            context.setPacketHandled(true);
        }
    }
}
