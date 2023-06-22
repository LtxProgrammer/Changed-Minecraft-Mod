package net.ltxprogrammer.changed.client.renderer.animate.armsets;

import net.ltxprogrammer.changed.client.renderer.animate.LatexAnimator;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import org.jetbrains.annotations.NotNull;

public class ArmSetTwoBobAnimator<T extends LatexEntity, M extends EntityModel<T>> extends LatexAnimator.Animator<T, M> {
    public final ModelPart leftArm;
    public final ModelPart rightArm;
    public final ModelPart leftArm2;
    public final ModelPart rightArm2;

    public ArmSetTwoBobAnimator(ModelPart leftArm, ModelPart rightArm, ModelPart leftArm2, ModelPart rightArm2) {
        this.leftArm = leftArm;
        this.rightArm = rightArm;
        this.leftArm2 = leftArm2;
        this.rightArm2 = rightArm2;
    }

    @Override
    public LatexAnimator.AnimateStage preferredStage() {
        return LatexAnimator.AnimateStage.BOB;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        rightArm2.z = 0.0F;
        rightArm2.y = rightArm.y - 2.0F;
        rightArm2.x = -core.torsoWidth;
        leftArm2.z = 0.0F;
        leftArm2.y = leftArm.y - 2.0F;
        leftArm2.x = core.torsoWidth;
    }
}
