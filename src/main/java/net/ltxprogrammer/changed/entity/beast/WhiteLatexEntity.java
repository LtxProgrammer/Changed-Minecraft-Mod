package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.init.ChangedParticles;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.Level;

public abstract class WhiteLatexEntity extends LightLatexWolfMale {
    public WhiteLatexEntity(EntityType<? extends WhiteLatexEntity> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(5, new FloatGoal(this));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, WhiteLatexEntity.class)).setAlertOthers());
    }

    @Override
    public LatexType getLatexType() {
        return LatexType.WHITE_LATEX;
    }

    @Override
    public ChangedParticles.Color3 getDripColor() {
        return ChangedParticles.Color3.WHITE;
    }
}
