package net.ltxprogrammer.changed.client.renderer.animate.upperbody;

import net.ltxprogrammer.changed.client.renderer.animate.LatexAnimator;
import net.ltxprogrammer.changed.client.renderer.animate.tail.WolfTailInitAnimator;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class WolfHeadInitAnimator<T extends LatexEntity, M extends EntityModel<T>> extends AbstractHeadAnimator<T, M> {
    public WolfHeadInitAnimator(ModelPart head) {
        super(head);
    }

    @Override
    public LatexAnimator.AnimateStage preferredStage() {
        return LatexAnimator.AnimateStage.INIT;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        final float ageAdjusted = ageInTicks * WolfTailInitAnimator.SWAY_RATE * 0.25f;
        float ageSin = Mth.sin(ageAdjusted * Mth.PI * 0.5f);
        float ageCos = Mth.cos(ageAdjusted * Mth.PI * 0.5f);
        float ageLerp = Mth.lerp(1.0f - Mth.abs(Mth.positiveModulo(ageAdjusted, 2.0f) - 1.0f),
                ageSin * ageSin * ageSin * ageSin, 1.0f - (ageCos * ageCos * ageCos * ageCos));

        boolean fallFlying = entity.getFallFlyingTicks() > 4;
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
            head.zRot = Mth.lerp(limbSwingAmount, -Mth.lerp(ageLerp, 0.03490659F, 0.08726646F), 0.0F);
        }
    }
}
