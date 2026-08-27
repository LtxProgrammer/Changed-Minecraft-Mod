package net.ltxprogrammer.changed.mixin.compatibility.Vivecraft;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.ltxprogrammer.changed.client.renderer.layers.LatexElytraLayer;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.extension.RequiredMods;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.util.Mth;
import org.joml.Matrix3f;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.vivecraft.client.ClientVRPlayers;
import org.vivecraft.client.utils.ModelUtils;
import org.vivecraft.common.utils.MathUtils;

@Mixin(value = LatexElytraLayer.class)
@RequiredMods("vivecraft")
public abstract class LatexElytraLayerMixin<T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> extends RenderLayer<T, M> {
    @Unique
    private final Vector3f vivecraft$tempV = new Vector3f();

    @Unique
    private final Matrix3f vivecraft$bodyRot = new Matrix3f();

    public LatexElytraLayerMixin(RenderLayerParent<T, M> renderer) {
        super(renderer);
    }

    // Copied from ElytraLayerMixin
    // https://github.com/Vivecraft/VivecraftMod/blob/Multiloader-1.20/common/src/main/java/org/vivecraft/mixin/client/renderer/entity/layers/ElytraLayerMixin.java

    @WrapOperation(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/ltxprogrammer/changed/entity/ChangedEntity;FFFFFF)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(DDD)V"))
    private void vivecraft$elytraPosition(
            PoseStack instance, double pX, double pY, double pZ, Operation<Void> original,
            @Local(argsOnly = true) ChangedEntity changedEntity, @Local(argsOnly = true, ordinal = 2) float partialTick) {

        if (changedEntity.getUnderlyingPlayer() instanceof AbstractClientPlayer entity) {
            // don't care about interpolation here, only needs the scales which aren't interpolated
            ClientVRPlayers.RotInfo rotInfo = ClientVRPlayers.getInstance().getLatestRotationsForPlayer(entity.getUUID());
            // only do this if it's a player model and a vr player
            M model = getParentModel();
            if (rotInfo != null) {
                ModelPart body = model.getTorso();
                this.vivecraft$bodyRot.rotationZYX(body.zRot, -body.yRot, -body.xRot);

                this.vivecraft$bodyRot.transform(MathUtils.UP, this.vivecraft$tempV);
                float xRotation = (float) Math.atan2(this.vivecraft$tempV.y, this.vivecraft$tempV.z) - Mth.HALF_PI;

                this.vivecraft$bodyRot.transform(MathUtils.LEFT, this.vivecraft$tempV);
                float yRotation = (float) -Math.atan2(this.vivecraft$tempV.x, this.vivecraft$tempV.y) + Mth.HALF_PI;

                // position the cape behind the body
                float yOffset = 0F;
                if (entity.isFallFlying()) {
                    // move it down, to not be in the players face
                    yOffset = 2F;
                } else if (entity.isCrouching()) {
                    // undo vanilla crouch offset
                    yOffset = -3F;
                }
                // transform offset to be body relative
                this.vivecraft$tempV.set(0F, yOffset, 2F - 0.5F * (body.xRot / Mth.HALF_PI));
                this.vivecraft$tempV.rotateX(xRotation);
                this.vivecraft$tempV.rotateZ(yRotation);

                // +24 because it should be the offset to the default position, which is at 24
                this.vivecraft$tempV.add(body.x, body.y + 24F, body.z);

                // no yaw, since we  need the vector to be player rotated anyway
                ModelUtils.modelToWorld(entity, this.vivecraft$tempV, rotInfo, 0F, false, false, this.vivecraft$tempV);
                original.call(instance, (double) this.vivecraft$tempV.x, (double) -this.vivecraft$tempV.y, (double) -this.vivecraft$tempV.z);

                // rotate elytra
                instance.mulPose(Axis.XP.rotation(xRotation));
                instance.mulPose(Axis.YP.rotation(yRotation));
            }
        } else {
            original.call(instance, pX, pY, pZ);
        }
    }

}