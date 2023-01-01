package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.OrganicLatex;
import net.ltxprogrammer.changed.init.ChangedParticles;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class LatexBeifeng extends AbstractLatexWolf implements DarkLatexEntity, OrganicLatex {
    public LatexBeifeng(EntityType<? extends LatexBeifeng> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    public ChangedParticles.Color3 getDripColor() {
        return level.random.nextInt(10) > 3 ? ChangedParticles.Color3.BLUE : ChangedParticles.Color3.WHITE;
    }

    @Override
    public boolean isMaskless() {
        return true;
    }
}
