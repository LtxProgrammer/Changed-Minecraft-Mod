package net.ltxprogrammer.changed.client.renderer.animate.upperbody;

import net.ltxprogrammer.changed.client.renderer.animate.LatexAnimator;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class DoubleArmUpperBodyCrouchAnimator<T extends LatexEntity, M extends EntityModel<T>> extends AbstractUpperBodyAnimator<T, M> {
    public final ModelPart upperLeftArm;
    public final ModelPart upperRightArm;
    public final ModelPart lowerLeftArm;
    public final ModelPart lowerRightArm;

    public DoubleArmUpperBodyCrouchAnimator(ModelPart head, ModelPart torso, ModelPart upperLeftArm, ModelPart upperRightArm, ModelPart lowerLeftArm, ModelPart lowerRightArm) {
        super(head, torso, upperLeftArm, upperRightArm);
        this.upperLeftArm = upperLeftArm;
        this.upperRightArm = upperRightArm;
        this.lowerLeftArm = lowerLeftArm;
        this.lowerRightArm = lowerRightArm;
    }

    @Override
    public LatexAnimator.AnimateStage preferredStage() {
        return LatexAnimator.AnimateStage.CROUCH;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        torso.z = Mth.lerp(core.ageLerp, -1.0f, -1.5f);
        head.z = torso.z;

        torso.xRot = Mth.lerp(core.ageLerp, 0.5f, 0.6f);
        upperRightArm.xRot += 0.3F;
        upperLeftArm.xRot += 0.3F;
        lowerRightArm.xRot += 0.55F;
        lowerLeftArm.xRot += 0.55F;

        torso.y = Mth.lerp(core.ageLerp, 3.2f, 4.0f) + core.hipOffset + (12.0f - core.legLength);
        head.y = torso.y + 0.5f;
        upperLeftArm.y = torso.y + 2.25f;
        upperRightArm.y = torso.y + 2.25f;
        lowerLeftArm.y = torso.y + 6.25f;
        lowerRightArm.y = torso.y + 6.25f;
    }
}