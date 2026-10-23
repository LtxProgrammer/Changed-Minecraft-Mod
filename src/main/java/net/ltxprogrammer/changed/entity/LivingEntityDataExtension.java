package net.ltxprogrammer.changed.entity;

import net.ltxprogrammer.changed.data.AccessorySlots;
import net.ltxprogrammer.changed.fluid.Gas;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.Optional;

public interface LivingEntityDataExtension {
    int getNoControlTicks();
    void setNoControlTicks(int ticks);

    int getInvertControlTicks();
    void setInvertControlTicks(int ticks);

    @Nullable
    LivingEntity getGrabbedBy();
    void setGrabbedBy(@Nullable LivingEntity holder);

    <T extends Gas> Optional<T> isEyeInGas(Class<T> gas);

    void do_hurtCurrentlyUsedShield(float blocked);
    void do_blockUsingShield(LivingEntity attacker);

    void changed$playHurtSound(DamageSource damageSource);
    Player changed$getLastHurtByPlayer();
    int changed$getLastHurtByPlayerTime();
    boolean changed$isAlwaysExperienceDropper();

    Optional<AccessorySlots> getAccessorySlots();
}
