package net.ltxprogrammer.changed.client.renderer.animate;

import net.ltxprogrammer.changed.entity.LatexEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LeglessFallFlyAnimator<T extends LatexEntity, M extends EntityModel<T>> extends AbstractLeglessAnimator<T, M> {
    public LeglessFallFlyAnimator(ModelPart abdomen, ModelPart lowerAbdomen, ModelPart tail, List<ModelPart> tailJoints) {
        super(abdomen, lowerAbdomen, tail, tailJoints);
    }

    @Override
    public LatexAnimator.AnimateStage preferredStage() {
        return LatexAnimator.AnimateStage.FALL_FLY;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float fallFlyingTicks = (float)entity.getFallFlyingTicks();
        float fallFlyAmount = Mth.clamp(fallFlyingTicks * fallFlyingTicks / 100.0F, 0.0F, 1.0F);
        abdomen.xRot = Mth.lerp(fallFlyAmount, abdomen.xRot, 0.0f);
        lowerAbdomen.xRot = Mth.lerp(fallFlyAmount, lowerAbdomen.xRot, 0.0f);
        tail.xRot = Mth.lerp(fallFlyAmount, tail.xRot, 0.0f);
    }
}
