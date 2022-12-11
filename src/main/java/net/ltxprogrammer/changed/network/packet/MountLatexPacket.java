package net.ltxprogrammer.changed.network.packet;

import jdk.jshell.execution.Util;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.TagUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
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
