package net.ltxprogrammer.changed.entity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public interface UniqueEffect {
    String getEffectName();
    void effectTick(Level level, LivingEntity self);
}
