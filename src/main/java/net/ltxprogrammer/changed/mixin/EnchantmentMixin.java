package net.ltxprogrammer.changed.mixin;

import net.ltxprogrammer.changed.data.AccessorySlots;
import net.ltxprogrammer.changed.item.AccessoryItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;

@Mixin(Enchantment.class)
public class EnchantmentMixin {

    @Inject(method = "getSlotItems", at = @At("RETURN"), cancellable = true)
    private void accessoriesEnchantmentHook(LivingEntity pEntity, CallbackInfoReturnable<Map<EquipmentSlot, ItemStack>> cir) {
        AccessorySlots.getForEntity(pEntity).ifPresent((slots) ->
                slots.forEachSlot((slotType, itemStack) -> {
                    if (itemStack.isEmpty()) return;
                    if (!(itemStack.getItem() instanceof AccessoryItem accessoryItem)) return;
                    if (accessoryItem.isConsideredByEnchantment(self(), itemStack, slotType, pEntity)) {
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

    @Unique
    private Enchantment self() {
        return (Enchantment) (Object) this;
    }
}
