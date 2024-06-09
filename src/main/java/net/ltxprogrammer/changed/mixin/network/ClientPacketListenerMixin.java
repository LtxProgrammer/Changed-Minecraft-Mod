package net.ltxprogrammer.changed.mixin.network;

import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {
    @Redirect(method = "handleEntityLinkPacket", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;getEntity(I)Lnet/minecraft/world/entity/Entity;"))
    protected Entity replaceWithVariantEntity(ClientLevel level, int id) {
        final var entity = level.getEntity(id);
        final var variant = ProcessTransfur.getPlayerTransfurVariant(EntityUtil.playerOrNull(entity));
        if (variant != null)
            return variant.getChangedEntity();
        return entity;
    }
}
