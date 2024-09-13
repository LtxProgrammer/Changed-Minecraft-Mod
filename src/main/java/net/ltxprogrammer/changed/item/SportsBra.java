package net.ltxprogrammer.changed.item;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.ltxprogrammer.changed.init.ChangedTabs;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Wearable;
import org.jetbrains.annotations.Nullable;

public class SportsBra extends Item implements Wearable, Shorts, ExtendedItemProperties {
    public SportsBra() {
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
        //return Changed.modResourceStr("textures/models/benign_pants_" + Mth.clamp(stack.getDamageValue() - 1, 0, 4) + ".png");
        return Changed.modResourceStr("textures/models/sports_bra.png");
    }
}
