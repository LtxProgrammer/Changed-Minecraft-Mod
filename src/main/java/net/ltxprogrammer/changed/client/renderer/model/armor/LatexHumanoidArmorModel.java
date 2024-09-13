package net.ltxprogrammer.changed.client.renderer.model.armor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModelInterface;
import net.ltxprogrammer.changed.client.tfanimations.HelperModel;
import net.ltxprogrammer.changed.client.tfanimations.Limb;
import net.ltxprogrammer.changed.client.tfanimations.TransfurHelper;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.item.Shorts;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public abstract class LatexHumanoidArmorModel<T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> extends AdvancedHumanoidModel<T> implements AdvancedHumanoidModelInterface<T, M> {
    public static final ModelPart EMPTY_PART = new ModelPart(List.of(), Map.of());
    public final ArmorModel armorModel;

    public LatexHumanoidArmorModel(ModelPart root, ArmorModel model) {
        super(root);
        this.armorModel = model;
    }

    public void scaleForSlot(RenderLayerParent<T, ?> parent, EquipmentSlot slot, PoseStack poseStack) {
        switch (slot) {
            case HEAD -> {
                if (parent.getModel() instanceof AdvancedHumanoidModelInterface<?,?> modelInterface)
                    modelInterface.scaleForHead(poseStack);
            }
            case CHEST, LEGS, FEET -> {
                if (parent.getModel() instanceof AdvancedHumanoidModelInterface<?,?> modelInterface)
                    modelInterface.scaleForBody(poseStack);
            }
        }
    }

    public abstract void renderForSlot(T entity, RenderLayerParent<T, ?> parent, ItemStack stack, EquipmentSlot slot,
                                       PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha);

    public static void prepareUnifiedLegsForArmor(ItemStack stack, ModelPart LeftLeg, ModelPart RightLeg) {
        prepareUnifiedLegsForArmor(stack, LeftLeg, RightLeg, null);
    }

    public static void prepareUnifiedLegsForArmor(ItemStack stack, ModelPart LeftLeg, ModelPart RightLeg, @Nullable ModelPart Tail) {
        if (stack.getItem() instanceof Shorts) {
            setAllPartsVisibility(LeftLeg.getChild("LeftLowerLeg"), false);
            setAllPartsVisibility(RightLeg.getChild("RightLowerLeg"), false);
            if (Tail != null)
                Tail.visible = false;
        } else {
            setAllPartsVisibility(LeftLeg, true);
            setAllPartsVisibility(RightLeg, true);
            if (Tail != null)
                Tail.visible = true;
        }
    }

    protected static void unprepareUnifiedLegsForArmor(ItemStack stack, ModelPart LeftLeg, ModelPart RightLeg) {
        unprepareUnifiedLegsForArmor(stack, LeftLeg, RightLeg, null);
    }

    protected static void unprepareUnifiedLegsForArmor(ItemStack stack, ModelPart LeftLeg, ModelPart RightLeg, @Nullable ModelPart Tail) {
        if (stack.getItem() instanceof Shorts) {
            setAllPartsVisibility(LeftLeg, true);
            setAllPartsVisibility(RightLeg, true);
            if (Tail != null)
                Tail.visible = true;
        }
    }

    protected static void addUnifiedLegs(PartDefinition partDefinition, ArmorModel layer) {
        PartDefinition RightLeg = partDefinition.addOrReplaceChild("RightLeg", CubeListBuilder.create(), PartPose.offset(-2.5F, 10.5F, 0.0F));

        PartDefinition RightThigh_r1 = RightLeg.addOrReplaceChild("RightThigh_r1", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, layer.altDeformation).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition RightLowerLeg = RightLeg.addOrReplaceChild("RightLowerLeg", CubeListBuilder.create(), PartPose.offset(0.0F, 6.375F, -3.45F));

        PartDefinition RightCalf_r1 = RightLowerLeg.addOrReplaceChild("RightCalf_r1", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-1.99F, -0.125F, -2.9F, 4.0F, 6.0F, 4.0F, layer.altDeformation).mirror(false), PartPose.offsetAndRotation(0.0F, -2.125F, 1.95F, 0.8727F, 0.0F, 0.0F));

        PartDefinition RightFoot = RightLowerLeg.addOrReplaceChild("RightFoot", CubeListBuilder.create(), PartPose.offset(0.0F, 0.8F, 7.175F));

        PartDefinition RightArch_r1 = RightFoot.addOrReplaceChild("RightArch_r1", CubeListBuilder.create().texOffs(2, 20).addBox(-2.0F, -8.45F, -0.725F, 4.0F, 6.0F, 3.0F, layer.altDeformation), PartPose.offsetAndRotation(0.0F, 7.075F, -4.975F, -0.3491F, 0.0F, 0.0F));

        PartDefinition RightPad = RightFoot.addOrReplaceChild("RightPad", CubeListBuilder.create().texOffs(21, 21).mirror().addBox(-2.0F, 0.0F, -2.5F, 4.0F, 2.0F, 5.0F, layer.deformation.extend(-0.25f)).mirror(false), PartPose.offset(0.0F, 4.325F, -4.425F));

        PartDefinition LeftLeg = partDefinition.addOrReplaceChild("LeftLeg", CubeListBuilder.create(), PartPose.offset(2.5F, 10.5F, 0.0F));

        PartDefinition LeftThigh_r1 = LeftLeg.addOrReplaceChild("LeftThigh_r1", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, layer.altDeformation), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition LeftLowerLeg = LeftLeg.addOrReplaceChild("LeftLowerLeg", CubeListBuilder.create(), PartPose.offset(0.0F, 6.375F, -3.45F));

        PartDefinition LeftCalf_r1 = LeftLowerLeg.addOrReplaceChild("LeftCalf_r1", CubeListBuilder.create().texOffs(0, 16).addBox(-2.01F, -0.125F, -2.9F, 4.0F, 6.0F, 4.0F, layer.altDeformation), PartPose.offsetAndRotation(0.0F, -2.125F, 1.95F, 0.8727F, 0.0F, 0.0F));

        PartDefinition LeftFoot = LeftLowerLeg.addOrReplaceChild("LeftFoot", CubeListBuilder.create(), PartPose.offset(0.0F, 0.8F, 7.175F));

        PartDefinition LeftArch_r1 = LeftFoot.addOrReplaceChild("LeftArch_r1", CubeListBuilder.create().texOffs(2, 20).mirror().addBox(-2.0F, -8.45F, -0.725F, 4.0F, 6.0F, 3.0F, layer.altDeformation).mirror(false), PartPose.offsetAndRotation(0.0F, 7.075F, -4.975F, -0.3491F, 0.0F, 0.0F));

        PartDefinition LeftPad = LeftFoot.addOrReplaceChild("LeftPad", CubeListBuilder.create().texOffs(21, 21).addBox(-2.0F, 0.0F, -2.5F, 4.0F, 2.0F, 5.0F, layer.deformation.extend(-0.25f)), PartPose.offset(0.0F, 4.325F, -4.425F));
    }

    protected float distanceTo(@NotNull T entity, @NotNull Entity other, float partialTicks) {
        Vec3 entityPos = entity.getPosition(partialTicks);
        Vec3 otherPos = other.getPosition(partialTicks);
        float f = (float)(entityPos.x - otherPos.x);
        float f1 = (float)(entityPos.y - otherPos.y);
        float f2 = (float)(entityPos.z - otherPos.z);
        return Mth.sqrt(f * f + f1 * f1 + f2 * f2);
    }

    @Override
    public void prepareMobModel(@NotNull T entity, float p_102862_, float p_102863_, float partialTicks) {
        this.prepareMobModel(getAnimator(), entity, p_102862_, p_102863_, partialTicks);
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        getAnimator().setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    }

    public static void setAllPartsVisibility(ModelPart part, boolean visible) {
        part.getAllParts().forEach(modelPart -> modelPart.visible = visible);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {}

    @Override
    public final void setupHand() {

    }

    @Nullable
    public HelperModel getTransfurHelperModel(Limb limb) {
        return switch (limb) {
            case LEFT_LEG -> TransfurHelper.getDigitigradeLeftLeg(this.armorModel);
            case RIGHT_LEG -> TransfurHelper.getDigitigradeRightLeg(this.armorModel);
            default -> null;
        };
    }

    public void prepareVisibility(EquipmentSlot armorSlot, ItemStack item) {}
    public void unprepareVisibility(EquipmentSlot armorSlot, ItemStack item) {}
}
