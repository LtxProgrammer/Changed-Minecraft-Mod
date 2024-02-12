package net.ltxprogrammer.changed.client.renderer.animate.legless;

import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LeglessStandAnimator<T extends ChangedEntity, M extends EntityModel<T>> extends AbstractLeglessAnimator<T, M> {
    public LeglessStandAnimator(ModelPart abdomen, ModelPart lowerAbdomen, ModelPart tail, List<ModelPart> tailJoints) {
        super(abdomen, lowerAbdomen, tail, tailJoints);
    }

    @Override
    public HumanoidAnimator.AnimateStage preferredStage() {
        return HumanoidAnimator.AnimateStage.STAND;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        abdomen.y = core.calculateLegPositionY();
        abdomen.z = 0.1F + core.forwardOffset;
    }
}
