package net.ltxprogrammer.changed.client.renderer.animate.camera;

import net.ltxprogrammer.changed.client.CameraExtender;
import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import static net.ltxprogrammer.changed.client.renderer.animate.upperbody.SharkUpperBodySwimAnimator.*;

public class SharkCameraSwimAnimator<T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> extends HumanoidAnimator.CameraAnimator<T, M> {
    @Override
    public HumanoidAnimator.AnimateStage preferredStage() {
        return HumanoidAnimator.AnimateStage.SWIM;
    }

    @Override
    public boolean requiresViewBob() {
        return true;
    }

    @Override
    public void setupAnim(@NotNull CameraExtender camera, @NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float midSegmentSway = -TORSO_SWAY_SCALE * Mth.cos(limbSwing * SWIM_RATE -
                (((float)Math.PI / 3.0F) * 0.75f));
        float headOffset = Mth.map(midSegmentSway, -0.35f, 0.35f, 2.0f, -2.0f);
        float computedOffset = Mth.lerp(core.swimAmount, 0.0f, headOffset * SWIM_SCALE) * 0.0625f;
        Vec3 leftOffset = camera.getLeftDirection().multiply(computedOffset, computedOffset, computedOffset);

        camera.setCameraPosition(camera.getCameraPosition().add(leftOffset));
    }
}
