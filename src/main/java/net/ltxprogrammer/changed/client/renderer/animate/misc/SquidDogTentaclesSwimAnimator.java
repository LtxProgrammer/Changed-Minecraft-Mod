package net.ltxprogrammer.changed.client.renderer.animate.misc;

import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SquidDogTentaclesSwimAnimator<T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> extends AbstractTentaclesAnimator<T, M> {
    public static final float SWAY_RATE = 0.33333334F * 0.25F;
    public static final float SWAY_SCALE = 0.025F;
    public static final float DRAG_SCALE = 0.75F;

    public SquidDogTentaclesSwimAnimator(List<ModelPart> upperLeftTentacle, List<ModelPart> upperRightTentacle, List<ModelPart> lowerLeftTentacle, List<ModelPart> lowerRightTentacle) {
        super(upperLeftTentacle, upperRightTentacle, lowerLeftTentacle, lowerRightTentacle);
    }

    @Override
    public HumanoidAnimator.AnimateStage preferredStage() {
        return HumanoidAnimator.AnimateStage.SWIM;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        swimTentacle(upperLeftTentacle, ageInTicks, -20.0f * Mth.DEG_TO_RAD, 60.0f * Mth.DEG_TO_RAD);
        swimTentacle(upperRightTentacle, ageInTicks, -20.0f * Mth.DEG_TO_RAD, -60.0f * Mth.DEG_TO_RAD);
        swimTentacle(lowerLeftTentacle, ageInTicks, -22.5f * Mth.DEG_TO_RAD, 60.0f * Mth.DEG_TO_RAD);
        swimTentacle(lowerRightTentacle, ageInTicks, -22.5f * Mth.DEG_TO_RAD, -60.0f * Mth.DEG_TO_RAD);
    }
}
