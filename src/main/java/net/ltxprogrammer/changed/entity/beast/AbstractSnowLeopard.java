package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.GenderedEntity;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.entity.PowderSnowWalkable;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public abstract class AbstractSnowLeopard extends LatexEntity implements GenderedEntity, PowderSnowWalkable {
    public AbstractSnowLeopard(EntityType<? extends AbstractSnowLeopard> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    public boolean causeFallDamage(float p_148859_, float p_148860_, DamageSource p_148861_) { return false; }

    @Override
    public int getTicksRequiredToFreeze() { return 420; }

    @Override
    public Color3 getDripColor() {
        return level.random.nextInt(10) > 3 ? Color3.GRAY : Color3.WHITE;
    }

    @Override
    public Color3 getHairColor(int layer) {
        return Color3.WHITE;
    }

    public LatexType getLatexType() {
        return LatexType.NEUTRAL;
    }
}
