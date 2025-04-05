package net.ltxprogrammer.changed.entity;

import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.util.CameraUtil;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public interface PlayerDataExtension extends LivingEntityDataExtension {
    @Nullable
    TransfurVariantInstance<?> getTransfurVariant();
    void setTransfurVariant(@Nullable TransfurVariantInstance<?> variant);

    default boolean isTransfurred() {
        return getTransfurVariant() != null;
    }

    @NotNull
    float getTransfurProgress();
    void setTransfurProgress(@NotNull float progress);

    CameraUtil.TugData getTugData();
    void setTugData(CameraUtil.TugData data);

    int getPaleExposure();
    void setPaleExposure(int level);

    @Nullable PlayerMoverInstance<?> getPlayerMover();
    void setPlayerMover(@Nullable PlayerMoverInstance<?> mover);
    default void setPlayerMoverType(@Nullable PlayerMover<?> moverType) {
        var existingMover = getPlayerMover();

        if (moverType == null)
            setPlayerMover(null);
        else if (existingMover == null || !existingMover.is(moverType))
            setPlayerMover(moverType.createInstance());
    }

    default boolean isPlayerMover(@Nullable PlayerMover<?> moverType) {
        var existingMover = getPlayerMover();

        if (moverType == null && existingMover == null)
            return true;
        else
            return existingMover != null && existingMover.is(moverType);
    }

    BasicPlayerInfo getBasicPlayerInfo();
    void setBasicPlayerInfo(BasicPlayerInfo basicPlayerInfo);
}
