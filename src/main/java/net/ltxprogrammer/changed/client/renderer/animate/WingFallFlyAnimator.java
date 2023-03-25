package net.ltxprogrammer.changed.client.renderer.animate;

import net.ltxprogrammer.changed.entity.LatexEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

/**
 * Animator that handles a slithering entity upright on land
 * @param <T>
 */
public class WingFallFlyAnimator<T extends LatexEntity, M extends EntityModel<T>> extends AbstractWingAnimator<T, M> {
    public WingFallFlyAnimator(ModelPart leftWing, ModelPart rightWing) {
        super(leftWing, rightWing);
    }

    @Override
    public LatexAnimator.AnimateStage preferredStage() {
        return LatexAnimator.AnimateStage.FALL_FLY;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float fallFlyingTicks = (float)entity.getFallFlyingTicks();
        float fallFlyingAmount = Mth.clamp(fallFlyingTicks * fallFlyingTicks / 100.0F, 0.0F, 1.0F);
        rightWing.zRot = Mth.lerp(fallFlyingAmount, rightWing.zRot, 0.8f);
        leftWing.zRot = Mth.lerp(fallFlyingAmount, leftWing.zRot, -0.8f);
    }
}
