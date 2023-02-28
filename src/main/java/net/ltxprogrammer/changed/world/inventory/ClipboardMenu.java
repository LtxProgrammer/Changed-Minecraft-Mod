package net.ltxprogrammer.changed.world.inventory;

import net.ltxprogrammer.changed.block.TextEnterable;
import net.ltxprogrammer.changed.init.ChangedMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.state.BlockState;

public class ClipboardMenu extends TextMenu {
    public ClipboardMenu(int id, Inventory inventory, BlockPos pos, BlockState state, TextEnterable textMenuBlockEntity) {
        super(ChangedMenus.CLIPBOARD, id, inventory, pos, state, textMenuBlockEntity);
    }

    public ClipboardMenu(int id, Inventory inventory, FriendlyByteBuf extraData) {
        super(ChangedMenus.CLIPBOARD, id, inventory, extraData);
    }
}
