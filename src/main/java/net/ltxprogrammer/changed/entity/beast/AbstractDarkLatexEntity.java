package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.init.ChangedParticles;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public abstract class AbstractDarkLatexEntity extends AbstractLatexWolf implements DarkLatexEntity {

    public AbstractDarkLatexEntity(EntityType<? extends AbstractLatexWolf> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    public boolean isMaskless() {
        return false;
    }

    @Override
    public LatexType getLatexType() {
        return LatexType.DARK_LATEX;
    }

    @Override
    public ChangedParticles.Color3 getHairColor(int layer) {
        return ChangedParticles.Color3.DARK;
    }

    @Override
    protected boolean targetSelectorTest(LivingEntity livingEntity) {
        if (!this.isMaskless()) {// Check if masked DL can see entity
            if (livingEntity.distanceToSqr(this) <= 1.0)
                return super.targetSelectorTest(livingEntity);
            if (getLevelBrightnessAt(livingEntity.blockPosition()) >= 5)
                return super.targetSelectorTest(livingEntity);

            var delta = livingEntity.getDeltaMovement();
            var xyMovement = delta.subtract(0, delta.y, 0);
            if (livingEntity.isCrouching() || xyMovement.lengthSqr() < Mth.EPSILON)
                return false;
        }

        return super.targetSelectorTest(livingEntity);
    }
}
