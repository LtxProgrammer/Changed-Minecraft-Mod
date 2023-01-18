package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.init.ChangedParticles;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class LatexSniperDog extends AbstractLatexWolf {
    public LatexSniperDog(EntityType<? extends LatexSniperDog> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }


    public ChangedParticles.Color3 getDripColor() {
        return ChangedParticles.Color3.getColor("#ef8f44");
    }

}