package net.ltxprogrammer.changed.item;

import net.ltxprogrammer.changed.data.AccessorySlots;
import net.ltxprogrammer.changed.entity.variant.ClothingShape;
import net.ltxprogrammer.changed.entity.variant.EntityShape;
import net.ltxprogrammer.changed.init.ChangedEnchantments;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEvent;

import javax.annotation.Nullable;

public interface ExtendedItemProperties {
    @Nullable
    default SoundEvent getEquipSound(ItemStack itemStack) {
        return SoundEvents.ARMOR_EQUIP_GENERIC;
    }

    default SoundEvent getBreakSound(ItemStack itemStack) {
        return SoundEvents.ITEM_BREAK;
    }

    default boolean customWearRenderer(ItemStack itemStack) {
        return false;
    }

    default boolean allowedInSlot(ItemStack itemStack, LivingEntity wearer, EquipmentSlot slot) {
        return switch (slot.getType()) {
            case ARMOR -> allowedToWear(itemStack, wearer, slot);
            case HAND -> true;
        };
    }

    // Should only be called with armor slots
    default boolean allowedToWear(ItemStack itemStack, LivingEntity wearer, EquipmentSlot slot) {
        if (!(itemStack.getEquipmentSlot() == slot || (itemStack.getItem() instanceof ArmorItem armorItem && armorItem.getEquipmentSlot() == slot)))
            return false;
        final EntityShape entityShape = EntityShape.getShapeOf(wearer).orElse(EntityShape.ANTHRO);
        return switch (slot) {
            case HEAD -> entityShape.getHeadShape() == getExpectedHeadShape(itemStack);
            case CHEST -> entityShape.getTorsoShape() == getExpectedTorsoShape(itemStack);
            case LEGS -> entityShape.getLegsShape() == getExpectedLegShape(itemStack);
            case FEET -> entityShape.getFeetShape() == getExpectedFeetShape(itemStack);
            default -> false;
        };
    }

    default ClothingShape.Head getExpectedHeadShape(ItemStack itemStack) {
        return ClothingShape.Head.ANTHRO;
    }

    default ClothingShape.Torso getExpectedTorsoShape(ItemStack itemStack) {
        return ClothingShape.Torso.ANTHRO;
    }

    default ClothingShape.Legs getExpectedLegShape(ItemStack itemStack) {
        return ClothingShape.Legs.BIPEDAL;
    }

    default ClothingShape.Feet getExpectedFeetShape(ItemStack itemStack) {
        return ClothingShape.Feet.BIPEDAL;
    }

    default void wearTick(ItemStack itemStack, LivingEntity wearer) {

    }

    static void handleTickEvent(LivingEvent.LivingTickEvent event) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (!slot.isArmor())
                continue;

            ItemStack itemStack = event.getEntity().getItemBySlot(slot);
            if (!(itemStack.getItem() instanceof ExtendedItemProperties extended))
                continue;

            if (!extended.allowedInSlot(itemStack, event.getEntity(), slot) &&
                    itemStack.getEnchantmentLevel(ChangedEnchantments.FORM_FITTING.get()) <= 0) {
                event.getEntity().setItemSlot(slot, ItemStack.EMPTY);
                AccessorySlots.defaultInvalidHandler(event.getEntity()).accept(itemStack);
                return;
            }

            extended.wearTick(itemStack, event.getEntity());
        }
    }
}
