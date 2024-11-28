package net.ltxprogrammer.changed.mixin.compatibility.Curios;

import net.ltxprogrammer.changed.extension.RequiredMods;
import net.ltxprogrammer.changed.extension.curios.CurioEntities;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.theillusivec4.curios.api.type.ISlotType;
import top.theillusivec4.curios.server.SlotHelper;

import java.util.Collection;
import java.util.stream.Collectors;

@Mixin(value = SlotHelper.class, remap = false)
@RequiredMods("curios")
public abstract class SlotHelperMixin {
    @Inject(method = "getSlotTypes(Lnet/minecraft/world/entity/LivingEntity;)Ljava/util/Collection;", at = @At("RETURN"), cancellable = true)
    public void removeInvalidSlots(LivingEntity livingEntity, CallbackInfoReturnable<Collection<ISlotType>> cir) {
        final EntityType<?> entityType = ProcessTransfur.getPlayerTransfurVariantSafe(EntityUtil.playerOrNull(livingEntity))
                .filter(variant -> !variant.isTemporaryFromSuit() && variant.shouldApplyAbilities())
                .map(variant -> (EntityType)variant.getParent().getEntityType())
                .orElse(livingEntity.getType());

        cir.setReturnValue(cir.getReturnValue().stream().filter(entry -> CurioEntities.INSTANCE.canEntityTypeUseSlot(entityType, entry.getIdentifier()))
                .collect(Collectors.toUnmodifiableSet()));
    }
}
