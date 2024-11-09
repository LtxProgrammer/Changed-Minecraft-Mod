package net.ltxprogrammer.changed.client.renderer.animate.multihead;

import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.beast.DoubleHeadedEntity;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class DoubleHeadAnimator<T extends ChangedEntity & DoubleHeadedEntity, M extends AdvancedHumanoidModel<T>> extends HumanoidAnimator.Animator<T, M> {
    private final ModelPart head;
    private final ModelPart head2;

    public DoubleHeadAnimator(ModelPart head, ModelPart head2) {
        this.head = head;
        this.head2 = head2;
    }

    @Override
    public HumanoidAnimator.AnimateStage preferredStage() {
        return HumanoidAnimator.AnimateStage.FINAL;
    }

    private float gap = 4.0f;
    private float tilt = Mth.DEG_TO_RAD * 5.0f;
    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        boolean fallFlying = entity.getFallFlyingTicks() > 4;
        this.head2.copyFrom(this.head);

        float partialTicks = Mth.positiveModulo(ageInTicks, 1.0f);

        float head2Yaw = Mth.clamp((entity.getHead2YRot(partialTicks) - Mth.rotLerp(partialTicks, entity.yBodyRotO, entity.yBodyRot)) % 360.0f, -90.0f, 90.0f);
        float head2Pitch = entity.getHead2XRot(partialTicks);
        head2.yRot = head2Yaw * ((float)Math.PI / 180F);
        head2.zRot = 0.0F;
        if (fallFlying) {
            head2.xRot = (-(float)Math.PI / 4F);
        } else if (core.swimAmount > 0.0F) {
            if (entity.isVisuallySwimming()) {
                head2.xRot = HumanoidAnimator.rotlerpRad(core.swimAmount, head2.xRot, (-(float)Math.PI / 4F));
            } else {
                head2.xRot = HumanoidAnimator.rotlerpRad(core.swimAmount, head2.xRot, head2Pitch * ((float)Math.PI / 180F));
            }
        } else {
            head2.xRot = head2Pitch * ((float)Math.PI / 180F);
        }

        this.head.x = -gap;
        this.head2.x = gap;
        this.head.zRot -= tilt;
        this.head2.zRot += tilt;
    }
}
