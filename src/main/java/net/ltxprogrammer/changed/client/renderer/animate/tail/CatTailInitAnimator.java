package net.ltxprogrammer.changed.client.renderer.animate.tail;

import net.ltxprogrammer.changed.client.renderer.animate.LatexAnimator;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CatTailInitAnimator<T extends LatexEntity, M extends EntityModel<T>> extends AbstractTailAnimator<T, M> {
    public static final float SWAY_RATE = 0.33333334F * 0.25F;
    public static final float SWAY_SCALE = 0.10F;

    public CatTailInitAnimator(ModelPart tail, List<ModelPart> tailJoints) {
        super(tail, tailJoints);
    }

    @Override
    public LatexAnimator.AnimateStage preferredStage() {
        return LatexAnimator.AnimateStage.INIT;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float f = 1.0F;
        if (entity.getFallFlyingTicks() > 4) {
            f = (float)entity.getDeltaMovement().lengthSqr();
            f /= 0.2F;
            f *= f * f;
        }

        if (f < 1.0F) {
            f = 1.0F;
        }
        tail.xRot = 0.0F;
        tail.zRot = 0.0F;

        float tailSway = SWAY_SCALE * Mth.cos(ageInTicks * SWAY_RATE + (((float)Math.PI / 3.0F) * 0.75f));
        float tailBalance = Mth.cos(limbSwing * 0.6662F) * 0.125F * limbSwingAmount / f;
        float tailDrag = entity.getTailDragAmount(ageInTicks);
        tail.yRot = Mth.lerp(limbSwingAmount, tailSway, tailBalance) + tailDrag * 0.75F;

        float offset = 0.0F;
        for (ModelPart joint : tailJoints) {
            joint.yRot = Mth.lerp(limbSwingAmount, SWAY_SCALE * Mth.cos(ageInTicks * SWAY_RATE -
                    (((float)Math.PI / 3.0F) * offset)), 0.0f) + tailDrag * 0.75F;
            offset += 0.75F;
        }
    }
}
