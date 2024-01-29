package net.ltxprogrammer.changed.ability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;

public class SwitchHandsAbilityInstance extends AbstractAbilityInstance {
    public ItemStack extraLeftHand = ItemStack.EMPTY;
    public ItemStack extraRightHand = ItemStack.EMPTY;

    public SwitchHandsAbilityInstance(AbstractAbility<?> ability, IAbstractLatex entity) {
        super(ability, entity);
    }

    @Override
    public void onRemove() {
        super.onRemove();
        this.entity.addItem(extraLeftHand);
        this.entity.addItem(extraRightHand);
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
        boolean isRightHanded = entity.getEntity().getMainArm() == HumanoidArm.RIGHT;

        ItemStack mainHand = entity.getEntity().getMainHandItem();
        ItemStack offHand = entity.getEntity().getOffhandItem();

        entity.getEntity().setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        entity.getEntity().setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);

        entity.getEntity().setItemInHand(InteractionHand.MAIN_HAND, isRightHanded ? extraRightHand : extraLeftHand);
        entity.getEntity().setItemInHand(InteractionHand.OFF_HAND, isRightHanded ? extraLeftHand : extraRightHand);
        extraRightHand = isRightHanded ? mainHand : offHand;
        extraLeftHand = isRightHanded ? offHand : mainHand;

        ability.setDirty(entity);
    }

    @Override
    public void tick() {}

    @Override
    public void stopUsing() {}

    @Override
    public void saveData(CompoundTag tag) {
        super.saveData(tag);
        tag.put("extraLeftHand", extraLeftHand.serializeNBT());
        tag.put("extraRightHand", extraRightHand.serializeNBT());
    }

    @Override
    public void readData(CompoundTag tag) {
        super.readData(tag);

        if (tag.contains("extraLeftHand"))
            extraLeftHand = ItemStack.of(tag.getCompound("extraLeftHand"));
        if (tag.contains("extraRightHand"))
            extraRightHand = ItemStack.of(tag.getCompound("extraRightHand"));

        { // Compatibility with previous saves
            CompoundTag persistentData = entity.getPersistentData();
            if (persistentData.contains("changed:extra_hands_rh"))
                extraRightHand = ItemStack.of(persistentData.getCompound("changed:extra_hands_rh"));
            if (persistentData.contains("changed:extra_hands_lh"))
                extraLeftHand = ItemStack.of(persistentData.getCompound("changed:extra_hands_lh"));
            persistentData.remove("changed:extra_hands_rh");
            persistentData.remove("changed:extra_hands_lh");
        }
    }
}
