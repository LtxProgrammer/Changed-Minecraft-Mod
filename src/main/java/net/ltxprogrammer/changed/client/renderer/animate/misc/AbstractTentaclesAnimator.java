package net.ltxprogrammer.changed.client.renderer.animate.misc;

import net.ltxprogrammer.changed.client.renderer.animate.LatexAnimator;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

import java.util.List;

public abstract class AbstractTentaclesAnimator<T extends LatexEntity, M extends EntityModel<T>> extends LatexAnimator.Animator<T, M> {
    protected final List<ModelPart> upperLeftTentacle;
    protected final List<ModelPart> upperRightTentacle;
    protected final List<ModelPart> lowerLeftTentacle;
    protected final List<ModelPart> lowerRightTentacle;

    public static final float SWAY_RATE = 0.33333334F * 0.25F;
    public static final float SWAY_SCALE = 0.025F;
    public static final float DRAG_SCALE = 0.75F;

    public AbstractTentaclesAnimator(List<ModelPart> upperLeftTentacle, List<ModelPart> upperRightTentacle, List<ModelPart> lowerLeftTentacle, List<ModelPart> lowerRightTentacle) {
        this.upperLeftTentacle = upperLeftTentacle;
        this.upperRightTentacle = upperRightTentacle;
        this.lowerLeftTentacle = lowerLeftTentacle;
        this.lowerRightTentacle = lowerRightTentacle;
    }

    protected void resetTentacle(List<ModelPart> tentacle) {
        for (ModelPart joint : tentacle) {
            joint.xRot = 0.0f;
            joint.yRot = 0.0f;
            joint.zRot = 0.0f;
        }
    }

    protected void idleTentacle(List<ModelPart> tentacle, float limbSwingAmount, float ageInTicks, float tentacleBalance, float balance, float tentacleDrag) {
        float offset = 0.0F;
        for (ModelPart joint : tentacle) {
            joint.yRot = Mth.lerp(limbSwingAmount, SWAY_SCALE * Mth.cos(ageInTicks * SWAY_RATE -
                    (((float)Math.PI / 3.0F) * offset)), 0.0f) + tentacleDrag * DRAG_SCALE;
            offset += 0.75F;
        }
    }

    protected void swimTentacle(List<ModelPart> tentacle, float ageInTicks, float yAngle, float zAngle) {
        var first = tentacle.get(0);
        first.yRot = Mth.lerp(core.swimAmount, first.yRot, yAngle);
        first.zRot = Mth.lerp(core.swimAmount, first.zRot, zAngle);

        float offset = 0.0F;
        for (ModelPart joint : tentacle) {
            joint.yRot = Mth.lerp(core.swimAmount, joint.yRot, SWAY_SCALE * Mth.cos(ageInTicks * SWAY_RATE -
                    (((float)Math.PI / 3.0F) * offset)));
            offset += 0.75F;
        }
    }

    protected void bendVerticalTentacle(List<ModelPart> tentacle, float scale) {
        for (ModelPart joint : tentacle) {
            joint.zRot += scale;
        }
    }

    protected void bendInTentacle(List<ModelPart> tentacle, float scale) {
        for (ModelPart joint : tentacle) {
            joint.yRot += scale;
        }
    }
}
