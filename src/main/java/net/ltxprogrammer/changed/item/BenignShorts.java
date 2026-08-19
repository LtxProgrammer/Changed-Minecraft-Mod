package net.ltxprogrammer.changed.item;

import net.ltxprogrammer.changed.entity.TransfurCause;
import net.ltxprogrammer.changed.entity.TransfurContext;
import net.ltxprogrammer.changed.entity.ai.LatexAssimilationDecision;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
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

public class BenignShorts extends ClothingItem implements Shorts, LaserReactiveItem {
    @Override
    protected ResourceLocation getClothingTexture(ItemStack stack, ClothingState clothingState, Entity wearer, EquipmentSlot renderSlot, @Nullable String type) {
        ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(stack.getItem());
        return ResourceLocation.fromNamespaceAndPath(itemId.getNamespace(),
                String.format("textures/models/%s_%s.png", itemId.getPath(), Mth.clamp(stack.getDamageValue() - 1, 0, 4)));
    }

    public static TransfurVariant<?> getBenignTransfurVariant(LivingEntity entity) {
        if (entity.isInWater()) {
            return ChangedTransfurVariants.LATEX_BENIGN_ORCA.get();
        }

        if (entity.getRandom().nextBoolean()) {
            // TODO: return ChangedTransfurVariants.BENIGN_DRAGON.get()
        }

        return ChangedTransfurVariants.LATEX_BENIGN_WOLF.get();
    }

    protected LatexAssimilationDecision<?> makeAssimilationDecision(LivingEntity target, float exposureLevel) {
        return LatexAssimilationDecision.fromBlockOrItem(getBenignTransfurVariant(target), TransfurContext.hazard(TransfurCause.BENIGN_SHORTS), exposureLevel);
    }

    @Override
    public boolean tickLaserExposure(LivingEntity wearer, ItemStack itemStack, float exposureLevel) {
        if (ProcessTransfur.progressTransfur(wearer, this.makeAssimilationDecision(wearer, exposureLevel))) {
            itemStack.shrink(1);
            return true;
        }

        return false;
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
