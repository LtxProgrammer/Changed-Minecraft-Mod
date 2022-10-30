package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.Gender;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class LatexSharkFemale extends AbstractLatexShark {
    public LatexSharkFemale(EntityType<? extends LatexSharkFemale> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    public Gender getGender() {
        return Gender.FEMALE;
    }
}
