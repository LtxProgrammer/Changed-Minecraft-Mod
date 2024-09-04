package net.ltxprogrammer.changed.item;

import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.TransfurCause;
import net.ltxprogrammer.changed.entity.TransfurContext;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.ltxprogrammer.changed.init.ChangedTabs;
import net.ltxprogrammer.changed.init.ChangedTransfurVariants;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class DarkLatexMask extends Item implements ExtendedItemProperties {
    public static final List<ResourceLocation> MASKED_LATEXES = new ArrayList<>(List.of(
            ChangedTransfurVariants.DARK_LATEX_WOLF_MALE.getId(),
            ChangedTransfurVariants.DARK_LATEX_WOLF_FEMALE.getId(),
            ChangedTransfurVariants.DARK_LATEX_YUFENG.getId(),
            ChangedTransfurVariants.DARK_LATEX_WOLF_PUP.getId()
    ));

    public DarkLatexMask() {
        super(new Item.Properties().tab(ChangedTabs.TAB_CHANGED_ITEMS));
    }

    @Override
    public EquipmentSlot getEquipmentSlot(ItemStack stack) {
        return EquipmentSlot.HEAD;
    }

    public void fillItemCategory(CreativeModeTab p_43356_, NonNullList<ItemStack> p_43357_) {
        if (this.allowdedIn(p_43356_)) {
            for (var variant : MASKED_LATEXES) {
                p_43357_.add(Syringe.setUnpureVariant(new ItemStack(this), variant));
            }
        }
    }

    @Override
    public void wearTick(ItemStack itemStack, LivingEntity wearer) {
        TransfurVariant<?> variant = Syringe.getVariant(itemStack);
        if (variant == null)
            variant = ChangedTransfurVariants.DARK_LATEX_WOLF_MALE.get();
        if (TransfurVariant.getEntityVariant(wearer) == ChangedTransfurVariants.DARK_LATEX_WOLF_PARTIAL.get()) {
            if (wearer.getRandom().nextFloat() > 0.005f || wearer.level.isClientSide) return; // 0.5% chance every tick the entity will switch TF into the mask variant

            ChangedSounds.broadcastSound(ProcessTransfur.changeTransfur(wearer, variant), ChangedSounds.POISON, 1.0f, 1.0f);
            itemStack.shrink(1);
            return;
        }

        if (ProcessTransfur.progressTransfur(wearer, 11.0f, variant, TransfurContext.hazard(TransfurCause.FACE_HAZARD)))
            itemStack.shrink(1);
    }

    @Override
    public void appendHoverText(ItemStack p_43359_, @Nullable Level p_43360_, List<Component> p_43361_, TooltipFlag p_43362_) {
        Syringe.addVariantTooltip(p_43359_, p_43361_);
    }

    @Override
    public boolean customWearRenderer(ItemStack itemStack) {
        return true;
    }

    @Override
    public boolean allowedToWear(ItemStack itemStack, LivingEntity wearer, EquipmentSlot slot) {
        if (TransfurVariant.getEntityVariant(wearer) == ChangedTransfurVariants.DARK_LATEX_WOLF_PARTIAL.get())
            return true;

        if (wearer instanceof ChangedEntity)
            return false;
        else if (wearer instanceof Player player && ProcessTransfur.isPlayerTransfurred(player))
            return false;
        else if (wearer instanceof AgeableMob ageableMob && ageableMob.isBaby())
            return false;
        else
            return true;
    }
}
