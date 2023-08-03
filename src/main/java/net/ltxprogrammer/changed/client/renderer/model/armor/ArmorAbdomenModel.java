package net.ltxprogrammer.changed.client.renderer.model.armor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.animate.AnimatorPresets;
import net.ltxprogrammer.changed.client.renderer.animate.LatexAnimator;
import net.ltxprogrammer.changed.entity.LatexEntity;
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
import java.util.Map;

public class ArmorAbdomenModel<T extends LatexEntity> extends LatexHumanoidArmorModel<T, ArmorAbdomenModel<T>> {
    public static final ModelLayerLocation INNER_ARMOR = ArmorModelLayerLocation.createInnerArmorLocation(Changed.modResource("armor_abdomen")).get();
    public static final ModelLayerLocation OUTER_ARMOR = ArmorModelLayerLocation.createOuterArmorLocation(Changed.modResource("armor_abdomen")).get();
    public static final ModelPart EMPTY_PART = new ModelPart(List.of(), Map.of());

    private final ModelPart Torso;
    private final ModelPart Abdomen;
    private final ModelPart LowerAbdomen;
    private final ModelPart Tail;
    private final LatexAnimator<T, ArmorAbdomenModel<T>> animator;

    public ArmorAbdomenModel(ModelPart root) {
        this.Torso = root.getChild("Torso");
        this.Abdomen = root.getChild("Abdomen");
        this.LowerAbdomen = Abdomen.getChild("LowerAbdomen");
        this.Tail = LowerAbdomen.getChild("Tail");

        this.animator = LatexAnimator.of(this).addPreset(AnimatorPresets.legless(Abdomen, LowerAbdomen, Tail, List.of(
                Tail.getChild("Joint"),
                Tail.getChild("Joint").getChild("Joint2"),
                Tail.getChild("Joint").getChild("Joint2").getChild("Joint3"))))
                .addPreset(AnimatorPresets.upperBody(EMPTY_PART, Torso, EMPTY_PART, EMPTY_PART));
    }

