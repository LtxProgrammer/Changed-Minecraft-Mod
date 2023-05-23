package net.ltxprogrammer.changed.mixin.entity;

import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.sensing.VillagerHostilesSensor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VillagerHostilesSensor.class)
public abstract class VillagerHostilesSensorMixin {
    @Inject(method = "isClose", at = @At("HEAD"), cancellable = true)
    private void isClose(LivingEntity villager, LivingEntity hostile, CallbackInfoReturnable<Boolean> callback) {
        if (hostile instanceof LatexEntity latex && !latex.getType().is(ChangedTags.EntityTypes.ORGANIC_LATEX)) {
            callback.setReturnValue(latex.distanceToSqr(villager) <= 64.0);
        }
    }

    @Inject(method = "isHostile", at = @At("HEAD"), cancellable = true)
    private void isHostile(LivingEntity hostile, CallbackInfoReturnable<Boolean> callback) {
        if (hostile instanceof LatexEntity latex && !latex.getType().is(ChangedTags.EntityTypes.ORGANIC_LATEX))
            callback.setReturnValue(true);
    }
}
