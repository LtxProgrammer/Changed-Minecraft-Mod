package net.ltxprogrammer.changed.client.renderer.animate.camera;

import net.ltxprogrammer.changed.client.CameraExtender;
import net.ltxprogrammer.changed.client.renderer.animate.LatexAnimator;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import static net.ltxprogrammer.changed.client.renderer.animate.upperbody.SharkUpperBodySwimAnimator.*;
import static net.ltxprogrammer.changed.client.renderer.animate.wing.DragonWingCreativeFlyAnimator.BODY_FLY_SCALE;
import static net.ltxprogrammer.changed.client.renderer.animate.wing.DragonWingCreativeFlyAnimator.WING_FLAP_RATE;

public class SharkCameraSwimAnimator<T extends LatexEntity, M extends EntityModel<T>> extends LatexAnimator.CameraAnimator<T, M> {
    @Override
    public LatexAnimator.AnimateStage preferredStage() {
        return LatexAnimator.AnimateStage.SWIM;
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
