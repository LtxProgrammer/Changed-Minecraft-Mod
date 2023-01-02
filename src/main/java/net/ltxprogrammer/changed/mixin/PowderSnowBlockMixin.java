package net.ltxprogrammer.changed.mixin;

import net.ltxprogrammer.changed.entity.PowderSnowWalkable;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.PowderSnowBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PowderSnowBlock.class)
public abstract class PowderSnowBlockMixin {
    @Inject(method = "canEntityWalkOnPowderSnow", at = @At("HEAD"), cancellable = true)
    private static void canEntityWalkOnPowderSnow(Entity p_154256_, CallbackInfoReturnable<Boolean> callback) {
        if (p_154256_ instanceof PowderSnowWalkable) {
            callback.setReturnValue(true);
        } else if (p_154256_ instanceof Player player && ProcessTransfur.isPlayerLatex(player) &&
                ProcessTransfur.getPlayerLatexVariant(player).getLatexEntity() instanceof PowderSnowWalkable) {
            callback.setReturnValue(true);
        }
    }
}
