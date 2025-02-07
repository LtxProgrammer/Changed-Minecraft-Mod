package net.ltxprogrammer.changed.mixin.compatibility.DoABarrelRoll;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.ltxprogrammer.changed.client.renderer.AdvancedHumanoidRenderer;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.extension.RequiredMods;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import nl.enjarai.doabarrelroll.DoABarrelRollClient;
import nl.enjarai.doabarrelroll.config.ModConfig;
import nl.enjarai.doabarrelroll.flight.ElytraMath;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AdvancedHumanoidRenderer.class)
@RequiredMods("do_a_barrel_roll")
public abstract class AdvancedHumanoidRendererMixin {
    private ChangedEntity entity;

    @Inject(
            method = "setupRotations(Lnet/ltxprogrammer/changed/entity/ChangedEntity;Lcom/mojang/blaze3d/vertex/PoseStack;FFF)V",
            at = @At("HEAD"),
            remap = false
    )
    private void doABarrelRoll$capturePlayer(ChangedEntity entity, PoseStack matrixStack, float f, float g, float h, CallbackInfo ci) {
        this.entity = entity;
    }

    @ModifyArg(
            method = {"setupRotations(Lnet/ltxprogrammer/changed/entity/ChangedEntity;Lcom/mojang/blaze3d/vertex/PoseStack;FFF)V"},
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/PoseStack;mulPose(Lcom/mojang/math/Quaternion;)V",
                    ordinal = 1
            ),
            index = 0
    )
    private Quaternion doABarrelRoll$modifyRoll(Quaternion original) {
        if (ModConfig.INSTANCE.getModEnabled() && this.entity.getUnderlyingPlayer() instanceof LocalPlayer) {
            double roll = ElytraMath.getRoll(this.entity.getYRot(), DoABarrelRollClient.left);
            return Vector3f.YP.rotationDegrees((float)roll);
        } else {
            return original;
        }
    }
}
