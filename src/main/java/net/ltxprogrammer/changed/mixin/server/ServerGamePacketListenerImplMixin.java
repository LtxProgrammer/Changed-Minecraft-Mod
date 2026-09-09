package net.ltxprogrammer.changed.mixin.server;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.ability.active.GrabEntityAbility;
import net.ltxprogrammer.changed.ability.active.spider.WallClimbAbility;
import net.ltxprogrammer.changed.ability.active.spider.WallClimbAbilityInstance;
import net.ltxprogrammer.changed.init.ChangedAbilities;
import net.minecraft.network.TickablePacketListener;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.network.ServerPlayerConnection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin implements ServerPlayerConnection, TickablePacketListener, ServerGamePacketListener {
    @WrapOperation(method = { "tick", "handleMovePlayer" },
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;isPassenger()Z"))
    public boolean changed$shouldIgnoreFloating(ServerPlayer instance, Operation<Boolean> original) {
        WallClimbAbilityInstance wallClimb = AbstractAbility.getAbilityInstance(instance, ChangedAbilities.WALL_CLIMB.get());
        if (wallClimb != null && wallClimb.isActive())
            return true;
        return GrabEntityAbility.isEntityNoControl(instance) || original.call(instance);
    }
}
