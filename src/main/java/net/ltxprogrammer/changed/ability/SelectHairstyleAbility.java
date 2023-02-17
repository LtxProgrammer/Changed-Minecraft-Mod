package net.ltxprogrammer.changed.ability;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.Gender;
import net.ltxprogrammer.changed.entity.GenderedEntity;
import net.ltxprogrammer.changed.entity.HairStyle;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.world.inventory.CentaurSaddleMenu;
import net.ltxprogrammer.changed.world.inventory.HairStyleRadialMenu;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;

public class SelectHairstyleAbility extends SimpleAbility {
    @Override
    public ResourceLocation getId() {
        return Changed.modResource("select_hairstyle");
    }

    @Override
    public boolean canUse(Player player, LatexVariant<?> variant) {
        return variant.getLatexEntity() != null && variant.getLatexEntity().getValidHairStyles() != null &&
                variant.getLatexEntity().getValidHairStyles().size() > 1;
    }

    @Override
    public void startUsing(Player player, LatexVariant<?> variant) {
        super.startUsing(player, variant);
        player.openMenu(new SimpleMenuProvider((id, inv, plr) ->
                new HairStyleRadialMenu(id, inv, null), HairStyleRadialMenu.CONTAINER_TITLE));
    }

    @Override
    public void saveData(CompoundTag tag, Player player, LatexVariant<?> variant) {
        super.saveData(tag, player, variant);
        tag.putString("HairStyle", variant.getLatexEntity().getHairStyle().name());
    }

    @Override
    public void readData(CompoundTag tag, Player player, LatexVariant<?> variant) {
        super.readData(tag, player, variant);
        if (tag.contains("HairStyle"))
            variant.getLatexEntity().setHairStyle(HairStyle.valueOf(tag.getString("HairStyle")));
    }
}
