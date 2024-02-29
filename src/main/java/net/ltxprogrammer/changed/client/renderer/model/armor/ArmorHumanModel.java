package net.ltxprogrammer.changed.client.renderer.model.armor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.animate.AnimatorPresets;
import net.ltxprogrammer.changed.client.renderer.animate.LatexAnimator;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.item.Shorts;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ArmorHumanModel<T extends LatexEntity> extends LatexHumanoidArmorModel<T, ArmorHumanModel<T>> {
    private final ModelPart Head;
    private final ModelPart Hat;
    private final ModelPart Torso;
    private final ModelPart LeftLeg;
    private final ModelPart RightLeg;
    private final ModelPart LeftArm;
    private final ModelPart RightArm;
    private final LatexAnimator<T, ArmorHumanModel<T>> animator;

    // Taken from HumanoidModel
    public ArmorHumanModel(ModelPart modelPart) {
        this.Head = modelPart.getChild("head");
        this.Hat = modelPart.getChild("hat");
        this.Torso = modelPart.getChild("body");
        this.LeftLeg = modelPart.getChild("left_leg");
        this.RightLeg = modelPart.getChild("right_leg");
        this.LeftArm = modelPart.getChild("left_arm");
        this.RightArm = modelPart.getChild("right_arm");

        this.animator = LatexAnimator.of(this).hipOffset(-1.5f).legLength(10.5f)
                .addPreset(AnimatorPresets.humanLike(Head, Torso, LeftArm, RightArm, LeftLeg, RightLeg));
    }

    @Override
    public void renderForSlot(T entity, ItemStack stack, EquipmentSlot slot, PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        switch (slot) {
            case HEAD ->  {
                Head.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
                Hat.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
            }
            case CHEST -> {
                Torso.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
                LeftArm.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
                RightArm.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
            }
            case LEGS -> {
                Torso.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
                LeftLeg.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
                RightLeg.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
            }
            case FEET -> {
                LeftLeg.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
                RightLeg.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
            }
        }
    }

    @Override
    public LatexAnimator<T, ArmorHumanModel<T>> getAnimator() {
        return animator;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        Hat.copyFrom(Head);
    }
}
