package net.ltxprogrammer.changed.entity;

import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;

public interface LivingEntityDataExtension {
    int getNoControlTicks();
    void setNoControlTicks(int ticks);

    @Nullable
    LivingEntity getGrabbedBy();
    void setGrabbedBy(@Nullable LivingEntity holder);
}
