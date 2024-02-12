package net.ltxprogrammer.changed.client.renderer.animate.bipedal;

import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import org.jetbrains.annotations.NotNull;

public class BipedalStandAnimator<T extends ChangedEntity, M extends EntityModel<T>> extends AbstractBipedalAnimator<T, M> {
    public BipedalStandAnimator(ModelPart leftLeg, ModelPart rightLeg) {
        super(leftLeg, rightLeg);
    }

    @Override
    public HumanoidAnimator.AnimateStage preferredStage() {
        return HumanoidAnimator.AnimateStage.STAND;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        rightLeg.z = 0.1F + core.forwardOffset;
        leftLeg.z = 0.1F + core.forwardOffset;
        rightLeg.y = core.calculateLegPositionY();
        leftLeg.y = core.calculateLegPositionY();
    }
}
