package net.ltxprogrammer.changed.client.renderer.animate.tail;

import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.SpringType;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DragonTailInitAnimator<T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> extends AbstractTailAnimator<T, M> {
    public static final float SWAY_RATE = 0.33333334F * 0.25F;
    public static final float SWAY_SCALE = 0.10F;

    public DragonTailInitAnimator(ModelPart tail, List<ModelPart> tailJoints) {
        super(tail, tailJoints);
    }

    @Override
    public HumanoidAnimator.AnimateStage preferredStage() {
        return HumanoidAnimator.AnimateStage.INIT;
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

        float tailSway = 0.0f;//SWAY_SCALE * Mth.cos(ageInTicks * SWAY_RATE + (((float)Math.PI / 3.0F) * 0.75f));
        float tailBalance = Mth.cos(limbSwing * 0.6662F) * 0.125F * limbSwingAmount / f;
        float tailDrag = entity.getTailDragAmount(this.core.partialTicks) * 0.5f;
        float verticalDrag = entity.getSimulatedSpring(SpringType.HEAVY_WEAK, SpringType.Direction.VERTICAL, this.core.partialTicks) * 0.4f;
        tail.yRot = Mth.lerp(limbSwingAmount, tailSway, tailBalance) + tailDrag * 0.75F;

        float offset = 0.0F;
        for (ModelPart joint : tailJoints) {
            joint.yRot = Mth.lerp(limbSwingAmount, 0.0f/*SWAY_SCALE * Mth.cos(ageInTicks * SWAY_RATE -
                    (((float)Math.PI / 3.0F) * offset))*/, 0.0f) + tailDrag * 0.75F;
            joint.xRot = -verticalDrag;
            offset += 0.75F;
        }
    }
}
