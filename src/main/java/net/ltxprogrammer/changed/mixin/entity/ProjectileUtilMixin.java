package net.ltxprogrammer.changed.mixin.entity;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.entity.LivingEntityDataExtension;
import net.ltxprogrammer.changed.entity.SeatEntity;
import net.ltxprogrammer.changed.init.ChangedAbilities;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.ltxprogrammer.changed.world.LatexCoverGetter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Predicate;

@Mixin(ProjectileUtil.class)
public abstract class ProjectileUtilMixin {
    @Unique
    private static boolean changed$targetIsNotVehicleOf(Entity source, Entity target) {
        return target.getRootVehicle() != source.getRootVehicle() || target.canRiderInteract();
    }

    @Unique
    private static boolean changed$targetIsNotGrabbedBy(Entity source, Entity target) {
        return !(target instanceof LivingEntityDataExtension ext) || ext.getGrabbedBy() != source;
    }

    @WrapMethod(method = "getEntityHitResult(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;D)Lnet/minecraft/world/phys/EntityHitResult;")
    private static EntityHitResult changed$filterEntityHitResults(Entity source, Vec3 start, Vec3 end, AABB bounds, Predicate<Entity> pred, double p_37293_, Operation<EntityHitResult> original) {
        Predicate<Entity> combinedPredicate = pred.and(target ->
                changed$targetIsNotVehicleOf(source, target) &&
                changed$targetIsNotGrabbedBy(source, target)
        );

        return original.call(source, start, end, bounds, combinedPredicate, p_37293_);
    }

    @WrapOperation(method = "getHitResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;clip(Lnet/minecraft/world/level/ClipContext;)Lnet/minecraft/world/phys/BlockHitResult;"))
    private static BlockHitResult extendedPOVHitResult(Level instance, ClipContext clipContext, Operation<BlockHitResult> original) {
        return LatexCoverGetter.wrap(instance).clip(clipContext, original.call(instance, clipContext));
    }
}
