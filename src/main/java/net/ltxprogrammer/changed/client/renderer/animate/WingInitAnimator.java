package net.ltxprogrammer.changed.client.renderer.animate;

import net.ltxprogrammer.changed.entity.LatexEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import org.jetbrains.annotations.NotNull;

/**
 * Animator that handles a slithering entity upright on land
 * @param <T>
 */
public class WingInitAnimator<T extends LatexEntity, M extends EntityModel<T>> extends AbstractWingAnimator<T, M> {
    public WingInitAnimator(ModelPart leftWing, ModelPart rightWing) {
        super(leftWing, rightWing);
    }

    @Override
    public LatexAnimator.AnimateStage preferredStage() {
        return LatexAnimator.AnimateStage.INIT;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        rightWing.zRot = 0.0F;
        leftWing.zRot = 0.0F;
    }
}
