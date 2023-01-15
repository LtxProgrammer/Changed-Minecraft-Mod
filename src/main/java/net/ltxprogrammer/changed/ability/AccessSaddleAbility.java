package net.ltxprogrammer.changed.ability;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.init.ChangedMenus;
import net.ltxprogrammer.changed.network.packet.SyncTransfurPacket;
import net.ltxprogrammer.changed.world.inventory.CentaurSaddleMenu;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.PacketDistributor;

import static net.ltxprogrammer.changed.world.inventory.CentaurSaddleMenu.SADDLE_LOCATION;

public class AccessSaddleAbility extends SimpleAbility {
    @Override
    public ResourceLocation getId() {
        return Changed.modResource("access_saddle");
    }

    @Override
    public boolean canUse(Player player, LatexVariant<?> variant) {
        return player.containerMenu == player.inventoryMenu;
    }

    @Override
    public boolean canKeepUsing(Player player, LatexVariant<?> variant) {
        return player.containerMenu instanceof CentaurSaddleMenu;
    }

    @Override
    public void startUsing(Player player, LatexVariant<?> variant) {
        player.openMenu(new SimpleMenuProvider((p_52229_, p_52230_, p_52231_) ->
                new CentaurSaddleMenu(p_52229_, p_52230_, null), CentaurSaddleMenu.CONTAINER_TITLE));
    }

    @Override
    public void tick(Player player, LatexVariant<?> variant) {
        ((CentaurSaddleMenu)player.containerMenu).tick(player);
    }

    @Override
    public void stopUsing(Player player, LatexVariant<?> variant) {
        player.closeContainer();
    }

    @Override
    public void onRemove(Player player, LatexVariant<?> variant) {
        CompoundTag tag = player.getPersistentData();
        if (tag.contains(SADDLE_LOCATION))
            player.drop(ItemStack.of(tag.getCompound(SADDLE_LOCATION)), true);
        tag.remove(SADDLE_LOCATION);
    }
}
