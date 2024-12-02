package net.ltxprogrammer.changed.client.renderer.animate.camera;

import net.ltxprogrammer.changed.client.CameraExtender;
import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class TaurCameraJumpAnimator<T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> extends HumanoidAnimator.CameraAnimator<T, M> {
    @Override
    public HumanoidAnimator.AnimateStage preferredStage() {
        return HumanoidAnimator.AnimateStage.BOB;
    }

    @Override
    public boolean requiresViewBob() {
        return true;
    }

    @Override
    public void setupAnim(@NotNull CameraExtender camera, @NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        final float deltaY = Mth.clamp((float) entity.getDeltaMovement(this.core.partialTicks).y, -0.8f, 0.8f);

        camera.setCameraPosition(camera.getCameraPosition().add(new Vec3(0.0, deltaY * 5.0 / 16.0, 0.0)));
    }
}
