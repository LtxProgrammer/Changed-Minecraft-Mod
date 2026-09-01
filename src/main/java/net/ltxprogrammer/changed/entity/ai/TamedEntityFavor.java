package net.ltxprogrammer.changed.entity.ai;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.init.ChangedAbilities;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.ltxprogrammer.changed.network.packet.GrabEntityPacket;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tier;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.TierSortingRegistry;
import net.minecraftforge.network.PacketDistributor;

public abstract class TamedEntityFavor {
    public interface GoalConsumer {
        void accept(int goalPriority, Goal goal);
    }

    public static final TamedEntityFavor NONE = new TamedEntityFavor() {
        @Override
        public void createFavorGoals(ChangedEntity entity, LivingEntity owner, GoalConsumer goalConsumer) {}
    };
    public static final TamedEntityFavor FISHING = new FishingFavor();
    public static final TamedEntityFavor CAVING = new CavingFavor();
    public static final TamedEntityFavor SUIT_OWNER = new SuitOwnerFavor();

    public TamedEntityFavor() {}

    public abstract void createFavorGoals(ChangedEntity entity, LivingEntity owner, GoalConsumer goalConsumer);

    public void favorSelected(ChangedEntity entity, LivingEntity owner) {}

    public void favorDeselected(ChangedEntity entity, LivingEntity owner) {}

    public int findMainHandSlot(TamedEntityInventory inventory, TamedEntityInventory.SlotFinder defaultFinder) {
        return defaultFinder.findSlot(inventory);
    }

    public int findOffhandSlot(TamedEntityInventory inventory, TamedEntityInventory.SlotFinder defaultFinder) {
        return defaultFinder.findSlot(inventory);
    }

    public static class FishingFavor extends TamedEntityFavor {
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

    public static class CavingFavor extends TamedEntityFavor {
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

    public static class SuitOwnerFavor extends TamedEntityFavor {
        @Override
        public void createFavorGoals(ChangedEntity entity, LivingEntity owner, GoalConsumer goalConsumer) {
            goalConsumer.accept(1, new LatexSuitOwnerGoal(entity, 0.28, true));
        }

        @Override
        public void favorDeselected(ChangedEntity entity, LivingEntity owner) {
            var grabEntityAbilityInstance = entity.getAbilityInstance(ChangedAbilities.GRAB_ENTITY_ABILITY.get());
            if (owner != null && grabEntityAbilityInstance != null && grabEntityAbilityInstance.grabbedEntity == owner) {
                grabEntityAbilityInstance.releaseEntity(false);
                Changed.PACKET_HANDLER.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity),
                        new GrabEntityPacket(entity, owner, GrabEntityPacket.GrabType.RELEASE));
                ChangedSounds.broadcastSound(entity, ChangedSounds.LATEX_UNSUIT_ENTITY, 1.0f, 1.0f);
            }

            if (entity.getTarget() == owner)
                entity.setTarget(null);
        }
    }
}
