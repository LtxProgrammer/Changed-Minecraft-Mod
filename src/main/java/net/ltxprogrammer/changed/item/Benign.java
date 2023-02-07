package net.ltxprogrammer.changed.item;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.init.ChangedItems;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.ltxprogrammer.changed.init.ChangedTabs;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantments;
import org.jetbrains.annotations.Nullable;

public class Benign implements ArmorMaterial {
    public static Benign INSTANCE = new Benign();

    @Override
    public int getDurabilityForSlot(EquipmentSlot slot) {
        return 5;
    }

    @Override
    public int getDefenseForSlot(EquipmentSlot slot) {
        return 2;
    }

    @Override
    public int getEnchantmentValue() {
        return 15;
    }

    @Override
    public SoundEvent getEquipSound() {
        return ChangedSounds.EQUIP3;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.of(ChangedItems.DARK_LATEX_GOO.get());
    }

    @Override
    public String getName() {
        return Changed.modResourceStr("benign");
    }

    @Override
    public float getToughness() {
        return 0;
    }

    @Override
    public float getKnockbackResistance() {
        return 0;
    }

    public static class Pants extends ArmorItem implements Shorts {
        public Pants() {
            super(INSTANCE, EquipmentSlot.LEGS, new Properties().tab(ChangedTabs.TAB_CHANGED_ITEMS));
        }

        @Nullable
        @Override
        public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
            return Changed.modResourceStr("textures/models/benign_pants.png");
        }
    }
}
