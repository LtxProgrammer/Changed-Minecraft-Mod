package net.ltxprogrammer.changed.entity.beast.boss;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class BehemothHandRight extends BehemothHand {
    public BehemothHandRight(EntityType<? extends BehemothHandRight> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    public void knockback(double p_147241_, double p_147242_, double p_147243_) {}

    @Override
    public boolean isPushable() {
        return false;
    }
}