package net.ltxprogrammer.changed.client.renderer.animate.ears;

import net.ltxprogrammer.changed.client.renderer.animate.LatexAnimator;
import net.ltxprogrammer.changed.client.renderer.animate.tail.WolfTailInitAnimator;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class WolfEarsInitAnimator<T extends LatexEntity, M extends EntityModel<T>> extends AbstractEarsAnimator<T, M> {
    public WolfEarsInitAnimator(ModelPart leftEar, ModelPart rightEar) {
        super(leftEar, rightEar);
    }

    @Override
    public LatexAnimator.AnimateStage preferredStage() {
        return LatexAnimator.AnimateStage.INIT;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        rightEar.zRot = Mth.lerp(core.ageLerp * 0.85f, -0.08726646F, 0.04363323F);
        leftEar.zRot = -Mth.lerp(core.ageLerp * 0.85f, -0.08726646F, 0.04363323F);
    }
}
