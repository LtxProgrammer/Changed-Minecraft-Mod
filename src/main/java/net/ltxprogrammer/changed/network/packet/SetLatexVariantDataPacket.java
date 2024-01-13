package net.ltxprogrammer.changed.network.packet;

import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.ltxprogrammer.changed.util.UniversalDist;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

public class SetLatexVariantDataPacket implements ChangedPacket {
    private final int id;
    @Nullable
    private final List<SynchedEntityData.DataItem<?>> packedItems;

    public SetLatexVariantDataPacket(int id, SynchedEntityData data, boolean clearDirty) {
        this.id = id;
        if (clearDirty) {
            this.packedItems = data.getAll();
            data.clearDirty();
        } else {
            this.packedItems = data.packDirty();
        }

    }

    public SetLatexVariantDataPacket(FriendlyByteBuf buffer) {
        this.id = buffer.readVarInt();
        this.packedItems = SynchedEntityData.unpack(buffer);
    }

    public void write(FriendlyByteBuf p_133158_) {
        p_133158_.writeVarInt(this.id);
        SynchedEntityData.pack(this.packedItems, p_133158_);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        var context = contextSupplier.get();
        if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
            var player = UniversalDist.getLevel().getEntity(this.id);

            ProcessTransfur.ifPlayerLatex(EntityUtil.playerOrNull(player), variant -> {
                if (packedItems == null)
                    return;
                variant.getLatexEntity().getEntityData().assignValues(packedItems);
            });
            context.setPacketHandled(true);
        }
    }

    @Nullable
    public List<SynchedEntityData.DataItem<?>> getUnpackedData() {
        return this.packedItems;
    }

    public int getId() {
        return this.id;
    }
}
