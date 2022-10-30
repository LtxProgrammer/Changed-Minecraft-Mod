package net.ltxprogrammer.changed.entity;

import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public enum LatexType implements StringRepresentable {
    NEUTRAL,
    DARK_LATEX,
    WHITE_LATEX;

    public static void setEntityLatexType(@NotNull Entity entity, LatexType type) {
        entity.getPersistentData().putString("changed:transfur_immunity", type.toString());
    }

    public static LatexType getEntityLatexType(@NotNull Entity entity) {
        if (entity instanceof LatexEntity latexEntity) {
            return latexEntity.getLatexType();
        }

        else if (entity instanceof Player player) {
            LatexVariant<?> variant = ProcessTransfur.getPlayerLatexVariant(player);
            if (variant != null) {
                return variant.getLatexType();
            }
        }

        return null;
    }

    public static boolean hasLatexType(@NotNull Entity entity) {
        return getEntityLatexType(entity) != null;
    }

    public static boolean hasFactionLatexType(@NotNull Entity entity) {
        return isFaction(getEntityLatexType(entity));
    }

    public static boolean isFaction(LatexType type) {
        if (type == NEUTRAL || type == null) return false;
        return true;
    }

    public static LatexType factionOrNull(LatexType type) {
        if (type == NEUTRAL) return null;
        return type;
    }

    public static LatexType getEntityFactionLatexType(@NotNull Entity entity) {
        return factionOrNull(getEntityLatexType(entity));
    }

    public boolean isHostileTo(LatexType other) {
        return (isFaction(this) && isFaction(other) && this != other) || other == null;
    }

    @Override
    public String getSerializedName() {
        return toString().toLowerCase();
    }
}
