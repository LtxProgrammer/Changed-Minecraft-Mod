package net.ltxprogrammer.changed.ability.active;

import net.ltxprogrammer.changed.ability.AbstractAbilityInstance;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.init.ChangedEntities;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
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

        var list = findWaterNearby(level, entity.getBlockPosition())
                .map(BlockPos::immutable).toList();
        int attempts = Math.min(list.size(), 2);

        if (attempts == 0) { // Spawn failed, grant reduced cooldown
            entity.getAbilityInstanceSafe(this)
                    .map(AbstractAbilityInstance::getController)
                    .ifPresent(controller -> controller.forceCooldown(20));
            return;
        }

        if (level.isClientSide)
            return;

        while (attempts > 0) {
            var blockPos = list.get(level.random.nextInt(list.size()));

            var shark = ChangedEntities.FERAL_LATEX_SHARK.get().create(level);
            level.addFreshEntity(shark);
            shark.setTarget(entity.getEntity().getLastHurtByMob());
            shark.moveTo(blockPos, shark.getYRot(), shark.getXRot());

            attempts--;
        }

        ChangedSounds.broadcastSound(entity.getEntity(), ChangedSounds.TIGER_SHARK_ROAR, 1.0f, 1.0f);
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

    private static final Collection<Component> DESCRIPTION = Collections.singleton(Component.translatable("ability.changed.summon_sharks.desc"));

    @Override
    public Collection<Component> getAbilityDescription(IAbstractChangedEntity entity) {
        return DESCRIPTION;
    }
}
