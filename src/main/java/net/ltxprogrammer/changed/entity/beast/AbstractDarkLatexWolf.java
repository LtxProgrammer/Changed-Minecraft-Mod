package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.GenderedLatexEntity;
import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.init.ChangedParticles;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.level.Level;

public abstract class AbstractDarkLatexWolf extends GenderedLatexEntity implements DarkLatexEntity {
    public AbstractDarkLatexWolf(EntityType<? extends AbstractDarkLatexWolf> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    public int getTicksRequiredToFreeze() { return 240; }

    @Override
    public LatexType getLatexType() {
        return LatexType.DARK_LATEX;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(5, new FloatGoal(this));
    }

    @Override
    public ChangedParticles.Color3 getDripColor() {
        return ChangedParticles.Color3.DARK;
    }

    @Override
    public boolean isMaskless() {
        return false;
    }
}
