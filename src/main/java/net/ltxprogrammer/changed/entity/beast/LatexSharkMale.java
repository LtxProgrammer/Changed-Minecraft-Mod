package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.Gender;
import net.ltxprogrammer.changed.entity.GenderedEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class LatexSharkMale extends AbstractLatexShark implements GenderedEntity {
    public LatexSharkMale(EntityType<? extends LatexSharkMale> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    public Gender getGender() {
        return Gender.MALE;
    }
}
