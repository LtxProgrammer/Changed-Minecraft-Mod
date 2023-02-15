package net.ltxprogrammer.changed.item;

import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.init.ChangedTabs;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class AbdomenArmor extends ArmorItem implements WearableItem {
    public static boolean useAbdomenModel(EquipmentSlot slot) {
        return slot == EquipmentSlot.LEGS || slot == EquipmentSlot.FEET;
    }

    public static boolean useInnerAbdomenModel(EquipmentSlot slot) {
        return slot == EquipmentSlot.FEET;
    }

    public AbdomenArmor(ArmorMaterial material, EquipmentSlot slot) {
        super(material, slot, new Properties().tab(ChangedTabs.TAB_CHANGED_COMBAT));
    }

    @Override
    public void wearTick(LivingEntity entity, ItemStack itemStack) {}

    @Override
    public boolean customWearRenderer() {
        return false;
    }

    @Override
    public boolean allowedToKeepWearing(LivingEntity entity) {
        var variant = LatexVariant.getEntityVariant(entity);
        return variant != null && !variant.hasLegs;
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
}
