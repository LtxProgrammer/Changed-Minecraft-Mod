package net.ltxprogrammer.changed.client.renderer.animate.misc;

import net.ltxprogrammer.changed.client.renderer.animate.LatexAnimator;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SquidDogTentaclesInitAnimator<T extends LatexEntity, M extends EntityModel<T>> extends AbstractTentaclesAnimator<T, M> {
    public static final float SWAY_RATE = 0.33333334F * 0.25F;
    public static final float SWAY_SCALE = 0.025F;
    public static final float DRAG_SCALE = 0.75F;

    public SquidDogTentaclesInitAnimator(List<ModelPart> upperLeftTentacle, List<ModelPart> upperRightTentacle, List<ModelPart> lowerLeftTentacle, List<ModelPart> lowerRightTentacle) {
        super(upperLeftTentacle, upperRightTentacle, lowerLeftTentacle, lowerRightTentacle);
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

        float tentacleSway = SWAY_SCALE * Mth.cos(ageInTicks * SWAY_RATE + (((float)Math.PI / 3.0F) * 0.75f));
        float tentacleBalance = Mth.cos(limbSwing * 0.6662F) * 0.125F * limbSwingAmount / f;
        float tentacleDrag = entity.getTailDragAmount(ageInTicks);
        float verticalDrag = entity.getVerticalDragAmount(ageInTicks) * 0.30f;

        resetTentacle(upperLeftTentacle);
        resetTentacle(upperRightTentacle);
        resetTentacle(lowerLeftTentacle);
        resetTentacle(lowerRightTentacle);

        animateTentacle(upperLeftTentacle, limbSwingAmount, ageInTicks, tentacleSway, tentacleBalance, tentacleDrag);
        animateTentacle(upperRightTentacle, limbSwingAmount, ageInTicks, tentacleSway, tentacleBalance, tentacleDrag);
        animateTentacle(lowerLeftTentacle, limbSwingAmount, ageInTicks, tentacleSway, tentacleBalance, tentacleDrag);
        animateTentacle(lowerRightTentacle, limbSwingAmount, ageInTicks, tentacleSway, tentacleBalance, tentacleDrag);

        bendTentacle(upperLeftTentacle, -12.5f * Mth.DEG_TO_RAD);
        bendTentacle(upperRightTentacle, 12.5f * Mth.DEG_TO_RAD);

        bendTentacle(upperLeftTentacle, verticalDrag);
        bendTentacle(upperRightTentacle, -verticalDrag);
        bendTentacle(lowerLeftTentacle, verticalDrag);
        bendTentacle(lowerRightTentacle, -verticalDrag);
    }
}
