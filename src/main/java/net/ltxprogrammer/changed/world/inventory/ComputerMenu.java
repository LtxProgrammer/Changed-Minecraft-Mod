package net.ltxprogrammer.changed.world.inventory;

import net.ltxprogrammer.changed.block.TextEnterable;
import net.ltxprogrammer.changed.init.ChangedMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ComputerMenu extends TextMenu {
    private ItemStack serverDisk;

    private final Map<Integer, Slot> customSlots = new HashMap<>();

    public ComputerMenu(int id, Inventory inventory, BlockPos pos, BlockState state, TextEnterable textMenuBlockEntity) {
        super(ChangedMenus.COMPUTER, id, inventory, pos, state, textMenuBlockEntity);
    }

    public ComputerMenu(int id, Inventory inventory, ItemStack disk) {
        super(ChangedMenus.COMPUTER, id, inventory, null);
        this.serverDisk = disk;
        inventory.removeItem(serverDisk);
        textCopy = disk.getOrCreateTag().getString("Text");
    }

    public ComputerMenu(int id, Inventory inventory, FriendlyByteBuf extraData) {
        super(ChangedMenus.COMPUTER, id, inventory, extraData);

        if (extraData != null) {
            serverDisk = null;
            //inventory.removeItem(extraData.readInt(), 1);
        }
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }

    @Override
    public boolean canEditExisting() {
        return true;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        player.addItem(serverDisk);
    }

    @Override
    public void update(CompoundTag payload, LogicalSide receiver) {
        if (receiver.isServer() && serverDisk != null) {
            if (payload.contains("Text")) {
                textCopy = payload.getString("Text");
                textCopyLastReceived = textCopy;
                serverDisk.getOrCreateTag().putString("Text", textCopy);
            }

            this.setDirty(payload);
        } else if (receiver.isClient()) {
            /*textCopy = payload.getString("Text");
            textCopyLastReceived = textCopy;
            serverDisk.getOrCreateTag().putString("Text", textCopy);*/

            // Handled by the server
        }
    }
}
