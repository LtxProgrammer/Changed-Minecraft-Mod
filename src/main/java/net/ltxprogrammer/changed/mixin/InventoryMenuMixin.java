package net.ltxprogrammer.changed.mixin;

import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(InventoryMenu.class)
public abstract class InventoryMenuMixin extends RecipeBookMenu<CraftingContainer> {
    @Shadow @Final public Player owner;

    public InventoryMenuMixin(MenuType<?> type, int id) {
        super(type, id);
    }

    @Redirect(method = "quickMoveStack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;getEquipmentSlotForItem(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/entity/EquipmentSlot;"))
    public EquipmentSlot denyInvalidArmorSlot(ItemStack itemStack) {
        var slot = Mob.getEquipmentSlotForItem(itemStack);
        if (slot.getType() != EquipmentSlot.Type.ARMOR)
            return slot;

        return ProcessTransfur.ifPlayerTransfurred(this.owner, variant -> {
            return variant.canWear(this.owner, itemStack, slot) ? slot : EquipmentSlot.MAINHAND;
        }, () -> slot);
    }
}
