package net.ltxprogrammer.changed.client.renderer.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.client.renderer.animate.AnimatorPresets;
import net.ltxprogrammer.changed.client.renderer.animate.LatexAnimator;
import net.ltxprogrammer.changed.client.renderer.animate.arm.ArmBobAnimator;
import net.ltxprogrammer.changed.client.renderer.animate.arm.ArmRideAnimator;
import net.ltxprogrammer.changed.client.renderer.animate.arm.ArmSwimAnimator;
import net.ltxprogrammer.changed.client.renderer.animate.upperbody.HeadInitAnimator;
import net.ltxprogrammer.changed.entity.beast.SpecialLatex;
import net.ltxprogrammer.changed.util.PatreonBenefits;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.HumanoidArm;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SpecialLatexModel extends LatexHumanoidModel<SpecialLatex> implements LatexHumanoidModelInterface<SpecialLatex, SpecialLatexModel> {
    private final ModelPart RightLeg;
    private final ModelPart LeftLeg;
    private final ModelPart RightArm;
    private final ModelPart LeftArm;
    private final ModelPart Head;
    private final ModelPart Torso;
    private final ModelPart Tail;
    private final ModelPart RightWing;
    private final ModelPart LeftWing;
    private final LatexAnimator<SpecialLatex, SpecialLatexModel> animator;

    public SpecialLatexModel(ModelPart root, PatreonBenefits.ModelData form) {
        super(root);
        this.RightLeg = root.getChild("RightLeg");
        this.LeftLeg = root.getChild("LeftLeg");
        this.Head = root.getChild("Head");
        this.Torso = root.getChild("Torso");
        this.Tail = form.animationData().hasTail() ? Torso.getChild("Tail") : null;
        this.RightArm = root.getChild("RightArm");
        this.LeftArm = root.getChild("LeftArm");
        animator = LatexAnimator.of(this); // TODO better configuration for patreon forms
        animator.addPreset(AnimatorPresets.upperBody(Head, Torso, LeftArm, RightArm));
        animator.addPreset(AnimatorPresets.bipedal(LeftLeg, RightLeg))
                .addAnimator(new HeadInitAnimator<>(Head))
                .addAnimator(new ArmBobAnimator<>(LeftArm, RightArm))
                .addAnimator(new ArmRideAnimator<>(LeftArm, RightArm));
        if (form.animationData().swimTail())
            animator.addAnimator(new ArmSwimAnimator<>(LeftArm, RightArm));
        if (Tail != null)
            animator.addPreset(form.animationData().swimTail() ? AnimatorPresets.aquaticTail(Tail, List.of()) : AnimatorPresets.standardTail(Tail, List.of()));
        if (form.animationData().hasWings()) {
            this.RightWing = Torso.getChild("RightWing");
            this.LeftWing = Torso.getChild("LeftWing");
            animator.addPreset(AnimatorPresets.winged(LeftWing, RightWing));
        } else if (form.animationData().hasWingsV2()) {
            this.RightWing = Torso.getChild("RightWing");
            this.LeftWing = Torso.getChild("LeftWing");
            ModelPart leftWingRoot = LeftWing.getChild("WingRoot");
            ModelPart rightWingRoot = RightWing.getChild("WingRoot2");
            animator.addPreset(AnimatorPresets.wingedV2(
                    leftWingRoot, leftWingRoot.getChild("bone3"), leftWingRoot.getChild("bone3").getChild("bone4"),
                    rightWingRoot, rightWingRoot.getChild("bone"), rightWingRoot.getChild("bone").getChild("bone2")));
        } else {
            this.RightWing = null;
            this.LeftWing = null;
        }

        animator.hipOffset = form.hipOffset();
        animator.torsoWidth = form.torsoWidth();
        animator.forwardOffset = form.forwardOffset();
        animator.torsoLength = form.torsoLength();
        animator.armLength = form.armLength();
        animator.legLength = form.legLength();
    }

    @Override
    public void prepareMobModel(SpecialLatex entity, float p_102862_, float p_102863_, float p_102864_) {
        this.prepareMobModel(animator, entity, p_102862_, p_102863_, p_102864_);
    }

    public void setupHand() {
        animator.setupHand();
    }

    @Override
    public void setupAnim(@NotNull SpecialLatex entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        animator.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    }

    public ModelPart getArm(HumanoidArm p_102852_) {
        return p_102852_ == HumanoidArm.LEFT ? this.LeftArm : this.RightArm;
    }

    public ModelPart getHead() {
        return this.Head;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {

        this.RightLeg.render(poseStack, buffer, packedLight, packedOverlay);
        this.LeftLeg.render(poseStack, buffer, packedLight, packedOverlay);
        this.Head.render(poseStack, buffer, packedLight, packedOverlay);
        this.Torso.render(poseStack, buffer, packedLight, packedOverlay);
        this.RightArm.render(poseStack, buffer, packedLight, packedOverlay);
        this.LeftArm.render(poseStack, buffer, packedLight, packedOverlay);
    }

    @Override
    public LatexAnimator<SpecialLatex, SpecialLatexModel> getAnimator() {
        return animator;
    }
}
