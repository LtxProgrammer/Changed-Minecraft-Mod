package net.ltxprogrammer.changed.client.renderer.animate.misc;

import net.ltxprogrammer.changed.client.renderer.animate.LatexAnimator;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SquidDogTentaclesInitAnimator<T extends LatexEntity, M extends EntityModel<T>> extends LatexAnimator.Animator<T, M> {
    private final List<ModelPart> upperLeftTentacle;
    private final List<ModelPart> upperRightTentacle;
    private final List<ModelPart> lowerLeftTentacle;
    private final List<ModelPart> lowerRightTentacle;

    public static final float SWAY_RATE = 0.33333334F * 0.25F;
    public static final float SWAY_SCALE = 0.025F;
    public static final float DRAG_SCALE = 0.75F;

    public SquidDogTentaclesInitAnimator(List<ModelPart> upperLeftTentacle, List<ModelPart> upperRightTentacle, List<ModelPart> lowerLeftTentacle, List<ModelPart> lowerRightTentacle) {
        this.upperLeftTentacle = upperLeftTentacle;
        this.upperRightTentacle = upperRightTentacle;
        this.lowerLeftTentacle = lowerLeftTentacle;
        this.lowerRightTentacle = lowerRightTentacle;
    }

    @Override
    public LatexAnimator.AnimateStage preferredStage() {
        return LatexAnimator.AnimateStage.INIT;
    }

    private void animateTentacle(List<ModelPart> tentacle, float limbSwingAmount, float ageInTicks, float tentacleBalance, float balance, float tentacleDrag) {
        float offset = 0.0F;
        for (ModelPart joint : tentacle) {
            joint.yRot = Mth.lerp(limbSwingAmount, SWAY_SCALE * Mth.cos(ageInTicks * SWAY_RATE -
                    (((float)Math.PI / 3.0F) * offset)), 0.0f) + tentacleDrag * DRAG_SCALE;
            offset += 0.75F;
        }
    }

    private void bendUpTentacle(List<ModelPart> tentacle, float scale) {
        for (ModelPart joint : tentacle) {
            joint.zRot = scale;
        }
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

        float tentacleSway = SWAY_SCALE * Mth.cos(ageInTicks * SWAY_RATE + (((float)Math.PI / 3.0F) * 0.75f));
        float tentacleBalance = Mth.cos(limbSwing * 0.6662F) * 0.125F * limbSwingAmount / f;
        float tentacleDrag = entity.getTailDragAmount(ageInTicks);

        animateTentacle(upperLeftTentacle, limbSwingAmount, ageInTicks, tentacleSway, tentacleBalance, tentacleDrag);
        animateTentacle(upperRightTentacle, limbSwingAmount, ageInTicks, tentacleSway, tentacleBalance, tentacleDrag);
        animateTentacle(lowerLeftTentacle, limbSwingAmount, ageInTicks, tentacleSway, tentacleBalance, tentacleDrag);
        animateTentacle(lowerRightTentacle, limbSwingAmount, ageInTicks, tentacleSway, tentacleBalance, tentacleDrag);

        bendUpTentacle(upperLeftTentacle, -12.5f * Mth.DEG_TO_RAD);
        bendUpTentacle(upperRightTentacle, 12.5f * Mth.DEG_TO_RAD);
    }
}
