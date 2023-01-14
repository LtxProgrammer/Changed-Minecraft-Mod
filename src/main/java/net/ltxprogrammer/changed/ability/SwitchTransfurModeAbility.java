package net.ltxprogrammer.changed.ability;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.TransfurMode;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class SwitchTransfurModeAbility extends AbstractAbility {
    @Override
    public ResourceLocation getId() {
        return Changed.modResource("switch_transfur_mode");
    }

    @Override
    public boolean canUse(Player player, LatexVariant<?> variant) {
        return true;
    }

    @Override
    public boolean canKeepUsing(Player player, LatexVariant<?> variant) { return false; }

    @Override
    public void startUsing(Player player, LatexVariant<?> variant) {
        if (variant.transfurMode == TransfurMode.ABSORPTION)
            variant.transfurMode = TransfurMode.REPLICATION;
        else
            variant.transfurMode = TransfurMode.ABSORPTION;

        setDirty(player, variant);
        Changed.chatLogLocal("Transfur mode is now: " + variant.transfurMode);
    }

    @Override
    public void saveData(CompoundTag tag, Player player, LatexVariant<?> variant) {
        super.saveData(tag, player, variant);
        tag.putString("TransfurMode", variant.transfurMode.toString());
    }

    @Override
    public void readData(CompoundTag tag, Player player, LatexVariant<?> variant) {
        super.readData(tag, player, variant);
        variant.transfurMode = TransfurMode.valueOf(tag.getString("TransfurMode"));
    }

    @Override
    public void tick(Player player, LatexVariant<?> variant) {}

    @Override
    public void stopUsing(Player player, LatexVariant<?> variant) {}
}
