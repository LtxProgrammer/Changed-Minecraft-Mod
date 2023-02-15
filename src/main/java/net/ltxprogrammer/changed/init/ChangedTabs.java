package net.ltxprogrammer.changed.init;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ChangedTabs {
    public static CreativeModeTab TAB_CHANGED_BLOCKS = new CreativeModeTab("tab_changed_blocks") {
        @Override
        public ItemStack makeIcon() { return new ItemStack(ChangedBlocks.WALL_LIGHTRED_STRIPED.get()); }
    };
    public static CreativeModeTab TAB_CHANGED_ITEMS = new CreativeModeTab("tab_changed_items") {
        @Override
        public ItemStack makeIcon() { return new ItemStack(ChangedItems.LATEX_BASE.get()); }
    };
    public static CreativeModeTab TAB_CHANGED_ENTITIES = new CreativeModeTab("tab_changed_entities") {
        @Override
        public ItemStack makeIcon() { return new ItemStack(ChangedItems.DARK_LATEX_MASK.get()); }
    };
    public static CreativeModeTab TAB_CHANGED_COMBAT = new CreativeModeTab("tab_changed_combat") {
        @Override
        public ItemStack makeIcon() { return new ItemStack(ChangedItems.TSC_BATON.get()); }
    };
}
