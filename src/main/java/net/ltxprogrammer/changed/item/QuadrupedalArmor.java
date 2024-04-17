package net.ltxprogrammer.changed.item;

import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.init.ChangedTabs;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

public class QuadrupedalArmor extends ArmorItem implements WearableItem {
    public static boolean useQuadrupedalModel(EquipmentSlot slot) {
        return slot == EquipmentSlot.LEGS || slot == EquipmentSlot.FEET;
    }

    public static boolean useInnerQuadrupedalModel(EquipmentSlot slot) {
        return slot == EquipmentSlot.LEGS;
    }

    public static ArmorMaterial increaseDurability(ArmorMaterial material) {
        return new ArmorMaterial() {
            @Override
            public int getDurabilityForSlot(EquipmentSlot slot) {
                return (int)((float)material.getDurabilityForSlot(slot) * 1.5f);
            }

            @Override
            public int getDefenseForSlot(EquipmentSlot slot) {
                return material.getDefenseForSlot(slot) + 2;
            }

            @Override
            public int getEnchantmentValue() {
                return material.getEnchantmentValue();
            }

            @Override
            public SoundEvent getEquipSound() {
                return material.getEquipSound();
            }

            @Override
            public Ingredient getRepairIngredient() {
                return material.getRepairIngredient();
            }

            @Override
            public String getName() {
                return material.getName();
            }

            @Override
            public float getToughness() {
                return material.getToughness();
            }

            @Override
            public float getKnockbackResistance() {
                return material.getKnockbackResistance();
            }
        };
    }

    public QuadrupedalArmor(ArmorMaterial material, EquipmentSlot slot) {
        super(increaseDurability(material), slot, new Properties().tab(ChangedTabs.TAB_CHANGED_COMBAT));
    }

    public QuadrupedalArmor(ArmorMaterial material, EquipmentSlot slot, Properties properties) {
        super(increaseDurability(material), slot, properties);
    }

    @Override
    public boolean canWalkOnPowderedSnow(ItemStack stack, LivingEntity wearer) {
        return material.getName().equals(ArmorMaterials.LEATHER.getName()) && slot == EquipmentSlot.FEET;
    }

    @Override
    public void wearTick(LivingEntity entity, ItemStack itemStack) {}

    @Override
    public boolean customWearRenderer() {
        return false;
    }

    @Override
    public boolean allowedToKeepWearing(LivingEntity entity) {
        var variant = TransfurVariant.getEntityVariant(entity);
        return variant != null && variant.legCount == 4;
    }

    @Nullable
    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, @Nullable String type) {
        String texture = this.material.getName();
        String domain = "changed";
        int idx = texture.indexOf(':');
        if (idx != -1) {
            domain = texture.substring(0, idx);
            texture = texture.substring(idx + 1);
        }
        return String.format(java.util.Locale.ROOT, "%s:textures/models/quadrupedal_armor/%s_layer_%d%s.png", domain, texture, (useInnerQuadrupedalModel(slot) ? 2 : 1),
                type == null ? "" : String.format(java.util.Locale.ROOT, "_%s", type));
    }
}
