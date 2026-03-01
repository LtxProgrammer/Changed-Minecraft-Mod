package net.ltxprogrammer.changed.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.ltxprogrammer.changed.data.AccessorySlotContext;
import net.ltxprogrammer.changed.data.AccessorySlots;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.init.ChangedAbilityTreeCodecs;
import net.ltxprogrammer.changed.item.AccessoryItem;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {
    @WrapMethod(method = "getEnchantmentLevel(Lnet/minecraft/world/item/enchantment/Enchantment;Lnet/minecraft/world/entity/LivingEntity;)I")
    private static int getModifiedEnchantmentLevel(Enchantment enchantment, LivingEntity entity, Operation<Integer> original) {
        var variant = ProcessTransfur.getPlayerTransfurVariant(EntityUtil.playerOrNull(entity));
        if (variant == null)
            return original.call(enchantment, entity);

        int originalLevel = original.call(enchantment, entity);
        AtomicInteger modifiedLevel = new AtomicInteger(originalLevel);
        AtomicInteger maximumLevel = new AtomicInteger(Integer.MAX_VALUE);

        variant.visitActiveNodeEffects(ChangedAbilityTreeCodecs.INTRINSIC_ENCHANTMENT_EFFECT.get(), effect -> {
            if (effect.enchantment == enchantment) {
                switch (effect.method) {
                    case MINIMUM -> modifiedLevel.updateAndGet(current -> Math.max(current, effect.level));
                    case MAXIMUM -> maximumLevel.updateAndGet(current -> Math.min(maximumLevel.get(), effect.level));
                    case ADD -> modifiedLevel.addAndGet(effect.level);
                }
            }
        });

        return Math.min(modifiedLevel.getAcquire(), maximumLevel.getAcquire());
    }

    @Inject(method = "getDepthStrider", at = @At("RETURN"), cancellable = true)
    private static void getDepthStrider(LivingEntity le, CallbackInfoReturnable<Integer> callback) {
        if (le instanceof ChangedEntity entity) {
            callback.setReturnValue(callback.getReturnValue() + entity.getDepthStriderLevel());
            return;
        }

        ProcessTransfur.ifPlayerTransfurred(EntityUtil.playerOrNull(le), variant -> {
            callback.setReturnValue(callback.getReturnValue() + variant.getChangedEntity().getDepthStriderLevel());
        });
    }

    @WrapOperation(method = "getRandomItemWith(Lnet/minecraft/world/item/enchantment/Enchantment;Lnet/minecraft/world/entity/LivingEntity;Ljava/util/function/Predicate;)Ljava/util/Map$Entry;",
            at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Lists;newArrayList()Ljava/util/ArrayList;", remap = false))
    @SuppressWarnings("deprecation")
    private static ArrayList<Map.Entry<EquipmentSlot, ItemStack>> orRandomAccessoryWith(Operation<ArrayList<Map.Entry<EquipmentSlot, ItemStack>>> original,
                                                                                        @Local(argsOnly = true) Enchantment enchantment,
                                                                                        @Local(argsOnly = true) LivingEntity livingEntity,
                                                                                        @Local(argsOnly = true) Predicate<ItemStack> predicate) {
        var list = original.call();
        AccessorySlots.getForEntity(livingEntity).ifPresent(slots -> {
            slots.forEachSlot((slot, itemStack) -> {
                if (!itemStack.isEmpty() &&
                        itemStack.getItem() instanceof AccessoryItem accessoryItem &&
                        EnchantmentHelper.getItemEnchantmentLevel(enchantment, itemStack) > 0 &&
                        accessoryItem.isConsideredByEnchantment(new AccessorySlotContext<>(livingEntity, slot, itemStack), enchantment) &&
                        predicate.test(itemStack)) {
                    list.add(new Map.Entry<>() {
                        @Override
                        public EquipmentSlot getKey() {
                            return slot.getEquivalentSlot();
                        }

                        @Override
                        public ItemStack getValue() {
                            return slots.getItem(slot).orElse(ItemStack.EMPTY);
                        }

                        @Override
                        public ItemStack setValue(ItemStack value) {
                            if (value == null)
                                throw new IllegalArgumentException("ItemStack cannot be null");
                            var old = getValue();
                            slots.setItem(slot, value);
                            return old;
                        }
                    });
                }
            });
        });
        return list;
    }

    @WrapOperation(method = "getEnchantmentLevel(Lnet/minecraft/world/item/enchantment/Enchantment;Lnet/minecraft/world/entity/LivingEntity;)I",
            at = @At(value = "INVOKE", target = "Ljava/util/Map;values()Ljava/util/Collection;"))
    @SuppressWarnings("unchecked")
    private static Collection<ItemStack> includingAccessorySlots(Map<EquipmentSlot, ItemStack> instance, Operation<Collection<ItemStack>> original,
                                                                 @Local(argsOnly = true) Enchantment enchantment,
                                                                 @Local(argsOnly = true) LivingEntity livingEntity) {
        var list = new ArrayList<ItemStack>(Objects.requireNonNullElse(original.call(instance), (Collection<ItemStack>)Collections.EMPTY_LIST));
        AccessorySlots.getForEntity(livingEntity).ifPresent(slots -> {
            slots.forEachSlot((slot, itemStack) -> {
                if (!itemStack.isEmpty() &&
                        itemStack.getItem() instanceof AccessoryItem accessoryItem &&
                        accessoryItem.isConsideredByEnchantment(new AccessorySlotContext<>(livingEntity, slot, itemStack), enchantment)) {
                    list.add(itemStack);
                }
            });
        });
        return list;
    }

    @Inject(method = { "doPostHurtEffects", "doPostDamageEffects" }, at = @At("TAIL"))
    private static void andPostEffectsOnAccessories(LivingEntity livingEntity, Entity target, CallbackInfo ci,
                                                   @Local EnchantmentHelper.EnchantmentVisitor visitor) {
        AccessorySlots.getForEntity(livingEntity).ifPresent(slots -> {
            slots.forEachSlot((slot, itemStack) -> {
                if (!itemStack.isEmpty() && itemStack.getItem() instanceof AccessoryItem accessoryItem) {
                    for (Map.Entry<Enchantment, Integer> entry : itemStack.getAllEnchantments().entrySet()) {
                        if (accessoryItem.isConsideredByEnchantment(new AccessorySlotContext<>(livingEntity, slot, itemStack), entry.getKey()))
                            visitor.accept(entry.getKey(), entry.getValue());
                    }
                }
            });
        });
    }
}
