package net.ltxprogrammer.changed.client.renderer.animate.quadrupedal;

import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import org.jetbrains.annotations.NotNull;

public class QuadrupedalStandAnimator<T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> extends AbstractQuadrupedalAnimator<T, M> {
    public QuadrupedalStandAnimator(ModelPart torso, ModelPart frontLeftLeg, ModelPart frontRightLeg, ModelPart backLeftLeg, ModelPart backRightLeg) {
        super(torso, frontLeftLeg, frontRightLeg, backLeftLeg, backRightLeg);
    }

    @Override
    public HumanoidAnimator.AnimateStage preferredStage() {
        return HumanoidAnimator.AnimateStage.STAND;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        frontRightLeg.y = 0.0F;
        frontLeftLeg.y = 0.0F;
        backRightLeg.y = 0.0F;
        backLeftLeg.y = 0.0F;
    }
}
