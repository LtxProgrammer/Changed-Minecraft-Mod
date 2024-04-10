package net.ltxprogrammer.changed.network;

import net.ltxprogrammer.changed.block.CustomFallable;
import net.ltxprogrammer.changed.network.packet.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ChangedPackets {
    private final SimpleChannel packetHandler;
    private int messageID = 0;

    public ChangedPackets(SimpleChannel packetHandler) {
        this.packetHandler = packetHandler;
    }

    public void registerPackets() {
        addNetworkMessage(CheckForUpdatesPacket.class, CheckForUpdatesPacket::new);
        addNetworkMessage(GrabEntityPacket.class, GrabEntityPacket::new);
        addNetworkMessage(GrabEntityPacket.KeyState.class, GrabEntityPacket.KeyState::new);
        addNetworkMessage(MountTransfurPacket.class, MountTransfurPacket::new);
        addNetworkMessage(SyncSwitchPacket.class, SyncSwitchPacket::new);
        addNetworkMessage(SyncTransfurPacket.class, SyncTransfurPacket::new);
        addNetworkMessage(SyncTransfurProgressPacket.class, SyncTransfurProgressPacket::new);
        addNetworkMessage(QueryTransfurPacket.class, QueryTransfurPacket::new);
        addNetworkMessage(VariantAbilityActivate.class, VariantAbilityActivate::new);
        addNetworkMessage(SyncVariantAbilityPacket.class, SyncVariantAbilityPacket::new);
        addNetworkMessage(MenuUpdatePacket.class, MenuUpdatePacket::new);
        addNetworkMessage(EmotePacket.class, EmotePacket::new);
        addNetworkMessage(SyncMoverPacket.class, SyncMoverPacket::new);
        addNetworkMessage(ServerboundSetGluBlockPacket.class, ServerboundSetGluBlockPacket::new);
        addNetworkMessage(BasicPlayerInfoPacket.class, BasicPlayerInfoPacket::new);
        addNetworkMessage(SetTransfurVariantDataPacket.class, SetTransfurVariantDataPacket::new);
        addNetworkMessage(TugCameraPacket.class, TugCameraPacket::new);
        addNetworkMessage(ExtraJumpKeybind.class, ExtraJumpKeybind::buffer, ExtraJumpKeybind::new, ExtraJumpKeybind::handler);
        addNetworkMessage(CustomFallable.UpdateFallingBlockEntityData.class, CustomFallable.UpdateFallingBlockEntityData::new);
    }

    private <T> void addNetworkMessage(Class<T> messageType, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder,
                                              BiConsumer<T, Supplier<NetworkEvent.Context>> messageConsumer) {
        packetHandler.registerMessage(messageID++, messageType, encoder, decoder, messageConsumer);
    }

    private <T extends ChangedPacket> void addNetworkMessage(Class<T> messageType, Function<FriendlyByteBuf, T> ctor) {
        addNetworkMessage(messageType, T::write, ctor, T::handle);
    }
}
