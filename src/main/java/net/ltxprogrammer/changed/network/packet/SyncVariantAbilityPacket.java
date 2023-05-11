package net.ltxprogrammer.changed.network.packet;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.UUID;
import java.util.function.Supplier;

public class SyncVariantAbilityPacket implements ChangedPacket {
    private final int id;
    private final CompoundTag data;
    private final UUID playerUUID; // Field is ignored when server receives packet

    public SyncVariantAbilityPacket(int id, CompoundTag data) {
        this.id = id;
        this.data = data;
        this.playerUUID = UUID.randomUUID();
    }

    public SyncVariantAbilityPacket(int id, CompoundTag data, UUID uuid) {
        this.id = id;
        this.data = data;
        this.playerUUID = uuid;
    }

    public SyncVariantAbilityPacket(FriendlyByteBuf buffer) {
        this.id = buffer.readInt();
        this.data = buffer.readNbt();
        this.playerUUID = buffer.readUUID();
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeInt(id);
        buffer.writeNbt(data);
        buffer.writeUUID(playerUUID);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        if (context.getDirection().getReceptionSide().isServer()) { // Mirror packet
            ServerPlayer sender = context.getSender();
            ProcessTransfur.ifPlayerLatex(sender, variant -> {
                var ability = ChangedRegistry.ABILITY.get().getValue(id);
                Changed.PACKET_HANDLER.send(PacketDistributor.ALL.noArg(), new SyncVariantAbilityPacket(id, data, sender.getUUID()));
                if (variant.abilityInstances.containsKey(ability))
                    variant.abilityInstances.get(ability).readData(data);
            });
            context.setPacketHandled(true);
        }

        else {
            Player affectedPlayer = Minecraft.getInstance().level.getPlayerByUUID(playerUUID);
            ProcessTransfur.ifPlayerLatex(Minecraft.getInstance().level.getPlayerByUUID(playerUUID), variant -> {
                var ability = ChangedRegistry.ABILITY.get().getValue(id);
                if (variant.abilityInstances.containsKey(ability))
                    variant.abilityInstances.get(ability).readData(data);
            });
            context.setPacketHandled(true);
        }
    }
}
