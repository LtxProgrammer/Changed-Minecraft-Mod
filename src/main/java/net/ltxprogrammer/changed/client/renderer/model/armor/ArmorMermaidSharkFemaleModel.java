package net.ltxprogrammer.changed.client.renderer.model.armor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.animate.AnimatorPresets;
import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.client.renderer.animate.arm.ArmBobAnimator;
import net.ltxprogrammer.changed.client.renderer.animate.arm.ArmRideAnimator;
import net.ltxprogrammer.changed.client.renderer.animate.upperbody.SharkHeadInitAnimator;
import net.ltxprogrammer.changed.client.renderer.animate.upperbody.SharkHeadSwimAnimator;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class ArmorMermaidSharkFemaleModel<T extends ChangedEntity> extends LatexHumanoidArmorModel<T, ArmorMermaidSharkFemaleModel<T>> {
    public static final ModelLayerLocation INNER_ARMOR = ArmorModelLayerLocation.createInnerArmorLocation(Changed.modResource("armor_mermaid_shark_female")).get();
    public static final ModelLayerLocation OUTER_ARMOR = ArmorModelLayerLocation.createOuterArmorLocation(Changed.modResource("armor_mermaid_shark_female")).get();

    private final ModelPart Head;
    private final ModelPart Torso;
    private final ModelPart LeftArm;
    private final ModelPart RightArm;
    private final HumanoidAnimator<T, ArmorMermaidSharkFemaleModel<T>> animator;

    public ArmorMermaidSharkFemaleModel(ModelPart root) {
        this.Head = root.getChild("Head");
        this.Torso = root.getChild("Torso");
        this.LeftArm = root.getChild("LeftArm");
        this.RightArm = root.getChild("RightArm");

        this.animator = HumanoidAnimator.of(this).hipOffset(-1.5f).torsoLength(9.0f).legLength(9.5f)
                .addPreset(AnimatorPresets.sharkUpperBody(Head, Torso, LeftArm, RightArm))
                .addAnimator(new SharkHeadInitAnimator<>(Head))
                .addAnimator(new SharkHeadSwimAnimator<>(Head))
                .addAnimator(new ArmBobAnimator<>(LeftArm, RightArm))
                .addAnimator(new ArmRideAnimator<>(LeftArm, RightArm));
    }

    public static LayerDefinition createArmorLayer(ArmorModel layer) {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, layer.deformation), PartPose.offset(0.0F, -0.5F, 0.0F));

        PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, layer.deformation), PartPose.offset(0.0F, -0.5F, 0.0F));

        PartDefinition Plantoids = Torso.addOrReplaceChild("Plantoids", CubeListBuilder.create(), PartPose.offset(0.0F, 1.0F, -2.0F));

        PartDefinition Plantoid_r1 = Plantoids.addOrReplaceChild("Plantoid_r1", CubeListBuilder.create().texOffs(18, 22).mirror().addBox(-4.0F, 2.3F, -0.8F, 8.0F, 1.0F, 2.0F, layer.deformation.extend(-0.5f)).mirror(false)
                .texOffs(18, 19).mirror().addBox(-4.0F, -1.7F, -0.8F, 8.0F, 3.0F, 2.0F, layer.deformation.extend(-0.5f)).mirror(false), PartPose.offsetAndRotation(0.0F, 2.0F, 0.0F, -0.2793F, 0.0F, 0.0F));

        PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(40, 16).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, layer.deformation), PartPose.offset(-5.0F, 1.5F, 0.0F));

        PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(40, 16).mirror().addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, layer.deformation).mirror(false), PartPose.offset(5.0F, 1.5F, 0.0F));

        PartDefinition Abdomen = partdefinition.addOrReplaceChild("Abdomen", CubeListBuilder.create().texOffs(16, 24).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 4.0F, 4.0F, layer.deformation.extend(0.25F)), PartPose.offset(0.0F, 8.5F, 0.0F));

        PartDefinition LowerAbdomen = Abdomen.addOrReplaceChild("LowerAbdomen", CubeListBuilder.create(), PartPose.offset(0.0F, 4.25F, 0.0F));

        PartDefinition Tail = LowerAbdomen.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offset(0.0F, 5.5F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    @Override
    public HumanoidAnimator<T, ArmorMermaidSharkFemaleModel<T>> getAnimator() {
        return animator;
    }

    @Override
    public void renderForSlot(T entity, ItemStack stack, EquipmentSlot slot, PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        switch (slot) {
            case HEAD -> Head.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
            case CHEST -> {
                Torso.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
                LeftArm.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
                RightArm.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
            }
        }
    }
}
