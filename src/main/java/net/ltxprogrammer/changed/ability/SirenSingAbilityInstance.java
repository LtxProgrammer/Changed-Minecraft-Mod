package net.ltxprogrammer.changed.ability;

import net.ltxprogrammer.changed.effect.Shock;
import net.ltxprogrammer.changed.entity.beast.LatexSiren;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

public class SirenSingAbilityInstance extends AbstractAbilityInstance {
    private int lastSingTick = 0;

    public SirenSingAbilityInstance(AbstractAbility<?> ability, IAbstractLatex entity) {
        super(ability, entity);
    }

    @Override
    public boolean canUse() {
        return true;
    }

    @Override
    public boolean canKeepUsing() {
        return true;
    }

    @Override
    public void startUsing() {

    }

    @Override
    public void tick() {
        var self = entity.getEntity();
        var level = entity.getLevel();

        if (!(self instanceof LatexSiren siren) || !siren.wantToSing())
            return;

        if (lastSingTick < self.tickCount) {
            siren.setSilent(self.isSilent());
            siren.playSound(ChangedSounds.SIREN, 1, 1);
            lastSingTick = self.tickCount + (8 * 20) + 10;
        }

        level.getNearbyEntities(LivingEntity.class, TargetingConditions.DEFAULT, self,
                new AABB(self.blockPosition()).inflate(8)).forEach(livingEntity -> {
            if (LatexVariant.getEntityVariant(livingEntity) != null)
                return;

            Shock.setNoControlTicks(livingEntity, 5);
            if (!livingEntity.isOnGround())
                return;
            Random random = new Random(livingEntity.getId() + (livingEntity.tickCount / 10));
            Vec3 randomXZdir = new Vec3(random.nextDouble(-1, 1), 0, random.nextDouble(-1, 1));
            randomXZdir = randomXZdir.normalize();
            final double moveScale = livingEntity.getSpeed() * 0.8;
            livingEntity.travel(randomXZdir.multiply(moveScale, 0, moveScale));
        });
    }

    @Override
    public void stopUsing() {

    }
}
