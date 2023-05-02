package net.ltxprogrammer.changed.world.enchantments;

import net.ltxprogrammer.changed.init.ChangedEnchantments;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class TransfurResistanceEnchantment extends Enchantment {
    public TransfurResistanceEnchantment(Rarity rarity, EquipmentSlot[] slots) {
        super(rarity, EnchantmentCategory.ARMOR, slots);
    }

    public int getMaxLevel() {
        return 4;
    }

    public static float getTransfurResistance(LivingEntity entity, float progression) {
        int i = EnchantmentHelper.getEnchantmentLevel(ChangedEnchantments.TRANSFUR_RESISTANCE.get(), entity);
        if (i > 0) {
            progression -= progression * (float)i * 0.15F;
        }

        return progression;
    }
}
