package net.ltxprogrammer.changed.client.renderer.animate;

import net.ltxprogrammer.changed.entity.LatexEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AquaticTailSwimAnimator<T extends LatexEntity, M extends EntityModel<T>> extends AbstractTailAnimator<T, M> {
    public AquaticTailSwimAnimator(ModelPart tail, List<ModelPart> tailJoints) {
        super(tail, tailJoints);
    }

    @Override
    public LatexAnimator.AnimateStage preferredStage() {
        return LatexAnimator.AnimateStage.SWIM;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        tail.xRot = Mth.lerp(core.swimAmount, tail.xRot, -1.1f);
        tail.yRot = Mth.lerp(core.swimAmount, tail.zRot, 0.0F);
        tail.zRot = Mth.lerp(core.swimAmount, 0.0F, tail.yRot);
        tail.zRot = Mth.lerp(core.swimAmount, tail.zRot, 0.25F * Mth.cos(limbSwing * 0.33333334F + (float)Math.PI));
    }
}
