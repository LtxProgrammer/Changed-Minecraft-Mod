package net.ltxprogrammer.changed.client.renderer.model.armor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.animate.AnimatorPresets;
import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.beast.LatexTaur;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ArmorLatexCentaurLowerModel<T extends ChangedEntity & LatexTaur<T>> extends LatexHumanoidArmorModel<T, ArmorLatexCentaurLowerModel<T>> {
    public static final ModelLayerLocation INNER_ARMOR = ArmorModelLayerLocation.createInnerArmorLocation(Changed.modResource("armor_latex_centaur_lower")).get();
    public static final ModelLayerLocation OUTER_ARMOR = ArmorModelLayerLocation.createOuterArmorLocation(Changed.modResource("armor_latex_centaur_lower")).get();

    private final ModelPart FrontRightLeg;
    private final ModelPart FrontLeftLeg;
    private final ModelPart BackRightLeg;
    private final ModelPart BackLeftLeg;
    private final ModelPart LowerTorso;
    private final HumanoidAnimator<T, ArmorLatexCentaurLowerModel<T>> animator;

    public ArmorLatexCentaurLowerModel(ModelPart root) {
        this.LowerTorso = root.getChild("LowerTorso");
        this.FrontRightLeg = LowerTorso.getChild("RightLeg");
        this.FrontLeftLeg = LowerTorso.getChild("LeftLeg");
        this.BackRightLeg = LowerTorso.getChild("RightLeg2");
        this.BackLeftLeg = LowerTorso.getChild("LeftLeg2");

        var leftLowerLeg = FrontLeftLeg.getChild("LeftLowerLeg");
        var rightLowerLeg = FrontRightLeg.getChild("RightLowerLeg");

        var leftLowerLeg2 = BackLeftLeg.getChild("LeftLowerLeg2");
        var leftFoot2 = leftLowerLeg2.getChild("LeftFoot2");
        var rightLowerLeg2 = BackRightLeg.getChild("RightLowerLeg2");
        var rightFoot2 = rightLowerLeg2.getChild("RightFoot2");

        animator = HumanoidAnimator.of(this)
                .addPreset(AnimatorPresets.taurLegs(EMPTY_PART, List.of(),
                        LowerTorso, FrontLeftLeg, leftLowerLeg, leftLowerLeg.getChild("LeftFoot"), FrontRightLeg, rightLowerLeg, rightLowerLeg.getChild("RightFoot"),
                        BackLeftLeg, leftLowerLeg2, leftFoot2, leftFoot2.getChild("LeftPad2"), BackRightLeg, rightLowerLeg2, rightFoot2, rightFoot2.getChild("RightPad2")))
                .forwardOffset(-7.0f).hipOffset(-1.5f).legLength(13.5f).torsoLength(11.05f);
    }

    public static LayerDefinition createArmorLayer(ArmorModel layer) {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition LowerTorso = partdefinition.addOrReplaceChild("LowerTorso", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -2.0F, -2.0F, 8.0F, 6.0F, 19.0F, layer.altDeformation), PartPose.offset(0.0F, 10.5F, -7.0F));

        PartDefinition LeftLeg2 = LowerTorso.addOrReplaceChild("LeftLeg2", CubeListBuilder.create(), PartPose.offset(3.5F, 0.0F, 16.375F));

        PartDefinition LeftThigh_r1 = LeftLeg2.addOrReplaceChild("LeftThigh_r1", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, layer.altDeformation.extend(0.1F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition LeftLowerLeg2 = LeftLeg2.addOrReplaceChild("LeftLowerLeg2", CubeListBuilder.create(), PartPose.offset(0.0F, 6.375F, -3.45F));

        PartDefinition LeftCalf_r1 = LeftLowerLeg2.addOrReplaceChild("LeftCalf_r1", CubeListBuilder.create().texOffs(35, 0).mirror().addBox(-2.01F, -0.125F, -2.9F, 4.0F, 6.0F, 4.0F, layer.altDeformation).mirror(false), PartPose.offsetAndRotation(0.0F, -2.125F, 1.95F, 0.8727F, 0.0F, 0.0F));

        PartDefinition LeftFoot2 = LeftLowerLeg2.addOrReplaceChild("LeftFoot2", CubeListBuilder.create(), PartPose.offset(0.0F, 0.8F, 7.175F));

        PartDefinition LeftArch_r1 = LeftFoot2.addOrReplaceChild("LeftArch_r1", CubeListBuilder.create().texOffs(35, 10).mirror().addBox(-2.0F, -8.45F, -0.725F, 4.0F, 6.0F, 3.0F, layer.altDeformation.extend(0.005F)).mirror(false), PartPose.offsetAndRotation(0.0F, 7.075F, -4.975F, -0.3491F, 0.0F, 0.0F));

        PartDefinition LeftPad2 = LeftFoot2.addOrReplaceChild("LeftPad2", CubeListBuilder.create().texOffs(0, 11).addBox(-2.0F, 0.0F, -2.5F, 4.0F, 2.0F, 5.0F, layer.deformation), PartPose.offset(0.0F, 4.325F, -4.425F));

        PartDefinition RightLeg2 = LowerTorso.addOrReplaceChild("RightLeg2", CubeListBuilder.create(), PartPose.offset(-3.5F, 0.0F, 16.375F));

        PartDefinition RightThigh_r1 = RightLeg2.addOrReplaceChild("RightThigh_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, layer.altDeformation.extend(0.1F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition RightLowerLeg2 = RightLeg2.addOrReplaceChild("RightLowerLeg2", CubeListBuilder.create(), PartPose.offset(0.0F, 6.375F, -3.45F));

        PartDefinition RightCalf_r1 = RightLowerLeg2.addOrReplaceChild("RightCalf_r1", CubeListBuilder.create().texOffs(35, 0).addBox(-1.99F, -0.125F, -2.9F, 4.0F, 6.0F, 4.0F, layer.altDeformation), PartPose.offsetAndRotation(0.0F, -2.125F, 1.95F, 0.8727F, 0.0F, 0.0F));

        PartDefinition RightFoot2 = RightLowerLeg2.addOrReplaceChild("RightFoot2", CubeListBuilder.create(), PartPose.offset(0.0F, 0.8F, 7.175F));

        PartDefinition RightArch_r1 = RightFoot2.addOrReplaceChild("RightArch_r1", CubeListBuilder.create().texOffs(35, 10).addBox(-2.0F, -8.45F, -0.725F, 4.0F, 6.0F, 3.0F, layer.altDeformation.extend(0.005F)), PartPose.offsetAndRotation(0.0F, 7.075F, -4.975F, -0.3491F, 0.0F, 0.0F));

        PartDefinition RightPad2 = RightFoot2.addOrReplaceChild("RightPad2", CubeListBuilder.create().texOffs(0, 11).mirror().addBox(-2.0F, 0.0F, -2.5F, 4.0F, 2.0F, 5.0F, layer.deformation).mirror(false), PartPose.offset(0.0F, 4.325F, -4.425F));

        PartDefinition LeftLeg = LowerTorso.addOrReplaceChild("LeftLeg", CubeListBuilder.create(), PartPose.offset(3.5F, 0.0F, -1.7F));

        PartDefinition LeftUpperLeg_r1 = LeftLeg.addOrReplaceChild("LeftUpperLeg_r1", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-2.0F, -6.89F, -4.2461F, 4.0F, 7.0F, 4.0F, layer.altDeformation.extend(0.1F)).mirror(false), PartPose.offsetAndRotation(0.25F, 5.5348F, 3.9528F, 0.0873F, 0.0F, 0.0F));

        PartDefinition LeftLowerLeg = LeftLeg.addOrReplaceChild("LeftLowerLeg", CubeListBuilder.create(), PartPose.offset(0.25F, 5.7848F, 3.7028F));

        PartDefinition LeftLowerLeg_r1 = LeftLowerLeg.addOrReplaceChild("LeftLowerLeg_r1", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-2.0F, 3.8638F, 2.7342F, 4.0F, 7.0F, 4.0F, layer.altDeformation).mirror(false), PartPose.offsetAndRotation(0.0F, -5.275F, -5.575F, -0.2182F, 0.0F, 0.0F));

        PartDefinition LeftFoot = LeftLowerLeg.addOrReplaceChild("LeftFoot", CubeListBuilder.create().texOffs(0, 11).addBox(-1.95F, 0.0F, -2.0F, 4.0F, 2.0F, 5.0F, layer.deformation), PartPose.offset(0.0F, 5.7152F, -4.3278F));

        PartDefinition RightLeg = LowerTorso.addOrReplaceChild("RightLeg", CubeListBuilder.create(), PartPose.offset(-4.0F, 0.0F, -1.7F));

        PartDefinition RightUpperLeg_r1 = RightLeg.addOrReplaceChild("RightUpperLeg_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-9.5F, -6.89F, -4.2461F, 4.0F, 7.0F, 4.0F, layer.altDeformation.extend(0.1F)), PartPose.offsetAndRotation(7.75F, 5.5348F, 3.9528F, 0.0873F, 0.0F, 0.0F));

        PartDefinition RightLowerLeg = RightLeg.addOrReplaceChild("RightLowerLeg", CubeListBuilder.create(), PartPose.offset(0.0F, 5.7848F, 3.7028F));

        PartDefinition RightLowerLeg_r1 = RightLowerLeg.addOrReplaceChild("RightLowerLeg_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, 3.8638F, 2.7342F, 4.0F, 7.0F, 4.0F, layer.altDeformation), PartPose.offsetAndRotation(0.25F, -5.275F, -5.575F, -0.2182F, 0.0F, 0.0F));

        PartDefinition RightFoot = RightLowerLeg.addOrReplaceChild("RightFoot", CubeListBuilder.create().texOffs(0, 11).mirror().addBox(-2.025F, 0.0F, -2.0F, 4.0F, 2.0F, 5.0F, layer.deformation).mirror(false), PartPose.offset(0.25F, 5.7152F, -4.3278F));

        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    @Override
    public HumanoidAnimator<T, ArmorLatexCentaurLowerModel<T>> getAnimator() {
        return animator;
    }

    @Override
    public void renderForSlot(T entity, ItemStack stack, EquipmentSlot slot, PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        switch (slot) {
            case LEGS, FEET -> LowerTorso.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        }
    }
}
