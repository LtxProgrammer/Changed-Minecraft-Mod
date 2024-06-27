package net.ltxprogrammer.changed.block.entity;

import net.ltxprogrammer.changed.entity.SeatEntity;

import javax.annotation.Nullable;

public interface SeatableBlockEntity {
    @Nullable SeatEntity getEntityHolder();
    void setEntityHolder(@Nullable SeatEntity entity);
}
