package net.ltxprogrammer.changed.mixin;

import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Inject(method = "updateFallFlying", at = @At("HEAD"), cancellable = true)
    private void updateFallFlying(CallbackInfo ci) {
        if (this.level.isClientSide) return;
        if ((LivingEntity)(Object)this instanceof Player player) {
            LatexVariant<?> variant = ProcessTransfur.getPlayerLatexVariant(player);
            if (variant != null && variant.canGlide()) {
                this.setSharedFlag(7, player.isFallFlying() && !player.isOnGround() && !player.isPassenger() && !player.hasEffect(MobEffects.LEVITATION));
                ci.cancel();
            }
        }
    }
}