    public static LayerDefinition createArmorLayer(ArmorModel layer) {
        final float deformationOffset = -0.25F;
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition Abdomen = partdefinition.addOrReplaceChild("Abdomen", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 10.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition Base_r1 = Abdomen.addOrReplaceChild("Base_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, 0.734F, -1.8537F, 8.0F, 2.0F, 5.0F, layer.deformation.extend(0.55F + deformationOffset))
                .texOffs(0, 7).addBox(-4.5F, 2.6799F, -2.3597F, 9.0F, 7.0F, 6.0F, layer.deformation.extend(0.7F + deformationOffset)), PartPose.offsetAndRotation(0.0F, 0.0478F, -0.6297F, 0.0F, 0.0F, 0.0F));

        PartDefinition LowerAbdomen = Abdomen.addOrReplaceChild("LowerAbdomen", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 9.0F, -1.0F, 0.8727F, 0.0F, 0.0F));

        PartDefinition Base_r2 = LowerAbdomen.addOrReplaceChild("Base_r2", CubeListBuilder.create().texOffs(0, 20).addBox(-4.5F, 0.3384F, -3.0281F, 9.0F, 6.0F, 6.0F, layer.dualDeformation.extend(0.5F + deformationOffset)), PartPose.offsetAndRotation(0.0F, -1.0025F, 1.0529F, 0.0F, 0.0F, 0.0F));

        PartDefinition Tail = LowerAbdomen.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 5.0F, 0.0F, 0.6981F, 0.0F, 0.0F));

        PartDefinition Base_r3 = Tail.addOrReplaceChild("Base_r3", CubeListBuilder.create().texOffs(30, 0).addBox(-3.5F, 3.8569F, -3.5836F, 7.0F, 7.0F, 5.0F, layer.altDeformation.extend(0.35F + deformationOffset)), PartPose.offsetAndRotation(0.0F, -5.0025F, 1.0529F, 0.2007F, 0.0F, 0.0F));

        PartDefinition Joint = Tail.addOrReplaceChild("Joint", CubeListBuilder.create(), PartPose.offset(0.0F, 5.8854F, 2.329F));

        PartDefinition Base_r4 = Joint.addOrReplaceChild("Base_r4", CubeListBuilder.create().texOffs(30, 0).addBox(-3.5F, 10.3541F, -3.4527F, 7.0F, 7.0F, 5.0F, layer.altDeformation.extend(-0.15F + deformationOffset)), PartPose.offsetAndRotation(0.0F, -10.8879F, -1.2762F, 0.2007F, 0.0F, 0.0F));

        PartDefinition Joint2 = Joint.addOrReplaceChild("Joint2", CubeListBuilder.create(), PartPose.offset(0.0F, 6.2215F, 1.5432F));

        PartDefinition Base_r5 = Joint2.addOrReplaceChild("Base_r5", CubeListBuilder.create().texOffs(30, 12).addBox(-2.5F, 16.8531F, -2.848F, 5.0F, 6.0F, 4.0F, layer.altDeformation.extend(0.1F + deformationOffset)), PartPose.offsetAndRotation(0.0F, -17.1093F, -2.8194F, 0.2007F, 0.0F, 0.0F));

        PartDefinition Joint3 = Joint2.addOrReplaceChild("Joint3", CubeListBuilder.create(), PartPose.offset(0.0F, 5.2785F, 0.4568F));

        PartDefinition Base_r6 = Joint3.addOrReplaceChild("Base_r6", CubeListBuilder.create().texOffs(46, 20).addBox(-2.5F, 20.8458F, -2.9497F, 5.0F, 8.0F, 4.0F, layer.altDeformation.extend(-0.15F + deformationOffset)), PartPose.offsetAndRotation(0.0F, -22.3879F, -3.2762F, 0.2007F, 0.0F, 0.0F));

        PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create(), PartPose.offset(0.0F, -2.0F, 0.0F));

        PartDefinition Torso_r1 = Torso.addOrReplaceChild("Torso_r1", CubeListBuilder.create().texOffs(40, 0).addBox(-4.0F, -12.0F, -2.0F, 8.0F, 12.0F, 4.0F, layer.deformation.extend(deformationOffset)), PartPose.offsetAndRotation(0.0F, 13.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    @Override
    public LatexAnimator<T, ArmorAbdomenModel<T>> getAnimator() {
        return animator;
    }

    @Override
    public void renderForSlot(T entity, ItemStack stack, EquipmentSlot slot, PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        switch (slot) {
            case LEGS -> {
                setAllPartsVisibility(Tail, false);
                Torso.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
                Abdomen.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
                setAllPartsVisibility(Tail, true);
            }
            case FEET -> Abdomen.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        }
    }

    public static class Remodel<T extends LatexEntity> extends LatexHumanoidArmorModel<T, Remodel<T>> {
        public static final ModelLayerLocation INNER_ARMOR = ArmorModelLayerLocation.createInnerArmorLocation(Changed.modResource("armor_abdomen_remodel")).get();
        public static final ModelLayerLocation OUTER_ARMOR = ArmorModelLayerLocation.createOuterArmorLocation(Changed.modResource("armor_abdomen_remodel")).get();
        public static final ModelPart EMPTY_PART = new ModelPart(List.of(), Map.of());

        private final ModelPart Torso;
        private final ModelPart Abdomen;
        private final ModelPart LowerAbdomen;
        private final ModelPart Tail;
        private final ModelPart Joint;
        private final LatexAnimator<T, Remodel<T>> animator;

        public Remodel(ModelPart root) {
            this.Torso = root.getChild("Torso");
            this.Abdomen = root.getChild("Abdomen");
            this.LowerAbdomen = Abdomen.getChild("LowerAbdomen");
            this.Tail = LowerAbdomen.getChild("Tail");
            this.Joint = Tail.getChild("Joint");

            this.animator = LatexAnimator.of(this).addPreset(AnimatorPresets.leglessV2(Abdomen, LowerAbdomen, Tail, List.of(
                            Joint,
                            Joint.getChild("Joint2"),
                            Joint.getChild("Joint2").getChild("Joint3"))))
                    .addPreset(AnimatorPresets.upperBody(EMPTY_PART, Torso, EMPTY_PART, EMPTY_PART)).hipOffset(0.0f);
        }

        public static LayerDefinition createArmorLayer(ArmorModel layer) {
            final float deformationOffset = -0.25F;
            MeshDefinition meshdefinition = new MeshDefinition();
            PartDefinition partdefinition = meshdefinition.getRoot();

            PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create().texOffs(40, 1).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 11.0F, 4.0F, layer.deformation.extend(deformationOffset))
                    .texOffs(0, 0).addBox(-4.0F, 11.0F, -2.5F, 8.0F, 2.0F, 5.0F, layer.deformation.extend(deformationOffset)), PartPose.offset(0.0F, 0.0F, 0.0F));

            PartDefinition Abdomen = partdefinition.addOrReplaceChild("Abdomen", CubeListBuilder.create().texOffs(0, 7).addBox(-4.5F, 1.25F, -3.0F, 9.0F, 6.0F, 6.0F, layer.deformation.extend(0.25F + deformationOffset)), PartPose.offset(0.0F, 12.0F, 0.0F));

            PartDefinition LowerAbdomen = Abdomen.addOrReplaceChild("LowerAbdomen", CubeListBuilder.create().texOffs(0, 7).addBox(-4.5F, 0.0F, -3.0F, 9.0F, 7.0F, 6.0F, layer.deformation.extend(0.75F + deformationOffset)), PartPose.offset(0.0F, 6.0F, 0.0F));

            PartDefinition Tail = LowerAbdomen.addOrReplaceChild("Tail", CubeListBuilder.create().texOffs(0, 20).addBox(-4.5F, 1.0F, -3.0F, 9.0F, 6.0F, 6.0F, layer.dualDeformation.extend(0.25F + deformationOffset)), PartPose.offset(0.0F, 5.0F, 0.0F));

            PartDefinition Joint = Tail.addOrReplaceChild("Joint", CubeListBuilder.create().texOffs(30, 0).addBox(-3.5F, 2.0F, -2.5F, 7.0F, 4.0F, 5.0F, layer.altDeformation.extend(0.5F + deformationOffset)), PartPose.offset(0.0F, 5.0F, 0.0F));

            PartDefinition Joint2 = Joint.addOrReplaceChild("Joint2", CubeListBuilder.create().texOffs(30, 12).addBox(-2.5F, 2.0F, -2.0F, 5.0F, 4.0F, 4.0F, layer.altDeformation.extend(0.5F + deformationOffset)), PartPose.offset(0.0F, 4.0F, 0.0F));

            PartDefinition Joint3 = Joint2.addOrReplaceChild("Joint3", CubeListBuilder.create().texOffs(46, 20).addBox(-2.5F, 2.0F, -2.0F, 5.0F, 5.0F, 4.0F, layer.altDeformation.extend(deformationOffset)), PartPose.offset(0.0F, 4.0F, 0.0F));

            PartDefinition Joint4 = Joint3.addOrReplaceChild("Joint4", CubeListBuilder.create().texOffs(54, 5).addBox(-2.0F, 2.0F, -1.5F, 4.0F, 5.0F, 3.0F, layer.altDeformation.extend(deformationOffset)), PartPose.offset(0.0F, 5.0F, 0.0F));

            return LayerDefinition.create(meshdefinition, 64, 32);
        }

        @Override
        public LatexAnimator<T, Remodel<T>> getAnimator() {
            return animator;
        }

        @Override
        public void renderForSlot(T entity, ItemStack stack, EquipmentSlot slot, PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
            switch (slot) {
                case LEGS -> {
                    setAllPartsVisibility(Joint, false);
                    Torso.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
                    Abdomen.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
                    setAllPartsVisibility(Joint, true);
                }
                case FEET -> Abdomen.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
            }
        }
    }
}
