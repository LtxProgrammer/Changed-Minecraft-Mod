package net.ltxprogrammer.changed.entity;

import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.util.CameraUtil;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public interface PlayerDataExtension {
    @Nullable
    TransfurVariantInstance<?> getLatexVariant();
    void setLatexVariant(@Nullable TransfurVariantInstance<?> variant);

    default boolean isLatex() {
        return getLatexVariant() != null;
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

    BasicPlayerInfo getBasicPlayerInfo();
    void setBasicPlayerInfo(BasicPlayerInfo basicPlayerInfo);
}
