package net.ltxprogrammer.changed.client.renderer.model.armor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.animate.AnimatorPresets;
import net.ltxprogrammer.changed.client.renderer.animate.LatexAnimator;
import net.ltxprogrammer.changed.client.renderer.model.LightLatexCentaurModel;
import net.ltxprogrammer.changed.entity.beast.LightLatexCentaur;
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

import java.util.List;

public class ArmorLightLatexCentaurModel extends LatexHumanoidArmorModel<LightLatexCentaur, ArmorLightLatexCentaurModel> {
    public static final ModelLayerLocation INNER_ARMOR = ArmorModelLayerLocation.createInnerArmorLocation(Changed.modResource("armor_light_latex_centaur")).get();
    public static final ModelLayerLocation OUTER_ARMOR = ArmorModelLayerLocation.createOuterArmorLocation(Changed.modResource("armor_light_latex_centaur")).get();

    private final ModelPart RightLeg;
    private final ModelPart LeftLeg;
    private final ModelPart RightLeg2;
    private final ModelPart LeftLeg2;
    private final ModelPart RightArm;
    private final ModelPart LeftArm;
    private final ModelPart Head;
    private final ModelPart Torso;
    private final ModelPart LowerTorso;
    private final ModelPart Tail;
    private final LatexAnimator<LightLatexCentaur, ArmorLightLatexCentaurModel> animator;

    public ArmorLightLatexCentaurModel(ModelPart root) {
        this.Head = root.getChild("Head");
        this.Torso = root.getChild("Torso");
        this.LowerTorso = root.getChild("LowerTorso");
        this.RightArm = root.getChild("RightArm");
        this.LeftArm = root.getChild("LeftArm");

        this.RightLeg = LowerTorso.getChild("RightLeg");
        this.LeftLeg = LowerTorso.getChild("LeftLeg");
        this.RightLeg2 = LowerTorso.getChild("RightLeg2");
        this.LeftLeg2 = LowerTorso.getChild("LeftLeg2");
        this.Tail = LowerTorso.getChild("Tail");
        animator = LatexAnimator.of(this).addPreset(AnimatorPresets.taurLike(Head, Torso, LeftArm, RightArm, Tail, List.of(), LeftLeg, RightLeg, LowerTorso, LeftLeg2, RightLeg2))
                .forwardOffset(-7.0f);
    }

