package net.ltxprogrammer.changed.client.renderer.animate.multihead;

import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.beast.DoubleHeadedEntity;
import net.ltxprogrammer.changed.entity.beast.TripleHeadedEntity;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class TripleHeadAnimator<T extends ChangedEntity & TripleHeadedEntity, M extends AdvancedHumanoidModel<T>> extends HumanoidAnimator.Animator<T, M> {
    private final ModelPart leftHead;
    private final ModelPart middleHead;
    private final ModelPart rightHead;

    public TripleHeadAnimator(ModelPart leftHead, ModelPart middleHead, ModelPart rightHead) {
        this.leftHead = leftHead;
        this.middleHead = middleHead;
        this.rightHead = rightHead;
    }

    @Override
    public HumanoidAnimator.AnimateStage preferredStage() {
        return HumanoidAnimator.AnimateStage.FINAL;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        boolean fallFlying = entity.getFallFlyingTicks() > 4;
        this.leftHead.copyFrom(this.middleHead);
        this.rightHead.copyFrom(this.middleHead);
        this.middleHead.z -= 1.0f;
        this.leftHead.z += 1.0f;
        this.rightHead.z += 1.0f;

        float partialTicks = Mth.positiveModulo(ageInTicks, 1.0f);
        float yBodyRot = Mth.rotLerp(partialTicks, entity.yBodyRotO, entity.yBodyRot);

        {
            float leftHeadYaw = Mth.clamp((entity.getLeftHeadYRot(partialTicks) - yBodyRot) % 360.0f, -90.0f, 10.0f);
            float leftHeadPitch = entity.getLeftHeadXRot(partialTicks);
            leftHead.yRot = leftHeadYaw * ((float) Math.PI / 180F);
            leftHead.zRot = 0.0F;
            if (fallFlying) {
                leftHead.xRot = (-(float) Math.PI / 4F);
            } else if (core.swimAmount > 0.0F) {
                if (entity.isVisuallySwimming()) {
                    leftHead.xRot = HumanoidAnimator.rotlerpRad(core.swimAmount, leftHead.xRot, (-(float) Math.PI / 4F));
                } else {
                    leftHead.xRot = HumanoidAnimator.rotlerpRad(core.swimAmount, leftHead.xRot, leftHeadPitch * ((float) Math.PI / 180F));
                }
            } else {
                leftHead.xRot = leftHeadPitch * ((float) Math.PI / 180F);
            }
        }

        {
            float rightHeadYaw = Mth.clamp((entity.getRightHeadYRot(partialTicks) - yBodyRot) % 360.0f, -10.0f, 90.0f);
            float rightHeadPitch = entity.getRightHeadXRot(partialTicks);
            rightHead.yRot = rightHeadYaw * ((float) Math.PI / 180F);
            rightHead.zRot = 0.0F;
            if (fallFlying) {
                rightHead.xRot = (-(float) Math.PI / 4F);
            } else if (core.swimAmount > 0.0F) {
                if (entity.isVisuallySwimming()) {
                    rightHead.xRot = HumanoidAnimator.rotlerpRad(core.swimAmount, rightHead.xRot, (-(float) Math.PI / 4F));
                } else {
                    rightHead.xRot = HumanoidAnimator.rotlerpRad(core.swimAmount, rightHead.xRot, rightHeadPitch * ((float) Math.PI / 180F));
                }
            } else {
                rightHead.xRot = rightHeadPitch * ((float) Math.PI / 180F);
            }
        }
    }
}
