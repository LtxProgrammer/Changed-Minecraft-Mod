package net.ltxprogrammer.changed.mixin;

import net.ltxprogrammer.changed.init.ChangedItems;
import net.ltxprogrammer.changed.item.Syringe;
import net.ltxprogrammer.changed.util.TagUtil;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.ltxprogrammer.changed.item.LatexTippedArrowItem.FORM_LOCATION;

@Mixin(Arrow.class)
public abstract class ArrowMixin extends AbstractArrow {
    protected ArrowMixin(EntityType<? extends AbstractArrow> p_36721_, Level p_36722_) {
        super(p_36721_, p_36722_);
    }

    @Inject(method = "getPickupItem", at = @At("HEAD"), cancellable = true)
    protected void getPickupItem(CallbackInfoReturnable<ItemStack> callbackInfoReturnable) {
        if (this.getPersistentData().contains(FORM_LOCATION)) {
            ItemStack itemStack = new ItemStack(ChangedItems.LATEX_TIPPED_ARROW.get());
            Syringe.setUnpureVariant(itemStack, TagUtil.getResourceLocation(this.getPersistentData(), FORM_LOCATION));
            callbackInfoReturnable.setReturnValue(itemStack);
        }
    }
}
