package net.ltxprogrammer.changed.item;

import net.ltxprogrammer.changed.init.ChangedTabs;
import net.minecraft.world.item.Item;

public class AbstractLatexItem extends Item {
    public AbstractLatexItem() {
        super(new Properties().tab(ChangedTabs.TAB_CHANGED_ITEMS));
    }
}
