package net.ltxprogrammer.changed.data;

import com.google.common.collect.Multimap;
import com.mojang.datafixers.util.Pair;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.LivingEntityDataExtension;
import net.ltxprogrammer.changed.extension.ChangedCompatibility;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.network.packet.AccessoryEventPacket;
import net.ltxprogrammer.changed.util.Cacheable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.network.PacketDistributor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AccessorySlots implements Container {
    private static final Logger LOGGER = LogManager.getLogger(AccessorySlots.class);

    public final @Nullable LivingEntity owner;
    private final Map<AccessorySlotType, ItemStack> items = new HashMap<>();
    private final Map<AccessorySlotType, ItemStack> lastItems = new HashMap<>();
    private final List<ItemStack> invalidItems = new ArrayList<>();
    private final Cacheable<List<AccessorySlotType>> orderedSlots = Cacheable.of(() -> {
        final var registry = ChangedRegistry.ACCESSORY_SLOTS.get();
        final var sorted = new ArrayList<>(registry.getValues().stream()
                .filter(items::containsKey)
                .map(slotType -> Pair.of(slotType, registry.getID(slotType)))
                .toList());
        sorted.sort(Comparator.comparingInt(Pair::getSecond));
        return sorted.stream().map(Pair::getFirst).toList();
    });

    public static final AccessorySlots DUMMY = new AccessorySlots(null);

    public AccessorySlots(@Nullable LivingEntity owner) {
        this.owner = owner;
    }

    public AccessorySlots() {
        this.owner = null;
    }

    /**
     * Slots are indexed in the order they are registered
     * @param slot
     * @return
     */
    public @Nullable AccessorySlotType getSlotTypeByIndex(int slot) {
        var sorted = orderedSlots.get();
        if (sorted.isEmpty() || sorted.size() <= slot)
            return null;
        return sorted.get(slot);
    }

    public @Nullable Integer getSlotIndexByType(AccessorySlotType slotType) {
        var sorted = orderedSlots.get();
        for (int i = 0; i < sorted.size(); ++i)
            if (sorted.get(i) == slotType)
                return i;
        return null;
    }

    public static Optional<AccessorySlots> getForEntity(LivingEntity livingEntity) {
        if (livingEntity instanceof ChangedEntity changedEntity && changedEntity.getUnderlyingPlayer() != null)
            return getForEntity(changedEntity.getUnderlyingPlayer());
        if (livingEntity instanceof LivingEntityDataExtension extension)
            return extension.getAccessorySlots();
        return Optional.empty();
    }

    public static void onBrokenAccessory(LivingEntity livingEntity, AccessorySlotType slotType) {
        if (livingEntity.level.isClientSide)
            return;

        Changed.PACKET_HANDLER.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> livingEntity),
                new AccessoryEventPacket(livingEntity.getId(), slotType, 1));
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

        orderedSlots.clear();
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

    public void getAttributeModifiers(Consumer<Multimap<Attribute, AttributeModifier>> modifierConsumer) {
        forEachSlot((slotType, stack) -> modifierConsumer.accept(stack.getAttributeModifiers(slotType.getEquivalentSlot())));
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

    public Stream<AccessorySlotType> getSlotTypes() {
        return items.keySet().stream();
    }

    public @NotNull Optional<ItemStack> getItem(AccessorySlotType slotType) {
        if (items.containsKey(slotType))
            return Optional.of(items.get(slotType));
        return Optional.empty();
    }

    public ItemStack getLastItem(AccessorySlotType slotType) {
        return Objects.requireNonNullElse(lastItems.get(slotType), ItemStack.EMPTY);
    }

    public void setLastItem(AccessorySlotType slotType, ItemStack stack) {
        lastItems.put(slotType, stack);
    }

    public Set<AccessorySlotType> getAccessorySlotsForItem(ItemStack stack) {
        return items.keySet().stream().filter(slotType -> slotType.canHoldItem(stack)).collect(Collectors.toSet());
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
            orderedSlots.clear();
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

    public void setAll(AccessorySlots other, boolean empty) {
        if (empty) this.emptySlots();
        items.putAll(other.items);
    }

    public static Consumer<ItemStack> defaultInvalidHandler(LivingEntity entity) {
        if (entity instanceof Player player)
            return stack -> ItemHandlerHelper.giveItemToPlayer(player, stack);
        else
            return dropItemHandler(entity);
    }

    public static Consumer<ItemStack> dropItemHandler(LivingEntity entity) {
        return stack -> {
            if (stack.isEmpty()) return;

            ItemEntity itemEntity = new ItemEntity(entity.level, entity.getX(), entity.getY() + 0.5, entity.getZ(), stack);
            itemEntity.setPickUpDelay(40);
            itemEntity.setDeltaMovement(itemEntity.getDeltaMovement().multiply(0, 1, 0));

            entity.level.addFreshEntity(itemEntity);
        };
    }

    @Override
    public int getContainerSize() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        return items.values().stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getItem(int id) {
        // Default to empty stack, while GUI re-evaluates
        return Objects.requireNonNullElse(items.get(getSlotTypeByIndex(id)), ItemStack.EMPTY);
    }

    @Override
    public ItemStack removeItem(int id, int count) {
        final AccessorySlotType slotType = getSlotTypeByIndex(id);
        return slotType != null && !items.get(slotType).isEmpty() && count > 0 ? items.get(slotType).split(count) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItemNoUpdate(int id) {
        final var slotType = getSlotTypeByIndex(id);
        final var taken = items.get(slotType);
        if (taken.isEmpty())
            return ItemStack.EMPTY;

        items.put(slotType, ItemStack.EMPTY);
        return taken;
    }

    public void setItem(AccessorySlotType slotType, ItemStack stack) {
        if (items.keySet().contains(slotType))
            items.put(slotType, stack);
    }

    @Override
    public void setItem(int id, ItemStack stack) {
        items.put(getSlotTypeByIndex(id), stack);
    }

    @Override
    public void setChanged() {

    }

    @Override
    public boolean stillValid(Player player) {
        if (this.owner == null || this.owner.isRemoved()) {
            return false;
        } else {
            return !(player.distanceToSqr(this.owner) > 64.0D);
        }
    }

    @Override
    public void clearContent() {
        items.clear();
    }

    public void replaceWith(@Nullable AccessorySlots other) {
        this.invalidItems.addAll(this.items.values());
        this.items.clear();

        if (other != null)
            this.items.putAll(other.items);
    }

    public void dropAll(Consumer<ItemStack> consumer) {
        this.items.entrySet().stream().filter(entry -> {
            final var event = new DropItemEvent(this.owner, entry.getKey(), entry.getValue());
            ChangedCompatibility.shouldAccessoryDropOnDeath(event);
            Changed.postModEvent(event);
            return event.shouldDrop();
        }).forEach(entry -> {
            consumer.accept(entry.getValue());
            entry.setValue(ItemStack.EMPTY);
        });
    }

    public static class DropItemEvent extends Event {
        protected final LivingEntity entity;
        protected final AccessorySlotType slot;
        protected final ItemStack stack;
        protected boolean doDrop = true;

        public DropItemEvent(LivingEntity entity, AccessorySlotType slot, ItemStack stack) {
            this.entity = entity;
            this.slot = slot;
            this.stack = stack;
        }

        public LivingEntity getEntity() {
            return entity;
        }

        public ItemStack getStack() {
            return stack;
        }

        public void keepItem() {
            this.doDrop = false;
        }

        public void dropItem() {
            this.doDrop = true;
        }

        public boolean shouldDrop() {
            return doDrop;
        }
    }
}
