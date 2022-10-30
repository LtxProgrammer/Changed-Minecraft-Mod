package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.init.ChangedParticles;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class LatexSilverFox extends AbstractLatexWolf {
    public LatexSilverFox(EntityType<? extends LatexSilverFox> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    public ChangedParticles.Color3 getDripColor() {
        return ChangedParticles.Color3.SILVER;
    }
}
