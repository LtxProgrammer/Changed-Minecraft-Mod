package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.init.ChangedBlocks;
import net.ltxprogrammer.changed.init.ChangedEntities;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.player.Player;
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
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, WhiteLatexEntity.class)));
    }

    @Override
    public void onDamagedBy(LivingEntity self, LivingEntity source) {
        super.onDamagedBy(self, source);
        if (source instanceof Player player && player.isCreative())
            return;

        double d0 = this.getAttributeValue(Attributes.FOLLOW_RANGE);
        AABB aabb = AABB.unitCubeFromLowerCorner(self.position()).inflate(d0, 10.0D, d0);
        this.level.getEntitiesOfClass(this.getClass(), aabb, EntitySelector.NO_SPECTATORS).forEach(whiteLatexEntity -> {
            if (whiteLatexEntity.getTarget() == null && !whiteLatexEntity.isAlliedTo(source))
                whiteLatexEntity.setTarget(source);
        });
    }

    @Override
    public LatexType getLatexType() {
        return LatexType.WHITE_LATEX;
    }

    @Override
    public Color3 getDripColor() {
        return Color3.WHITE;
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
