package net.ltxprogrammer.changed.client.renderer.animate.upperbody;

import net.ltxprogrammer.changed.client.renderer.animate.LatexAnimator;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import static net.ltxprogrammer.changed.client.renderer.animate.wing.DragonWingCreativeFlyAnimator.BODY_FLY_SCALE;
import static net.ltxprogrammer.changed.client.renderer.animate.wing.DragonWingCreativeFlyAnimator.WING_FLAP_RATE;

public class DragonUpperBodyCreativeFlyAnimator<T extends LatexEntity, M extends EntityModel<T>> extends AbstractUpperBodyAnimator<T, M> {
    public DragonUpperBodyCreativeFlyAnimator(ModelPart head, ModelPart torso, ModelPart leftArm, ModelPart rightArm) {
        super(head, torso, leftArm, rightArm);
    }

    @Override
    public LatexAnimator.AnimateStage preferredStage() {
        return LatexAnimator.AnimateStage.CREATIVE_FLY;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float flapAmount = Mth.cos(ageInTicks * WING_FLAP_RATE);
        flapAmount = Mth.map(flapAmount * flapAmount, 0.0f, 1.0f, -BODY_FLY_SCALE, BODY_FLY_SCALE);
        torso.y = Mth.lerp(core.flyAmount, torso.y, flapAmount - 1);
        torso.z += Mth.lerp(core.flyAmount, 0.0f, -1.0f);
        leftArm.y = Mth.lerp(core.flyAmount, leftArm.y, flapAmount + 1);
        leftArm.z += Mth.lerp(core.flyAmount, 0.0f, 1.0f);
        rightArm.y = Mth.lerp(core.flyAmount, rightArm.y, flapAmount + 1);
        rightArm.z += Mth.lerp(core.flyAmount, 0.0f, 1.0f);

        torso.xRot = Mth.lerp(core.flyAmount, torso.xRot, Mth.DEG_TO_RAD * 45.0f);
        leftArm.xRot = Mth.lerp(core.flyAmount, leftArm.xRot, Mth.DEG_TO_RAD * 30.0f);
        rightArm.xRot = Mth.lerp(core.flyAmount, rightArm.xRot, Mth.DEG_TO_RAD * 30.0f);
    }
}
