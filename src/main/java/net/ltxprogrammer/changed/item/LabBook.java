package net.ltxprogrammer.changed.item;

import net.ltxprogrammer.changed.init.ChangedBlocks;
import net.ltxprogrammer.changed.init.ChangedTabs;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.level.block.Block;

public class LabBook extends ItemNameBlockItem {
    public LabBook() {
        super(ChangedBlocks.BOOK_STACK.get(), new Properties().tab(ChangedTabs.TAB_CHANGED_ITEMS));
    }
}
