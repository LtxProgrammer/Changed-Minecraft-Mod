package net.ltxprogrammer.changed.mixin.entity;

import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.function.Predicate;

@Mixin(NearestAttackableTargetGoal.class)
public abstract class NearestAttackableTargetGoalMixin<T extends LivingEntity> {
    @Shadow protected TargetingConditions targetConditions;

    @Inject(method = "<init>(Lnet/minecraft/world/entity/Mob;Ljava/lang/Class;IZZLjava/util/function/Predicate;)V", at = @At("RETURN"))
    public void noTargetScary(Mob self, Class<T> target, int interval, boolean mustSee, boolean mustReach, @Nullable Predicate<LivingEntity> predicate, CallbackInfo ci) {
        if (!(self instanceof PathfinderMob pathfinderMob)) return;

        final Predicate<LivingEntity> scaryPredicate = livingEntity -> {
            return IAbstractChangedEntity.forEitherSafe(livingEntity)
                    .map(changedEntity -> {
                        TransfurVariant variant = changedEntity.getSelfVariant();
                        if (variant == null || variant.scares == null)
                            return true;
                        return !variant.scares.test(changedEntity.getChangedEntity(), pathfinderMob);
                    }).orElse(true);
        };

        final Predicate<LivingEntity> neutralPredicate = livingEntity -> {
            if (livingEntity instanceof ChangedEntity) {
                return ProcessTransfur.isGoo(livingEntity);
            }

            return true;
        };

        final Predicate<LivingEntity> custom = scaryPredicate.and(neutralPredicate);

        this.targetConditions = this.targetConditions.selector(predicate != null ? predicate.and(custom) : custom);
    }
}
