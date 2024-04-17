package net.ltxprogrammer.changed.item;

import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.init.ChangedTabs;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class GasMaskItem extends Item implements WearableItem {
    public GasMaskItem() {
        super(new Item.Properties().tab(ChangedTabs.TAB_CHANGED_ITEMS).stacksTo(1));
    }

    @Override
    public EquipmentSlot getEquipmentSlot(ItemStack stack) {
        return EquipmentSlot.HEAD;
    }


    @Override
    public void wearTick(LivingEntity entity, ItemStack itemStack) {

    }

    @Override
    public boolean customWearRenderer() {
        return true;
    }

    @Override
    public boolean allowedToKeepWearing(LivingEntity entity) {
        return ProcessTransfur.getEntityVariant(entity).map(variant -> {
            if (DarkLatexMask.MASKED_LATEXES.contains(variant.getFormId()))
                return false;
            if (variant == TransfurVariant.LATEX_ALIEN)
                return false;
            if (variant == TransfurVariant.LATEX_BENIGN_WOLF)
                return false;
            return true;
        }).orElse(true);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        EquipmentSlot equipmentslot = Mob.getEquipmentSlotForItem(itemstack);
        ItemStack itemstack1 = player.getItemBySlot(equipmentslot);
        if (itemstack1.isEmpty() && this.allowedToKeepWearing(player)) {
            player.setItemSlot(equipmentslot, itemstack.copy());
            if (!level.isClientSide()) {
                player.awardStat(Stats.ITEM_USED.get(this));
            }

            itemstack.setCount(0);
            return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
        } else {
            return InteractionResultHolder.fail(itemstack);
        }
    }
}
