package net.ltxprogrammer.changed.mixin.entity;

import net.ltxprogrammer.changed.util.StackUtil;
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

import javax.annotation.Nullable;
import java.util.function.Predicate;

@Mixin(ProjectileUtil.class)
public abstract class ProjectileUtilMixin {
    @Inject(method = "getEntityHitResult(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;D)Lnet/minecraft/world/phys/EntityHitResult;", at = @At("HEAD"), cancellable = true)
    private static void getEntityHitResultNotRiding(Entity source, Vec3 p_37289_, Vec3 p_37290_, AABB aabb, Predicate<Entity> pred, double p_37293_, CallbackInfoReturnable<EntityHitResult> cir) {
        if (StackUtil.isRecursive())
            return;
        ProjectileUtil.getEntityHitResult(source, p_37289_, p_37290_, aabb, pred.and(target -> {
            return target.getRootVehicle() != source.getRootVehicle() || target.canRiderInteract();
        }), p_37293_);
    }
}
