package net.ltxprogrammer.changed.mixin.network;

import net.ltxprogrammer.changed.block.StasisChamber;
import net.ltxprogrammer.changed.entity.SeatEntity;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {
    @Shadow private ClientLevel level;

    @Redirect(method = "handleEntityLinkPacket", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;getEntity(I)Lnet/minecraft/world/entity/Entity;"))
    protected Entity replaceWithVariantEntity(ClientLevel level, int id) {
        final var entity = level.getEntity(id);
        final var variant = ProcessTransfur.getPlayerTransfurVariant(EntityUtil.playerOrNull(entity));
        if (variant != null)
            return variant.getChangedEntity();
        return entity;
    }

    @Inject(method = "handleSetEntityPassengersPacket", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;setOverlayMessage(Lnet/minecraft/network/chat/Component;Z)V"), cancellable = true)
    protected void hideDismountMessage(ClientboundSetPassengersPacket packet, CallbackInfo ci) {
        if (this.level.getEntity(packet.getVehicle()) instanceof SeatEntity seatEntity && !seatEntity.canSeatedDismount())
            ci.cancel();
    }
}
