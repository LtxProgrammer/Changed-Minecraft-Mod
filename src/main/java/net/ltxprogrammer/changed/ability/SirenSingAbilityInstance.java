package net.ltxprogrammer.changed.ability;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.effect.Shock;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.ltxprogrammer.changed.util.CameraUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Random;

public class SirenSingAbilityInstance extends AbstractAbilityInstance {
    private int lastSingTick = 0;

    public SirenSingAbilityInstance(AbstractAbility<?> ability, IAbstractChangedEntity entity) {
        super(ability, entity);
    }

    @Override
    public boolean canUse() {
        return true;
    }

    @Override
    public boolean canKeepUsing() {
        return lastSingTick >= entity.getEntity().tickCount;
    }

    @Override
    public void startUsing() {
        final var self = entity.getEntity();
        self.playSound(ChangedSounds.SIREN, 1, 1);
        lastSingTick = entity.getEntity().tickCount + (8 * 20) + 10;
    }

    public void applyEffect(@Nullable LivingEntity livingEntity) {
        if (livingEntity == null)
            return;
        if (livingEntity instanceof Player && !Changed.config.server.playerControllingAbilities.get())
            return;

        Shock.setNoControlTicks(livingEntity, 5);
        Random random = new Random(livingEntity.getId() + ((livingEntity.getId() + livingEntity.tickCount) / 10));
        Vec3 randomXZdir = (new Vec3(random.nextDouble(-1, 1), 0, random.nextDouble(-1, 1))).normalize();

        CameraUtil.tugEntityLookDirection(livingEntity, randomXZdir, 0.125);

        if (!livingEntity.isOnGround())
            return;
        final double moveScale = livingEntity.getSpeed() * 0.8 * (livingEntity instanceof Player ? 10.0 : 1.0);
        livingEntity.travel(randomXZdir.multiply(moveScale, 0, moveScale));
    }

    @Override
    public void tick() {
        var self = entity.getEntity();
        var level = entity.getLevel();

        self.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 10, 1, false, false, false));

        level.getNearbyEntities(LivingEntity.class, TargetingConditions.DEFAULT, self,
                new AABB(self.blockPosition()).inflate(8)).forEach(livingEntity -> {
            if (TransfurVariant.getEntityVariant(livingEntity) != null)
                return;
            if (!livingEntity.getType().is(ChangedTags.EntityTypes.HUMANOIDS))
                return;

            if (self instanceof ChangedEntity && livingEntity instanceof Player player) {
                var tag = new CompoundTag();
                tag.putUUID("id", player.getUUID());
                this.sendPayload(tag, player);
            } else {
                applyEffect(livingEntity);
            }
        });
    }

    @Override
    public void acceptPayload(CompoundTag tag) {
        super.acceptPayload(tag);
        applyEffect(this.entity.getLevel().getPlayerByUUID(tag.getUUID("id")));
    }

    @Override
    public void stopUsing() {

    }
}
