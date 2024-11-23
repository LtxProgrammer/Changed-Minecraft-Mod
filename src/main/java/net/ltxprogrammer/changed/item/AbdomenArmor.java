package net.ltxprogrammer.changed.item;

import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.init.ChangedTabs;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class AbdomenArmor extends ArmorItem implements ExtendedItemProperties {
    public static boolean useAbdomenModel(EquipmentSlot slot) {
        return slot == EquipmentSlot.LEGS || slot == EquipmentSlot.FEET;
    }

    public static boolean useInnerAbdomenModel(EquipmentSlot slot) {
        return slot == EquipmentSlot.FEET;
    }

    public AbdomenArmor(ArmorMaterial material, EquipmentSlot slot) {
        super(material, slot, new Properties().tab(ChangedTabs.TAB_CHANGED_COMBAT));
    }

    public AbdomenArmor(ArmorMaterial material, EquipmentSlot slot, Properties properties) {
        super(material, slot, properties);
    }

    @Override
    public boolean canWalkOnPowderedSnow(ItemStack stack, LivingEntity wearer) {
        return material.getName().equals(ArmorMaterials.LEATHER.getName()) && slot == EquipmentSlot.FEET;
    }

    @Override
    public void wearTick(ItemStack itemStack, LivingEntity wearer) {}

    @Override
    public boolean allowedToWear(ItemStack itemStack, LivingEntity wearer, EquipmentSlot slot) {
        if (!ExtendedItemProperties.super.allowedToWear(itemStack, wearer, slot))
            return false;

        var instance = ProcessTransfur.getPlayerTransfurVariant(EntityUtil.playerOrNull(wearer));
        if (instance != null) {
            return !instance.getParent().hasLegs && instance.shouldApplyAbilities();
        }

        else {
            var variant = TransfurVariant.getEntityVariant(wearer);
            return variant != null && !variant.hasLegs;
        }
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
        return String.format(java.util.Locale.ROOT, "%s:textures/models/abdomen_armor/%s_layer_%d%s.png", domain, texture, (useInnerAbdomenModel(slot) ? 2 : 1),
                type == null ? "" : String.format(java.util.Locale.ROOT, "_%s", type));
    }

    @Override
    public int getExpectedLegCount(ItemStack itemStack) {
        return 0;
    }
}
