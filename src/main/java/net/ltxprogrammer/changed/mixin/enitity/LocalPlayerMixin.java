package net.ltxprogrammer.changed.mixin.enitity;


import com.mojang.authlib.GameProfile;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.tags.FluidTags;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@OnlyIn(Dist.CLIENT)
@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends AbstractClientPlayer {
    public LocalPlayerMixin(ClientLevel p_108548_, GameProfile p_108549_) {
        super(p_108548_, p_108549_);
    }

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

    @Inject(method = "aiStep", at = @At("HEAD"))
    public void aiStep(CallbackInfo ci) {
        LocalPlayer player = (LocalPlayer)(Object)this;
        if (!player.level.isClientSide) return;

        ProcessTransfur.ifPlayerLatex(player, variant -> {
            if (variant.getParent().canGlide) {
                boolean flag = player.input.jumping;

                KeyboardInput kb = null;
                if (player.input instanceof KeyboardInput k) kb = k;

                boolean jumping = flag;
                boolean flying = player.getAbilities().flying;

                jumping = kb.options.keyJump.isDown();

                boolean flag3 = false;
                if (player.autoJumpTime > 0) {
                    flag3 = true;
                    jumping = true;
                }

                boolean flag7 = false;
                if (player.getAbilities().mayfly) {
                    if (player.minecraft.gameMode.isAlwaysFlying()) {
                        if (!flying) {
                            flying = true;
                            flag7 = true;
                        }
                    } else if (!flag && jumping && !flag3) {
                        if (this.jumpTriggerTime != 0 && !player.isSwimming()) {
                            flying = !flying;
                            flag7 = true;
                        }
                    }
                }

                if (jumping && !flag7 && !flag && !flying && !player.isPassenger() && !player.onClimbable()) {
                    if (player.tryToStartFallFlying()) {
                        player.connection.send(new ServerboundPlayerCommandPacket(player, ServerboundPlayerCommandPacket.Action.START_FALL_FLYING));
                    }
                }
            }

            if (variant.getParent().swimSpeed >= 2.0F && player.isUnderWater())
                player.setSprinting(true);
        });
    }

    @Inject(method = "isMovingSlowly", at = @At("HEAD"), cancellable = true)
    public void isMovingSlowly(CallbackInfoReturnable<Boolean> callback) {
        ProcessTransfur.ifPlayerLatex(this, variant -> {
            if (variant.getLatexEntity() != null)
                callback.setReturnValue(variant.getLatexEntity().isMovingSlowly());
        });
    }
}
