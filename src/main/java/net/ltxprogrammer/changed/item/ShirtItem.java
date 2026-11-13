package net.ltxprogrammer.changed.item;

import net.ltxprogrammer.changed.init.ChangedSounds;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public class ShirtItem extends ClothingItem {
    @Override
    public SoundEvent getEquipSound(ItemStack itemStack) {
        return ChangedSounds.SHIRT_EQUIP.get();
    }

    @Override
    public SoundEvent getBreakSound(ItemStack itemStack) {
        return ChangedSounds.SHIRT_BREAK.get();
    }

    @Override
    protected @Nullable ResourceLocation getClothingTexture(ItemStack stack, ClothingState clothingState, Entity wearer, EquipmentSlot renderSlot, @Nullable String type, boolean slim) {
        ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(stack.getItem());
        String stem = slim ? "_slim" : "";
        return ResourceLocation.fromNamespaceAndPath(itemId.getNamespace(),
                    String.format("textures/models/%s%s.png", itemId.getPath(), stem));
    }
}
