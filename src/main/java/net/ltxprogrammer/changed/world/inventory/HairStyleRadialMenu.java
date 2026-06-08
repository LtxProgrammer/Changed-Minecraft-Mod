package net.ltxprogrammer.changed.world.inventory;

import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.init.ChangedMenus;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class HairStyleRadialMenu extends AbstractContainerMenu {
    public static final Component CONTAINER_TITLE = Component.translatable("container.changed.radial_hairstyle");

    public final Level world;
    public final Player player;
    public final TransfurVariantInstance<?> variant;
    public int x, y, z;

    public HairStyleRadialMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        this(id, inv);
    }

    public HairStyleRadialMenu(int id, Inventory inv) {
        super(ChangedMenus.HAIRSTYLE_RADIAL.get(), id);
        this.world = inv.player.level();
        this.player = inv.player;
        this.variant = ProcessTransfur.getPlayerTransfurVariant(player);
    }

    @Override
    public ItemStack quickMoveStack(Player p_38941_, int p_38942_) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return ProcessTransfur.isPlayerTransfurred(player);
    }
}
