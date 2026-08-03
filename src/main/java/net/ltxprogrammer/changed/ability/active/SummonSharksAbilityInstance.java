package net.ltxprogrammer.changed.ability.active;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.ability.AbstractAbilityInstance;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.init.ChangedEntities;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.ltxprogrammer.changed.init.ChangedVariantFeatures;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.EntityGetter;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.stream.Stream;

public class SummonSharksAbilityInstance extends AbstractAbilityInstance {
    protected List<PathfinderMob> sharksToLead = new ObjectArrayList<>();

    public SummonSharksAbilityInstance(AbstractAbility<?> ability, IAbstractChangedEntity entity) {
        super(ability, entity);
    }

    @Override
    public boolean canUse() {
        return entity.isInWaterOrBubble();
    }

    @Override
    public boolean canKeepUsing() {
        return canUse();
    }

    protected Stream<BlockPos> findWaterNearby(BlockGetter level, BlockPos near) {
        return BlockPos.betweenClosedStream(near.offset(-4, -2, -4), near.offset(4, 2, 4)).filter(
                pos -> level.getFluidState(pos).is(FluidTags.WATER)
        );
    }

    protected List<PathfinderMob> findSharksNearby(EntityGetter level, BlockPos near) {
        return level.getEntitiesOfClass(PathfinderMob.class, new AABB(near).inflate(8, 6, 8), pathfinder -> {
            return EntityUtil.maybeGetOverlaying(pathfinder).getType().is(ChangedTags.EntityTypes.RETARGETABLE_SHARKS);
        });
    }

    @Override
    public void tickIdle() {
        super.tickIdle();

        if (entity.getLevel().isClientSide)
            return;

        if (!getController().isCoolingDown()) {
            sharksToLead.clear();
            return;
        }

        boolean followMyLead = entity.hasFeature(ChangedVariantFeatures.SHARKS_FOLLOW_MY_LEAD.get());
        if (!followMyLead)
            return;

        LivingEntity target = entity.getEntity().getLastHurtMob();
        if (target == null)
            target = entity.getEntity().getLastHurtByMob();

        for (var shark : sharksToLead) {
            shark.setTarget(target);
        }
    }

    @Override
    public void startUsing() {
        var level = entity.getLevel();

        var list = findWaterNearby(level, entity.getBlockPosition())
                .map(BlockPos::immutable).toList();
        int attempts = Math.min(list.size(), 2 + (int) entity.getFeatureLevel(ChangedVariantFeatures.SHARKS_BONUS_SHARKS.get()));

        boolean callToArms = entity.hasFeature(ChangedVariantFeatures.SHARKS_CALL_TO_ARMS.get());
        List<PathfinderMob> retargetSharks = callToArms ? findSharksNearby(level, entity.getBlockPosition()) : List.of();

        if (attempts == 0 && retargetSharks.isEmpty()) { // Spawn failed, grant reduced cooldown
            getController().forceCooldown(20);
            return;
        }

        if (level.isClientSide)
            return;

        LivingEntity target = entity.getEntity().getLastHurtMob();
        if (target == null)
            target = entity.getEntity().getLastHurtByMob();

        while (attempts > 0) {
            var blockPos = list.get(level.random.nextInt(list.size()));

            var shark = ChangedEntities.FERAL_LATEX_SHARK.get().create(level);
            level.addFreshEntity(shark);
            shark.setTarget(entity.getEntity().getLastHurtByMob());
            shark.moveTo(blockPos, shark.getYRot(), shark.getXRot());
            if (callToArms)
                shark.setTarget(target);
            sharksToLead.add(shark);

            attempts--;
        }

        for (var shark : retargetSharks) {
            shark.setTarget(target);
            sharksToLead.add(shark);
        }

        ChangedSounds.broadcastSound(entity.getEntity(), ChangedSounds.TIGER_SHARK_ROAR, 1.0f, 1.0f);
    }

    @Override
    public void tick() {

    }

    @Override
    public void stopUsing() {

    }
}
