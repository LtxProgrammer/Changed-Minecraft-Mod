package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.TransfurMode;
import net.ltxprogrammer.changed.init.ChangedParticles;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class LatexBenignWolf extends AbstractLatexWolf {
    public LatexBenignWolf(EntityType<? extends LatexBenignWolf> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    public TransfurMode getTransfurMode() {
        return TransfurMode.ABSORPTION;
    }


    public ChangedParticles.Color3 getDripColor() {
        return ChangedParticles.Color3.getColor("#282828");
    }
}