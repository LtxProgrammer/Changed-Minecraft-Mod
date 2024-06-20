package net.ltxprogrammer.changed.network.packet;

import net.ltxprogrammer.changed.entity.PlayerDataExtension;
import net.ltxprogrammer.changed.util.CameraUtil;
import net.ltxprogrammer.changed.util.UniversalDist;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class TugCameraPacket implements ChangedPacket {
    private final CameraUtil.TugData tug;

    public TugCameraPacket(CameraUtil.TugData tug) {
        this.tug = tug;
    }

    public TugCameraPacket(FriendlyByteBuf buffer) {
        this.tug = CameraUtil.TugData.readFromBuffer(buffer);
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        this.tug.writeToBuffer(buffer);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        var context = contextSupplier.get();
        if (context.getDirection().getReceptionSide() != LogicalSide.CLIENT)
            return;
        if (!(UniversalDist.getLocalPlayer() instanceof PlayerDataExtension ext))
            return;
        ext.setTugData(this.tug);
        context.setPacketHandled(true);
    }
}
