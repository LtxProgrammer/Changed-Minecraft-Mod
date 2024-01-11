package net.ltxprogrammer.changed.client.renderer.model.armor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.client.renderer.LatexHumanoidRenderer;
import net.ltxprogrammer.changed.client.renderer.animate.LatexAnimator;
import net.ltxprogrammer.changed.client.renderer.model.LatexHumanoidModelInterface;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.item.Shorts;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public abstract class LatexHumanoidArmorModel<T extends LatexEntity, M extends EntityModel<T>> extends EntityModel<T> implements LatexHumanoidModelInterface<T, M> {
    public abstract void renderForSlot(T entity, ItemStack stack, EquipmentSlot slot,
            PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha);

    protected static void prepareLegsForArmor(ItemStack stack, ModelPart LeftLeg, ModelPart RightLeg) {
        prepareLegsForArmor(stack, LeftLeg, RightLeg, null);
    }

    protected static void prepareLegsForArmor(ItemStack stack, ModelPart LeftLeg, ModelPart RightLeg, @Nullable ModelPart Tail) {
        if (stack.getItem() instanceof Shorts) {
            setAllPartsVisibility(LeftLeg.getChild("LeftLowerLeg"), false);
            setAllPartsVisibility(RightLeg.getChild("RightLowerLeg"), false);
            if (Tail != null)
                Tail.visible = false;
        }
    }

    protected static void unprepareLegsForArmor(ItemStack stack, ModelPart LeftLeg, ModelPart RightLeg) {
        unprepareLegsForArmor(stack, LeftLeg, RightLeg, null);
    }

    protected static void unprepareLegsForArmor(ItemStack stack, ModelPart LeftLeg, ModelPart RightLeg, @Nullable ModelPart Tail) {
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

    protected static void addV2Legs(PartDefinition partDefinition, ArmorModel layer) {
        PartDefinition RightLeg = partDefinition.addOrReplaceChild("RightLeg", CubeListBuilder.create(), PartPose.offset(-2.25F, 12.0F, 0.0F));

        PartDefinition leg_r1 = RightLeg.addOrReplaceChild("leg_r1", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-2.0F, -6.7987F, -2.9677F, 4.0F, 6.0F, 4.0F, layer.altDeformation).mirror(false), PartPose.offsetAndRotation(0.0F, 5.275F, 4.9F, 1.2217F, 0.0F, 0.0F));

        PartDefinition thigh_r1 = RightLeg.addOrReplaceChild("thigh_r1", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, layer.altDeformation.extend(0.05F)), PartPose.offsetAndRotation(0.0F, -0.1F, 0.0F, -0.0873F, 0.0F, 0.0F));

        PartDefinition RightLower = RightLeg.addOrReplaceChild("RightLower", CubeListBuilder.create(), PartPose.offset(0.0F, 4.0F, 4.5F));

        PartDefinition leg_r2 = RightLower.addOrReplaceChild("leg_r2", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 7.775F, -3.0F, 4.0F, 0.0F, 4.0F, layer.dualDeformation.extend(0.025F))
                .texOffs(0, layer == ArmorModel.OUTER ? 20 : 16).addBox(-2.0F, -0.225F, -3.0F, 4.0F, 8.0F, 4.0F, layer.dualDeformation.extend(0.025F)), PartPose.offsetAndRotation(0.0F, 0.15F, -1.0F, -0.0873F, 0.0F, 0.0F));

        PartDefinition LeftLeg = partDefinition.addOrReplaceChild("LeftLeg", CubeListBuilder.create(), PartPose.offset(2.25F, 12.0F, 0.0F));

        PartDefinition leg_r3 = LeftLeg.addOrReplaceChild("leg_r3", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-2.0F, -6.7987F, -2.9677F, 4.0F, 6.0F, 4.0F, layer.altDeformation).mirror(false), PartPose.offsetAndRotation(0.0F, 5.275F, 4.9F, 1.2217F, 0.0F, 0.0F));

        PartDefinition thigh_r2 = LeftLeg.addOrReplaceChild("thigh_r2", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, layer.altDeformation.extend(0.05F)).mirror(false), PartPose.offsetAndRotation(0.0F, -0.1F, 0.0F, -0.0873F, 0.0F, 0.0F));

        PartDefinition LeftLower = LeftLeg.addOrReplaceChild("LeftLower", CubeListBuilder.create(), PartPose.offset(0.0F, 4.0F, 4.5F));

        PartDefinition leg_r4 = LeftLower.addOrReplaceChild("leg_r4", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-2.0F, 7.775F, -3.0F, 4.0F, 0.0F, 4.0F, layer.dualDeformation.extend(0.025F)).mirror(false)
                .texOffs(0, layer == ArmorModel.OUTER ? 20 : 16).mirror().addBox(-2.0F, -0.225F, -3.0F, 4.0F, 8.0F, 4.0F, layer.dualDeformation.extend(0.025F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.15F, -1.0F, -0.0873F, 0.0F, 0.0F));
    }

    protected float distanceTo(@NotNull T entity, @NotNull Entity other, float partialTicks) {
        Vec3 entityPos = entity.getPosition(partialTicks);
        Vec3 otherPos = other.getPosition(partialTicks);
        float f = (float)(entityPos.x - otherPos.x);
        float f1 = (float)(entityPos.y - otherPos.y);
        float f2 = (float)(entityPos.z - otherPos.z);
        return Mth.sqrt(f * f + f1 * f1 + f2 * f2);
    }

    public void prepareMobModel(LatexAnimator<T, M> animator, T entity, float p_102862_, float p_102863_, float partialTicks) {
        super.prepareMobModel(entity, p_102862_, p_102863_, partialTicks);
        animator.setupVariables(entity, partialTicks);
    }

    @Override
    public void prepareMobModel(@NotNull T entity, float p_102862_, float p_102863_, float partialTicks) {
        this.prepareMobModel(getAnimator(), entity, p_102862_, p_102863_, partialTicks);
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        getAnimator().setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    }

    public static void setAllPartsVisibility(ModelPart part, boolean visible) {
        part.getAllParts().forEach(modelPart -> modelPart.visible = visible);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {}

    @Override
    public final void setupHand() {

    }

    @Override
    public final ModelPart getArm(HumanoidArm arm) {
        return null;
    }
}
