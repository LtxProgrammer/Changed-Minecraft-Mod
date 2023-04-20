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

import java.util.List;

public class ArmorLatexSharkModel<T extends LatexEntity> extends LatexHumanoidArmorModel<T, ArmorLatexSharkModel<T>> {
    public static final ModelLayerLocation INNER_ARMOR = ArmorModelLayerLocation.createInnerArmorLocation(Changed.modResource("armor_latex_shark")).get();
    public static final ModelLayerLocation OUTER_ARMOR = ArmorModelLayerLocation.createOuterArmorLocation(Changed.modResource("armor_latex_shark")).get();

    private final ModelPart Head;
    private final ModelPart Torso;
    private final ModelPart Tail;
    private final ModelPart LeftLeg;
    private final ModelPart RightLeg;
    private final ModelPart LeftArm;
    private final ModelPart RightArm;
    private final LatexAnimator<T, ArmorLatexSharkModel<T>> animator;

    public ArmorLatexSharkModel(ModelPart modelPart) {
        this.Head = modelPart.getChild("Head");
        this.Torso = modelPart.getChild("Torso");
        this.Tail = Torso.getChild("Tail");
        this.LeftLeg = modelPart.getChild("LeftLeg");
        this.RightLeg = modelPart.getChild("RightLeg");
        this.LeftArm = modelPart.getChild("LeftArm");
        this.RightArm = modelPart.getChild("RightArm");

        this.animator = LatexAnimator.of(this).addPreset(AnimatorPresets.sharkLike(Head, Torso, LeftArm, RightArm, Tail, List.of(), LeftLeg, RightLeg));
    }

    public static LayerDefinition createArmorLayer(ArmorModel layer) {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition RightLeg = partdefinition.addOrReplaceChild("RightLeg", CubeListBuilder.create(), PartPose.offset(-2.75F, 10.0F, 0.0F));

        PartDefinition RightFoot_r1 = RightLeg.addOrReplaceChild("RightFoot_r1", CubeListBuilder.create().texOffs(0, 26).addBox(-4.0F, -4.0F, -2.0F, 4.0F, 2.0F, 4.0F, layer.deformation), PartPose.offsetAndRotation(2.0F, 16.0F, -0.75F, 0.0F, 0.0F, 0.0F));

        PartDefinition RightLowerLeg_r1 = RightLeg.addOrReplaceChild("RightLowerLeg_r1", CubeListBuilder.create().texOffs(1, 17).addBox(-2.0F, -7.5F, -1.0F, 4.0F, 6.0F, 3.0F, layer.altDeformation), PartPose.offsetAndRotation(0.0F, 14.25F, -1.25F, -0.5236F, 0.0F, 0.0F));

        PartDefinition RightMidLeg_r1 = RightLeg.addOrReplaceChild("RightMidLeg_r1", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, layer.slightAltDeformation), PartPose.offsetAndRotation(0.0F, 4.25F, -1.5F, 0.5672F, 0.0F, 0.0F));

        PartDefinition RightUpperLeg_r1 = RightLeg.addOrReplaceChild("RightUpperLeg_r1", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, layer.altDeformation), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition LeftLeg = partdefinition.addOrReplaceChild("LeftLeg", CubeListBuilder.create(), PartPose.offset(2.75F, 10.0F, 0.0F));

        PartDefinition LeftFoot_r1 = LeftLeg.addOrReplaceChild("LeftFoot_r1", CubeListBuilder.create().texOffs(0, 26).mirror().addBox(-4.0F, -4.0F, -2.0F, 4.0F, 2.0F, 4.0F, layer.deformation).mirror(false), PartPose.offsetAndRotation(2.0F, 16.0F, -0.75F, 0.0F, 0.0F, 0.0F));

        PartDefinition LeftLowerLeg_r1 = LeftLeg.addOrReplaceChild("LeftLowerLeg_r1", CubeListBuilder.create().texOffs(1, 17).mirror().addBox(-2.0F, -7.5F, -1.0F, 4.0F, 6.0F, 3.0F, layer.altDeformation).mirror(false), PartPose.offsetAndRotation(0.0F, 14.25F, -1.25F, -0.5236F, 0.0F, 0.0F));

