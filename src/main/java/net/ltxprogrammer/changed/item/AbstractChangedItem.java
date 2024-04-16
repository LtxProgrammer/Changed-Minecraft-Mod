package net.ltxprogrammer.changed.item;

import net.ltxprogrammer.changed.init.ChangedTabs;
import net.minecraft.world.item.Item;

public class AbstractChangedItem extends Item {
    public AbstractChangedItem() {
        super(new Properties().tab(ChangedTabs.TAB_CHANGED_ITEMS));
    }
}
