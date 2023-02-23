package net.ltxprogrammer.changed.entity;

import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.CameraUtil;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public interface PlayerDataExtension {
    @Nullable
    LatexVariant<?> getLatexVariant();
    void setLatexVariant(@Nullable LatexVariant<?> variant);

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
