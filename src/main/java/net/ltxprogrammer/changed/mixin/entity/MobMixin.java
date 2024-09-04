package net.ltxprogrammer.changed.mixin.entity;

import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
public abstract class MobMixin extends LivingEntity {
    protected MobMixin(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    @Inject(method = "registerGoals", at = @At("HEAD"))
    protected void registerGoals(CallbackInfo info) {
        Mob self = (Mob)(Object)this;
        if (self.getType().is(ChangedTags.EntityTypes.HUMANOIDS) || self instanceof IronGolem || self instanceof Raider)
            self.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(self, ChangedEntity.class, true));
        if (self instanceof IronGolem ironGolem)
            self.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(self, Player.class, true, livingEntity -> {
                if (ironGolem.isPlayerCreated())
                    return false;
                if (livingEntity instanceof Player player)
                    return !ProcessTransfur.isPlayerNotLatex(player);
                else return false;
            }));
    }

    @Redirect(method = "dropLeash", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerChunkCache;broadcast(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/network/protocol/Packet;)V"))
    public void dropLeashForUnderlying(ServerChunkCache instance, Entity entity, Packet<?> packet) {
        if (entity instanceof ChangedEntity changedEntity && changedEntity.getUnderlyingPlayer() != null) {
            instance.broadcastAndSend(changedEntity.getUnderlyingPlayer(), packet);
        } else
            instance.broadcast(entity, packet);
    }

    @Redirect(method = "setLeashedTo", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerChunkCache;broadcast(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/network/protocol/Packet;)V"))
    public void setLeashedForUnderlying(ServerChunkCache instance, Entity entity, Packet<?> packet) {
        if (entity instanceof ChangedEntity changedEntity && changedEntity.getUnderlyingPlayer() != null) {
            instance.broadcastAndSend(changedEntity.getUnderlyingPlayer(), packet);
        } else
            instance.broadcast(entity, packet);
    }
}
