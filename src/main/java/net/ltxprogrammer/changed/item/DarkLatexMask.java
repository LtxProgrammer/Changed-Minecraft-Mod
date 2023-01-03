package net.ltxprogrammer.changed.item;

import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.fluid.AbstractLatexFluid;
import net.ltxprogrammer.changed.init.ChangedItems;
import net.ltxprogrammer.changed.init.ChangedTabs;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class DarkLatexMask extends Item implements WearableItem {
    public DarkLatexMask() {
        super(new Item.Properties().tab(ChangedTabs.TAB_CHANGED_ITEMS));
    }

    @Override
    public EquipmentSlot getEquipmentSlot(ItemStack stack) {
        return EquipmentSlot.HEAD;
    }

    @Override
    public void wearTick(LivingEntity entity, ItemStack itemStack) {
        if (ProcessTransfur.progressTransfur(entity, 2500, LatexVariant.DARK_LATEX_WOLF.male().getFormId()))
            itemStack.shrink(1);
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
        else
            return true;
    }
}
