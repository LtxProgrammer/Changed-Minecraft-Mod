package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.GenderedEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public abstract class AbstractGenderedGooShark extends AbstractGooShark implements GenderedEntity {
    public AbstractGenderedGooShark(EntityType<? extends AbstractGenderedGooShark> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }
}
