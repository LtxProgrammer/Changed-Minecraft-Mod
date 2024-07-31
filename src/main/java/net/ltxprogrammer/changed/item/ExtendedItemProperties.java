package net.ltxprogrammer.changed.item;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Arrays;

public interface ExtendedItemProperties {
    default SoundEvent getBreakSound(ItemStack itemStack) {
        return SoundEvents.ITEM_BREAK;
    }

    default boolean customWearRenderer(ItemStack itemStack) {
        return false;
    }

    default boolean allowedToWear(ItemStack itemStack, LivingEntity wearer) {
        var slot = itemStack.getEquipmentSlot();
        if (slot == null || slot.getType() != EquipmentSlot.Type.ARMOR)
            return false;

        return allowedToWear(itemStack, wearer, slot);
    }

    default boolean allowedToWear(ItemStack itemStack, LivingEntity wearer, EquipmentSlot slot) {
        return itemStack.getEquipmentSlot() == slot || (itemStack.getItem() instanceof ArmorItem armorItem && armorItem.getSlot() == slot);
    }

    default void wearTick(ItemStack itemStack, LivingEntity wearer) {

    }

    @Mod.EventBusSubscriber
    class Event {
        @SubscribeEvent
        public static void onEntityTick(LivingEvent.LivingUpdateEvent event) {
            Arrays.stream(EquipmentSlot.values()).filter(slot -> slot.getType() == EquipmentSlot.Type.ARMOR)
                            .forEach(slot -> {
                                var itemStack = event.getEntityLiving().getItemBySlot(slot);
                                if (itemStack.getItem() instanceof ExtendedItemProperties extended) {
                                    if (!extended.allowedToWear(itemStack, event.getEntityLiving(), slot)) {
                                        ItemStack nStack = itemStack.copy();
                                        itemStack.setCount(0);
                                        if (event.getEntityLiving() instanceof Player player)
                                            player.addItem(nStack);
                                        else
                                            Block.popResource(event.getEntityLiving().level, event.getEntityLiving().blockPosition(), nStack);
                                        return;
                                    }

                                    extended.wearTick(itemStack, event.getEntityLiving());
                                }
                            });
        }
    }
}
