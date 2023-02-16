package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.Gender;
import net.ltxprogrammer.changed.entity.TransfurMode;
import net.ltxprogrammer.changed.init.ChangedParticles;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class DarkLatexWolfMale extends AbstractDarkLatexWolf {
    public DarkLatexWolfMale(EntityType<? extends DarkLatexWolfMale> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    public TransfurMode getTransfurMode() {
        return TransfurMode.REPLICATION;
    }

    @Override
    public ChangedParticles.Color3 getHairColor() {
        return ChangedParticles.Color3.DARK;
    }

    @Override
    public Gender getGender() {
        return Gender.MALE;
    }
}
