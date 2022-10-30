package net.ltxprogrammer.changed.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public abstract class GenderedLatexEntity extends LatexEntity implements GenderedEntity {
    public GenderedLatexEntity(EntityType<? extends GenderedLatexEntity> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }
}
