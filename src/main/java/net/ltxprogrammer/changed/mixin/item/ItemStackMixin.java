package net.ltxprogrammer.changed.mixin.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Supplier;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    private Supplier<? extends Item> itemSupplier;

    @Inject(method = "getItem", at = @At("HEAD"), cancellable = true)
    public void getItem(CallbackInfoReturnable<Item> ci) {
        if (itemSupplier != null)
            ci.setReturnValue(itemSupplier.get());
    }

    @Inject(method = "toString", at = @At("HEAD"), cancellable = true)
    public void toString(CallbackInfoReturnable<String> ci) {
        if (itemSupplier != null)
            ci.setReturnValue(new ItemStack(itemSupplier.get()).toString());
    }
}
