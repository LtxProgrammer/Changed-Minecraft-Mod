package net.ltxprogrammer.changed.client.renderer.animate.armsets;

import net.ltxprogrammer.changed.client.renderer.animate.LatexAnimator;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import org.jetbrains.annotations.NotNull;

public class ArmSetThreeBobAnimator<T extends LatexEntity, M extends EntityModel<T>> extends LatexAnimator.Animator<T, M> {
    public final ModelPart leftArm;
    public final ModelPart rightArm;
    public final ModelPart leftArm3;
    public final ModelPart rightArm3;

    public ArmSetThreeBobAnimator(ModelPart leftArm, ModelPart rightArm, ModelPart leftArm3, ModelPart rightArm3) {
        this.leftArm = leftArm;
        this.rightArm = rightArm;
        this.leftArm3 = leftArm3;
        this.rightArm3 = rightArm3;
    }

    @Override
    public LatexAnimator.AnimateStage preferredStage() {
        return LatexAnimator.AnimateStage.BOB;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        rightArm3.z = 0.0F;
        rightArm3.y = rightArm.y + 2.0F;
        rightArm3.x = -core.torsoWidth;
        leftArm3.z = 0.0F;
        leftArm3.y = leftArm.y + 2.0F;
        leftArm3.x = core.torsoWidth;
    }
}
