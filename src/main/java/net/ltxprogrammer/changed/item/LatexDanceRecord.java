package net.ltxprogrammer.changed.item;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.ltxprogrammer.changed.init.ChangedTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.RecordItem;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class LatexDanceRecord extends RecordItem {
    public LatexDanceRecord() {
        super(0, ChangedSounds.REGISTRY.get(Changed.modResource("latex_dance")),
                new Item.Properties().tab(ChangedTabs.TAB_CHANGED_ITEMS).stacksTo(1).rarity(Rarity.RARE));
    }
}
