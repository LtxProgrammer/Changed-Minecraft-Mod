package net.ltxprogrammer.changed.item;

import net.ltxprogrammer.changed.Changed;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

public interface Clothing {
    ArmorMaterial MATERIAL = new ArmorMaterial() {
        @Override
        public int getDurabilityForSlot(EquipmentSlot p_40410_) {
            return 5;
        }

        @Override
        public int getDefenseForSlot(EquipmentSlot p_40411_) {
            return 0;
        }

        @Override
        public int getEnchantmentValue() {
            return 0;
        }

        @Override
        public SoundEvent getEquipSound() {
            return null;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return null;
        }

        @Override
        public String getName() {
            return Changed.modResourceStr("clothing");
        }

        @Override
        public float getToughness() {
            return 0;
        }

        @Override
        public float getKnockbackResistance() {
            return 0;
        }
    };
}
