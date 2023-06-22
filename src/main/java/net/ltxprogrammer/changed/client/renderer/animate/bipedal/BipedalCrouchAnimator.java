package net.ltxprogrammer.changed.client.renderer.animate.bipedal;

import net.ltxprogrammer.changed.client.renderer.animate.LatexAnimator;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import org.jetbrains.annotations.NotNull;

public class BipedalCrouchAnimator<T extends LatexEntity, M extends EntityModel<T>> extends AbstractBipedalAnimator<T, M> {
    public BipedalCrouchAnimator(ModelPart leftLeg, ModelPart rightLeg) {
        super(leftLeg, rightLeg);
    }

    @Override
    public LatexAnimator.AnimateStage preferredStage() {
        return LatexAnimator.AnimateStage.CROUCH;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        rightLeg.z = 4.0F + core.forwardOffset + ((core.torsoLength - 12.0f) / 1.83048772171f);
        leftLeg.z = 4.0F + core.forwardOffset + ((core.torsoLength - 12.0f) / 1.83048772171f);
        rightLeg.y = 12.2F + core.hipOffset;
        leftLeg.y = 12.2F + core.hipOffset;
    }
}
