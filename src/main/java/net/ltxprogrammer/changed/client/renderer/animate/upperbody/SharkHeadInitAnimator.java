package net.ltxprogrammer.changed.client.renderer.animate.upperbody;

import net.ltxprogrammer.changed.client.renderer.animate.LatexAnimator;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class SharkHeadInitAnimator<T extends LatexEntity, M extends EntityModel<T>> extends AbstractHeadAnimator<T, M> {
    public SharkHeadInitAnimator(ModelPart head) {
        super(head);
    }

    @Override
    public LatexAnimator.AnimateStage preferredStage() {
        return LatexAnimator.AnimateStage.INIT;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        boolean fallFlying = entity.getFallFlyingTicks() > 4;
        head.x = 0.0F;
        head.yRot = netHeadYaw * ((float)Math.PI / 180F);
        head.zRot = 0.0F;
        if (fallFlying) {
            head.xRot = (-(float)Math.PI / 4F);
        } else if (core.swimAmount > 0.0F) {
            if (entity.isVisuallySwimming()) {
                head.xRot = LatexAnimator.rotlerpRad(core.swimAmount, head.xRot, (-(float)Math.PI / 4F));
            } else {
                head.xRot = LatexAnimator.rotlerpRad(core.swimAmount, head.xRot, headPitch * ((float)Math.PI / 180F));
            }
        } else {
            head.xRot = headPitch * ((float)Math.PI / 180F);
        }
    }
}
