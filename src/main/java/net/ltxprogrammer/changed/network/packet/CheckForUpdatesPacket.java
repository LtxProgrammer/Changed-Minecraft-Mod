package net.ltxprogrammer.changed.network.packet;

import net.ltxprogrammer.changed.util.PatreonBenefits;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CheckForUpdatesPacket implements ChangedPacket {
    private final int serverVersion;

    public CheckForUpdatesPacket(int serverVersion) {
        this.serverVersion = serverVersion;
    }

    public CheckForUpdatesPacket(FriendlyByteBuf buffer) {
        serverVersion = buffer.readInt();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeInt(serverVersion);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        if (PatreonBenefits.currentVersion == serverVersion) return;

        try {
            PatreonBenefits.checkForUpdates();
        }

        catch (Exception ex) {

        }
    }
}
