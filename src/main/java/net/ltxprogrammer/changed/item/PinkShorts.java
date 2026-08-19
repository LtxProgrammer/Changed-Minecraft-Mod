package net.ltxprogrammer.changed.item;

import net.ltxprogrammer.changed.data.AccessorySlotContext;
import net.ltxprogrammer.changed.entity.TransfurCause;
import net.ltxprogrammer.changed.entity.TransfurContext;
import net.ltxprogrammer.changed.entity.ai.LatexAssimilationDecision;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.init.ChangedEntities;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.ltxprogrammer.changed.init.ChangedTransfurVariants;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public class PinkShorts extends ClothingItem implements Shorts, LatexFusingItem {
    @Override
    protected @Nullable ResourceLocation getClothingTexture(ItemStack stack, ClothingState clothingState, Entity wearer, EquipmentSlot renderSlot, @Nullable String type) {
        ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(stack.getItem());
        return ResourceLocation.fromNamespaceAndPath(itemId.getNamespace(),
                String.format("textures/models/%s_%s.png", itemId.getPath(), Mth.clamp(stack.getDamageValue() - 1, 0, 4)));
    }

    @Override
    public TransfurVariant<?> getFusionVariant(TransfurVariant<?> currentVariant, LivingEntity livingEntity, ItemStack itemStack) {
        if (livingEntity.level().isClientSide)
            return currentVariant;

        if (currentVariant.is(ChangedTransfurVariants.LATEX_DEER))
            return ChangedTransfurVariants.LATEX_PINK_DEER.get();
        else if (currentVariant.is(ChangedTransfurVariants.LATEX_YUIN))
            return ChangedTransfurVariants.LATEX_PINK_YUIN_DRAGON.get();
        else {
            if (livingEntity.getRandom().nextBoolean()) {
                var newEntity = currentVariant.getEntityType().create(livingEntity.level());
                newEntity.moveTo(livingEntity.position());
                livingEntity.level().addFreshEntity(newEntity);
                return ChangedTransfurVariants.LATEX_PINK_WYVERN.get();
            } else {
                var wyvern = ChangedEntities.LATEX_PINK_WYVERN.get().create(livingEntity.level());
                wyvern.moveTo(livingEntity.position());
                livingEntity.level().addFreshEntity(wyvern);
                return currentVariant; // Return current to consume pants (Yummy)
            }
        }
    }

    protected LatexAssimilationDecision<?> makeAssimilationDecision(LivingEntity target) {
        return LatexAssimilationDecision.fromBlockOrItem(ChangedTransfurVariants.LATEX_PINK_WYVERN.get(), TransfurContext.hazard(TransfurCause.PINK_SHORTS), 3.0f);
    }

    @Override
    public void accessoryTick(AccessorySlotContext<?> slotContext) {
        ItemStack itemStack = slotContext.stack();
        LivingEntity wearer = slotContext.wearer();

        var tag = itemStack.getOrCreateTag();
        var age = (tag.contains("age") ? tag.getInt("age") : 0) + 1;
        tag.putInt("age", age);
        if (age < 12000) // Half a minecraft day
            return;
        if (ProcessTransfur.progressTransfur(wearer, this.makeAssimilationDecision(wearer)))
            itemStack.shrink(1);
    }

    @Override
    public SoundEvent getEquipSound(ItemStack itemStack) {
        return ChangedSounds.SHORTS_EQUIP.get();
    }

    @Override
    public SoundEvent getBreakSound(ItemStack itemStack) {
        return ChangedSounds.SHORTS_BREAK.get();
    }
}
