package net.ltxprogrammer.changed.client.renderer.animate.misc;

import net.ltxprogrammer.changed.client.renderer.animate.LatexAnimator;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SquidDogTentaclesBobAnimator<T extends LatexEntity, M extends EntityModel<T>> extends AbstractTentaclesAnimator<T, M> {
    public SquidDogTentaclesBobAnimator(List<ModelPart> upperLeftTentacle, List<ModelPart> upperRightTentacle, List<ModelPart> lowerLeftTentacle, List<ModelPart> lowerRightTentacle) {
        super(upperLeftTentacle, upperRightTentacle, lowerLeftTentacle, lowerRightTentacle);
    }

    @Override
    public LatexAnimator.AnimateStage preferredStage() {
        return LatexAnimator.AnimateStage.BOB;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float scale = Mth.lerp(limbSwingAmount, 0.9f, 0.125f);

        bobTentacle(upperLeftTentacle, ageInTicks * 0.8f, scale);
        bobTentacle(upperRightTentacle, ageInTicks * 1.1f, -scale);
        bobTentacle(lowerLeftTentacle, ageInTicks * 1.2f, -scale);
        bobTentacle(lowerRightTentacle, ageInTicks * 0.9f, scale);
    }
}
