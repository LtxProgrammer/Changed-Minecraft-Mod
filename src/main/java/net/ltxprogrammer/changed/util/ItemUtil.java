package net.ltxprogrammer.changed.util;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import net.ltxprogrammer.changed.data.AccessorySlotType;
import net.ltxprogrammer.changed.data.AccessorySlots;
import net.ltxprogrammer.changed.entity.TransfurCause;
import net.ltxprogrammer.changed.entity.TransfurContext;
import net.ltxprogrammer.changed.init.ChangedItems;
import net.ltxprogrammer.changed.init.ChangedTransfurVariants;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ItemUtil {
    public record SlottedItem(Either<EquipmentSlot, AccessorySlotType> slot, ItemStack itemStack) {
        SlottedItem(EquipmentSlot slot, ItemStack stack) {
            this(Either.left(slot), stack);
        }

        SlottedItem(AccessorySlotType slot, ItemStack stack) {
            this(Either.right(slot), stack);
        }
    }

    public static boolean tryEquipAccessory(LivingEntity entity, ItemStack stack, AccessorySlotType slotType) {
        return AccessorySlots.getForEntity(entity)
                .map(slots -> slots.moveToSlot(slotType, stack)).orElse(false);
    }

    public static Optional<SlottedItem> isWearingItem(LivingEntity entity, Item item) {
        return isWearingItem(entity, stack -> stack.is(item));
    }

    public static Optional<SlottedItem> isWearingItem(LivingEntity entity, Predicate<ItemStack> filter) {
        return getWearingItems(entity, filter).findFirst();
    }

    public static Stream<SlottedItem> getWearingItems(LivingEntity entity) {
        return Stream.concat(
                Arrays.stream(EquipmentSlot.values()).filter(slot -> slot.getType() == EquipmentSlot.Type.ARMOR)
                                .map(slot -> new SlottedItem(slot, entity.getItemBySlot(slot))),

                AccessorySlots.getForEntity(entity).stream().flatMap(slots -> slots.getMergedStream(SlottedItem::new))
        );
    }

    public static Stream<SlottedItem> getWearingItems(LivingEntity entity, Predicate<ItemStack> filter) {
        return getWearingItems(entity).filter(slottedItem -> filter.test(slottedItem.itemStack));
    }

    public static Stream<SlottedItem> getWearingItems(LivingEntity entity, TagKey<Item> tag) {
        return getWearingItems(entity, itemStack -> itemStack.is(tag));
    }
}
