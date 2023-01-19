package net.ltxprogrammer.changed.item;

import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.init.ChangedTabs;
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

public class DarkLatexMask extends Item implements WearableItem {
    public static final List<ResourceLocation> MASKED_LATEXES = new ArrayList<>(List.of(
            LatexVariant.DARK_LATEX_WOLF.male().getFormId(),
            LatexVariant.DARK_LATEX_WOLF.female().getFormId(),
            LatexVariant.DARK_LATEX_YUFENG.getFormId()
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
    public void wearTick(LivingEntity entity, ItemStack itemStack) {
        LatexVariant<?> variant = Syringe.getVariant(itemStack);
        if (variant == null)
            variant = LatexVariant.DARK_LATEX_WOLF.male();
        if (ProcessTransfur.progressTransfur(entity, 2500, variant.getFormId()))
            itemStack.shrink(1);
    }

    @Override
    public void appendHoverText(ItemStack p_43359_, @Nullable Level p_43360_, List<Component> p_43361_, TooltipFlag p_43362_) {
        Syringe.addVariantTooltip(p_43359_, p_43361_);
    }

    @Override
    public boolean customWearRenderer() {
        return true;
    }

    @Override
    public boolean allowedToKeepWearing(LivingEntity entity) {
        if (entity instanceof LatexEntity)
            return false;
        else if (entity instanceof Player player && ProcessTransfur.isPlayerLatex(player))
            return false;
        else if (entity instanceof AgeableMob ageableMob && ageableMob.isBaby())
            return false;
        else
            return true;
    }
}
