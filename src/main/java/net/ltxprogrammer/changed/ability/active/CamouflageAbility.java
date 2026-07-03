package net.ltxprogrammer.changed.ability.active;

import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

public class CamouflageAbility extends SimpleToggleAbility {
    public CamouflageAbility() {
        super(true);
    }

    @Override
    public boolean canUse(IAbstractChangedEntity entity) {
        return true;
    }

    @Override
    public void tickIdle(IAbstractChangedEntity entity) {
        super.tickIdle(entity);

        if (entity.getLevel().isClientSide || !isActive(entity) || !isEntitySneaky(entity))
            return;

        AABB boundingBoxLow = new AABB(entity.getEntity().blockPosition()).inflate(0.75, 0.0, 0.75);
        AABB boundingBoxHigh = new AABB(EntityUtil.getEyeBlock(entity.getEntity())).inflate(0.75, 0.0, 0.75);

        Level level = entity.getLevel();
        AtomicInteger countLow = new AtomicInteger(0);
        AtomicInteger countHigh = new AtomicInteger(0);
        final int requiredCount = getRequiredBlocks(entity) / 2;
        boolean camouflageActive = level.getBlockStatesIfLoaded(boundingBoxLow).anyMatch(blockState -> {
            if (blockState.is(ChangedTags.Blocks.FOLIAGE))
                return countLow.incrementAndGet() == requiredCount;

            return false;
        }) && level.getBlockStatesIfLoaded(boundingBoxHigh).anyMatch(blockState -> {
            if (blockState.is(ChangedTags.Blocks.FOLIAGE))
                return countHigh.incrementAndGet() == requiredCount;

            return false;
        });

        if (camouflageActive) {
            entity.getEntity().addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 2, 0, true, false, true));
            entity.getEntity().addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 2, 1, true, false, true));
        }
    }

    protected int getRequiredBlocks(IAbstractChangedEntity entity) {
        return Math.max((4 - getAbilityLevel(entity)) * 2, 0);
    }

    protected boolean isEntitySneaky(IAbstractChangedEntity entity) {
        var self = entity.getEntity();
        if (self.isCrouching() || self.isVisuallyCrawling())
            return true;

        return false;
    }

    private static final Collection<Component> DESCRIPTION = Collections.singleton(Component.translatable("ability.changed.camouflage.desc"));
    private static final Component ENABLE = Component.translatable("ability.changed.camouflage.enable");
    private static final Component DISABLE = Component.translatable("ability.changed.camouflage.disable");

    @Override
    public Collection<Component> getAbilityDescription(IAbstractChangedEntity entity) {
        return DESCRIPTION;
    }

    @Override
    public void startUsing(IAbstractChangedEntity entity) {
        super.startUsing(entity);
        entity.displayClientMessage(isActive(entity) ? ENABLE : DISABLE, true);
    }
}
