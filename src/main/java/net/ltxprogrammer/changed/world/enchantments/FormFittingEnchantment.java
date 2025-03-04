package net.ltxprogrammer.changed.world.enchantments;

import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.variant.ClothingShape;
import net.ltxprogrammer.changed.init.ChangedEnchantments;
import net.ltxprogrammer.changed.item.ExtendedItemProperties;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.stream.Stream;

public class FormFittingEnchantment extends Enchantment {
    public FormFittingEnchantment(Rarity rarity, EquipmentSlot[] slots) {
        super(rarity, EnchantmentCategory.ARMOR, slots);
    }

    public int getMaxLevel() {
        return 1;
    }

    private static Stream<ArmorItem> getArmors(ArmorMaterial material, EquipmentSlot slot) {
        return ForgeRegistries.ITEMS.getValues().stream().map(item -> {
            if (item instanceof ArmorItem armor)
                return armor;
            else
                return null;
        }).filter(Objects::nonNull)
                .filter(armorItem -> armorItem.getSlot() == slot)
                .filter(armorItem -> armorItem.getMaterial().getName().equals(material.getName()));
    }

    private static @Nullable ItemStack findEquivalentItemForHeadShape(ClothingShape.Head shape, ItemStack itemStack) {
        if (itemStack.getItem() instanceof ExtendedItemProperties ext && ext.getExpectedHeadShape(itemStack) == shape)
            return itemStack;
        else if (!(itemStack.getItem() instanceof ExtendedItemProperties) && shape == ClothingShape.Head.DEFAULT)
            return itemStack;
        if (!(itemStack.getItem() instanceof ArmorItem armorItem))
            return null;

        return getArmors(armorItem.getMaterial(), EquipmentSlot.HEAD).map(candidate -> {
            ItemStack pseudo = new ItemStack(candidate, itemStack.getCount());
            if (candidate instanceof ExtendedItemProperties ext && ext.getExpectedHeadShape(pseudo) == shape) {
                pseudo.setTag(itemStack.getTag());
                return pseudo;
            } else if (!(candidate instanceof ExtendedItemProperties) && shape == ClothingShape.Head.DEFAULT) {
                pseudo.setTag(itemStack.getTag());
                return pseudo;
            }
            return null;
        }).filter(Objects::nonNull).findFirst().orElse(null);
    }

    private static @Nullable ItemStack findEquivalentItemForTorsoShape(ClothingShape.Torso shape, ItemStack itemStack) {
        if (itemStack.getItem() instanceof ExtendedItemProperties ext && ext.getExpectedTorsoShape(itemStack) == shape)
            return itemStack;
        else if (!(itemStack.getItem() instanceof ExtendedItemProperties) && shape == ClothingShape.Torso.DEFAULT)
            return itemStack;
        if (!(itemStack.getItem() instanceof ArmorItem armorItem))
            return null;

        return getArmors(armorItem.getMaterial(), EquipmentSlot.CHEST).map(candidate -> {
            ItemStack pseudo = new ItemStack(candidate, itemStack.getCount());
            if (candidate instanceof ExtendedItemProperties ext && ext.getExpectedTorsoShape(pseudo) == shape) {
                pseudo.setTag(itemStack.getTag());
                return pseudo;
            } else if (!(candidate instanceof ExtendedItemProperties) && shape == ClothingShape.Torso.DEFAULT) {
                pseudo.setTag(itemStack.getTag());
                return pseudo;
            }
            return null;
        }).filter(Objects::nonNull).findFirst().orElse(null);
    }

