package net.ltxprogrammer.changed.client.renderer.animate.upperbody;

import net.ltxprogrammer.changed.client.renderer.animate.LatexAnimator;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class SharkUpperBodyCrouchAnimator<T extends LatexEntity, M extends EntityModel<T>> extends AbstractUpperBodyAnimator<T, M> {
    public SharkUpperBodyCrouchAnimator(ModelPart head, ModelPart torso, ModelPart leftArm, ModelPart rightArm) {
        super(head, torso, leftArm, rightArm);
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
        rightArm.xRot += 0.3F;
        leftArm.xRot += 0.3F;

        torso.y = Mth.lerp(core.ageLerp, 3.2f, 4.0f) + core.hipOffset + (12.0f - core.legLength);
        head.y = torso.y + 0.5f;
        leftArm.y = torso.y + 2.25f;
        rightArm.y = torso.y + 2.25f;
    }
}
