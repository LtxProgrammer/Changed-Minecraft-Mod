package net.ltxprogrammer.changed.item;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;

public class DyeableAbdomenArmor extends AbdomenArmor implements DyeableLeatherItem {
    public DyeableAbdomenArmor(ArmorMaterial material, EquipmentSlot slot) {
        super(material, slot);
    }
}