    public static LayerDefinition createArmorLayer(ArmorModel layer) {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -9.0F, -4.0F, 8.0F, 8.0F, 8.0F, layer.deformation), PartPose.offset(0.0F, -2.0F, -7.0F));

        PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, -1.0F, -2.0F, 8.0F, 12.0F, 4.0F, layer.dualDeformation), PartPose.offset(0.0F, -2.0F, -7.0F));

        PartDefinition LowerTorso = partdefinition.addOrReplaceChild("LowerTorso", CubeListBuilder.create(), PartPose.offset(0.0F, 10.0F, -7.0F));

        PartDefinition Tail = LowerTorso.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offset(0.0F, 1.0F, 15.25F));

        PartDefinition Base_r1 = Tail.addOrReplaceChild("Base_r1", CubeListBuilder.create().texOffs(2, 17).addBox(-1.5F, 2.1914F, -2.1983F, 3.0F, 7.0F, 3.0F, layer.altDeformation), PartPose.offsetAndRotation(0.0F, 2.0F, 5.0F, 1.4835F, 0.0F, 0.0F));

        PartDefinition Base_r2 = Tail.addOrReplaceChild("Base_r2", CubeListBuilder.create().texOffs(2, 17).addBox(-1.5F, 2.0F, -1.0F, 3.0F, 7.0F, 3.0F, layer.altDeformation), PartPose.offsetAndRotation(0.0F, 0.0F, -1.0F, 1.1781F, 0.0F, 0.0F));

        PartDefinition LeftLeg = LowerTorso.addOrReplaceChild("LeftLeg", CubeListBuilder.create().texOffs(0, 26).mirror().addBox(-1.75F, 12.0F, -1.8F, 4.0F, 2.0F, 4.0F, layer.deformation).mirror(false)
                .texOffs(3, 29).mirror().addBox(-1.75F, 12.0F, -2.8F, 4.0F, 2.0F, 1.0F, layer.deformation).mirror(false), PartPose.offset(3.25F, 0.0F, 0.0F));

        PartDefinition LeftLowerLeg_r1 = LeftLeg.addOrReplaceChild("LeftLowerLeg_r1", CubeListBuilder.create().texOffs(16, 38).mirror().addBox(-2.0F, 9.8638F, 2.7342F, 4.0F, 1.0F, 4.0F, layer.slightAltDeformation).mirror(false)
                .texOffs(0, 16).mirror().addBox(-2.0F, 2.8638F, 2.7342F, 4.0F, 7.0F, 4.0F, layer.slightAltDeformation).mirror(false), PartPose.offsetAndRotation(0.25F, 1.0348F, -2.0472F, -0.2182F, 0.0F, 0.0F));

        PartDefinition LeftUpperLeg_r1 = LeftLeg.addOrReplaceChild("LeftUpperLeg_r1", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-2.0F, -0.3172F, -0.0274F, 4.0F, 6.0F, 4.0F, layer.inverseDeformation).mirror(false), PartPose.offsetAndRotation(0.25F, 1.0348F, -2.0472F, 0.3054F, 0.0F, 0.0F));

        PartDefinition RightLeg = LowerTorso.addOrReplaceChild("RightLeg", CubeListBuilder.create().texOffs(0, 26).addBox(-1.75F, 12.0F, -1.8F, 4.0F, 2.0F, 4.0F, layer.deformation)
                .texOffs(3, 29).addBox(-1.75F, 12.0F, -2.8F, 4.0F, 2.0F, 1.0F, layer.deformation), PartPose.offset(-3.75F, 0.0F, 0.0F));

        PartDefinition RightLowerLeg_r1 = RightLeg.addOrReplaceChild("RightLowerLeg_r1", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 9.8638F, 2.7342F, 4.0F, 1.0F, 4.0F, layer.slightAltDeformation)
                .texOffs(0, 16).addBox(-2.0F, 2.8638F, 2.7342F, 4.0F, 7.0F, 4.0F, layer.slightAltDeformation), PartPose.offsetAndRotation(0.25F, 1.0348F, -2.0472F, -0.2182F, 0.0F, 0.0F));

        PartDefinition RightUpperLeg_r1 = RightLeg.addOrReplaceChild("RightUpperLeg_r1", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, -0.3172F, -0.0274F, 4.0F, 6.0F, 4.0F, layer.inverseDeformation), PartPose.offsetAndRotation(0.25F, 1.0348F, -2.0472F, 0.3054F, 0.0F, 0.0F));

        PartDefinition LeftLeg2 = LowerTorso.addOrReplaceChild("LeftLeg2", CubeListBuilder.create().texOffs(0, 26).mirror().addBox(-2.0F, 12.0F, 0.25F, 4.0F, 2.0F, 4.0F, layer.deformation).mirror(false), PartPose.offset(3.5F, 0.0F, 15.0F));

        PartDefinition LeftLowerLeg_r2 = LeftLeg2.addOrReplaceChild("LeftLowerLeg_r2", CubeListBuilder.create().texOffs(1, 17).mirror().addBox(-2.0F, 3.5131F, 2.8162F, 4.0F, 6.0F, 3.0F, layer.altDeformation).mirror(false), PartPose.offsetAndRotation(0.0F, 2.1238F, 0.5226F, -0.2182F, 0.0F, 0.0F));

        PartDefinition LeftMidLeg_r1 = LeftLeg2.addOrReplaceChild("LeftMidLeg_r1", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-2.0F, -0.098F, -4.8445F, 4.0F, 7.0F, 4.0F, layer.slightAltDeformation).mirror(false), PartPose.offsetAndRotation(0.0F, 2.1238F, 0.5226F, 0.8727F, 0.0F, 0.0F));

        PartDefinition LeftUpperLeg_r2 = LeftLeg2.addOrReplaceChild("LeftUpperLeg_r2", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-2.0F, -2.0208F, -2.9291F, 4.0F, 6.0F, 4.0F, layer.altDeformation).mirror(false), PartPose.offsetAndRotation(0.0F, 2.1238F, 0.5226F, -0.0873F, 0.0F, 0.0F));

        PartDefinition RightLeg2 = LowerTorso.addOrReplaceChild("RightLeg2", CubeListBuilder.create().texOffs(0, 26).addBox(-2.0F, 12.0F, 0.25F, 4.0F, 2.0F, 4.0F, layer.deformation), PartPose.offset(-3.5F, 0.0F, 15.0F));

        PartDefinition RightLowerLeg_r2 = RightLeg2.addOrReplaceChild("RightLowerLeg_r2", CubeListBuilder.create().texOffs(1, 17).addBox(-2.0F, 3.5131F, 2.8162F, 4.0F, 6.0F, 3.0F, layer.altDeformation), PartPose.offsetAndRotation(0.0F, 2.1238F, 0.5226F, -0.2182F, 0.0F, 0.0F));

        PartDefinition RightMidLeg_r1 = RightLeg2.addOrReplaceChild("RightMidLeg_r1", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, -0.098F, -4.8445F, 4.0F, 7.0F, 4.0F, layer.slightAltDeformation), PartPose.offsetAndRotation(0.0F, 2.1238F, 0.5226F, 0.8727F, 0.0F, 0.0F));

        PartDefinition RightUpperLeg_r2 = RightLeg2.addOrReplaceChild("RightUpperLeg_r2", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, -2.0208F, -2.9291F, 4.0F, 6.0F, 4.0F, layer.altDeformation), PartPose.offsetAndRotation(0.0F, 2.1238F, 0.5226F, -0.0873F, 0.0F, 0.0F));

        PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(40, 16).addBox(-3.0F, -3.0F, -2.0F, 4.0F, 12.0F, 4.0F, layer.deformation), PartPose.offset(-5.0F, 0.0F, -7.0F));

        PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(40, 16).mirror().addBox(-1.0F, -3.0F, -2.0F, 4.0F, 12.0F, 4.0F, layer.deformation).mirror(false), PartPose.offset(5.0F, 0.0F, -7.0F));

        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    @Override
    public LatexAnimator<LightLatexCentaur, ArmorLightLatexCentaurModel> getAnimator() {
        return animator;
    }

    @Override
    public void renderForSlot(LightLatexCentaur entity, ItemStack stack, EquipmentSlot slot, PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        switch (slot) {
            case HEAD -> Head.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
            case CHEST -> {
                Torso.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
                LeftArm.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
                RightArm.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
            }
            case LEGS -> {
                if (stack.getItem() instanceof Shorts) {
                    setAllPartsVisibility(LowerTorso, false);
                    LeftLeg2.getChild("LeftUpperLeg_r2").visible = true;
                    RightLeg2.getChild("RightUpperLeg_r2").visible = true;
                }

                LowerTorso.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);

                if (stack.getItem() instanceof Shorts) {
                    setAllPartsVisibility(LowerTorso, true);
                }
            }
            case FEET -> LowerTorso.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        }
    }
}
