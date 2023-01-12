package net.ltxprogrammer.changed.ability;

import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public abstract class AbstractAbility {
    public TranslatableComponent getDisplayName() {
        return new TranslatableComponent("ability." + getId().toString().replace(':', '.'));
    }

    public abstract ResourceLocation getId();

    public abstract boolean canUse(Player player, LatexVariant<?> variant);
    public abstract boolean canKeepUsing(Player player, LatexVariant<?> variant);

    public abstract void startUsing(Player player, LatexVariant<?> variant);
    public abstract void tick(Player player, LatexVariant<?> variant);
    public abstract void stopUsing(Player player, LatexVariant<?> variant);

    // Called when the player loses the variant (death or untransfur)
    public void onRemove(Player player, LatexVariant<?> variant) {}
}