    private static @Nullable ItemStack findEquivalentItemForLegsShape(ClothingShape.Legs shape, ItemStack itemStack) {
        if (itemStack.getItem() instanceof ExtendedItemProperties ext && ext.getExpectedLegShape(itemStack) == shape)
            return itemStack;
        else if (!(itemStack.getItem() instanceof ExtendedItemProperties) && shape == ClothingShape.Legs.DEFAULT)
            return itemStack;
        if (!(itemStack.getItem() instanceof ArmorItem armorItem))
            return null;

        return getArmors(armorItem.getMaterial(), EquipmentSlot.LEGS).map(candidate -> {
            ItemStack pseudo = new ItemStack(candidate, itemStack.getCount());
            if (candidate instanceof ExtendedItemProperties ext && ext.getExpectedLegShape(pseudo) == shape) {
                pseudo.setTag(itemStack.getTag());
                return pseudo;
            } else if (!(candidate instanceof ExtendedItemProperties) && shape == ClothingShape.Legs.DEFAULT) {
                pseudo.setTag(itemStack.getTag());
                return pseudo;
            }
            return null;
        }).filter(Objects::nonNull).findFirst().orElse(null);
    }

    private static @Nullable ItemStack findEquivalentItemForFeetShape(ClothingShape.Feet shape, ItemStack itemStack) {
        if (itemStack.getItem() instanceof ExtendedItemProperties ext && ext.getExpectedFeetShape(itemStack) == shape)
            return itemStack;
        else if (!(itemStack.getItem() instanceof ExtendedItemProperties) && shape == ClothingShape.Feet.DEFAULT)
            return itemStack;
        if (!(itemStack.getItem() instanceof ArmorItem armorItem))
            return null;

        return getArmors(armorItem.getMaterial(), EquipmentSlot.FEET).map(candidate -> {
            ItemStack pseudo = new ItemStack(candidate, itemStack.getCount());
            if (candidate instanceof ExtendedItemProperties ext && ext.getExpectedFeetShape(pseudo) == shape) {
                pseudo.setTag(itemStack.getTag());
                return pseudo;
            } else if (!(candidate instanceof ExtendedItemProperties) && shape == ClothingShape.Feet.DEFAULT) {
                pseudo.setTag(itemStack.getTag());
                return pseudo;
            }
            return null;
        }).filter(Objects::nonNull).findFirst().orElse(null);
    }

    public static @NotNull ItemStack getFormFitted(LivingEntity wearer, ItemStack itemStack, EquipmentSlot slot) {
        if (slot.getType() != EquipmentSlot.Type.ARMOR)
            return itemStack;
        if (EnchantmentHelper.getItemEnchantmentLevel(ChangedEnchantments.FORM_FITTING.get(), itemStack) <= 0)
            return itemStack;

        final ItemStack equivalent = IAbstractChangedEntity.forEitherSafe(wearer)
                .map(IAbstractChangedEntity::getChangedEntity)
                .map(ChangedEntity::getEntityShape).map(shape -> {
                    return switch (slot) {
                        case HEAD -> findEquivalentItemForHeadShape(shape.headShape, itemStack);
                        case CHEST -> findEquivalentItemForTorsoShape(shape.torsoShape, itemStack);
                        case LEGS -> findEquivalentItemForLegsShape(shape.legsShape, itemStack);
                        case FEET -> findEquivalentItemForFeetShape(shape.feetShape, itemStack);
                        default -> null;
                    };
                }).orElseGet(() -> {
                    return switch (slot) {
                        case HEAD -> findEquivalentItemForHeadShape(ClothingShape.Head.DEFAULT, itemStack);
                        case CHEST -> findEquivalentItemForTorsoShape(ClothingShape.Torso.DEFAULT, itemStack);
                        case LEGS -> findEquivalentItemForLegsShape(ClothingShape.Legs.DEFAULT, itemStack);
                        case FEET -> findEquivalentItemForFeetShape(ClothingShape.Feet.DEFAULT, itemStack);
                        default -> null;
                    };
                });

        return equivalent == null ? itemStack : equivalent;
    }

    public static float getLatexProtection(LivingEntity entity, float progression) {
        int protection = EnchantmentHelper.getEnchantmentLevel(Enchantments.ALL_DAMAGE_PROTECTION, entity);
        int tfResistance = EnchantmentHelper.getEnchantmentLevel(ChangedEnchantments.TRANSFUR_RESISTANCE.get(), entity);

        float tfResistanceDiscount = progression * (float)tfResistance * 0.15F;
        float protectionDiscount = progression * (float)protection * 0.075F;

        return progression - Math.max(tfResistanceDiscount, protectionDiscount);
    }
}
