package net.ltxprogrammer.changed.entity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.player.Player;

public interface TamableLatexEntity extends OwnableEntity {
    boolean isFollowingOwner();
    void setFollowOwner(boolean value);

    default boolean isTame() {
        return this.getOwner() != null;
    }
    
    default boolean wantsToAttack(LivingEntity target, LivingEntity owner) {
        if (!(target instanceof Creeper) && !(target instanceof Ghast)) {
            if (target instanceof TamableLatexEntity tamable) {
                return !tamable.isTame() || tamable.getOwner() != owner;
            } else if (target instanceof Player && owner instanceof Player && !((Player)owner).canHarmPlayer((Player)target)) {
                return false;
            } else if (target instanceof AbstractHorse && ((AbstractHorse)target).isTamed()) {
                return false;
            } else {
                return !(target instanceof TamableAnimal) || !((TamableAnimal)target).isTame();
            }
        } else {
            return false;
        }
    }
}
