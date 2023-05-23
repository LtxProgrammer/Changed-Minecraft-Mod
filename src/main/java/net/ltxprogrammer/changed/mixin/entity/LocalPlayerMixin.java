package net.ltxprogrammer.changed.mixin.entity;


import com.mojang.authlib.GameProfile;
import net.ltxprogrammer.changed.entity.PlayerDataExtension;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.InputWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.tags.FluidTags;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@OnlyIn(Dist.CLIENT)
@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends AbstractClientPlayer implements PlayerDataExtension {
    public LocalPlayerMixin(ClientLevel p_108548_, GameProfile p_108549_) {
        super(p_108548_, p_108549_);
    }

    @Shadow public Input input;
    @Shadow @Final public Minecraft minecraft;
    @Shadow public int autoJumpTime;
    @Shadow @Final public ClientPacketListener connection;
    @Shadow public abstract boolean isMovingSlowly();

    @Shadow public float yBobO;

    @Shadow public float yBob;

    @Shadow public float xBobO;

    @Shadow public float xBob;

    @Inject(method = "getWaterVision", at = @At("RETURN"), cancellable = true)
    private void getWaterVision(CallbackInfoReturnable<Float> callback) {
        ProcessTransfur.ifPlayerLatex(this, variant -> {
            if (!variant.getParent().getBreatheMode().canBreatheWater())
                return;
            if (!this.isEyeInFluid(FluidTags.WATER))
                return;
            for (var level : Thread.currentThread().getStackTrace()) {
                if (level.toString().contains("LightTexture")) // Light texture breaks when returning > 1.0F
                    return;
            }

            callback.setReturnValue(callback.getReturnValue() * 4.0F);
        });
    }

    @Inject(method = "aiStep", at = @At("HEAD"), cancellable = true)
    public void aiStep(CallbackInfo ci) {
        var playerMover = getPlayerMover();
        if (playerMover != null) {
            this.input.tick(this.isMovingSlowly());
            playerMover.aiStep((LocalPlayer)(Object)this, InputWrapper.from(this), LogicalSide.CLIENT);
            super.aiStep();
            ci.cancel();
            return;
        }

        LocalPlayer player = (LocalPlayer)(Object)this;
        if (!player.level.isClientSide) return;

        ProcessTransfur.ifPlayerLatex(player, variant -> {
            if (variant.getParent().canGlide) {
                KeyboardInput kb = null;
                if (input instanceof KeyboardInput k) kb = k;

                boolean jumping = input.jumping;
                boolean flying = this.getAbilities().flying;

                jumping = kb.options.keyJump.isDown();

                boolean flag3 = false;
                if (this.autoJumpTime > 0) {
                    flag3 = true;
                    jumping = true;
                }

                boolean flag7 = false;
                if (this.getAbilities().mayfly) {
                    if (this.minecraft.gameMode.isAlwaysFlying()) {
                        if (!flying) {
                            flying = true;
                            flag7 = true;
                        }
                    } else if (!input.jumping && jumping && !flag3) {
                        if (this.jumpTriggerTime != 0 && !this.isSwimming()) {
                            flying = !flying;
                            flag7 = true;
                        }
                    }
                }

                if (jumping && !flag7 && !input.jumping && !flying && !this.isPassenger() && !this.onClimbable()) {
                    if (this.tryToStartFallFlying()) {
                        this.connection.send(new ServerboundPlayerCommandPacket(this, ServerboundPlayerCommandPacket.Action.START_FALL_FLYING));
                    }
                }
            }

            if (variant.getParent().swimSpeed >= 2.0F && player.isUnderWater())
                player.setSprinting(true);
        });
    }
    
    @Inject(method = "serverAiStep", at = @At("HEAD"), cancellable = true)
    public void serverAiStep(CallbackInfo ci) {
        var playerMover = getPlayerMover();
        if (playerMover != null) {
            this.yBobO = this.yBob;
            this.xBobO = this.xBob;
            this.xBob += (this.getXRot() - this.xBob) * 0.5F;
            this.yBob += (this.getYRot() - this.yBob) * 0.5F;
            playerMover.serverAiStep((LocalPlayer)(Object)this, InputWrapper.from(this), LogicalSide.CLIENT);
            super.serverAiStep();
            ci.cancel();
        }
    }

    @Inject(method = "isAutoJumpEnabled", at = @At("HEAD"), cancellable = true)
    public void isAutoJumpEnabled(CallbackInfoReturnable<Boolean> callback) {
        if (getPlayerMover() != null) {
            callback.setReturnValue(false);
        }
    }

    @Inject(method = "isMovingSlowly", at = @At("HEAD"), cancellable = true)
    public void isMovingSlowly(CallbackInfoReturnable<Boolean> callback) {
        ProcessTransfur.ifPlayerLatex(this, variant -> {
            if (variant.getLatexEntity() != null)
                callback.setReturnValue(variant.getLatexEntity().isMovingSlowly());
        });
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo callback) {
        var playerMover = getPlayerMover();
        if (playerMover != null && playerMover.shouldRemoveMover(this, InputWrapper.from(this), LogicalSide.CLIENT))
            setPlayerMover(null);
    }
}
