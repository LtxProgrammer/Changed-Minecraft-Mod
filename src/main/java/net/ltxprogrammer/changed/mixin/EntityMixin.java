package net.ltxprogrammer.changed.mixin;

import net.ltxprogrammer.changed.block.WhiteLatexTransportInterface;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.commands.CommandSource;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.entity.EntityAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin extends net.minecraftforge.common.capabilities.CapabilityProvider<Entity> implements Nameable, EntityAccess, CommandSource, net.minecraftforge.common.extensions.IForgeEntity {
    protected EntityMixin(Class<Entity> baseClass) {
        super(baseClass);
    }

    /*@Inject(method = "playStepSound", at = @At("HEAD"), cancellable = true)
    protected void playStepSound(BlockPos pos, BlockState blockState, CallbackInfo ci) {
        if (((Entity) (Object) this) instanceof Player player) {
            if (ProcessTransfur.isPlayerLatex(player)) {
                player.playSound(ProcessTransfur.getPlayerLatexVariant(player).getLatexEntity().getStepSound(pos, blockState), 0.15f, 1);
                ci.cancel();
            }
        }
    }*/

    @Inject(method = "getTicksRequiredToFreeze", at = @At("HEAD"), cancellable = true)
    public void getTicksRequiredToFreeze(CallbackInfoReturnable<Integer> ci) {
        if ((Entity)(Object)this instanceof Player player && ProcessTransfur.isPlayerLatex(player)) {
            LatexVariant<?> variant = ProcessTransfur.getPlayerLatexVariant(player);
            ci.setReturnValue(variant.getTicksRequiredToFreeze(player.level));
            ci.cancel();
        }
    }

    @Inject(method = "isSwimming", at = @At("HEAD"), cancellable = true)
    public void isSwimming(CallbackInfoReturnable<Boolean> ci) {
        if (((Entity) (Object) this) instanceof LivingEntity le) {
            if (WhiteLatexTransportInterface.isEntityInWhiteLatex(le)) {
                ci.setReturnValue(true);
                ci.cancel();
            }
        }
    }

    @Inject(method = "isInWater", at = @At("HEAD"), cancellable = true)
    public void isInWater(CallbackInfoReturnable<Boolean> ci) {
        if (((Entity) (Object) this) instanceof LivingEntity le) {
            if (WhiteLatexTransportInterface.isEntityInWhiteLatex(le)) {
                ci.setReturnValue(true);
                ci.cancel();
            }
        }
    }
}