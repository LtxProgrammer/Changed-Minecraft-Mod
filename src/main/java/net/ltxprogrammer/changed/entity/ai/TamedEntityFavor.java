package net.ltxprogrammer.changed.entity.ai;

import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

public abstract class TamedEntityFavor {
    public interface GoalConsumer {
        void accept(int goalPriority, Goal goal);
    }

    public TamedEntityFavor() {}

    public abstract void createFavorGoals(ChangedEntity entity, LivingEntity owner, GoalConsumer goalConsumer);

    public void favorSelected(ChangedEntity entity, LivingEntity owner) {}

    public void favorDeselected(ChangedEntity entity, LivingEntity owner) {}

    public void tickSelectedFavor(ChangedEntity entity, LivingEntity owner) {}

    public int findMainHandSlot(TamedEntityInventory inventory, TamedEntityInventory.SlotFinder defaultFinder) {
        return defaultFinder.findSlot(inventory);
    }

    public int findOffhandSlot(TamedEntityInventory inventory, TamedEntityInventory.SlotFinder defaultFinder) {
        return defaultFinder.findSlot(inventory);
    }
}
