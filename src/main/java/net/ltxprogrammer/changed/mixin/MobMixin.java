package net.ltxprogrammer.changed.mixin;

import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.ltxprogrammer.changed.process.ProcessTransfur;
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
            self.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(self, LatexEntity.class, true));
        if (self instanceof IronGolem ironGolem)
            self.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(self, Player.class, true, livingEntity -> {
                if (ironGolem.isPlayerCreated())
                    return false;
                if (livingEntity instanceof Player player)
                    return !ProcessTransfur.isPlayerOrganic(player);
                else return false;
            }));
    }
}
