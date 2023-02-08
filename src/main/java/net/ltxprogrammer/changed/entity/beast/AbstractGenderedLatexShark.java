package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.GenderedEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public abstract class AbstractGenderedLatexShark extends AbstractLatexShark implements GenderedEntity {
    public AbstractGenderedLatexShark(EntityType<? extends AbstractGenderedLatexShark> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }
}
