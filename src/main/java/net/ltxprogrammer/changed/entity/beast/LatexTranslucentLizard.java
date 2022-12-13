package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.entity.TransfurMode;
import net.ltxprogrammer.changed.init.ChangedParticles;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class LatexTranslucentLizard extends LatexEntity {
    public LatexTranslucentLizard(EntityType<? extends LatexTranslucentLizard> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    public LatexType getLatexType() {
        return LatexType.NEUTRAL;
    }

    @Override
    public TransfurMode getTransfurMode() {
        return TransfurMode.ABSORPTION;
    }

    @Override
    public ChangedParticles.Color3 getDripColor() {
        return ChangedParticles.Color3.getColor("#ffb84b");
    }
}
