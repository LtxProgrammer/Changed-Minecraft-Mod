package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.init.ChangedBlocks;
import net.ltxprogrammer.changed.init.ChangedEntities;
import net.ltxprogrammer.changed.init.ChangedParticles;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.function.Predicate;

import static net.ltxprogrammer.changed.block.AbstractLatexBlock.COVERED;

public abstract class WhiteLatexEntity extends LightLatexWolfMale {
    public WhiteLatexEntity(EntityType<? extends WhiteLatexEntity> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, WhiteLatexEntity.class)).setAlertOthers());
    }

    @Override
    public LatexType getLatexType() {
        return LatexType.WHITE_LATEX;
    }

    @Override
    public ChangedParticles.Color3 getDripColor() {
        return ChangedParticles.Color3.WHITE;
    }

    public static final Predicate<WhiteLatexEntity> IS_STANDING_ON_WHITE_LATEX = whiteLatexEntity -> {
        if (!whiteLatexEntity.isOnGround())
            return false;
        BlockState standingOn = whiteLatexEntity.level.getBlockState(whiteLatexEntity.blockPosition().below());
        if (standingOn.is(ChangedBlocks.WHITE_LATEX_BLOCK.get()))
            return true;
        if (standingOn.getProperties().contains(COVERED))
            return standingOn.getValue(COVERED) == LatexType.WHITE_LATEX;
        return false;
    };

    @Override
    public void tick() {
        super.tick();
        if (this.level.isClientSide)
            return;
        if (this.isDeadOrDying())
            return;

        if (this.tickCount % 20 != 0)
            return;

        var entities = this.level.getEntitiesOfClass(WhiteLatexEntity.class, new AABB(blockPosition()).inflate(2.0), IS_STANDING_ON_WHITE_LATEX);
        if (entities.size() <= 12)
            return;

        var behemoth = ChangedEntities.BEHEMOTH_HEAD.get().create(this.level);
        if (behemoth == null)
            return;
        behemoth.moveTo(position());
        if (!this.level.addFreshEntity(behemoth))
            return;
        entities.forEach(Entity::discard);
        ChangedSounds.broadcastSound(behemoth, ChangedSounds.POISON, 1.0f, 1.0f);
    }
}
