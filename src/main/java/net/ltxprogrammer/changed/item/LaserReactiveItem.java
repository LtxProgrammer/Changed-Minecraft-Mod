package net.ltxprogrammer.changed.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface LaserReactiveItem {
    boolean tickLaserExposure(LivingEntity wearer, ItemStack itemStack, float exposureLevel);
}
