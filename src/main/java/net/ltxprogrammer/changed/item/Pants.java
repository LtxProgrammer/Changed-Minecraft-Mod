package net.ltxprogrammer.changed.item;

import net.ltxprogrammer.changed.client.animations.LimbExtensions;
import net.ltxprogrammer.changed.client.animations.ModelPartIdentifier;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

public class Pants extends ClothingItem {
    @Override
    public void hideModelParts(ItemStack stack, Entity entity, EquipmentSlot renderSlot, Consumer<ModelPartIdentifier> partsToHide) {
        super.hideModelParts(stack, entity, renderSlot, partsToHide);
        partsToHide.accept(ModelPartIdentifier.forExtension(LimbExtensions.TAIL));
        partsToHide.accept(ModelPartIdentifier.forExtension(LimbExtensions.LEFT_LEG_PARTS, "foot"));
        partsToHide.accept(ModelPartIdentifier.forExtension(LimbExtensions.RIGHT_LEG_PARTS, "foot"));
    }

    @Override
    public SoundEvent getEquipSound(ItemStack itemStack) {
        return ChangedSounds.PANTS_EQUIP.get();
    }

    @Override
    public SoundEvent getBreakSound(ItemStack itemStack) {
        return ChangedSounds.PANTS_BREAK.get();
    }
}
