package net.ltxprogrammer.changed.entity;

import net.ltxprogrammer.changed.entity.variant.LatexVariantInstance;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.CameraUtil;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public interface PlayerDataExtension {
    @Nullable
    LatexVariantInstance<?> getLatexVariant();
    void setLatexVariant(@Nullable LatexVariantInstance<?> variant);

    default boolean isLatex() {
        return getLatexVariant() != null;
    }

    @NotNull
    ProcessTransfur.TransfurProgress getTransfurProgress();
    void setTransfurProgress(@NotNull ProcessTransfur.TransfurProgress progress);

    CameraUtil.TugData getTugData();
    void setTugData(CameraUtil.TugData data);

    int getPaleExposure();
    void setPaleExposure(int level);
}
