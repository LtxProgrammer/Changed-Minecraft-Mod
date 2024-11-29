package net.ltxprogrammer.changed.item;

import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.variant.ClothingShape;
import net.ltxprogrammer.changed.entity.variant.EntityShape;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Arrays;

public interface ExtendedItemProperties {
    default SoundEvent getBreakSound(ItemStack itemStack) {
        return SoundEvents.ITEM_BREAK;
    }

    default boolean customWearRenderer(ItemStack itemStack) {
        return false;
    }

    default boolean allowedInSlot(ItemStack itemStack, LivingEntity wearer, EquipmentSlot slot) {
        return switch (slot.getType()) {
            case ARMOR -> allowedToWear(itemStack, wearer, slot);
            case HAND -> true;
        };
    }

    // Should only be called with armor slots
    default boolean allowedToWear(ItemStack itemStack, LivingEntity wearer, EquipmentSlot slot) {
        if (!(itemStack.getEquipmentSlot() == slot || (itemStack.getItem() instanceof ArmorItem armorItem && armorItem.getSlot() == slot)))
            return false;
        final EntityShape entityShape = IAbstractChangedEntity.forEitherSafe(wearer)
                .map(IAbstractChangedEntity::getChangedEntity)
                .map(ChangedEntity::getEntityShape)
                .orElse(EntityShape.ANTHRO);
        return switch (slot) {
            case HEAD -> entityShape.getHeadShape() == getExpectedHeadShape(itemStack);
            case CHEST -> entityShape.getTorsoShape() == getExpectedTorsoShape(itemStack);
            case LEGS -> entityShape.getLegsShape() == getExpectedLegShape(itemStack);
            case FEET -> entityShape.getFeetShape() == getExpectedFeetShape(itemStack);
            default -> false;
        };
    }

    default ClothingShape.Head getExpectedHeadShape(ItemStack itemStack) {
        return ClothingShape.Head.ANTRHO;
    }

    default ClothingShape.Torso getExpectedTorsoShape(ItemStack itemStack) {
        return ClothingShape.Torso.ANTRHO;
    }

    default ClothingShape.Legs getExpectedLegShape(ItemStack itemStack) {
        return ClothingShape.Legs.BIPEDAL;
    }

    default ClothingShape.Feet getExpectedFeetShape(ItemStack itemStack) {
        return ClothingShape.Feet.BIPEDAL;
    }

    default void wearTick(ItemStack itemStack, LivingEntity wearer) {

    }

    @Mod.EventBusSubscriber
    class Event {
        @SubscribeEvent
        public static void onEntityTick(LivingEvent.LivingUpdateEvent event) {
            Arrays.stream(EquipmentSlot.values()).filter(slot -> slot.getType() == EquipmentSlot.Type.ARMOR)
                            .forEach(slot -> {
                                var itemStack = event.getEntityLiving().getItemBySlot(slot);
                                if (itemStack.getItem() instanceof ExtendedItemProperties extended) {
                                    if (!extended.allowedInSlot(itemStack, event.getEntityLiving(), slot)) {
                                        ItemStack nStack = itemStack.copy();
                                        itemStack.setCount(0);
                                        if (event.getEntityLiving() instanceof Player player)
                                            player.addItem(nStack);
                                        else
                                            Block.popResource(event.getEntityLiving().level, event.getEntityLiving().blockPosition(), nStack);
                                        return;
                                    }

                                    extended.wearTick(itemStack, event.getEntityLiving());
                                }
                            });
        }
    }
}
