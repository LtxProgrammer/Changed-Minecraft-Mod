package net.ltxprogrammer.changed.entity.ai;

import net.ltxprogrammer.changed.entity.beast.DoubleHeadedEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class LookWithPrimaryHead<T extends Mob & DoubleHeadedEntity> extends Goal {
    private final T mob;
    private int lookTime;
    protected final float probability;

    public LookWithPrimaryHead(T mob) {
        this(mob, 0.02f);
    }

    public LookWithPrimaryHead(T mob, float probability) {
        this.mob = mob;
        this.probability = probability;
    }

    public boolean canUse() {
        if (this.mob.getRandom().nextFloat() >= this.probability) {
            return false;
        } else {
            return true;
        }
    }

    public boolean canContinueToUse() {
        return this.lookTime > 0;
    }

    public void start() {
        this.lookTime = this.adjustedTickDelay(40 + this.mob.getRandom().nextInt(40));
    }

    public void tick() {
        mob.setYHeadRot(Mth.rotLerp(0.5f, mob.getYHeadRot(), mob.getHead2YRot()));
        mob.setXRot(Mth.rotLerp(0.5f, mob.getXRot(), mob.getHead2XRot()));
        --this.lookTime;
    }
}