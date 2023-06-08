package net.ltxprogrammer.changed.ability;

import net.ltxprogrammer.changed.entity.TransfurMode;
import net.ltxprogrammer.changed.entity.variant.LatexVariantInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class SwitchTransfurModeAbility extends SimpleAbility {
    @Override
    public boolean canUse(Player player, LatexVariantInstance<?> variant) {
        return variant.transfurMode != TransfurMode.NONE;
    }

    @Override
    public boolean canKeepUsing(Player player, LatexVariantInstance<?> variant) { return false; }

    @Override
    public void startUsing(Player player, LatexVariantInstance<?> variant) {
        if (variant.transfurMode == TransfurMode.NONE)
            return;

        if (variant.transfurMode == TransfurMode.ABSORPTION)
            variant.transfurMode = TransfurMode.REPLICATION;
        else
            variant.transfurMode = TransfurMode.ABSORPTION;

        setDirty(player, variant);
        player.displayClientMessage(
                new TranslatableComponent("ability.changed.switch_transfur_mode.select", variant.transfurMode.toString()),
                true);
    }

    @Override
    public void saveData(CompoundTag tag, Player player, LatexVariantInstance<?> variant) {
        super.saveData(tag, player, variant);
        tag.putString("TransfurMode", variant.transfurMode.toString());
    }

    @Override
    public void readData(CompoundTag tag, Player player, LatexVariantInstance<?> variant) {
        super.readData(tag, player, variant);
        variant.transfurMode = TransfurMode.valueOf(tag.getString("TransfurMode"));
    }

    @Override
    public void tick(Player player, LatexVariantInstance<?> variant) {}

    @Override
    public void stopUsing(Player player, LatexVariantInstance<?> variant) {}

    @Override
    public ResourceLocation getTexture(Player player, LatexVariantInstance<?> variant) {
        if (variant.transfurMode == TransfurMode.NONE)
            return new ResourceLocation(getRegistryName().getNamespace(), "textures/abilities/" + getRegistryName().getPath() + "_replication.png");

        return new ResourceLocation(getRegistryName().getNamespace(), "textures/abilities/" + getRegistryName().getPath() + "_" +
                variant.transfurMode.toString().toLowerCase() + ".png");
    }
}
