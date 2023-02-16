package net.ltxprogrammer.changed.entity.beast;


import net.ltxprogrammer.changed.entity.HairStyle;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.entity.TransfurMode;
import net.ltxprogrammer.changed.init.ChangedParticles;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;


public class LatexMedusaCat extends LatexEntity {
    public LatexMedusaCat(EntityType<? extends LatexMedusaCat> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }


    @Override
    public ChangedParticles.Color3 getDripColor() {
        return ChangedParticles.Color3.getColor(this.random.nextInt(4) < 3 ? "#ffdb4f" : "#f0f0f0");
    }

    @Override
    public HairStyle getDefaultHairStyle() {
        return HairStyle.FEMALE_SIDE_BANGS;
    }

    @Override
    public ChangedParticles.Color3 getHairColor() {
        return ChangedParticles.Color3.getColor("#719b6f");
    }

    @Override
    public LatexType getLatexType() {
        return LatexType.NEUTRAL;
    }

    @Override
    public TransfurMode getTransfurMode() {
        return TransfurMode.ABSORPTION;
    }
}