package net.ltxprogrammer.changed.ability;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class SlitherAbility extends SimpleAbility {
    @Override
    public ResourceLocation getId() {
        return Changed.modResource("slither");
    }

    @Override
    public boolean canUse(Player player, LatexVariant<?> variant) {
        return variant.getLatexEntity().overrideVisuallySwimming <= 0;
    }

    @Override
    public boolean canKeepUsing(Player player, LatexVariant<?> variant) {
        return !player.isCrouching();
    }

    @Override
    public void startUsing(Player player, LatexVariant<?> variant) {
        super.startUsing(player, variant);
        variant.getLatexEntity().overrideVisuallySwimming++;
        setDirty(player, variant);
    }

    @Override
    public void stopUsing(Player player, LatexVariant<?> variant) {
        super.stopUsing(player, variant);
        variant.getLatexEntity().overrideVisuallySwimming--;
        setDirty(player, variant);
    }

    @Override
    public void saveData(CompoundTag tag, Player player, LatexVariant<?> variant) {
        super.saveData(tag, player, variant);
        tag.putInt("overrideSwimming", variant.getLatexEntity().overrideVisuallySwimming);
    }

    @Override
    public void readData(CompoundTag tag, Player player, LatexVariant<?> variant) {
        super.readData(tag, player, variant);
        variant.getLatexEntity().overrideVisuallySwimming = tag.getInt("overrideSwimming");
    }
}
