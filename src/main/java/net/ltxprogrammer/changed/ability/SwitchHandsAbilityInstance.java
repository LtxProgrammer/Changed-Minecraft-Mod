package net.ltxprogrammer.changed.ability;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class SwitchHandsAbilityInstance extends AbstractAbilityInstance {
    private final NonNullList<ItemStack> extraMainHandSlots = NonNullList.createWithCapacity(1);
    private final NonNullList<ItemStack> extraOffHandSlots = NonNullList.createWithCapacity(1);

    public SwitchHandsAbilityInstance(AbstractAbility<?> ability, IAbstractChangedEntity entity) {
        super(ability, entity);
        extraMainHandSlots.add(ItemStack.EMPTY);
        extraOffHandSlots.add(ItemStack.EMPTY);
    }

    public ItemStack getNextMainHandItem() {
        return extraMainHandSlots.isEmpty() ? ItemStack.EMPTY : extraMainHandSlots.get(0);
    }

    public ItemStack getNextOffHandItem() {
        return extraOffHandSlots.isEmpty() ? ItemStack.EMPTY : extraOffHandSlots.get(0);
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

        if (!entity.getLevel().isClientSide)
            ability.setDirty(entity);
    }

    @Override
    public void onRemove() {
        super.onRemove();
        extraMainHandSlots.forEach(this.entity::addItem);
        extraOffHandSlots.forEach(this.entity::addItem);
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
            outputItems.set(itemSlot, ItemStack.of((CompoundTag)itemTag));
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
}
