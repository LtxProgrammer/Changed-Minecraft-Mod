package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.OrganicLatex;
import net.ltxprogrammer.changed.init.ChangedParticles;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class LightLatexWolfOrganic extends AbstractLatexWolf implements OrganicLatex {
    public LightLatexWolfOrganic(EntityType<? extends LightLatexWolfOrganic> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }


    public ChangedParticles.Color3 getDripColor() {
        return ChangedParticles.Color3.getColor("#ffffff");
    }

}