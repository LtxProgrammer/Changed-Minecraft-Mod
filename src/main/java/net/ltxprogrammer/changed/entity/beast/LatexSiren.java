package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.Gender;
import net.ltxprogrammer.changed.entity.TransfurMode;
import net.ltxprogrammer.changed.entity.UniqueEffect;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class LatexSiren extends AbstractGenderedLatexShark implements UniqueEffect {
    public LatexSiren(EntityType<? extends LatexSiren> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    public Gender getGender() {
        return Gender.FEMALE;
    }

    @Override
    public TransfurMode getTransfurMode() {
        return TransfurMode.ABSORPTION;
    }

    private boolean wantToSing() {
        return getTarget() != null;
    }

    @Override
    public String getEffectName() {
        return "siren_sing";
    }

    private int lastSingTick = 0;

    @Override
    public void effectTick(Level level, LivingEntity self) {
        if (self instanceof LatexSiren siren && !siren.wantToSing())
            return;

        if (lastSingTick < self.tickCount) {
            this.playSound(ChangedSounds.SIREN, 1, 1);
            lastSingTick = self.tickCount + (8 * 20) + 10;
        }

        level.getNearbyEntities(LivingEntity.class, TargetingConditions.DEFAULT, self,
                new AABB(self.blockPosition()).inflate(8)).forEach(livingEntity -> {
            if (LatexVariant.getEntityVariant(livingEntity) != null)
                return;

            LatexHypnoCat.tugEntityLookDirection(livingEntity, self.getEyePosition().subtract(livingEntity.getEyePosition()).normalize(), 0.1);
            // TODO mode entity randomly
        });
    }
}
