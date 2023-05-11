package net.ltxprogrammer.changed.ability;

import net.ltxprogrammer.changed.entity.beast.SpecialLatex;
import net.ltxprogrammer.changed.entity.variant.LatexVariantInstance;
import net.ltxprogrammer.changed.world.inventory.SpecialStateRadialMenu;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;

public class SelectSpecialStateAbility extends SimpleAbility {
    @Override
    public boolean canUse(Player player, LatexVariantInstance<?> variant) {
        return variant.getLatexEntity() instanceof SpecialLatex;
    }

    @Override
    public void startUsing(Player player, LatexVariantInstance<?> variant) {
        super.startUsing(player, variant);
        player.openMenu(new SimpleMenuProvider((id, inv, plr) ->
                new SpecialStateRadialMenu(id, inv, null), SpecialStateRadialMenu.CONTAINER_TITLE));
    }

    @Override
    public void saveData(CompoundTag tag, Player player, LatexVariantInstance<?> variant) {
        super.saveData(tag, player, variant);
        if (variant.getLatexEntity() instanceof SpecialLatex specialLatex)
            tag.putString("State", specialLatex.wantedState);
    }

    @Override
    public void readData(CompoundTag tag, Player player, LatexVariantInstance<?> variant) {
        super.readData(tag, player, variant);
        if (tag.contains("State") && variant.getLatexEntity() instanceof SpecialLatex specialLatex)
            specialLatex.wantedState = tag.getString("State");
    }
}
