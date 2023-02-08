package net.ltxprogrammer.changed.item;

import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public interface LatexFusingItem extends WearableItem {
    LatexVariant<?> getFusionVariant(LatexVariant<?> currentVariant, LivingEntity livingEntity, ItemStack itemStack);

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
    class Event {
        @SubscribeEvent
        static void onVariantAssigned(ProcessTransfur.EntityVariantAssigned event) {
            if (event.isRedundant())
                return;
            if (event.variant == null)
                return;

            event.livingEntity.getArmorSlots().forEach(itemStack -> {
                if (itemStack.getItem() instanceof LatexFusingItem fusingItem) {
                    var newVariant = fusingItem.getFusionVariant(event.variant, event.livingEntity, itemStack);
                    if (newVariant == null) {
                        return;
                    }
                    itemStack.shrink(1);
                    ChangedSounds.broadcastSound(event.livingEntity, newVariant.sound, 1, 1);
                    event.variant = newVariant;
                }
            });
        }
    }
}
