package net.ltxprogrammer.changed.client.renderer.animate.upperbody;

import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class WolfHeadInitAnimator<T extends ChangedEntity, M extends EntityModel<T>> extends AbstractHeadAnimator<T, M> {
    public WolfHeadInitAnimator(ModelPart head) {
        super(head);
    }

    @Override
    public HumanoidAnimator.AnimateStage preferredStage() {
        return HumanoidAnimator.AnimateStage.INIT;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        boolean fallFlying = entity.getFallFlyingTicks() > 4;
        head.yRot = netHeadYaw * ((float)Math.PI / 180F);
        head.zRot = 0.0F;
        if (fallFlying) {
            head.xRot = (-(float)Math.PI / 4F);
        } else if (core.swimAmount > 0.0F) {
            if (entity.isVisuallySwimming()) {
                head.xRot = HumanoidAnimator.rotlerpRad(core.swimAmount, head.xRot, (-(float)Math.PI / 4F));
            } else {
                head.xRot = HumanoidAnimator.rotlerpRad(core.swimAmount, head.xRot, headPitch * ((float)Math.PI / 180F));
            }
        } else {
            head.xRot = headPitch * ((float)Math.PI / 180F);
            if (entity.getUnderlyingPlayer() == null)
                head.zRot = Mth.lerp(limbSwingAmount, -Mth.lerp(core.ageLerp, 0.03490659F * 0.65F, 0.08726646F * 0.65F), 0.0F);
        }
    }
}
