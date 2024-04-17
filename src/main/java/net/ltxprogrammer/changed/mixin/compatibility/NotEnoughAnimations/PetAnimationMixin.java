package net.ltxprogrammer.changed.mixin.compatibility.NotEnoughAnimations;

import dev.tr7zw.notenoughanimations.access.PlayerData;
import dev.tr7zw.notenoughanimations.animations.hands.PetAnimation;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PetAnimation.class, remap = false)
public abstract class PetAnimationMixin {
    @Shadow private Entity targetPet;

    @Inject(method = "isValid", at = @At("RETURN"), cancellable = true)
    public void isValidForChanged(AbstractClientPlayer entity, PlayerData data, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue())
            return;

        if (entity.isCrouching()) {
            double d = 1.0D;
            Vec3 vec3 = entity.getEyePosition(0.0F);
            Vec3 vec32 = entity.getViewVector(1.0F);
            Vec3 vec33 = vec3.add(vec32.x * d, vec32.y * d, vec32.z * d);
            AABB aABB = entity.getBoundingBox().expandTowards(vec32.scale(d)).inflate(1.0D, 1.0D, 1.0D);
            EntityHitResult entHit = ProjectileUtil.getEntityHitResult(entity, vec3, vec33, aABB, (en) -> {
                return !en.isSpectator();
            }, d);
            if (entHit == null || !(entHit.getEntity() instanceof LivingEntity livingEntity))
                return;
            ProcessTransfur.getEntityVariant(livingEntity).ifPresent(variant -> {
                if (livingEntity instanceof ChangedEntity ChangedEntity && ChangedEntity.isPreventingPlayerRest(entity))
                    return;

                if (variant == TransfurVariant.DARK_LATEX_PUP) {
                    double dif = livingEntity.getY() - entity.getY();
                    if (Math.abs(dif) < 0.6D) {
                        this.targetPet = livingEntity;
                        cir.setReturnValue(true);
                    }
                }
            });
        }
    }
}
