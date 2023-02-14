package net.ltxprogrammer.changed.item;

import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.init.ChangedTabs;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;

public class AbdomenArmor extends ArmorItem implements WearableItem {
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
}
