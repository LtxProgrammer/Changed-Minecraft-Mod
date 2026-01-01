package net.ltxprogrammer.changed.mixin;

import net.ltxprogrammer.changed.data.AccessorySlotContext;
import net.ltxprogrammer.changed.data.AccessorySlots;
import net.ltxprogrammer.changed.item.AccessoryItem;
import net.ltxprogrammer.changed.item.AccessoryItem.ExecutionContext;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;

@Mixin(Enchantment.class)
public class EnchantmentMixin {

    @Inject(method = "getSlotItems", at = @At("RETURN"), cancellable = true)
    private void accessoriesEnchantmentDetectionIdle(LivingEntity pEntity, CallbackInfoReturnable<Map<EquipmentSlot, ItemStack>> cir) {
        AccessorySlots.getForEntity(pEntity).ifPresent((slots) ->
                slots.forEachSlot((slotType, itemStack) -> {
                    if (itemStack.isEmpty()) return;
                    if (!(itemStack.getItem() instanceof AccessoryItem accessoryItem)) return;
                    if (accessoryItem.isConsideredByEnchantment(AccessorySlotContext.of(pEntity, slotType), self(), ExecutionContext.IDLE)) {
                        Map<EquipmentSlot, ItemStack> returnValue = cir.getReturnValue();
                        Map<EquipmentSlot, ItemStack> newReturnValue = new HashMap<>();
                        if (returnValue != null) {
                            newReturnValue.put(slotType.getEquivalentSlot(), itemStack);
                            cir.setReturnValue(newReturnValue);
                        }
                    }
                })
        );
    }

    @Inject(method = "doPostAttack", at = @At("HEAD"), cancellable = true)
    private void accessoriesEnchantmentDetectionPostAttack(LivingEntity attacker, Entity target, int pLevel, CallbackInfo ci) {
        AccessorySlots.getForEntity(attacker).ifPresent((slots) ->
                slots.forEachSlot((slotType, itemStack) -> {
                    if (itemStack.isEmpty()) return;
                    if (!(itemStack.getItem() instanceof AccessoryItem accessoryItem)) return;
                    if (!accessoryItem.isConsideredByEnchantment(AccessorySlotContext.of(attacker, slotType), self(), ExecutionContext.POST_ATTACK)) {
                        ci.cancel(); //Cancel behavior if the accessory item don't support it
                    }
                })
        );
    }

    @Inject(method = "doPostHurt", at = @At("HEAD"), cancellable = true)
    private void accessoriesEnchantmentDetectionPostHurt(LivingEntity attacker, Entity target, int pLevel, CallbackInfo ci) {
        AccessorySlots.getForEntity(attacker).ifPresent((slots) ->
                slots.forEachSlot((slotType, itemStack) -> {
                    if (itemStack.isEmpty()) return;
                    if (!(itemStack.getItem() instanceof AccessoryItem accessoryItem)) return;
                    if (!accessoryItem.isConsideredByEnchantment(AccessorySlotContext.of(attacker, slotType), self(), ExecutionContext.POST_HURT)) {
                        ci.cancel(); //Cancel behavior if the accessory item don't support it
                    }
                })
        );
    }

    @Unique
    private Enchantment self() {
        return (Enchantment) (Object) this;
    }
}
