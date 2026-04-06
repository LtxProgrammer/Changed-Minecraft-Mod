package net.ltxprogrammer.changed.mixin.server;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.entity.LivingEntityDataExtension;
import net.ltxprogrammer.changed.entity.PathFinderMobDataExtension;
import net.ltxprogrammer.changed.entity.beast.DoubleHeadedEntity;
import net.ltxprogrammer.changed.network.packet.*;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(ServerEntity.class)
public abstract class ServerEntityMixin {
    @Shadow @Final private Entity entity;

    @Inject(method = "sendDirtyEntityData", at = @At("RETURN"))
    private void andSendLatexVariant(CallbackInfo ci) {
        ProcessTransfur.ifPlayerTransfurred(EntityUtil.playerOrNull(this.entity), variant -> {
            var entity = variant.getChangedEntity();

            SynchedEntityData synchedentitydata = entity.getEntityData();
            final var data = synchedentitydata.packDirty();
            if (data != null && !data.isEmpty()) {
                var packet = new SetTransfurVariantDataPacket(this.entity.getId(), data);
                Changed.PACKET_HANDLER.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> this.entity), packet);
            }
        });
    }

    @Inject(method = "sendPairingData", at = @At("RETURN"))
    private void andSendChangedData(ServerPlayer packetListener, Consumer<Packet<?>> connectionSend, CallbackInfo ci) {
        if (entity instanceof Player player) {
            BasicPlayerInfoPacket.Builder builderBPI = new BasicPlayerInfoPacket.Builder();
            if (packetListener != player) builderBPI.addPlayer(player);
            if (builderBPI.worthSending())
                connectionSend.accept(
                        Changed.PACKET_HANDLER.toVanillaPacket(builderBPI.build(), NetworkDirection.PLAY_TO_CLIENT)
                );

            SyncTransfurPacket.Builder builderTf = new SyncTransfurPacket.Builder();
            builderTf.addPlayer(player, true);
            if (builderTf.worthSending())
                connectionSend.accept(
                        Changed.PACKET_HANDLER.toVanillaPacket(builderTf.build(), NetworkDirection.PLAY_TO_CLIENT)
                );

            SyncMoversPacket.Builder builderMover = new SyncMoversPacket.Builder();
            builderMover.addPlayer(player, true);
            if (builderMover.worthSending())
                connectionSend.accept(
                    Changed.PACKET_HANDLER.toVanillaPacket(builderMover.build(), NetworkDirection.PLAY_TO_CLIENT)
                );
        }
        if (entity instanceof LivingEntityDataExtension ext) {
            ext.getAccessorySlots().ifPresent(slots -> {
                if (!slots.isEmpty())
                    connectionSend.accept(
                            Changed.PACKET_HANDLER.toVanillaPacket(new AccessorySyncPacket(this.entity.getId(), slots), NetworkDirection.PLAY_TO_CLIENT)
                    );
            });
        }
        if (entity instanceof PathFinderMobDataExtension ext && ext.isLatexAssimilated()) {
            connectionSend.accept(
                    Changed.PACKET_HANDLER.toVanillaPacket(new AssimilatedEntitySyncPacket(this.entity.getId(), true), NetworkDirection.PLAY_TO_CLIENT)
            );
        }
    }

    @Unique private int yHead2Rotp;
    @Unique private int xHead2Rotp;

    @Inject(method = "sendChanges", at = @At("RETURN"))
    private void andSendCustomChanges(CallbackInfo ci) {
        // Send double head movement
        IAbstractChangedEntity.forEitherSafe(this.entity)
                .map(IAbstractChangedEntity::getChangedEntity)
                .ifPresent(effectedEntity -> {
                    int yH2 = 0;
                    int xH2 = 0;
                    int yH3 = 0;
                    int xH3 = 0;
                    boolean send = false;

                    if (effectedEntity instanceof DoubleHeadedEntity doubleHeadedEntity) {
                        yH2 = Mth.floor(doubleHeadedEntity.getHead2YRot() * 256.0F / 360.0F);
                        if (Math.abs(yH2 - this.yHead2Rotp) >= 1)
                            send = true;
                        this.yHead2Rotp = yH2;

                        xH2 = Mth.floor(doubleHeadedEntity.getHead2XRot() * 256.0F / 360.0F);
                        if (Math.abs(xH2 - this.xHead2Rotp) >= 1)
                            send = true;
                        this.xHead2Rotp = xH2;
                    }

                    if (!send) return;

                    // Triple Head check

                    Changed.PACKET_HANDLER.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> this.entity),
                            new MultiRotateHeadPacket(this.entity, (byte)yH2, (byte)xH2, (byte)yH3, (byte)xH3));
                });
    }
}
