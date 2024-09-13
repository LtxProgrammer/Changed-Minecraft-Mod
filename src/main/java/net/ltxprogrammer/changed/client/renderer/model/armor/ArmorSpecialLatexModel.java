package net.ltxprogrammer.changed.client.renderer.model.armor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.client.renderer.animate.AnimatorPresets;
import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.client.renderer.animate.arm.ArmBobAnimator;
import net.ltxprogrammer.changed.client.renderer.animate.arm.ArmRideAnimator;
import net.ltxprogrammer.changed.client.renderer.animate.arm.ArmSwimAnimator;
import net.ltxprogrammer.changed.client.renderer.animate.upperbody.HeadInitAnimator;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.item.Shorts;
import net.ltxprogrammer.changed.util.PatreonBenefits;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ArmorSpecialLatexModel<T extends ChangedEntity> extends LatexHumanoidArmorModel<T, ArmorSpecialLatexModel<T>> {
    private final ModelPart RightLeg;
    private final ModelPart LeftLeg;
    private final ModelPart RightArm;
    private final ModelPart LeftArm;
    private final ModelPart Head;
    private final ModelPart Torso;
    private final ModelPart Tail;
    private final HumanoidAnimator<T, ArmorSpecialLatexModel<T>> animator;

    public ArmorSpecialLatexModel(ModelPart root, ArmorModel model, PatreonBenefits.ModelData form) {
        super(root, model);
        this.RightLeg = root.getChild("RightLeg");
        this.LeftLeg = root.getChild("LeftLeg");
        this.Head = root.getChild("Head");
        this.Torso = root.getChild("Torso");
        this.Tail = form.animationData().hasTail() ? Torso.getChild("Tail") : null;
        this.RightArm = root.getChild("RightArm");
        this.LeftArm = root.getChild("LeftArm");
        animator = HumanoidAnimator.of(this); // TODO better configuration for patreon forms
        animator.addPreset(AnimatorPresets.upperBody(Head, Torso, LeftArm, RightArm));
        animator.addPreset(AnimatorPresets.bipedal(LeftLeg, RightLeg))
                .addAnimator(new HeadInitAnimator<>(Head))
                .addAnimator(new ArmBobAnimator<>(LeftArm, RightArm))
                .addAnimator(new ArmRideAnimator<>(LeftArm, RightArm));
        if (form.animationData().swimTail())
            animator.addAnimator(new ArmSwimAnimator<>(LeftArm, RightArm));
        if (Tail != null)
            animator.addPreset(form.animationData().swimTail() ? AnimatorPresets.aquaticTail(Tail, List.of()) : AnimatorPresets.standardTail(Tail, List.of()));
        animator.hipOffset = form.hipOffset();
        animator.torsoWidth = form.torsoWidth();
        animator.forwardOffset = form.forwardOffset();
        animator.torsoLength = form.torsoLength();
        animator.armLength = form.armLength();
        animator.legLength = form.legLength();
    }

    @Override
    public void renderForSlot(T entity, RenderLayerParent<T, ?> parent, ItemStack stack, EquipmentSlot slot, PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        poseStack.pushPose();
        this.scaleForSlot(parent, slot, poseStack);

        switch (slot) {
            case HEAD -> Head.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
            case CHEST -> {
                Torso.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
                LeftArm.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
                RightArm.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
            }
            case LEGS -> {
                if (stack.getItem() instanceof Shorts) {
                    setAllPartsVisibility(LeftLeg, false);
                    setAllPartsVisibility(RightLeg, false);
                    try {
                        LeftLeg.getChild("LeftUpperLeg_r1").visible = true;
                        RightLeg.getChild("RightUpperLeg_r1").visible = true;
                    } catch (Exception ignored) {}
                }

                Torso.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
                LeftLeg.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
                RightLeg.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);

                if (stack.getItem() instanceof Shorts) {
                    setAllPartsVisibility(LeftLeg, true);
                    setAllPartsVisibility(RightLeg, true);
                }
            }
            case FEET -> {
                LeftLeg.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
                RightLeg.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
            }
        }

        poseStack.popPose();
    }

    @Override
    public HumanoidAnimator<T, ArmorSpecialLatexModel<T>> getAnimator() {
        return animator;
    }

    public ModelPart getArm(HumanoidArm arm) {
        return arm == HumanoidArm.LEFT ? this.LeftArm : this.RightArm;
    }

    public ModelPart getLeg(HumanoidArm leg) {
        return leg == HumanoidArm.LEFT ? this.LeftLeg : this.RightLeg;
    }

    public ModelPart getHead() {
        return this.Head;
    }

    public ModelPart getTorso() {
        return Torso;
    }
}
