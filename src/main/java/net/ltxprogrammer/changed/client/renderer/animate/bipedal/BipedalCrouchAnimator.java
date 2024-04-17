package net.ltxprogrammer.changed.client.renderer.animate.bipedal;

import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import org.jetbrains.annotations.NotNull;

public class BipedalCrouchAnimator<T extends ChangedEntity, M extends EntityModel<T>> extends AbstractBipedalAnimator<T, M> {
    public BipedalCrouchAnimator(ModelPart leftLeg, ModelPart rightLeg) {
        super(leftLeg, rightLeg);
    }

    @Override
    public HumanoidAnimator.AnimateStage preferredStage() {
        return HumanoidAnimator.AnimateStage.CROUCH;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        rightLeg.z = 4.0F + core.forwardOffset + ((core.torsoLength - 12.0f) / 1.83048772171f);
        leftLeg.z = 4.0F + core.forwardOffset + ((core.torsoLength - 12.0f) / 1.83048772171f);
        rightLeg.y = core.calculateLegPositionY() + 0.2f;
        leftLeg.y = core.calculateLegPositionY() + 0.2f;
    }
}
