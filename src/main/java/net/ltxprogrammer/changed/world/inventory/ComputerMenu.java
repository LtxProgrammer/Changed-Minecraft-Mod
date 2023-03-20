package net.ltxprogrammer.changed.world.inventory;

import net.ltxprogrammer.changed.block.TextEnterable;
import net.ltxprogrammer.changed.init.ChangedMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ComputerMenu extends TextMenu implements SpecialLoadingMenu {
    private ItemStack serverDisk;

    private final Map<Integer, Slot> customSlots = new HashMap<>();

    public ComputerMenu(int id, Inventory inventory, BlockPos pos, BlockState state, TextEnterable textMenuBlockEntity) {
        super(ChangedMenus.COMPUTER, id, inventory, pos, state, textMenuBlockEntity);
    }

    public ComputerMenu(int id, Inventory inventory, FriendlyByteBuf extraData) {
        super(ChangedMenus.COMPUTER, id, inventory, extraData);
    }

    public ComputerMenu setDisk(ItemStack disk) {
        serverDisk = disk;
        return this;
    }

    public ItemStack getDisk() {
        return this.getSlot(0).getItem();
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }

    @Override
    public void afterInit(AbstractContainerMenu menu) {
        this.setDirty(serverDisk.serializeNBT());
    }

    @Override
    public void update(CompoundTag payload, LogicalSide receiver) {
        if (receiver.isServer() && serverDisk != null) {
            if (payload.contains("Text")) {
                textCopy = payload.getString("Text");
                serverDisk.getOrCreateTag().putString("Text", textCopy);
            } else {
                this.setDirty(serverDisk.serializeNBT());
            }
        } else if (receiver.isClient()) {
            serverDisk = ItemStack.of(payload);
            textCopy = serverDisk.getOrCreateTag().getString("Text");
        }
    }
}
