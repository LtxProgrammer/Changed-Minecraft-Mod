package net.ltxprogrammer.changed.client.renderer.animate.legless;

import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Animator that handles a slithering entity upright on land
 * @param <T>
 */
public class LeglessRideAnimator<T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> extends AbstractLeglessAnimator<T, M> {
    public LeglessRideAnimator(ModelPart abdomen, ModelPart lowerAbdomen, ModelPart tail, List<ModelPart> tailJoints) {
        super(abdomen, lowerAbdomen, tail, tailJoints);
    }

    @Override
    public HumanoidAnimator.AnimateStage preferredStage() {
        return HumanoidAnimator.AnimateStage.RIDE;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        abdomen.xRot = Mth.DEG_TO_RAD * -17.5f;
        abdomen.yRot = Mth.DEG_TO_RAD * -7.5f;
        abdomen.zRot = 0f;
        lowerAbdomen.xRot = Mth.DEG_TO_RAD * -67.5f;
        lowerAbdomen.yRot = Mth.DEG_TO_RAD * -12.5f;
        lowerAbdomen.zRot = Mth.DEG_TO_RAD * -12.5f;
        tail.xRot = Mth.DEG_TO_RAD * 15.0f;
        tail.yRot = 0f;
        tail.zRot = Mth.DEG_TO_RAD * -15.0f;

        int index = 0;
        for (ModelPart joint : tailJoints) {
            joint.xRot = index < 3 ? Mth.DEG_TO_RAD * 15.0f : Mth.DEG_TO_RAD * 12.5f;
            joint.yRot = 0.0F;
            joint.zRot = index == 0 ? Mth.DEG_TO_RAD * -15.0f : 0f;

            index++;
        }
    }
}
