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
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Map;

public class ArmorSnakeAbdomenModel<T extends ChangedEntity> extends LatexHumanoidArmorModel<T, ArmorSnakeAbdomenModel<T>> implements LeglessModel {
    public static final ArmorModelSet<ChangedEntity, ArmorSnakeAbdomenModel<ChangedEntity>> MODEL_SET =
            ArmorModelSet.of(Changed.modResource("armor_snake_abdomen"), ArmorSnakeAbdomenModel::createArmorLayer, ArmorSnakeAbdomenModel::new);

    private final ModelPart Abdomen;
    private final ModelPart LowerAbdomen;
    private final ModelPart Tail;
    private final HumanoidAnimator<T, ArmorSnakeAbdomenModel<T>> animator;

    public ArmorSnakeAbdomenModel(ModelPart modelPart, ArmorModel model) {
        super(modelPart, model);
        this.Abdomen = modelPart.getChild("Abdomen");
        this.LowerAbdomen = Abdomen.getChild("LowerAbdomen");
        this.Tail = LowerAbdomen.getChild("Tail");

        var tailPrimary = Tail.getChild("TailPrimary");
        var tailSecondary = tailPrimary.getChild("TailSecondary");
        var tailTertiary = tailSecondary.getChild("TailTertiary");

        animator = HumanoidAnimator.of(this).hipOffset(-1.5f).torsoLength(9.0f).legLength(9.5f)
                .addPreset(AnimatorPresets.snakeAbdomenArmor(
                        Abdomen, LowerAbdomen,
                        Tail, List.of(tailPrimary, tailSecondary, tailTertiary)));
    }

    public static LayerDefinition createArmorLayer(ArmorModel layer) {
        final float deformationOffset = -0.25F;
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition Abdomen = partdefinition.addOrReplaceChild("Abdomen", CubeListBuilder.create().texOffs(40, 7).addBox(-4.0F, 0.5F, -2.0F, 8.0F, 4.0F, 4.0F, layer.deformation.extend(0.05F)), PartPose.offset(0.0F, 8.0F, 0.0F));

        PartDefinition LowerAbdomen = Abdomen.addOrReplaceChild("LowerAbdomen", CubeListBuilder.create().texOffs(0, 20).mirror().addBox(-4.0F, 0.75F, -2.5F, 8.0F, 6.0F, 4.0F, layer.deformation.extend(0.14F)), PartPose.offset(0.0F, 2.75F, 0.5F));

        PartDefinition Tail = LowerAbdomen.addOrReplaceChild("Tail", CubeListBuilder.create().texOffs(0, 20).addBox(-4.0F, 0.25F, -3.0F, 8.0F, 6.0F, 4.0F, layer.altDeformation.extend(0.21F)), PartPose.offset(0.0F, 5.5F, 0.5F));

        PartDefinition TailPrimary = Tail.addOrReplaceChild("TailPrimary", CubeListBuilder.create().texOffs(40, 21).addBox(-3.5F, 0.25F, -3.0F, 7.0F, 5.0F, 4.0F, layer.altDeformation.extend(0.07F)), PartPose.offset(0.0F, 6.0F, 0.0F));

        PartDefinition TailSecondary = TailPrimary.addOrReplaceChild("TailSecondary", CubeListBuilder.create().texOffs(24, 16).addBox(-3.0F, 0.25F, -3.0F, 6.0F, 5.0F, 4.0F, layer.altDeformation.extend(-0.05F)), PartPose.offset(0.0F, 5.0F, 0.0F));

        PartDefinition TailTertiary = TailSecondary.addOrReplaceChild("TailTertiary", CubeListBuilder.create().texOffs(1, 23).addBox(-2.0F, 0.25F, -2.5F, 4.0F, 5.0F, 3.0F, layer.altDeformation.extend(0.03F)), PartPose.offset(0.0F, 5.0F, 0.0F));

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
            case LEGS, FEET -> Abdomen.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
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
