package net.ltxprogrammer.changed.entity.beast;

import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.FlyingAnimal;

public interface IFlyingCapable extends FlyingAnimal {
    void setFlying(boolean flying);
    boolean canSafelyLandHere();

    PathNavigation getNavigationForFlyingState(boolean flying);
}
