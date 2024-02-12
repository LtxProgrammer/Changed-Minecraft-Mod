package net.ltxprogrammer.changed.client.renderer.animate.legless;

import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LeglessCrouchAnimator<T extends ChangedEntity, M extends EntityModel<T>> extends AbstractLeglessAnimator<T, M> {
    public LeglessCrouchAnimator(ModelPart abdomen, ModelPart lowerAbdomen, ModelPart tail, List<ModelPart> tailJoints) {
        super(abdomen, lowerAbdomen, tail, tailJoints);
    }

    @Override
    public HumanoidAnimator.AnimateStage preferredStage() {
        return HumanoidAnimator.AnimateStage.CROUCH;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        abdomen.y = 12.2F + core.hipOffset + (12.0f - core.legLength);
        abdomen.z = 4.0F + core.forwardOffset;
        lowerAbdomen.xRot = (float) Math.toRadians(60);
    }
}