        PartDefinition LeftMidLeg_r1 = LeftLeg.addOrReplaceChild("LeftMidLeg_r1", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, layer.slightAltDeformation).mirror(false), PartPose.offsetAndRotation(0.0F, 4.25F, -1.5F, 0.5672F, 0.0F, 0.0F));

        PartDefinition LeftUpperLeg_r1 = LeftLeg.addOrReplaceChild("LeftUpperLeg_r1", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, layer.altDeformation).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create(), PartPose.offset(0.0F, -2.0F, 0.0F));

        PartDefinition Head_r1 = Head.addOrReplaceChild("Head_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, layer.deformation), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create(), PartPose.offset(0.0F, -2.0F, 0.0F));

        PartDefinition Torso_r1 = Torso.addOrReplaceChild("Torso_r1", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, -12.0F, -2.0F, 8.0F, 12.0F, 4.0F, layer.dualDeformation.extend(0.2f)), PartPose.offsetAndRotation(0.0F, 13.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition Tail = Torso.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

        PartDefinition Base_r1 = Tail.addOrReplaceChild("Base_r1", CubeListBuilder.create().texOffs(1, 20).addBox(-1.5F, -1.3563F, -0.6088F, 3.0F, 5.0F, 3.0F, layer.altDeformation), PartPose.offsetAndRotation(0.0F, 4.5F, 9.25F, 1.309F, 0.0F, 0.0F));

        PartDefinition Base_r2 = Tail.addOrReplaceChild("Base_r2", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 8.75F, -0.8F, 4.0F, 0.0F, 4.0F, layer.altDeformation)
                .texOffs(0, 16).addBox(-2.0F, 0.75F, -0.8F, 4.0F, 8.0F, 4.0F, layer.altDeformation), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 1.1781F, 0.0F, 0.0F));

        PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create(), PartPose.offset(-5.0F, 0.0F, 0.0F));

        PartDefinition RightArm_r1 = RightArm.addOrReplaceChild("RightArm_r1", CubeListBuilder.create().texOffs(40, 16).addBox(-4.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, layer.deformation), PartPose.offsetAndRotation(1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create(), PartPose.offset(5.0F, 0.0F, 0.0F));

        PartDefinition LeftArm_r1 = LeftArm.addOrReplaceChild("LeftArm_r1", CubeListBuilder.create().texOffs(40, 16).mirror().addBox(0.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, layer.deformation).mirror(false), PartPose.offsetAndRotation(-1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 32);
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
            case LEGS -> {
                if (stack.getItem() instanceof Shorts) {
                    setAllPartsVisibility(LeftLeg, false);
                    setAllPartsVisibility(RightLeg, false);
                    LeftLeg.getChild("LeftUpperLeg_r1").visible = true;
                    RightLeg.getChild("RightUpperLeg_r1").visible = true;
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
    }

    @Override
    public LatexAnimator<T, ArmorLatexSharkModel<T>> getAnimator() {
        return animator;
    }

    public static class RemodelMale<T extends LatexEntity> extends LatexHumanoidArmorModel<T, RemodelMale<T>> {
        public static final ModelLayerLocation INNER_ARMOR = ArmorModelLayerLocation.createInnerArmorLocation(Changed.modResource("armor_latex_shark_male")).get();
        public static final ModelLayerLocation OUTER_ARMOR = ArmorModelLayerLocation.createOuterArmorLocation(Changed.modResource("armor_latex_shark_male")).get();

        private final ModelPart Head;
        private final ModelPart Torso;
        private final ModelPart Tail;
        private final ModelPart LeftLeg;
        private final ModelPart RightLeg;
        private final ModelPart LeftArm;
        private final ModelPart RightArm;
        private final LatexAnimator<T, RemodelMale<T>> animator;

        public RemodelMale(ModelPart modelPart) {
            this.Head = modelPart.getChild("Head");
            this.Torso = modelPart.getChild("Torso");
            this.Tail = Torso.getChild("Tail");
            this.LeftLeg = modelPart.getChild("LeftLeg");
            this.RightLeg = modelPart.getChild("RightLeg");
            this.LeftArm = modelPart.getChild("LeftArm");
            this.RightArm = modelPart.getChild("RightArm");

            this.animator = LatexAnimator.of(this).addPreset(AnimatorPresets.sharkLike(Head, Torso, LeftArm, RightArm, Tail, List.of(), LeftLeg, RightLeg)).hipOffset(0.0f);
        }

        public static LayerDefinition createArmorLayer(ArmorModel layer) {
            MeshDefinition meshdefinition = new MeshDefinition();
            PartDefinition partdefinition = meshdefinition.getRoot();

            PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, layer.deformation), PartPose.offset(0.0F, 0.0F, 0.0F));

            PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, layer.dualDeformation), PartPose.offset(0.0F, 0.0F, 0.0F));

            PartDefinition Tail = Torso.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offset(0.0F, 10.75F, -0.25F));

            PartDefinition Base_r1 = Tail.addOrReplaceChild("Base_r1", CubeListBuilder.create().texOffs(1, 20).addBox(-1.5F, -1.3563F, -0.6088F, 3.0F, 5.0F, 3.0F, layer.altDeformation), PartPose.offsetAndRotation(0.0F, 4.5F, 9.25F, 1.309F, 0.0F, 0.0F));

            PartDefinition Base_r2 = Tail.addOrReplaceChild("Base_r2", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.75F, -0.8F, 4.0F, 8.0F, 4.0F, layer.altDeformation), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 1.1781F, 0.0F, 0.0F));

            PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(40, 16).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, layer.deformation), PartPose.offset(-5.0F, 2.0F, 0.0F));

            PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(40, 16).mirror().addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, layer.deformation).mirror(false), PartPose.offset(5.0F, 2.0F, 0.0F));

            PartDefinition RightLeg = partdefinition.addOrReplaceChild("RightLeg", CubeListBuilder.create(), PartPose.offset(-2.25F, 12.0F, 0.0F));

            PartDefinition leg_r1 = RightLeg.addOrReplaceChild("leg_r1", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-2.0F, -6.7987F, -2.9677F, 4.0F, 6.0F, 4.0F, layer.altDeformation).mirror(false), PartPose.offsetAndRotation(0.0F, 5.275F, 4.9F, 1.2217F, 0.0F, 0.0F));

            PartDefinition thigh_r1 = RightLeg.addOrReplaceChild("thigh_r1", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, layer.altDeformation.extend(0.05F)), PartPose.offsetAndRotation(0.0F, -0.1F, 0.0F, -0.0873F, 0.0F, 0.0F));

            PartDefinition RightLower = RightLeg.addOrReplaceChild("RightLower", CubeListBuilder.create(), PartPose.offset(0.0F, 4.0F, 4.5F));

            PartDefinition leg_r2 = RightLower.addOrReplaceChild("leg_r2", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, -0.225F, -3.0F, 4.0F, 8.0F, 4.0F, layer.dualDeformation.extend(0.025F)), PartPose.offsetAndRotation(0.0F, 0.15F, -1.0F, -0.0873F, 0.0F, 0.0F));

            PartDefinition LeftLeg = partdefinition.addOrReplaceChild("LeftLeg", CubeListBuilder.create(), PartPose.offset(2.25F, 12.0F, 0.0F));

            PartDefinition leg_r3 = LeftLeg.addOrReplaceChild("leg_r3", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-2.0F, -6.7987F, -2.9677F, 4.0F, 6.0F, 4.0F, layer.altDeformation).mirror(false), PartPose.offsetAndRotation(0.0F, 5.275F, 4.9F, 1.2217F, 0.0F, 0.0F));

            PartDefinition thigh_r2 = LeftLeg.addOrReplaceChild("thigh_r2", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, layer.altDeformation.extend(0.05F)).mirror(false), PartPose.offsetAndRotation(0.0F, -0.1F, 0.0F, -0.0873F, 0.0F, 0.0F));

            PartDefinition LeftLower = LeftLeg.addOrReplaceChild("LeftLower", CubeListBuilder.create(), PartPose.offset(0.0F, 4.0F, 4.5F));

            PartDefinition leg_r4 = LeftLower.addOrReplaceChild("leg_r4", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-2.0F, -0.225F, -3.0F, 4.0F, 8.0F, 4.0F, layer.dualDeformation.extend(0.025F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.15F, -1.0F, -0.0873F, 0.0F, 0.0F));

            return LayerDefinition.create(meshdefinition, 64, 32);
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
                case LEGS -> {
                    if (stack.getItem() instanceof Shorts) {
                        setAllPartsVisibility(LeftLeg, false);
                        setAllPartsVisibility(RightLeg, false);
                        LeftLeg.getChild("thigh_r2").visible = true;
                        RightLeg.getChild("thigh_r1").visible = true;
                        LeftLeg.visible = true;
                        RightLeg.visible = true;
                        Tail.visible = false;
                    }

                    Torso.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
                    LeftLeg.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
                    RightLeg.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);

                    if (stack.getItem() instanceof Shorts) {
                        setAllPartsVisibility(LeftLeg, true);
                        setAllPartsVisibility(RightLeg, true);
                        Tail.visible = true;
                    }
                }
                case FEET -> {
                    LeftLeg.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
                    RightLeg.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
                }
            }
        }

        @Override
        public LatexAnimator<T, RemodelMale<T>> getAnimator() {
            return animator;
        }
    }

    public static class RemodelFemale<T extends LatexEntity> extends LatexHumanoidArmorModel<T, RemodelFemale<T>> {
        public static final ModelLayerLocation INNER_ARMOR = ArmorModelLayerLocation.createInnerArmorLocation(Changed.modResource("armor_latex_shark_female")).get();
        public static final ModelLayerLocation OUTER_ARMOR = ArmorModelLayerLocation.createOuterArmorLocation(Changed.modResource("armor_latex_shark_female")).get();

        private final ModelPart Head;
        private final ModelPart Torso;
        private final ModelPart Tail;
        private final ModelPart LeftLeg;
        private final ModelPart RightLeg;
        private final ModelPart LeftArm;
        private final ModelPart RightArm;
        private final LatexAnimator<T, RemodelFemale<T>> animator;

        public RemodelFemale(ModelPart modelPart) {
            this.Head = modelPart.getChild("Head");
            this.Torso = modelPart.getChild("Torso");
            this.Tail = Torso.getChild("Tail");
            this.LeftLeg = modelPart.getChild("LeftLeg");
            this.RightLeg = modelPart.getChild("RightLeg");
            this.LeftArm = modelPart.getChild("LeftArm");
            this.RightArm = modelPart.getChild("RightArm");

            this.animator = LatexAnimator.of(this).addPreset(AnimatorPresets.sharkLike(Head, Torso, LeftArm, RightArm, Tail, List.of(), LeftLeg, RightLeg)).hipOffset(0.0f);
        }

        public static LayerDefinition createArmorLayer(ArmorModel layer) {
            MeshDefinition meshdefinition = new MeshDefinition();
            PartDefinition partdefinition = meshdefinition.getRoot();

            PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, layer.deformation), PartPose.offset(0.0F, 0.0F, 0.0F));

            PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, layer.dualDeformation), PartPose.offset(0.0F, 0.0F, 0.0F));

            PartDefinition Tail = Torso.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offset(0.0F, 10.75F, -0.25F));

            PartDefinition Base_r1 = Tail.addOrReplaceChild("Base_r1", CubeListBuilder.create().texOffs(1, 20).addBox(-1.5F, -1.3563F, -0.6088F, 3.0F, 5.0F, 3.0F, layer.altDeformation), PartPose.offsetAndRotation(0.0F, 4.5F, 9.25F, 1.309F, 0.0F, 0.0F));

            PartDefinition Base_r2 = Tail.addOrReplaceChild("Base_r2", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.75F, -0.8F, 4.0F, 8.0F, 4.0F, layer.altDeformation), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 1.1781F, 0.0F, 0.0F));

            PartDefinition Breasts = Torso.addOrReplaceChild("Breasts", CubeListBuilder.create().texOffs(18, 18).addBox(-4.0F, -2.75F, -1.0F, 8.0F, 3.0F, 2.0F, layer.deformation.extend(-0.5f))
                    .texOffs(18, 22).addBox(-4.0F, 1.25F, -1.0F, 8.0F, 1.0F, 2.0F, layer.deformation.extend(-0.5f)), PartPose.offsetAndRotation(0.0F, 2.5F, -2.0F, -0.4363F, 0.0F, 0.0F));

            PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(40, 16).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, layer.deformation), PartPose.offset(-5.0F, 2.0F, 0.0F));

            PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(40, 16).mirror().addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, layer.deformation).mirror(false), PartPose.offset(5.0F, 2.0F, 0.0F));

            PartDefinition RightLeg = partdefinition.addOrReplaceChild("RightLeg", CubeListBuilder.create(), PartPose.offset(-2.25F, 12.0F, 0.0F));

            PartDefinition leg_r1 = RightLeg.addOrReplaceChild("leg_r1", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-2.0F, -6.7987F, -2.9677F, 4.0F, 6.0F, 4.0F, layer.altDeformation).mirror(false), PartPose.offsetAndRotation(0.0F, 5.275F, 4.9F, 1.2217F, 0.0F, 0.0F));

            PartDefinition thigh_r1 = RightLeg.addOrReplaceChild("thigh_r1", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, layer.altDeformation.extend(0.05F)), PartPose.offsetAndRotation(0.0F, -0.1F, 0.0F, -0.0873F, 0.0F, 0.0F));

            PartDefinition RightLower = RightLeg.addOrReplaceChild("RightLower", CubeListBuilder.create(), PartPose.offset(0.0F, 4.0F, 4.5F));

            PartDefinition leg_r2 = RightLower.addOrReplaceChild("leg_r2", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, -0.225F, -3.0F, 4.0F, 8.0F, 4.0F, layer.dualDeformation.extend(0.025F)), PartPose.offsetAndRotation(0.0F, 0.15F, -1.0F, -0.0873F, 0.0F, 0.0F));

            PartDefinition LeftLeg = partdefinition.addOrReplaceChild("LeftLeg", CubeListBuilder.create(), PartPose.offset(2.25F, 12.0F, 0.0F));

            PartDefinition leg_r3 = LeftLeg.addOrReplaceChild("leg_r3", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-2.0F, -6.7987F, -2.9677F, 4.0F, 6.0F, 4.0F, layer.altDeformation).mirror(false), PartPose.offsetAndRotation(0.0F, 5.275F, 4.9F, 1.2217F, 0.0F, 0.0F));

            PartDefinition thigh_r2 = LeftLeg.addOrReplaceChild("thigh_r2", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, layer.altDeformation.extend(0.05F)).mirror(false), PartPose.offsetAndRotation(0.0F, -0.1F, 0.0F, -0.0873F, 0.0F, 0.0F));

            PartDefinition LeftLower = LeftLeg.addOrReplaceChild("LeftLower", CubeListBuilder.create(), PartPose.offset(0.0F, 4.0F, 4.5F));

            PartDefinition leg_r4 = LeftLower.addOrReplaceChild("leg_r4", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-2.0F, -0.225F, -3.0F, 4.0F, 8.0F, 4.0F, layer.dualDeformation.extend(0.025F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.15F, -1.0F, -0.0873F, 0.0F, 0.0F));

            return LayerDefinition.create(meshdefinition, 64, 32);
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
                case LEGS -> {
                    if (stack.getItem() instanceof Shorts) {
                        setAllPartsVisibility(LeftLeg, false);
                        setAllPartsVisibility(RightLeg, false);
                        LeftLeg.getChild("thigh_r2").visible = true;
                        RightLeg.getChild("thigh_r1").visible = true;
                        LeftLeg.visible = true;
                        RightLeg.visible = true;
                        Tail.visible = false;
                    }

                    Torso.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
                    LeftLeg.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
                    RightLeg.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);

                    if (stack.getItem() instanceof Shorts) {
                        setAllPartsVisibility(LeftLeg, true);
                        setAllPartsVisibility(RightLeg, true);
                        Tail.visible = true;
                    }
                }
                case FEET -> {
                    LeftLeg.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
                    RightLeg.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
                }
            }
        }

        @Override
        public LatexAnimator<T, RemodelFemale<T>> getAnimator() {
            return animator;
        }
    }
}
