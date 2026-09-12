package net.ltxprogrammer.changed.mixin.client;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.ltxprogrammer.changed.block.AbstractLatexBlock;
import net.ltxprogrammer.changed.block.AlertingPuddle;
import net.ltxprogrammer.changed.client.MultiPlayerGameModeExtension;
import net.ltxprogrammer.changed.entity.latex.LatexType;
import net.ltxprogrammer.changed.init.ChangedLatexTypes;
import net.ltxprogrammer.changed.world.LatexCoverHitResult;
import net.ltxprogrammer.changed.world.LatexCoverState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.InputEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Shadow @Nullable public Entity cameraEntity;

    @Shadow @Nullable public HitResult hitResult;

    @Shadow @Nullable public LocalPlayer player;

    @Shadow @Nullable public ClientLevel level;

    @Shadow @Nullable public MultiPlayerGameMode gameMode;

    @Shadow @Final public ParticleEngine particleEngine;

    @Shadow @Final public Options options;

    @WrapMethod(method = "shouldEntityAppearGlowing")
    public boolean isEntityMovingOnWhiteLatex(Entity entity, Operation<Boolean> original) {
        if (!(entity instanceof LivingEntity livingEntity))
            return original.call(entity);
        if (this.cameraEntity == null)
            return original.call(entity);
        if (LatexType.getEntityLatexType(this.cameraEntity) != ChangedLatexTypes.WHITE_LATEX.get())
            return original.call(entity);
        if (LatexType.getEntityLatexType(livingEntity) == ChangedLatexTypes.WHITE_LATEX.get())
            return original.call(entity);

        BlockState standing = livingEntity.level().getBlockState(livingEntity.blockPosition().below());
        if (standing == null || standing.isAir())
            return original.call(entity);
        if (AbstractLatexBlock.isSurfaceOfType(entity.level(), livingEntity.blockPosition(), Direction.DOWN, ChangedLatexTypes.WHITE_LATEX.get()))
            return true;
        return original.call(entity);
    }

    @WrapMethod(method = "shouldEntityAppearGlowing")
    public boolean isEntityMovingOnAlertPuddle(Entity entity, Operation<Boolean> original) {
        if (!(entity instanceof LivingEntity livingEntity))
            return original.call(entity);
        if (this.cameraEntity == null)
            return original.call(entity);

        BlockState standing = livingEntity.level().getBlockState(livingEntity.blockPosition());
        if (standing.getBlock() instanceof AlertingPuddle alertingPuddle && alertingPuddle.shouldGlowLocally(livingEntity, this.cameraEntity))
            return true;
        standing = livingEntity.level().getBlockState(livingEntity.blockPosition().below());
        if (standing.getBlock() instanceof AlertingPuddle alertingPuddle && alertingPuddle.shouldGlowLocally(livingEntity, this.cameraEntity))
            return true;
        return original.call(entity);
    }

    @Inject(method = "continueAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;isEmptyBlock(Lnet/minecraft/core/BlockPos;)Z"), cancellable = true)
    private void changed$continueAttackOnLatexCover(boolean shouldContinue, CallbackInfo ci,
                                                    @Local BlockPos blockPos) {
        if (!(this.hitResult instanceof LatexCoverHitResult latexHitResult)) {
            ((MultiPlayerGameModeExtension)this.gameMode).changed$stopDestroyLatexCover();
            return;
        }

        ci.cancel();

        InputEvent.InteractionKeyMappingTriggered inputEvent = ForgeHooksClient.onClickInput(0, this.options.keyAttack, InteractionHand.MAIN_HAND);
        if (inputEvent.isCanceled()) {
            if (inputEvent.shouldSwingHand()) {
                this.particleEngine.addBlockHitEffects(blockPos, latexHitResult);
                this.player.swing(InteractionHand.MAIN_HAND);
            }

            return;
        }

        Direction direction = latexHitResult.getDirection();
        if (((MultiPlayerGameModeExtension)this.gameMode).changed$continueDestroyLatexCover(blockPos, direction) && inputEvent.shouldSwingHand()) {
            this.particleEngine.addBlockHitEffects(blockPos, latexHitResult);
            this.player.swing(InteractionHand.MAIN_HAND);
        }
    }

    @Inject(method = "continueAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;stopDestroyBlock()V"), cancellable = true)
    private void changed$stopAttackOnCover(boolean shouldContinue, CallbackInfo ci) {
        if (((MultiPlayerGameModeExtension)this.gameMode).changed$isDestroyingLatexCover()) {
            ((MultiPlayerGameModeExtension)this.gameMode).changed$stopDestroyLatexCover();
            ci.cancel();
        }
    }

    @Inject(method = "startAttack", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/client/event/InputEvent$InteractionKeyMappingTriggered;isCanceled()Z", remap = false), cancellable = true)
    public void changed$startAttackOnLatexCover(CallbackInfoReturnable<Boolean> cir,
                                                @Local InputEvent.InteractionKeyMappingTriggered inputEvent) {
        if (inputEvent.isCanceled())
            return;

        if (!(this.hitResult instanceof LatexCoverHitResult latexHitResult))
            return;

        cir.cancel();

        boolean flag = false;

        BlockPos blockpos = latexHitResult.getBlockPos();
        if (!LatexCoverState.getAt(this.level, blockpos).isAir()) {
            ((MultiPlayerGameModeExtension)this.gameMode).changed$startDestroyLatexCover(blockpos, latexHitResult.getDirection());
            if (LatexCoverState.getAt(this.level, blockpos).isAir()) {
                flag = true;
            }
        }

        if (inputEvent.shouldSwingHand()) {
            this.player.swing(InteractionHand.MAIN_HAND);
        }

        cir.setReturnValue(flag);
    }
}
