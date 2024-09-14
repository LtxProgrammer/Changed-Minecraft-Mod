package net.ltxprogrammer.changed.item;

import net.ltxprogrammer.changed.init.ChangedSounds;
import net.ltxprogrammer.changed.init.ChangedTabs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Wearable;
import org.jetbrains.annotations.Nullable;

public class ClothingItem extends Item implements Wearable, Clothing, ExtendedItemProperties {
    public ClothingItem() {
        super(new Properties().tab(ChangedTabs.TAB_CHANGED_ITEMS).durability(5));
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return false;
    }

    @Override
    public SoundEvent getEquipSound() {
        return ChangedSounds.EQUIP3;
    }

    @Override
    public SoundEvent getBreakSound(ItemStack itemStack) {
        return ChangedSounds.SLASH10;
    }

    @Nullable
    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        ResourceLocation itemId = stack.getItem().getRegistryName();
        //return String.format("%s:textures/models/%s_%s.png", itemId.getNamespace(), itemId.getPath(), Mth.clamp(stack.getDamageValue() - 1, 0, 4));
        return String.format("%s:textures/models/%s.png", itemId.getNamespace(), itemId.getPath());
    }
}
