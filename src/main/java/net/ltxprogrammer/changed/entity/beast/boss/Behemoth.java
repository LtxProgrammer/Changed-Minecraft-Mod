package net.ltxprogrammer.changed.entity.beast.boss;

import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.entity.TransfurMode;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.init.ChangedParticles;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public abstract class Behemoth extends LatexEntity {
    public Behemoth(EntityType<? extends Behemoth> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    public LatexType getLatexType() {
        return LatexType.WHITE_LATEX;
    }

    @Override
    public TransfurMode getTransfurMode() {
        return TransfurMode.REPLICATION;
    }

    @Override
    public ChangedParticles.Color3 getDripColor() {
        return ChangedParticles.Color3.WHITE;
    }

    public LatexVariant<?> getSelfVariant() {
        return null;
    }

    public LatexVariant<?> getTransfurVariant() {
        return LatexVariant.LIGHT_LATEX_WOLF.male();
    }
}
