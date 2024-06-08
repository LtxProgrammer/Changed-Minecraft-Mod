package net.ltxprogrammer.changed.mixin.network;

import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundSetEntityLinkPacket;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientboundSetEntityLinkPacket.class)
public abstract class ClientboundSetEntityLinkPacketMixin implements Packet<ClientGamePacketListener> {
    @Redirect(method = "<init>(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getId()I"))
    protected int replaceWithUnderlyingPlayer(Entity entity) {
        if (entity instanceof ChangedEntity changedEntity && changedEntity.getUnderlyingPlayer() != null)
            return changedEntity.getUnderlyingPlayer().getId();

        return entity.getId();
    }
}
