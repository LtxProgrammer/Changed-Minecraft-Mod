package net.ltxprogrammer.changed.client.renderer.animate.upperbody;

import net.ltxprogrammer.changed.client.renderer.animate.LatexAnimator;
import net.ltxprogrammer.changed.client.renderer.animate.tail.WolfTailInitAnimator;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class WolfUpperBodyStandAnimator<T extends LatexEntity, M extends EntityModel<T>> extends AbstractUpperBodyAnimator<T, M> {
    public WolfUpperBodyStandAnimator(ModelPart head, ModelPart torso, ModelPart leftArm, ModelPart rightArm) {
        super(head, torso, leftArm, rightArm);
    }

    @Override
    public LatexAnimator.AnimateStage preferredStage() {
        return LatexAnimator.AnimateStage.STAND;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        final float ageAdjusted = ageInTicks * WolfTailInitAnimator.SWAY_RATE * 0.25f;
        float ageSin = Mth.sin(ageAdjusted * Mth.PI * 0.5f);
        float ageCos = Mth.cos(ageAdjusted * Mth.PI * 0.5f);
        float ageLerp = Mth.lerp(1.0f - Mth.abs(Mth.positiveModulo(ageAdjusted, 2.0f) - 1.0f),
                ageSin * ageSin * ageSin * ageSin, 1.0f - (ageCos * ageCos * ageCos * ageCos));

        torso.z = Mth.lerp(ageLerp, -2.0f, -2.5f);
        torso.z = Mth.lerp(limbSwingAmount, torso.z, -0.5f);
        head.z = torso.z;
        leftArm.z = torso.z;
        rightArm.z = torso.z;

        torso.xRot = Mth.lerp(ageLerp, (float)Math.PI / 16.0f, (float)Math.PI / 12.0f);
        torso.xRot = Mth.lerp(limbSwingAmount, torso.xRot, (float)Math.PI / 30.0f);
        torso.yRot = 0.0F;

        torso.y = Mth.lerp(ageLerp, 0.0f, Mth.lerp(limbSwingAmount, 1.0f, 0.25f)) + core.hipOffset + (12.0f - core.legLength);
        head.y = torso.y + Mth.lerp(limbSwingAmount, 0.5f, 0.05f);
        leftArm.y = torso.y + 2.75f;
        rightArm.y = torso.y + 2.75f;
    }
}
