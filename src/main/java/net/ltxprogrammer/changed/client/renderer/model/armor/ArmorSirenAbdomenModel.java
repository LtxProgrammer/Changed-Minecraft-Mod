package net.ltxprogrammer.changed.client.renderer.model.armor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.animate.AnimatorPresets;
import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
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

import java.util.List;

public class ArmorSirenAbdomenModel<T extends ChangedEntity> extends LatexHumanoidArmorModel<T, ArmorSirenAbdomenModel<T>> {
    public static final ModelLayerLocation INNER_ARMOR = ArmorModelLayerLocation.createInnerArmorLocation(Changed.modResource("armor_siren_abdomen")).get();
    public static final ModelLayerLocation OUTER_ARMOR = ArmorModelLayerLocation.createOuterArmorLocation(Changed.modResource("armor_siren_abdomen")).get();

    private final ModelPart Abdomen;
    private final ModelPart LowerAbdomen;
    private final ModelPart Tail;
    private final ModelPart TailPrimary;
    private final HumanoidAnimator<T, ArmorSirenAbdomenModel<T>> animator;

    public ArmorSirenAbdomenModel(ModelPart root) {
        this.Abdomen = root.getChild("Abdomen");
        this.LowerAbdomen = Abdomen.getChild("LowerAbdomen");
        this.Tail = LowerAbdomen.getChild("Tail");
        this.TailPrimary = Tail.getChild("TailPrimary");

        this.animator = HumanoidAnimator.of(this).hipOffset(-1.5f).torsoLength(9.0f).legLength(9.5f)
                .addPreset(AnimatorPresets.leglessV2(Abdomen, LowerAbdomen, Tail, List.of(
                        TailPrimary,
                        TailPrimary.getChild("TailSecondary"),
                        TailPrimary.getChild("TailSecondary").getChild("TailTertiary"),
                        TailPrimary.getChild("TailSecondary").getChild("TailTertiary").getChild("TailQuaternary"))));
    }

    public static LayerDefinition createArmorLayer(ArmorModel layer) {
        final float deformationOffset = -0.25f;
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition Abdomen = partdefinition.addOrReplaceChild("Abdomen", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, 1.5F, -2.5F, 8.0F, 2.0F, 5.0F, layer.deformation.extend(0.2F + deformationOffset)), PartPose.offset(0.0F, 8.0F, 0.0F));

        PartDefinition LowerAbdomen = Abdomen.addOrReplaceChild("LowerAbdomen", CubeListBuilder.create().texOffs(0, 7).addBox(-4.5F, -1.25F, -3.0F, 9.0F, 7.0F, 6.0F, layer.deformation.extend(-0.14F + deformationOffset)), PartPose.offset(0.0F, 4.25F, 0.0F));

        PartDefinition Tail = LowerAbdomen.addOrReplaceChild("Tail", CubeListBuilder.create().texOffs(0, 20).addBox(-4.5F, -0.25F, -3.0F, 9.0F, 6.0F, 6.0F, layer.dualDeformation.extend(-0.45F + deformationOffset)), PartPose.offset(0.0F, 5.5F, 0.0F));

        PartDefinition TailPrimary = Tail.addOrReplaceChild("TailPrimary", CubeListBuilder.create().texOffs(30, 0).addBox(-3.5F, -0.25F, -2.5F, 7.0F, 5.0F, 5.0F, layer.dualDeformation.extend(deformationOffset)), PartPose.offset(0.0F, 4.5F, 0.0F));

        PartDefinition TailSecondary = TailPrimary.addOrReplaceChild("TailSecondary", CubeListBuilder.create().texOffs(30, 12).addBox(-2.5F, -0.25F, -2.0F, 5.0F, 5.0F, 4.0F, layer.dualDeformation.extend(deformationOffset)), PartPose.offset(0.0F, 4.25F, 0.0F));

        PartDefinition TailTertiary = TailSecondary.addOrReplaceChild("TailTertiary", CubeListBuilder.create().texOffs(47, 21).addBox(-2.0F, -0.25F, -1.5F, 4.0F, 3.0F, 3.0F, layer.dualDeformation.extend(deformationOffset)), PartPose.offset(0.0F, 4.5F, 0.0F));

        PartDefinition TailQuaternary = TailTertiary.addOrReplaceChild("TailQuaternary", CubeListBuilder.create().texOffs(48, 21).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 4.0F, 3.0F, layer.dualDeformation.extend(-0.05F + deformationOffset)), PartPose.offset(0.0F, 2.55F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    @Override
    public HumanoidAnimator<T, ArmorSirenAbdomenModel<T>> getAnimator() {
        return animator;
    }

    @Override
    public void renderForSlot(T entity, ItemStack stack, EquipmentSlot slot, PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        switch (slot) {
            case LEGS -> {
                setAllPartsVisibility(TailPrimary, false);
                //Torso.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
                Abdomen.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
                setAllPartsVisibility(TailPrimary, true);
            }
            case FEET -> Abdomen.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        }
    }
}
