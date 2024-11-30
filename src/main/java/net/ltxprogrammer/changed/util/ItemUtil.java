package net.ltxprogrammer.changed.util;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
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
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ItemUtil {
    public record SlottedItem(Either<EquipmentSlot, SlotContext> slot, ItemStack itemStack) {}

    public static boolean tryEquipCurio(LivingEntity entity, ItemStack stack, String slot) {
        return CuriosApi.getCuriosHelper().getCuriosHandler(entity).resolve()
                .flatMap(handler -> handler.getStacksHandler(slot))
                .map(ICurioStacksHandler::getStacks)
                .map(handler -> {
                    for (int i = 0; i < handler.getSlots(); ++i) {
                        if (!handler.getStackInSlot(i).isEmpty()) continue;

                        ItemStack copied = stack.copy();
                        copied.setCount(1);
                        handler.setStackInSlot(i, copied);
                        return true;
                    }

                    return false;
                }).orElse(false);
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
                                .map(slot -> new SlottedItem(Either.left(slot), entity.getItemBySlot(slot))),

                CuriosApi.getCuriosHelper().getCuriosHandler(entity).resolve()
                        .map(handler -> Pair.of(handler.getWearer(), handler.getCurios().values().stream())).stream()
                        .flatMap(entityStreamPair -> entityStreamPair.getSecond().flatMap(handler -> {
                            final var stacks = handler.getStacks();
                            return Stream.iterate(0, idx -> idx < stacks.getSlots(), idx -> idx + 1)
                                    .map(idx -> new SlottedItem(
                                            Either.right(new SlotContext(handler.getIdentifier(), entityStreamPair.getFirst(), idx, handler.hasCosmetic(), handler.isVisible())),
                                            stacks.getStackInSlot(idx)));
                        }))
        );
    }

    public static Stream<SlottedItem> getWearingItems(LivingEntity entity, Predicate<ItemStack> filter) {
        return getWearingItems(entity).filter(slottedItem -> filter.test(slottedItem.itemStack));
    }

    public static Stream<SlottedItem> getWearingItems(LivingEntity entity, TagKey<Item> tag) {
        return getWearingItems(entity, itemStack -> itemStack.is(tag));
    }
}
