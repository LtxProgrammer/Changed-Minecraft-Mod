package net.ltxprogrammer.changed.ability;

import net.ltxprogrammer.changed.init.ChangedEntities;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.BlockGetter;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Stream;

public class SummonSharksAbility extends SimpleAbility {
    @Override
    public boolean canUse(IAbstractChangedEntity entity) {
        return entity.isInWaterOrBubble();
    }

    protected Stream<BlockPos> findWaterNearby(BlockGetter level, BlockPos near) {
        return BlockPos.betweenClosedStream(near.offset(-4, -2, -4), near.offset(4, 2, 4)).filter(
                pos -> level.getFluidState(pos).is(FluidTags.WATER)
        );
    }

    @Override
    public void startUsing(IAbstractChangedEntity entity) {
        var level = entity.getLevel();
        if (level.isClientSide)
            return;

        ChangedSounds.broadcastSound(entity.getEntity(), ChangedSounds.MONSTER2, 1.0f, 1.0f);

        var list = findWaterNearby(level, entity.getBlockPosition()).toList();
        int attempts = Math.min(list.size(), 2);

        while (attempts > 0) {
            var blockPos = list.get(level.random.nextInt(list.size()));

            var shark = ChangedEntities.SHARK.get().create(level);
            level.addFreshEntity(shark);
            shark.setTarget(entity.getEntity().getLastHurtByMob());
            shark.moveTo(blockPos, 0.0f, 0.0f);

            attempts--;
        }
    }

    @Override
    public UseType getUseType(IAbstractChangedEntity entity) {
        return UseType.CHARGE_TIME;
    }

    @Override
    public int getChargeTime(IAbstractChangedEntity entity) {
        return 40;
    }

    @Override
    public int getCoolDown(IAbstractChangedEntity entity) {
        return 2 * 60 * 20; // 2 Minutes
    }

    private static final Collection<Component> DESCRIPTION = Collections.singleton(new TranslatableComponent("ability.changed.summon_sharks.desc"));

    @Override
    public Collection<Component> getAbilityDescription(IAbstractChangedEntity entity) {
        return DESCRIPTION;
    }
}
