package net.ltxprogrammer.changed.mixin.item;

import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.extensions.IForgeItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Supplier;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements IForgeItemStack {
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

    @Override
    public boolean canEquip(EquipmentSlot armorType, Entity entity) {
        Player player = EntityUtil.playerOrNull(entity);
        return ProcessTransfur.ifPlayerLatex(player, variant -> {
            if ((Object)this instanceof ItemStack stack)
                return variant.canWear(player, stack);
            return IForgeItemStack.super.canEquip(armorType, entity);
        }, () -> IForgeItemStack.super.canEquip(armorType, entity));
    }
}
