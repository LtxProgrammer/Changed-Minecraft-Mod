package net.ltxprogrammer.changed.ability;

import net.ltxprogrammer.changed.entity.variant.LatexVariantInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public class SlitherAbility extends SimpleAbility {
    @Override
    public boolean canUse(Player player, LatexVariantInstance<?> variant) {
        return true;
    }

    @Override
    public boolean canKeepUsing(Player player, LatexVariantInstance<?> variant) {
        return !player.isCrouching() && !player.isSleeping();
    }

    @Override
    public void startUsing(Player player, LatexVariantInstance<?> variant) {
        super.startUsing(player, variant);
        variant.getLatexEntity().overrideVisuallySwimming = true;
        setDirty(player, variant);
    }

    @Override
    public void stopUsing(Player player, LatexVariantInstance<?> variant) {
        super.stopUsing(player, variant);
        variant.getLatexEntity().overrideVisuallySwimming = false;
        setDirty(player, variant);
    }

    @Override
    public void saveData(CompoundTag tag, Player player, LatexVariantInstance<?> variant) {
        super.saveData(tag, player, variant);
        tag.putBoolean("overrideSwimming", variant.getLatexEntity().overrideVisuallySwimming);
    }

    @Override
    public void readData(CompoundTag tag, Player player, LatexVariantInstance<?> variant) {
        super.readData(tag, player, variant);
        variant.getLatexEntity().overrideVisuallySwimming = tag.getBoolean("overrideSwimming");
    }
}
