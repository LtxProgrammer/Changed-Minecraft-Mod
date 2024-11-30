package net.ltxprogrammer.changed.client.renderer.model.armor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.animate.AnimatorPresets;
import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.client.renderer.model.LeglessModel;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Map;

public class ArmorSnakeAbdomenModel<T extends ChangedEntity> extends LatexHumanoidArmorModel<T, ArmorSnakeAbdomenModel<T>> implements LeglessModel {
    public static final ArmorModelSet<ChangedEntity, ArmorSnakeAbdomenModel<ChangedEntity>> MODEL_SET =
            ArmorModelSet.of(Changed.modResource("armor_snake_abdomen"), ArmorSnakeAbdomenModel::createArmorLayer, ArmorSnakeAbdomenModel::new);
    public static final ModelPart EMPTY_PART = new ModelPart(List.of(), Map.of());

    private final ModelPart Torso;
    private final ModelPart Abdomen;
    private final ModelPart LowerAbdomen;
    private final ModelPart Tail;
    private final HumanoidAnimator<T, ArmorSnakeAbdomenModel<T>> animator;

    public ArmorSnakeAbdomenModel(ModelPart modelPart, ArmorModel model) {
        super(modelPart, model);
        this.Torso = modelPart.getChild("Torso");
        this.Abdomen = modelPart.getChild("Abdomen");
        this.LowerAbdomen = Abdomen.getChild("LowerAbdomen");
        this.Tail = LowerAbdomen.getChild("Tail");

        this.animator = HumanoidAnimator.of(this).addPreset(AnimatorPresets.legless(Abdomen, LowerAbdomen, Tail, List.of(
                        Tail.getChild("Joint"),
                        Tail.getChild("Joint").getChild("Joint2"))))
                .addPreset(AnimatorPresets.upperBody(EMPTY_PART, Torso, EMPTY_PART, EMPTY_PART));
    }

    public static LayerDefinition createArmorLayer(ArmorModel layer) {
        final float deformationOffset = -0.25F;
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition Abdomen = partdefinition.addOrReplaceChild("Abdomen", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 10.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition Base_r1 = Abdomen.addOrReplaceChild("Base_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, 0.734F, -1.8537F, 8.0F, 2.0F, 5.0F, layer.deformation.extend(0.3F + deformationOffset))
                .texOffs(0, 7).addBox(-4.5F, 2.6799F, -2.3597F, 9.0F, 7.0F, 6.0F, layer.deformation.extend(0.15F + deformationOffset)), PartPose.offsetAndRotation(0.0F, 0.0478F, -0.6297F, 0.0F, 0.0F, 0.0F));

        PartDefinition LowerAbdomen = Abdomen.addOrReplaceChild("LowerAbdomen", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 9.0F, -1.0F, 0.8727F, 0.0F, 0.0F));

        PartDefinition Base_r2 = LowerAbdomen.addOrReplaceChild("Base_r2", CubeListBuilder.create().texOffs(0, 20).addBox(-4.5F, 0.3384F, -3.0281F, 9.0F, 6.0F, 6.0F, layer.dualDeformation.extend(deformationOffset)), PartPose.offsetAndRotation(0.0F, -1.0025F, 1.0529F, 0.0F, 0.0F, 0.0F));

        PartDefinition Tail = LowerAbdomen.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 5.0F, 0.0F, 0.6981F, 0.0F, 0.0F));

        PartDefinition Base_r3 = Tail.addOrReplaceChild("Base_r3", CubeListBuilder.create().texOffs(30, 0).addBox(-3.5F, 3.8569F, -3.4336F, 7.0F, 7.0F, 5.0F, layer.altDeformation.extend(0.1F + deformationOffset)), PartPose.offsetAndRotation(0.0F, -5.0025F, 1.0529F, 0.2007F, 0.0F, 0.0F));

        PartDefinition Joint = Tail.addOrReplaceChild("Joint", CubeListBuilder.create(), PartPose.offset(0.0F, 5.8854F, 2.329F));

        PartDefinition Base_r4 = Joint.addOrReplaceChild("Base_r4", CubeListBuilder.create().texOffs(30, 12).addBox(-2.5F, 10.8541F, -2.9527F, 5.0F, 6.0F, 4.0F, layer.altDeformation.extend(0.35F + deformationOffset)), PartPose.offsetAndRotation(0.0F, -10.8879F, -1.2762F, 0.2007F, 0.0F, 0.0F));

        PartDefinition Joint2 = Joint.addOrReplaceChild("Joint2", CubeListBuilder.create(), PartPose.offset(0.0F, 6.2157F, 1.3003F));

        PartDefinition Base_r5 = Joint2.addOrReplaceChild("Base_r5", CubeListBuilder.create().texOffs(46, 20).addBox(-2.5F, 16.8531F, -2.848F, 5.0F, 8.0F, 4.0F, layer.altDeformation.extend(-0.15F + deformationOffset)), PartPose.offsetAndRotation(0.0F, -17.1036F, -2.5765F, 0.2007F, 0.0F, 0.0F));

        PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create(), PartPose.offset(0.0F, -2.0F, 0.0F));

        PartDefinition Torso_r1 = Torso.addOrReplaceChild("Torso_r1", CubeListBuilder.create().texOffs(40, 0).addBox(-4.0F, -12.0F, -2.0F, 8.0F, 12.0F, 4.0F, layer.deformation.extend(deformationOffset)), PartPose.offsetAndRotation(0.0F, 13.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    @Override
    public HumanoidAnimator<T, ArmorSnakeAbdomenModel<T>> getAnimator(T entity) {
        return animator;
    }

    @Override
    public void prepareVisibility(EquipmentSlot armorSlot, ItemStack item) {
        super.prepareVisibility(armorSlot, item);
        if (armorSlot == EquipmentSlot.LEGS) {
            setAllPartsVisibility(Tail, false);
        }
    }

    @Override
    public void unprepareVisibility(EquipmentSlot armorSlot, ItemStack item) {
        super.unprepareVisibility(armorSlot, item);
        if (armorSlot == EquipmentSlot.LEGS) {
            setAllPartsVisibility(Tail, true);
        }
    }

    @Override
    public void renderForSlot(T entity, RenderLayerParent<? super T, ?> parent, ItemStack stack, EquipmentSlot slot, PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        poseStack.pushPose();
        this.scaleForSlot(parent, slot, poseStack);

        switch (slot) {
            case LEGS -> {
                Torso.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
                Abdomen.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
            }
            case FEET -> Abdomen.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        }

        poseStack.popPose();
    }

    public ModelPart getArm(HumanoidArm arm) {
        return null;
    }

    public ModelPart getLeg(HumanoidArm leg) {
        return null;
    }

    public ModelPart getHead() {
        return NULL_PART;
    }

    public ModelPart getTorso() {
        return NULL_PART;
    }

    @Override
    public ModelPart getAbdomen() {
        return Abdomen;
    }
}
