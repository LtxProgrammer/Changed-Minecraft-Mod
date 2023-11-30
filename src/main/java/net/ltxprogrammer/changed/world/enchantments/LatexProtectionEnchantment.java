package net.ltxprogrammer.changed.world.enchantments;

import net.ltxprogrammer.changed.init.ChangedEnchantments;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

public class LatexProtectionEnchantment extends Enchantment {
    public LatexProtectionEnchantment(Rarity rarity, EquipmentSlot[] slots) {
        super(rarity, EnchantmentCategory.ARMOR, slots);
    }

    public int getMaxLevel() {
        return 4;
    }

    public static float getLatexProtection(LivingEntity entity, float progression) {
        int protection = EnchantmentHelper.getEnchantmentLevel(Enchantments.ALL_DAMAGE_PROTECTION, entity);
        int tfResistance = EnchantmentHelper.getEnchantmentLevel(ChangedEnchantments.TRANSFUR_RESISTANCE.get(), entity);

        float tfResistanceDiscount = progression * (float)tfResistance * 0.15F;
        float protectionDiscount = progression * (float)protection * 0.075F;

        return progression - Math.max(tfResistanceDiscount, protectionDiscount);
    }
}
