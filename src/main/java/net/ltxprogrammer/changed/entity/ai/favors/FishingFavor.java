package net.ltxprogrammer.changed.entity.ai.favors;

import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.ai.FishingGoal;
import net.ltxprogrammer.changed.entity.ai.TamedEntityFavor;
import net.ltxprogrammer.changed.entity.ai.TamedEntityInventory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.Tags;

public class FishingFavor extends TamedEntityFavor {
    @Override
    public void createFavorGoals(ChangedEntity entity, LivingEntity owner, GoalConsumer goalConsumer) {
        goalConsumer.accept(1, new FishingGoal(entity, 0.3, 24, 3));
    }

    @Override
    public int findMainHandSlot(TamedEntityInventory inventory, TamedEntityInventory.SlotFinder defaultFinder) {
        for (int i = 0; i < inventory.getContainerSize(); ++i) {
            var slot = inventory.getItem(i);
            if (slot.isEmpty())
                continue;

            if (slot.is(Tags.Items.TOOLS_FISHING_RODS))
                return i;
        }

        return defaultFinder.findSlot(inventory);
    }
}
