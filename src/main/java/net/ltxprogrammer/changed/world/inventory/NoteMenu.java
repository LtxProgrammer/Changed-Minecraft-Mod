package net.ltxprogrammer.changed.world.inventory;

import net.ltxprogrammer.changed.block.TextEnterable;
import net.ltxprogrammer.changed.init.ChangedMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.state.BlockState;

public class NoteMenu extends TextMenu {
    public NoteMenu(int id, Inventory inventory, BlockPos pos, BlockState state, TextEnterable textMenuBlockEntity) {
        super(ChangedMenus.NOTE, id, inventory, pos, state, textMenuBlockEntity);
    }

    public NoteMenu(int id, Inventory inventory, FriendlyByteBuf extraData) {
        super(ChangedMenus.NOTE, id, inventory, extraData);
    }
}
