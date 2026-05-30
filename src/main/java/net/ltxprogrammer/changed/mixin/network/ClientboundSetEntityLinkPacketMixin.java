package net.ltxprogrammer.changed.mixin.network;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundSetEntityLinkPacket;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientboundSetEntityLinkPacket.class)
public abstract class ClientboundSetEntityLinkPacketMixin implements Packet<ClientGamePacketListener> {
    @WrapOperation(method = "<init>(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getId()I"))
    protected int replaceWithUnderlyingPlayer(Entity instance, Operation<Integer> original) {
        if (instance instanceof ChangedEntity changedEntity)
            return original.call(changedEntity.maybeGetUnderlying());

        return original.call(instance);
    }
}
