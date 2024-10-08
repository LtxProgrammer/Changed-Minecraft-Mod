package net.ltxprogrammer.changed.entity.ai;

import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.TamableLatexEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

import java.util.EnumSet;

public class LatexOwnerHurtByTargetGoal<T extends ChangedEntity & TamableLatexEntity> extends TargetGoal {
    private final T tameAnimal;
    private LivingEntity ownerLastHurtBy;
    private int timestamp;

    public LatexOwnerHurtByTargetGoal(T p_26107_) {
        super(p_26107_, false);
        this.tameAnimal = p_26107_;
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    public boolean canUse() {
        if (this.tameAnimal.isTame()) {
            if (!(this.tameAnimal.getOwner() instanceof LivingEntity livingentity)) {
                return false;
            } else {
                this.ownerLastHurtBy = livingentity.getLastHurtByMob();
                int i = livingentity.getLastHurtByMobTimestamp();
                return i != this.timestamp && this.canAttack(this.ownerLastHurtBy, TargetingConditions.DEFAULT) && this.tameAnimal.wantsToAttack(this.ownerLastHurtBy, livingentity);
            }
        } else {
            return false;
        }
    }

    public void start() {
        this.mob.setTarget(this.ownerLastHurtBy);
        if (this.tameAnimal.getOwner() instanceof LivingEntity livingentity) {
            this.timestamp = livingentity.getLastHurtByMobTimestamp();
        }

        super.start();
    }
}