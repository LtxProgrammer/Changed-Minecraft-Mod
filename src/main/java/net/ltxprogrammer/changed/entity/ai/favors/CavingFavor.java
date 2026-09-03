package net.ltxprogrammer.changed.entity.ai.favors;

import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.ai.CaveHarvestGoal;
import net.ltxprogrammer.changed.entity.ai.CaveTorchingGoal;
import net.ltxprogrammer.changed.entity.ai.TamedEntityFavor;
import net.ltxprogrammer.changed.entity.ai.TamedEntityInventory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tier;
import net.minecraftforge.common.TierSortingRegistry;

public class CavingFavor extends TamedEntityFavor {
    @Override
    public void createFavorGoals(ChangedEntity entity, LivingEntity owner, GoalConsumer goalConsumer) {
        goalConsumer.accept(1, new CaveHarvestGoal(entity, 0.3, 24, 3));
        goalConsumer.accept(2, new CaveTorchingGoal(entity, 0.3, 24, 3));
    }

    @Override
    public int findMainHandSlot(TamedEntityInventory inventory, TamedEntityInventory.SlotFinder defaultFinder) {
        Tier bestTier = null;
        int bestSlot = -1;

        for (int i = 0; i < inventory.getContainerSize(); ++i) {
            var slot = inventory.getItem(i);
            if (slot.isEmpty())
                continue;

            if (slot.getItem() instanceof PickaxeItem pickaxeItem) {
                if (bestTier == null || TierSortingRegistry.getTiersLowerThan(pickaxeItem.getTier()).contains(bestTier)) {
                    bestTier = pickaxeItem.getTier();
                    bestSlot = i;
                }
            }
        }

        return bestSlot == -1 ? defaultFinder.findSlot(inventory) : bestSlot;
    }

    @Override
    public int findOffhandSlot(TamedEntityInventory inventory, TamedEntityInventory.SlotFinder defaultFinder) {
        for (int slotIndex = 0; slotIndex < inventory.getContainerSize(); ++slotIndex) {
            var slot = inventory.getItem(slotIndex);
            if (slot.isEmpty())
                continue;

            if (slot.is(Items.TORCH))
                return slotIndex;
        }

        return defaultFinder.findSlot(inventory);
    }
}
