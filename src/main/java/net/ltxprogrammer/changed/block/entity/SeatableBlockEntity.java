package net.ltxprogrammer.changed.block.entity;

import net.ltxprogrammer.changed.entity.SeatEntity;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;

public interface SeatableBlockEntity {
    @Nullable SeatEntity getEntityHolder();
    void setEntityHolder(@Nullable SeatEntity entity);

    @Nullable
    default LivingEntity getSeatedEntity() {
        final var holder = this.getEntityHolder();
        if (holder != null && holder.getFirstPassenger() instanceof LivingEntity livingEntity)
            return livingEntity;
        return null;
    }
}
