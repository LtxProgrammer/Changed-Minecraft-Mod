package net.ltxprogrammer.changed.item;

import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.ltxprogrammer.changed.process.TransfurEvents;
import net.ltxprogrammer.changed.util.ItemUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public interface LatexFusingItem extends ExtendedItemProperties {
    TransfurVariant<?> getFusionVariant(TransfurVariant<?> currentVariant, LivingEntity livingEntity, ItemStack itemStack);

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
    class Event {
        @SubscribeEvent
        static void onAssimilationDecision(TransfurEvents.AssimilationDecisionEvent event) {
            if (!event.getTransfurVariant().is(ChangedTags.TransfurVariants.LATEX))
                return;

            final var oldVariant = event.getTransfurVariant();

            ItemUtil.getWearingItems(event.getEntity()).forEach(slottedItem -> {
                if (slottedItem.itemStack().getItem() instanceof LatexFusingItem fusingItem) {
                    var newVariant = fusingItem.getFusionVariant(event.getTransfurVariant(), event.getEntity(), slottedItem.itemStack());
                    if (newVariant == null) {
                        return;
                    }

                    event.setTransfurVariant(newVariant);
                    event.appendTransfurListener(entity -> {
                        slottedItem.itemStack().shrink(1);
                    });
                }
            });

            if (event.getTransfurVariant() != oldVariant) {
                ChangedSounds.broadcastSound(event.getEntity(), event.getTransfurVariant().sound, 1, 1);
            }
        }
    }
}
