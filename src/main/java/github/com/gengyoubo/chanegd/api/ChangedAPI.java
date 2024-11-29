package github.com.gengyoubo.chanegd.api;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ChangedAPI {
    /**
     * @return will return the latex variant of the current entity.
     */
    public static TransfurVariant<?> getEntityLatexVariant(LivingEntity entity) {
        return TransfurVariant.getEntityVariant(entity);
    }
    /**
     * @return will return the player's latex variant.
     */
    public static TransfurVariant<?> getPlayerLatexVariant(ServerPlayer player) {
        return getEntityLatexVariant(player);
    }
    /**
     * @param player current player
     * @param variant Latex variants that need to be compared
     * @return Returns true if the player variant is the same as the latex variant being compared, otherwise it returns false.
     */
    public static boolean isPlayerLatexVariantAppointed(@NotNull ServerPlayer player,@NotNull String variant) {
        ResourceKey<?> importVariantKey = ResourceKey.createRegistryKey(Changed.modResource(variant));
        TransfurVariant<?> nowVariant = getPlayerLatexVariant(player);
        if (nowVariant == null || nowVariant.getRegistryName() == null) {
            return false;
        }
        return Objects.requireNonNull(nowVariant.getRegistryName()).equals(importVariantKey.location());
    }
}
