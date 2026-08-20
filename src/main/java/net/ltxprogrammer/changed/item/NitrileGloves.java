package net.ltxprogrammer.changed.item;

import net.ltxprogrammer.changed.init.ChangedSounds;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public class NitrileGloves extends ClothingItem implements Gloves {
    @Override
    protected ResourceLocation getClothingTexture(ItemStack stack, ClothingState clothingState, Entity wearer, EquipmentSlot renderSlot, @Nullable String type) {
        ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(stack.getItem());
        return ResourceLocation.fromNamespaceAndPath(itemId.getNamespace(),
                String.format("textures/models/%s_%s.png", itemId.getPath(), Mth.clamp(stack.getDamageValue() - 1, 0, 4)));
    }

    @Override
    public SoundEvent getEquipSound(ItemStack itemStack) {
        return ChangedSounds.GLOVES_EQUIP.get();
    }

    @Override
    public SoundEvent getBreakSound(ItemStack itemStack) {
        return ChangedSounds.GLOVES_BREAK.get();
    }
}
