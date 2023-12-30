package net.ltxprogrammer.changed.client.renderer.animate.upperbody;

import net.ltxprogrammer.changed.client.renderer.animate.LatexAnimator;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class SharkUpperBodyInitAnimator<T extends LatexEntity, M extends EntityModel<T>> extends AbstractUpperBodyAnimator<T, M> {
    public SharkUpperBodyInitAnimator(ModelPart head, ModelPart torso, ModelPart leftArm, ModelPart rightArm) {
        super(head, torso, leftArm, rightArm);
    }

    @Override
    public LatexAnimator.AnimateStage preferredStage() {
        return LatexAnimator.AnimateStage.INIT;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        boolean fallFlying = entity.getFallFlyingTicks() > 4;
        torso.yRot = 0.0F;
        torso.zRot = 0.0F;
        torso.x = 0.0F;
        rightArm.z = core.forwardOffset;
        rightArm.x = -core.torsoWidth;
        leftArm.z = core.forwardOffset;
        leftArm.x = core.torsoWidth;
        float f = 1.0F;
        if (fallFlying) {
            f = (float)entity.getDeltaMovement().lengthSqr();
            f /= 0.2F;
            f *= f * f;
        }

        if (f < 1.0F) {
            f = 1.0F;
        }

        rightArm.zRot = 0.0F;
        leftArm.zRot = 0.0F;

        float rightSwayStand = Mth.cos((limbSwing * 0.6662F) + Mth.PI);
        float rightSwaySwim = Mth.cos((limbSwing * 0.6662F * 0.25f) + Mth.PI);
        float leftSwayStand = Mth.cos(limbSwing * 0.6662F);
        float leftSwaySwim = Mth.cos(limbSwing * 0.6662F * 0.25f);
        float swayScale = Mth.lerp(core.swimAmount, 1.0F, 0.1f);
        rightArm.xRot = swayScale * Mth.lerp(core.swimAmount, rightSwayStand, rightSwaySwim) * 2.0F * limbSwingAmount * 0.5F / f;
        leftArm.xRot = swayScale * Mth.lerp(core.swimAmount, leftSwayStand, leftSwaySwim) * 2.0F * limbSwingAmount * 0.5F / f;
    }
}