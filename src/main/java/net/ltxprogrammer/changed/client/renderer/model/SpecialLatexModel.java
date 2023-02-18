package net.ltxprogrammer.changed.client.renderer.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.entity.beast.SpecialLatex;
import net.ltxprogrammer.changed.util.PatreonBenefits;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.HumanoidArm;
import org.jetbrains.annotations.NotNull;

public class SpecialLatexModel extends LatexHumanoidModel<SpecialLatex> implements LatexHumanoidModelInterface {
    private final ModelPart RightLeg;
    private final ModelPart LeftLeg;
    private final ModelPart RightArm;
    private final ModelPart LeftArm;
    private final ModelPart Head;
    private final ModelPart Torso;
    private final ModelPart Tail;
    private final ModelPart RightWing;
    private final ModelPart LeftWing;
    private final LatexHumanoidModelController controller;

    public SpecialLatexModel(ModelPart root, PatreonBenefits.ModelData form) {
        super(root);
        this.RightLeg = root.getChild("RightLeg");
        this.LeftLeg = root.getChild("LeftLeg");
        this.Head = root.getChild("Head");
        this.Torso = root.getChild("Torso");
        this.Tail = form.animationData().hasTail() ? Torso.getChild("Tail") : null;
        this.RightArm = root.getChild("RightArm");
        this.LeftArm = root.getChild("LeftArm");
        LatexHumanoidModelController.Builder builder = LatexHumanoidModelController.Builder.of(this, Head, Torso, Tail, RightArm, LeftArm, RightLeg, LeftLeg);
        if (form.animationData().hasWings()) {
            this.RightWing = Torso.getChild("RightWing");
            this.LeftWing = Torso.getChild("LeftWing");
            builder.wings(RightWing, LeftWing);
        }
        else {
            this.RightWing = null;
            this.LeftWing = null;
        }

        if (form.animationData().swimTail())
            builder.tailAidsInSwim();
        controller = builder.build();
    }

    @Override
    public void prepareMobModel(SpecialLatex entity, float p_102862_, float p_102863_, float p_102864_) {
        this.prepareMobModel(controller, entity, p_102862_, p_102863_, p_102864_);
    }

    public void setupHand() {
        controller.setupHand();
    }

    @Override
    public void setupAnim(@NotNull SpecialLatex entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        controller.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
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
    public LatexHumanoidModelController getController() {
        return controller;
    }
}
