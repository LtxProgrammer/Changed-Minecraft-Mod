package net.ltxprogrammer.changed.network;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.ability.tree.AbilityTrees;
import net.ltxprogrammer.changed.block.CustomFallable;
import net.ltxprogrammer.changed.entity.AccessoryEntities;
import net.ltxprogrammer.changed.network.packet.*;
import net.ltxprogrammer.changed.network.packet.debugger.FacilityAddPiecesPayload;
import net.ltxprogrammer.changed.util.UniversalDist;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.common.util.LogicalSidedProvider;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.concurrent.CompletableFuture;
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
        addNetworkMessage(GrabEntityPacket.class, GrabEntityPacket::new);
        addNetworkMessage(GrabEntityPacket.GrabKeyState.class, GrabEntityPacket.GrabKeyState::new);
        addNetworkMessage(GrabEntityPacket.EscapeKeyState.class, GrabEntityPacket.EscapeKeyState::new);
        addNetworkMessage(GrabEntityPacket.SyncGrabStrength.class, GrabEntityPacket.SyncGrabStrength::new);
        addNetworkMessage(GrabEntityPacket.AnnounceEscapeSeed.class, GrabEntityPacket.AnnounceEscapeSeed::new);
        addNetworkMessage(MountTransfurPacket.class, MountTransfurPacket::new);
        addNetworkMessage(SyncSwitchPacket.class, SyncSwitchPacket::new);
        addNetworkMessage(SyncTransfurPacket.class, SyncTransfurPacket::new);
        addNetworkMessage(SyncTransfurProgressPacket.class, SyncTransfurProgressPacket::new);
        addNetworkMessage(QueryTransfurPacket.class, QueryTransfurPacket::new);
        addNetworkMessage(VariantAbilityActivate.class, VariantAbilityActivate::new);
        addNetworkMessage(SyncVariantAbilityPacket.class, SyncVariantAbilityPacket::new);
        addNetworkMessage(MenuUpdatePacket.class, MenuUpdatePacket::new);
        addNetworkMessage(EmotePacket.class, EmotePacket::new);
        addNetworkMessage(SyncMoversPacket.class, SyncMoversPacket::new);
        addNetworkMessage(ServerboundSetGluBlockPacket.class, ServerboundSetGluBlockPacket::new);
        addNetworkMessage(BasicPlayerInfoPacket.class, BasicPlayerInfoPacket::new);
        addNetworkMessage(SetTransfurVariantDataPacket.class, SetTransfurVariantDataPacket::new);
        addNetworkMessage(TugCameraPacket.class, TugCameraPacket::new);
        addNetworkMessage(ExtraJumpKeybind.class, ExtraJumpKeybind::buffer, ExtraJumpKeybind::new, ExtraJumpKeybind::handler);
        addNetworkMessage(CustomFallable.UpdateFallingBlockEntityData.class, CustomFallable.UpdateFallingBlockEntityData::new);
        addNetworkMessage(SeatEntityInfoPacket.class, SeatEntityInfoPacket::new);
        addNetworkMessage(TransfurEntityEventPacket.class, TransfurEntityEventPacket::new);
        addNetworkMessage(AbilityPayloadPacket.class, AbilityPayloadPacket::new);
        addNetworkMessage(MultiRotateHeadPacket.class, MultiRotateHeadPacket::new);
        addNetworkMessage(AnimationEventPacket.class, AnimationEventPacket::new);
        addNetworkMessage(AccessoryEntities.SyncPacket.class, AccessoryEntities.SyncPacket::new);
        addNetworkMessage(AccessorySyncPacket.class, AccessorySyncPacket::new);
        addNetworkMessage(AccessoryEventPacket.class, AccessoryEventPacket::new);
        addNetworkMessage(LatexCoverUpdatePacket.class, LatexCoverUpdatePacket::new);
        addNetworkMessage(SectionLatexCoversUpdatePacket.class, SectionLatexCoversUpdatePacket::new);
        addNetworkMessage(CustomLevelEventPacket.class, CustomLevelEventPacket::new);
        addNetworkMessage(AbilityTreeMenuPacket.class, AbilityTreeMenuPacket::new);
        addNetworkMessage(AbilityTrees.SyncPacket.class, AbilityTrees.SyncPacket::new);
        addNetworkMessage(AbilityTreeSyncInstancePacket.class, AbilityTreeSyncInstancePacket::new);
        addNetworkMessage(SyncActiveNodeEffectsPacket.class, SyncActiveNodeEffectsPacket::new);

        addNetworkMessage(DebuggerPacket.class, DebuggerPacket::new);
        DebuggerPacket.registerDebugPacket(FacilityAddPiecesPayload.IDENTIFIER, FacilityAddPiecesPayload::new);
    }

    private <T> BiConsumer<T, FriendlyByteBuf> wrapEncoder(Class<T> messageType, BiConsumer<T, FriendlyByteBuf> encoder) {
        return (packet, buffer) -> {
            try {
                encoder.accept(packet, buffer);
            } catch (Exception e) {
                throw new RuntimeException("Exception while encoding " + messageType.getSimpleName() + ": " + e, e);
            }
        };
    }

    private <T> Function<FriendlyByteBuf, T> wrapDecoder(Class<T> messageType, Function<FriendlyByteBuf, T> decoder) {
        return buffer -> {
            try {
                return decoder.apply(buffer);
            } catch (Exception e) {
                throw new RuntimeException("Exception while decoding " + messageType.getSimpleName() + ": " + e, e);
            }
        };
    }

    private <T extends ChangedPacket> BiConsumer<T, Supplier<NetworkEvent.Context>> wrapHandler(Class<T> messageType, ChangedPacket.Handler<T> handler) {
        return (packet, contextSupplier) -> {
            final var context = contextSupplier.get();
            final var executor = LogicalSidedProvider.WORKQUEUE.get(context.getDirection().getReceptionSide());
            final var levelFuture = CompletableFuture.supplyAsync(() -> UniversalDist.getLevel(context), executor);
            final var future = handler.accept(packet, context, levelFuture, executor)
                    .exceptionally(error -> {
                        Changed.LOGGER.error("Exception while handling {}: {}", messageType.getSimpleName(), error);
                        return null;
                    });

            if (future.isDone())
                levelFuture.cancel(false);
        };
    }

    private <T> void addNetworkMessage(Class<T> messageType, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder,
                                              BiConsumer<T, Supplier<NetworkEvent.Context>> handler) {
        packetHandler.registerMessage(messageID++, messageType,
                wrapEncoder(messageType, encoder),
                wrapDecoder(messageType, decoder),
                handler);
    }

    private <T extends ChangedPacket> void addNetworkMessage(Class<T> messageType, Function<FriendlyByteBuf, T> ctor) {
        packetHandler.registerMessage(messageID++, messageType,
                wrapEncoder(messageType, T::write),
                wrapDecoder(messageType, ctor),
                wrapHandler(messageType, T::handle));
    }
}
