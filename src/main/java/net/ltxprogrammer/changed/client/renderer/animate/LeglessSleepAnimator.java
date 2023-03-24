package net.ltxprogrammer.changed.client.renderer.animate;

import net.ltxprogrammer.changed.entity.LatexEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LeglessSleepAnimator<T extends LatexEntity, M extends EntityModel<T>> extends AbstractLeglessAnimator<T, M> {
    public LeglessSleepAnimator(ModelPart abdomen, ModelPart lowerAbdomen, ModelPart tail, List<ModelPart> tailJoints) {
        super(abdomen, lowerAbdomen, tail, tailJoints);
    }

    @Override
    public LatexAnimator.AnimateStage preferredStage() {
        return LatexAnimator.AnimateStage.SLEEP;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        abdomen.xRot = 0.0F;
        lowerAbdomen.xRot = 0.0F;
        tail.xRot = 0.0F;
        for (ModelPart joint : tailJoints) {
            joint.zRot = 0.0F;
        }
    }
}
