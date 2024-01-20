package net.ltxprogrammer.changed.mixin.entity;

import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.entity.LivingEntityDataExtension;
import net.ltxprogrammer.changed.init.ChangedAbilities;
import net.ltxprogrammer.changed.util.StackUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

@Mixin(ProjectileUtil.class)
public abstract class ProjectileUtilMixin {
    @Unique
    private static Predicate<Entity> targetIsNotVehicleOf(Entity source) {
        return target -> target.getRootVehicle() != source.getRootVehicle() || target.canRiderInteract();
    }

    @Unique
    private static Predicate<Entity> targetIsNotGrabbedBy(Entity source) {
        return target -> !(target instanceof LivingEntityDataExtension ext) || ext.getGrabbedBy() != source;
    }

    @Unique
    private static boolean targetIsNotInSuit(Entity target) {
        if (!(target instanceof LivingEntityDataExtension ext))
            return true;
        if (ext.getGrabbedBy() == null)
            return true;
        var ability = AbstractAbility.getAbilityInstance(ext.getGrabbedBy(), ChangedAbilities.GRAB_ENTITY_ABILITY.get());
        if (ability == null)
            return true;
        return !ability.suited;
    }

    @Inject(method = "getEntityHitResult(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;D)Lnet/minecraft/world/phys/EntityHitResult;", at = @At("HEAD"), cancellable = true)
    private static void getEntityHitResultNotRidingOrGrabbed(Entity source, Vec3 p_37289_, Vec3 p_37290_, AABB aabb, Predicate<Entity> pred, double p_37293_, CallbackInfoReturnable<EntityHitResult> cir) {
        if (StackUtil.isRecursive())
            return;
        EntityHitResult result = ProjectileUtil.getEntityHitResult(source, p_37289_, p_37290_, aabb, pred
                .and(targetIsNotVehicleOf(source))
                .and(targetIsNotGrabbedBy(source))
                .and(ProjectileUtilMixin::targetIsNotInSuit), p_37293_);
        cir.setReturnValue(result);
    }
}
