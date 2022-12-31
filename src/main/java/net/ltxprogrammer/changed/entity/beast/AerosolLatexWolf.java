package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.OrganicLatex;
import net.ltxprogrammer.changed.init.ChangedParticles;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class AerosolLatexWolf extends AbstractLatexWolf implements OrganicLatex {
    public AerosolLatexWolf(EntityType<? extends AerosolLatexWolf> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    public ChangedParticles.Color3 getDripColor() {
        return level.random.nextInt(10) > 3 ? ChangedParticles.Color3.BROWN : ChangedParticles.Color3.WHITE;
    }
}
