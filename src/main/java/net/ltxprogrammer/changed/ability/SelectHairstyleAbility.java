package net.ltxprogrammer.changed.ability;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.HairStyle;
import net.ltxprogrammer.changed.entity.variant.LatexVariantInstance;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.world.inventory.HairStyleRadialMenu;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;

public class SelectHairstyleAbility extends SimpleAbility {
    @Override
    public boolean canUse(Player player, LatexVariantInstance<?> variant) {
        return variant.getLatexEntity() != null && variant.getLatexEntity().getValidHairStyles() != null &&
                variant.getLatexEntity().getValidHairStyles().size() > 1;
    }

    @Override
    public void startUsing(Player player, LatexVariantInstance<?> variant) {
        super.startUsing(player, variant);
        player.openMenu(new SimpleMenuProvider((id, inv, plr) ->
                new HairStyleRadialMenu(id, inv, null), HairStyleRadialMenu.CONTAINER_TITLE));
    }

    @Override
    public void saveData(CompoundTag tag, Player player, LatexVariantInstance<?> variant) {
        super.saveData(tag, player, variant);
        tag.putInt("HairStyle", ChangedRegistry.HAIR_STYLE.get().getID(variant.getLatexEntity().getHairStyle()));
    }

    @Override
    public void readData(CompoundTag tag, Player player, LatexVariantInstance<?> variant) {
        super.readData(tag, player, variant);
        if (tag.contains("HairStyle"))
            variant.getLatexEntity().setHairStyle(ChangedRegistry.HAIR_STYLE.get().getValue(tag.getInt("HairStyle")));
    }
}
