package net.ltxprogrammer.changed.item;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.init.ChangedItems;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.ltxprogrammer.changed.init.ChangedTabs;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

public class PinkLatex implements ArmorMaterial {
    public static PinkLatex INSTANCE = new PinkLatex();

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
        return Ingredient.of();
    }

    @Override
    public String getName() {
        return Changed.modResourceStr("pink");
    }

    @Override
    public float getToughness() {
        return 0;
    }

    @Override
    public float getKnockbackResistance() {
        return 0;
    }

    public static class Pants extends ArmorItem implements LatexFusingItem {
        public Pants() {
            super(INSTANCE, EquipmentSlot.LEGS, new Properties().tab(ChangedTabs.TAB_CHANGED_ITEMS));
        }

        @Nullable
        @Override
        public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
            return Changed.modResourceStr("textures/models/pink_pants.png");
        }

        @Override
        public LatexVariant<?> getFusionVariant(LatexVariant<?> currentVariant, LivingEntity livingEntity, ItemStack itemStack) {
            if (currentVariant.is(LatexVariant.LATEX_DEER))
                return LatexVariant.LATEX_PINK_DEER;
            return null;
        }

        @Override
        public void nonLatexWearTick(LivingEntity entity, ItemStack itemStack) {
            if (ProcessTransfur.progressTransfur(entity, 3000, LatexVariant.LATEX_PINK_WYVERN.getFormId()))
                itemStack.shrink(1);
        }

        @Override
        public boolean customWearRenderer() {
            return false;
        }

        @Override
        public boolean allowedToKeepWearing(LivingEntity entity) {
            return true;
        }
    }
}
