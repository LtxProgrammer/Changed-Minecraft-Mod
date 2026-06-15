package net.ltxprogrammer.changed.ability;

import net.ltxprogrammer.changed.init.ChangedAbilities;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SupportType;

public class CobwebRappelAbilityInstance extends AbstractAbilityInstance {
    public CobwebRappelAbilityInstance(AbstractAbility<?> ability, IAbstractChangedEntity entity) {
        super(ability, entity);
    }

    @Override
    public AbstractAbility.UseType getUseType() {
        return AbstractAbility.UseType.HOLD;
    }

    protected BlockPos getCobwebCursor() {
        return EntityUtil.getEyeBlock(entity.getEntity());
    }

    @Override
    public boolean canUse() {
        var position = getCobwebCursor();
        var level = entity.getLevel();
        var blockStateAbove = level.getBlockState(position.above());
        var blockState = level.getBlockState(position);
        var blockStateBelow = level.getBlockState(position.below());

        if (!blockState.canBeReplaced() && !blockState.is(Blocks.COBWEB))
            return false;

        if (!blockStateAbove.isFaceSturdy(level, position.above(), Direction.DOWN, SupportType.FULL) && !blockStateAbove.is(Blocks.COBWEB))
            return false;

        if (!blockStateBelow.canBeReplaced() && !blockStateBelow.is(Blocks.COBWEB))
            return false;

        var wallClimb = entity.getAbilityInstance(ChangedAbilities.WALL_CLIMB.get());
        if (wallClimb == null || !wallClimb.isActive())
            return false;

        return true;
    }

    @Override
    public boolean canKeepUsing() {
        var wallClimb = entity.getAbilityInstance(ChangedAbilities.WALL_CLIMB.get());
        if (wallClimb == null || !wallClimb.isActive())
            return false;

        return true;
    }

    @Override
    public void startUsing() {

    }

    @Override
    public void tick() {
        var position = getCobwebCursor();
        var level = entity.getLevel();
        var blockState = level.getBlockState(position);
        var blockStateAbove = level.getBlockState(position.above());

        if (!blockStateAbove.isFaceSturdy(level, position.above(), Direction.DOWN, SupportType.FULL) && !blockStateAbove.is(Blocks.COBWEB))
            return;

        if (!blockState.canBeReplaced() && !blockState.is(Blocks.COBWEB))
            return;

        level.setBlock(position, Blocks.COBWEB.defaultBlockState(), 11);
    }

    @Override
    public void stopUsing() {

    }
}
