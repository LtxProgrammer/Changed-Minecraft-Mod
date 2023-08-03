package net.ltxprogrammer.changed.entity.ai;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.Level;

public class DudNavigator extends GroundPathNavigation {
    public DudNavigator(Mob mob, Level level) {
        super(mob, level);
    }

    @Override
    protected boolean canUpdatePath() {
        return false;
    }

    @Override
    public boolean isDone() {
        return true;
    }
}
