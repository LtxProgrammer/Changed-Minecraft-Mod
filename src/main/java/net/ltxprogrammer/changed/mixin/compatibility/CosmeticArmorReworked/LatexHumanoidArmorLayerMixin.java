package net.ltxprogrammer.changed.mixin.compatibility.CosmeticArmorReworked;

import net.ltxprogrammer.changed.client.renderer.layers.LatexHumanoidArmorLayer;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.extension.RequiredMods;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.lang.reflect.Method;

/** Uses Cosmetic Armor Reworked's client-side cosmetic inventory when rendering Changed entities. */
@Mixin(LatexHumanoidArmorLayer.class)
@RequiredMods("cosmeticarmorreworked")
public abstract class LatexHumanoidArmorLayerMixin {
    private static final Method GET_COSMETIC_STACKS = findMethod();

    private static Method findMethod() {
        try {
            Class<?> api = Class.forName("lain.mods.cos.api.CosArmorAPI");
            return api.getMethod("getCAStacksClient", java.util.UUID.class);
        } catch (ReflectiveOperationException ignored) {
            return null;
        }
    }

    @Redirect(method = "renderArmorPiece", at = @At(value = "INVOKE", target = "Lnet/ltxprogrammer/changed/entity/ChangedEntity;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack changed$getCosmeticArmor(ChangedEntity entity, EquipmentSlot slot) {
        if (GET_COSMETIC_STACKS == null || entity.getUnderlyingPlayer() == null)
            return entity.getItemBySlot(slot);

        int cosmeticSlot = switch (slot) {
            case FEET -> 0;
            case LEGS -> 1;
            case CHEST -> 2;
            case HEAD -> 3;
            default -> -1;
        };
        if (cosmeticSlot < 0)
            return entity.getItemBySlot(slot);

        try {
            Object stacks = GET_COSMETIC_STACKS.invoke(null, entity.getUnderlyingPlayer().getUUID());
            boolean hideEquippedArmor = (boolean) stacks.getClass().getMethod("isSkinArmor", int.class).invoke(stacks, cosmeticSlot);
            if (hideEquippedArmor)
                return ItemStack.EMPTY;

            ItemStack cosmetic = (ItemStack) stacks.getClass().getMethod("getStackInSlot", int.class).invoke(stacks, cosmeticSlot);
            return cosmetic == null || cosmetic.isEmpty() ? entity.getItemBySlot(slot) : cosmetic;
        } catch (ReflectiveOperationException | RuntimeException ignored) {
            return entity.getItemBySlot(slot);
        }
    }
}
