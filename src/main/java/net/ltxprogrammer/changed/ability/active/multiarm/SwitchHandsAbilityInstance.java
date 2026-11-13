package net.ltxprogrammer.changed.ability.active.multiarm;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.ability.AbstractAbilityInstance;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.init.ChangedVariantFeatures;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class SwitchHandsAbilityInstance extends AbstractAbilityInstance implements Container {
    private NonNullList<ItemStack> extraMainHandSlots = NonNullList.withSize(1, ItemStack.EMPTY);
    private NonNullList<ItemStack> extraOffHandSlots = NonNullList.withSize(1, ItemStack.EMPTY);
    private final List<NonNullList<ItemStack>> compartments = new ObjectArrayList<>();

    public SwitchHandsAbilityInstance(AbstractAbility<?> ability, IAbstractChangedEntity entity) {
        super(ability, entity);
    }

    public ItemStack getNextMainHandItem() {
        return getNthNextMainHandItem(0);
    }

    public ItemStack getNextOffHandItem() {
        return getNthNextOffHandItem(0);
    }

    public ItemStack getNthNextMainHandItem(int index) {
        if (extraMainHandSlots.size() <= index)
            return ItemStack.EMPTY;
        return extraMainHandSlots.get(index);
    }

    public ItemStack getNthNextOffHandItem(int index) {
        if (extraOffHandSlots.size() <= index)
            return ItemStack.EMPTY;
        return extraOffHandSlots.get(index);
    }

    public Stream<ItemStack> getMainHandItems() {
        return Stream.concat(
                Stream.of(entity.getEntity().getMainHandItem()),
                extraMainHandSlots.stream()
        );
    }

    public Stream<ItemStack> getOffHandItems() {
        return Stream.concat(
                Stream.of(entity.getEntity().getOffhandItem()),
                extraOffHandSlots.stream()
        );
    }

    public int getSlotCount() {
        return Math.max((int)entity.getFeatureLevel(ChangedVariantFeatures.SWITCH_HANDS_BONUS_HANDS.get()), 0) + 2;
    }

    protected void updateSlotCount() {
        int extraSlots = Math.max((int)entity.getFeatureLevel(ChangedVariantFeatures.SWITCH_HANDS_BONUS_HANDS.get()), 0);
        if ((extraMainHandSlots.size() + extraOffHandSlots.size() - 2) == extraSlots)
            return;

        var lastExtraMain = extraMainHandSlots;
        var lastExtraOff = extraOffHandSlots;

        int slotCountMain = 1 + (extraSlots / 2) + (extraSlots % 2);
        int slotCountOff = 1 + (extraSlots / 2);

        extraMainHandSlots = NonNullList.withSize(slotCountMain, ItemStack.EMPTY);
        extraOffHandSlots = NonNullList.withSize(slotCountOff, ItemStack.EMPTY);
        compartments.clear();
        compartments.add(extraMainHandSlots);
        compartments.add(extraOffHandSlots);

        for (int i = 0; i < lastExtraMain.size(); ++i) {
            var item = lastExtraMain.get(i);
            if (i < extraMainHandSlots.size())
                extraMainHandSlots.set(i, item);
            else
                this.addOrDrop(item);
        }

        for (int i = 0; i < lastExtraOff.size(); ++i) {
            var item = lastExtraOff.get(i);
            if (i < extraOffHandSlots.size())
                extraOffHandSlots.set(i, item);
            else
                this.addOrDrop(item);
        }
    }

    protected void cycle() {
        ItemStack mainHand = entity.getEntity().getMainHandItem();
        ItemStack offHand = entity.getEntity().getOffhandItem();

        entity.getEntity().setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        entity.getEntity().setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);

        entity.getEntity().setItemInHand(InteractionHand.MAIN_HAND, this.getNextMainHandItem());
        entity.getEntity().setItemInHand(InteractionHand.OFF_HAND, this.getNextOffHandItem());

        for (int i = 0; i < extraMainHandSlots.size(); ++i) {
            if (i == extraMainHandSlots.size() - 1) {
                extraMainHandSlots.set(i, mainHand);
            } else {
                extraMainHandSlots.set(i, extraMainHandSlots.get(i + 1));
            }
        }

        for (int i = 0; i < extraOffHandSlots.size(); ++i) {
            if (i == extraOffHandSlots.size() - 1) {
                extraOffHandSlots.set(i, offHand);
            } else {
                extraOffHandSlots.set(i, extraOffHandSlots.get(i + 1));
            }
        }
    }

    protected void addOrDrop(ItemStack item) {
        if (item.isEmpty())
            return;
        if (!entity.addItem(item))
            entity.drop(item, false, true);
    }

    @Override
    public void onRemove() {
        super.onRemove();
        extraMainHandSlots.forEach(this::addOrDrop);
        extraOffHandSlots.forEach(this::addOrDrop);
    }

    @Override
    public boolean canUse() {
        return true;
    }

    @Override
    public boolean canKeepUsing() {
        return false;
    }

    @Override
    public void startUsing() {
        this.cycle();
    }

    @Override
    public void tick() {}

    @Override
    public void tickIdle() {
        super.tickIdle();
        this.updateSlotCount();
    }

    @Override
    public void stopUsing() {}

    protected ListTag saveList(NonNullList<ItemStack> items) {
        ListTag tag = new ListTag();
        items.forEach(item -> tag.add(item.serializeNBT()));
        return tag;
    }

    protected void loadList(ListTag tag, NonNullList<ItemStack> outputItems) {
        AtomicInteger slot = new AtomicInteger(0);
        tag.forEach(itemTag -> {
            int itemSlot = slot.getAndIncrement();
            if (itemSlot < outputItems.size())
                outputItems.set(itemSlot, ItemStack.of((CompoundTag)itemTag));
            else
                this.addOrDrop(ItemStack.of((CompoundTag)itemTag));
        });
    }

    @Override
    public void saveData(CompoundTag tag) {
        super.saveData(tag);
        tag.put("mainHands", saveList(extraMainHandSlots));
        tag.put("offHands", saveList(extraOffHandSlots));
    }

    @Override
    public void readData(CompoundTag tag) {
        super.readData(tag);

        this.updateSlotCount();
        loadList(tag.getList("mainHands", 10), extraMainHandSlots);
        loadList(tag.getList("offHands", 10), extraOffHandSlots);

        var rightHandSlots = this.entity.getEntity().getMainArm() == HumanoidArm.RIGHT ? extraMainHandSlots : extraOffHandSlots;
        var leftHandSlots = this.entity.getEntity().getMainArm() == HumanoidArm.LEFT ? extraMainHandSlots : extraOffHandSlots;

        { // Compatibility with previous saves
            if (tag.contains("extraLeftHand"))
                rightHandSlots.set(0, ItemStack.of(tag.getCompound("extraLeftHand")));
            if (tag.contains("extraRightHand"))
                leftHandSlots.set(0, ItemStack.of(tag.getCompound("extraRightHand")));
        }

        { // Compatibility with even older saves
            CompoundTag persistentData = entity.getPersistentData();
            if (persistentData.contains("changed:extra_hands_rh"))
                rightHandSlots.set(0, ItemStack.of(persistentData.getCompound("changed:extra_hands_rh")));
            if (persistentData.contains("changed:extra_hands_lh"))
                leftHandSlots.set(0, ItemStack.of(persistentData.getCompound("changed:extra_hands_lh")));
            persistentData.remove("changed:extra_hands_rh");
            persistentData.remove("changed:extra_hands_lh");
        }
    }

    @Override
    public int getContainerSize() {
        return extraMainHandSlots.size() + extraOffHandSlots.size();
    }

    @Override
    public boolean isEmpty() {
        for (var slot : extraMainHandSlots)
            if (!slot.isEmpty())
                return false;

        for (var slot : extraOffHandSlots)
            if (!slot.isEmpty())
                return false;

        return true;
    }

    // Slot index will be staggered between main and offset (0, 1), (2, 3), (4, 5) etc.
    protected NonNullList<ItemStack> getListForIndex(int index) {
        return index % 2 == 0 ? extraMainHandSlots : extraOffHandSlots;
    }

    protected int getListIndexForIndex(int index) {
        return index / 2;
    }

    @Override
    public ItemStack getItem(int slotIndex) {
        return getListForIndex(slotIndex).get(getListIndexForIndex(slotIndex));
    }

    @Override
    public ItemStack removeItem(int slotIndex, int count) {
        List<ItemStack> list = getListForIndex(slotIndex);
        slotIndex = getListIndexForIndex(slotIndex);

        return list != null && !list.get(slotIndex).isEmpty() ? ContainerHelper.removeItem(list, slotIndex, count) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slotIndex) {
        NonNullList<ItemStack> list = getListForIndex(slotIndex);
        slotIndex = getListIndexForIndex(slotIndex);

        if (list != null && !list.get(slotIndex).isEmpty()) {
            ItemStack itemstack = list.get(slotIndex);
            list.set(slotIndex, ItemStack.EMPTY);
            return itemstack;
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public void setItem(int slotIndex, ItemStack itemStack) {
        NonNullList<ItemStack> list = getListForIndex(slotIndex);
        slotIndex = getListIndexForIndex(slotIndex);

        if (list != null) {
            list.set(slotIndex, itemStack);
        }
    }

    @Override
    public void setChanged() {

    }

    @Override
    public boolean stillValid(Player player) {
        if (this.entity.getEntity().isRemoved()) {
            return false;
        } else {
            return !(player.distanceToSqr(this.entity.getEntity()) > 64.0D);
        }
    }

    @Override
    public void clearContent() {
        this.extraMainHandSlots.clear();
        this.extraOffHandSlots.clear();
    }
}
