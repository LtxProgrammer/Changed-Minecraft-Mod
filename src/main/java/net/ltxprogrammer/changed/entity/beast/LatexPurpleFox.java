package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.PowderSnowWalkable;
import net.ltxprogrammer.changed.init.ChangedParticles;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class LatexPurpleFox extends AbstractLatexWolf implements PowderSnowWalkable {
    public LatexPurpleFox(EntityType<? extends LatexPurpleFox> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    public ChangedParticles.Color3 getDripColor() {
        return ChangedParticles.Color3.getColor("#cebbe8");
    }
}
