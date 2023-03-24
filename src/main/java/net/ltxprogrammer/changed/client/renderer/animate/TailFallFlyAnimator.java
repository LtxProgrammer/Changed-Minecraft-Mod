package net.ltxprogrammer.changed.client.renderer.animate;

import net.ltxprogrammer.changed.entity.LatexEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TailFallFlyAnimator<T extends LatexEntity, M extends EntityModel<T>> extends AbstractTailAnimator<T, M> {
    public TailFallFlyAnimator(ModelPart tail, List<ModelPart> tailJoints) {
        super(tail, tailJoints);
    }

    @Override
    public LatexAnimator.AnimateStage preferredStage() {
        return LatexAnimator.AnimateStage.FALL_FLY;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float fallFlyingTicks = (float)entity.getFallFlyingTicks();
        float fallFlyAmount = Mth.clamp(fallFlyingTicks * fallFlyingTicks / 100.0F, 0.0F, 1.0F);
        tail.xRot = Mth.lerp(fallFlyAmount, tail.xRot, -1.0f);
    }
}
