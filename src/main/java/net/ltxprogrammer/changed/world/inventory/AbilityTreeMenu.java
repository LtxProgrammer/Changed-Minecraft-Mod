package net.ltxprogrammer.changed.world.inventory;

import net.ltxprogrammer.changed.ability.tree.AbilityTreeInstance;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.init.ChangedMenus;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class AbilityTreeMenu extends AbstractContainerMenu {
    public final TransfurVariantInstance<?> variant;
    public final AbilityTreeInstance abilityTree;

    public AbilityTreeMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        this(id, inv);
    }

    public AbilityTreeMenu(int id, Inventory inv) {
        super(ChangedMenus.ABILITY_TREE.get(), id);
        this.variant = ProcessTransfur.getPlayerTransfurVariant(inv.player);
        this.abilityTree = AbilityTreeInstance.getForPlayer(inv.player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slot) {
        return null;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
