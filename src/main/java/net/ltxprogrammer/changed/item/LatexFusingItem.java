package net.ltxprogrammer.changed.item;

import net.ltxprogrammer.changed.init.ChangedTags;
import net.ltxprogrammer.changed.process.TransfurEvents;
import net.ltxprogrammer.changed.util.ItemUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public interface LatexFusingItem extends ExtendedItemProperties {
    boolean handleLatexAssimilation(TransfurEvents.AssimilationDecisionEvent event, ItemStack itemStack);

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
    class EventHandler {
        @SubscribeEvent
        static void onAssimilationDecision(TransfurEvents.AssimilationDecisionEvent event) {
            if (!event.getTransfurVariant().is(ChangedTags.TransfurVariants.LATEX))
                return;

            ItemUtil.getWearingItems(event.getEntity()).forEach(slottedItem -> {
                if (slottedItem.itemStack().getItem() instanceof LatexFusingItem fusingItem) {
                    boolean consumeItem = fusingItem.handleLatexAssimilation(event, slottedItem.itemStack());
                    if (consumeItem) {
                        event.appendTransfurListener(entity -> {
                            slottedItem.itemStack().shrink(1);
                        });
                    }
                }
            });
        }
    }
}
