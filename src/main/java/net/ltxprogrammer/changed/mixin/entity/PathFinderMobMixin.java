package net.ltxprogrammer.changed.mixin.entity;

import net.ltxprogrammer.changed.entity.PathFinderMobDataExtension;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PathfinderMob.class)
public abstract class PathFinderMobMixin extends Mob implements PathFinderMobDataExtension {
    @Unique
    private boolean isLatexAssimilated = false;

    protected PathFinderMobMixin(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    @Override
    public boolean isLatexAssimilated() {
        return isLatexAssimilated;
    }

    @Override
    public void markAsLatexAssimilated(boolean state) {
        isLatexAssimilated = state;
    }
}
