package net.ltxprogrammer.changed.client.renderer.animate;

import net.ltxprogrammer.changed.entity.LatexEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LeglessCrouchAnimator<T extends LatexEntity, M extends EntityModel<T>> extends AbstractLeglessAnimator<T, M> {
    public LeglessCrouchAnimator(ModelPart abdomen, ModelPart lowerAbdomen, ModelPart tail, List<ModelPart> tailJoints) {
        super(abdomen, lowerAbdomen, tail, tailJoints);
    }

    @Override
    public LatexAnimator.AnimateStage preferredStage() {
        return LatexAnimator.AnimateStage.CROUCH;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        abdomen.y = 12.2F + core.hipOffset;
        abdomen.z = 4.0F + core.forwardOffset;
        lowerAbdomen.xRot = (float) Math.toRadians(60);
    }
}
