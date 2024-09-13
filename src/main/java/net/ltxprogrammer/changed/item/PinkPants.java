package net.ltxprogrammer.changed.item;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.TransfurCause;
import net.ltxprogrammer.changed.entity.TransfurContext;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.init.ChangedEntities;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.ltxprogrammer.changed.init.ChangedTabs;
import net.ltxprogrammer.changed.init.ChangedTransfurVariants;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import org.jetbrains.annotations.Nullable;

public class PinkPants extends ArmorItem implements Wearable, LatexFusingItem, Shorts {
    public PinkPants() {
        super(MATERIAL, EquipmentSlot.LEGS, new Properties().tab(ChangedTabs.TAB_CHANGED_ITEMS));
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return false;
    }

    @Override
    public SoundEvent getEquipSound() {
        return ChangedSounds.EQUIP3;
    }

    @Override
    public SoundEvent getBreakSound(ItemStack itemStack) {
        return ChangedSounds.SLASH10;
    }

    @Nullable
    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return Changed.modResourceStr("textures/models/pink_pants_" + Mth.clamp(stack.getDamageValue() - 1, 0, 4) + ".png");
    }

    @Override
    public TransfurVariant<?> getFusionVariant(TransfurVariant<?> currentVariant, LivingEntity livingEntity, ItemStack itemStack) {
        if (livingEntity.level.isClientSide)
            return currentVariant;

        if (currentVariant.is(ChangedTransfurVariants.LATEX_DEER))
            return ChangedTransfurVariants.LATEX_PINK_DEER.get();
        else if (currentVariant.is(ChangedTransfurVariants.LATEX_YUIN))
            return ChangedTransfurVariants.LATEX_PINK_YUIN_DRAGON.get();
        else {
            if (livingEntity.getRandom().nextBoolean()) {
                var newEntity = currentVariant.getEntityType().create(livingEntity.level);
                newEntity.moveTo(livingEntity.position());
                livingEntity.level.addFreshEntity(newEntity);
                return ChangedTransfurVariants.LATEX_PINK_WYVERN.get();
            } else {
                var wyvern = ChangedEntities.LATEX_PINK_WYVERN.get().create(livingEntity.level);
                wyvern.moveTo(livingEntity.position());
                livingEntity.level.addFreshEntity(wyvern);
                return currentVariant; // Return current to consume pants (Yummy)
            }
        }
    }

    @Override
    public void wearTick(ItemStack itemStack, LivingEntity wearer) {
        var tag = itemStack.getOrCreateTag();
        var age = (tag.contains("age") ? tag.getInt("age") : 0) + 1;
        tag.putInt("age", age);
        if (age < 12000) // Half a minecraft day
            return;
        if (ProcessTransfur.progressTransfur(wearer, 3.0f, ChangedTransfurVariants.LATEX_PINK_WYVERN.get(), TransfurContext.hazard(TransfurCause.PINK_PANTS)))
            itemStack.shrink(1);
    }
}
