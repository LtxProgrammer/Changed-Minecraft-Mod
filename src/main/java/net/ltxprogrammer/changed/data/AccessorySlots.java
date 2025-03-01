package net.ltxprogrammer.changed.data;

import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.LivingEntityDataExtension;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AccessorySlots {
    private static final Logger LOGGER = LogManager.getLogger(AccessorySlots.class);

    private final Map<AccessorySlotType, ItemStack> items = new HashMap<>();
    private final List<ItemStack> invalidItems = new ArrayList<>();

    public static Optional<AccessorySlots> getForEntity(LivingEntity livingEntity) {
        if (livingEntity instanceof ChangedEntity changedEntity && changedEntity.getUnderlyingPlayer() != null)
            return getForEntity(changedEntity.getUnderlyingPlayer());
        if (livingEntity instanceof LivingEntityDataExtension extension)
            return extension.getAccessorySlots();
        return Optional.empty();
    }

    public static void onBrokenAccessory(LivingEntity livingEntity, AccessorySlotType slotType) {
        // TODO
    }

    /**
     * Call when the slot carrier may change configuration, causing
     * @param allowedSlots test for which slots may stay
     * @param removed consumer for itemStacks that are no longer valid
     */
    public boolean initialize(Predicate<AccessorySlotType> allowedSlots, Consumer<ItemStack> removed) {
        AtomicBoolean stateChanged = new AtomicBoolean(!invalidItems.isEmpty());

        invalidItems.forEach(removed);
        invalidItems.clear();

        for (var slotType : items.keySet().stream().filter(allowedSlots.negate()).collect(Collectors.toSet())) {
            removed.accept(items.get(slotType));
            items.remove(slotType);
            stateChanged.set(true);
        }

        ChangedRegistry.ACCESSORY_SLOTS.get().getValues().stream()
                .filter(allowedSlots)
                .forEach(slotType -> items.computeIfAbsent(slotType, x -> {
                    stateChanged.set(true);
                    return ItemStack.EMPTY;
                }));

        return stateChanged.getAcquire();
    }

    public boolean moveToSlot(AccessorySlotType slot, ItemStack stack) {
        if (!slot.canHoldItem(stack))
            return false;

        ItemStack oldStack = items.get(slot);
        if (oldStack.isEmpty()) {
            items.put(slot, stack.copy());
            stack.setCount(0);
        } else if (ItemStack.isSameItemSameTags(stack, oldStack)) {
            int maxMove = oldStack.getMaxStackSize() - oldStack.getCount();
            int toMove = Math.min(stack.getCount(), maxMove);
            oldStack.grow(toMove);
            stack.shrink(toMove);
        }

        return stack.isEmpty();
    }

    public boolean quickMoveStack(ItemStack stack) {
        for (var slot : items.keySet()) {
            if (moveToSlot(slot, stack))
                return true;
        }

        return stack.isEmpty();
    }

    public boolean hasSlot(AccessorySlotType slotType) {
        return items.containsKey(slotType);
    }

    public void forEachSlot(BiConsumer<AccessorySlotType, ItemStack> consumer) {
        for (var entry : items.entrySet())
            consumer.accept(entry.getKey(), entry.getValue());
    }

    public void forEachItem(Consumer<ItemStack> consumer) {
        for (var stack : items.values())
            if (!stack.isEmpty())
                consumer.accept(stack);
    }

    public Stream<ItemStack> getItems() {
        return items.values().stream().filter(stack -> !stack.isEmpty());
    }

    public @NotNull Optional<ItemStack> getItemInSlot(AccessorySlotType slotType) {
        if (items.containsKey(slotType))
            return Optional.of(items.get(slotType));
        return Optional.empty();
    }

    public <T> Stream<T> getMergedStream(BiFunction<AccessorySlotType, ItemStack, T> mapper) {
        return items.entrySet().stream().mapMulti((entry, sink) -> sink.accept(mapper.apply(entry.getKey(), entry.getValue())));
    }

    public boolean anyItemMatches(Predicate<ItemStack> predicate) {
        return getItems().anyMatch(predicate);
    }

    private void emptySlots() {
        items.keySet().forEach(slotType -> items.put(slotType, ItemStack.EMPTY));
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        forEachSlot((slotType, stack) -> tag.put(slotType.getRegistryName().toString(), stack.serializeNBT()));
        return tag;
    }

    public void load(CompoundTag tag) {
        this.emptySlots();
        tag.getAllKeys().forEach(key -> {
            ResourceLocation id = new ResourceLocation(key);
            ItemStack value = ItemStack.of(tag.getCompound(key));
            if (!ChangedRegistry.ACCESSORY_SLOTS.get().containsKey(id)) {
                invalidItems.add(value);
                LOGGER.warn("Could not find slot of type {}", id);
                return;
            }

            AccessorySlotType slotType = ChangedRegistry.ACCESSORY_SLOTS.get().getValue(new ResourceLocation(key));
            items.put(slotType, value);
        });
    }

    public void writeNetwork(FriendlyByteBuf buffer) {
        buffer.writeMap(items,
                (byteBuf, slotType) -> byteBuf.writeInt(ChangedRegistry.ACCESSORY_SLOTS.get().getID(slotType)),
                (byteBuf, stack) -> byteBuf.writeNbt(stack.serializeNBT()));
    }

    public void readNetwork(FriendlyByteBuf buffer) {
        this.emptySlots();
        items.putAll(buffer.readMap(
                byteBuf -> ChangedRegistry.ACCESSORY_SLOTS.get().getValue(byteBuf.readInt()),
                byteBuf -> ItemStack.of(byteBuf.readAnySizeNbt())
        ));
    }

    public void setAll(AccessorySlots other) {
        this.emptySlots();
        items.putAll(other.items);
    }

    public static Consumer<ItemStack> defaultInvalidHandler(LivingEntity entity) {
        if (entity instanceof Player player)
            return stack -> ItemHandlerHelper.giveItemToPlayer(player, stack);
        else
            return stack -> {
                ItemEntity itemEntity = new ItemEntity(entity.level, entity.getX(), entity.getY() + 0.5, entity.getZ(), stack);
                itemEntity.setPickUpDelay(40);
                itemEntity.setDeltaMovement(itemEntity.getDeltaMovement().multiply(0, 1, 0));

                entity.level.addFreshEntity(itemEntity);
            };
    }
}
