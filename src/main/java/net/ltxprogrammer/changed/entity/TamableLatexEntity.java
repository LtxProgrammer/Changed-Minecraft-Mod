package net.ltxprogrammer.changed.entity;

import net.minecraft.world.entity.OwnableEntity;

public interface TamableLatexEntity extends OwnableEntity {
    boolean isFollowingOwner();
    void setFollowOwner(boolean value);
}
