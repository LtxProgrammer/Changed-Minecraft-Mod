package net.ltxprogrammer.changed.client.renderer.model.armor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.client.CubeListBuilderExtender;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.client.tfanimations.HelperModel;
import net.ltxprogrammer.changed.client.animations.Limb;
import net.ltxprogrammer.changed.client.tfanimations.TransfurHelper;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.item.Clothing;
import net.ltxprogrammer.changed.item.Shorts;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public abstract class LatexHumanoidArmorModel<T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> extends AdvancedHumanoidModel<T> {
    public static final ModelPart EMPTY_PART = new ModelPart(List.of(), Map.of());
    public final ArmorModel armorModel;

    /**
     * Represents a deformation that hides the cube.
     * CubeListBuilderMixin will not add cubes that use this deformation,
     * saving memory and render steps on geometry that shouldn't be visible anyway.
     * It overrides extend() so that existing armor model code is compatible.
     */
    public static final CubeDeformation HIDDEN_CUBE = new CubeDeformation(0.0f) {
        @Override
        public @NotNull CubeDeformation extend(float scale) {
            return this;
        }

        @Override
        public @NotNull CubeDeformation extend(float x, float y, float z) {
            return this;
        }
    };

    public LatexHumanoidArmorModel(ModelPart root, ArmorModel model) {
        super(root);
        this.armorModel = model;
    }

    public void scaleForSlot(RenderLayerParent<? super T, ?> parent, EquipmentSlot slot, PoseStack poseStack) {
        switch (slot) {
            case HEAD -> {
                if (parent.getModel() instanceof AdvancedHumanoidModel<?> model)
                    model.scaleForHead(poseStack);
            }
            case CHEST, LEGS, FEET -> {
                if (parent.getModel() instanceof AdvancedHumanoidModel<?> model)
                    model.scaleForBody(poseStack);
            }
        }
    }

    public abstract void renderForSlot(T entity, RenderLayerParent<? super T, ?> parent, ItemStack stack, EquipmentSlot slot,
                                       PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha);

    public static void prepareUnifiedLegsForArmor(ItemStack stack, ModelPart LeftLeg, ModelPart RightLeg) {
        setAllPartsVisibility(LeftLeg, true);
        setAllPartsVisibility(RightLeg, true);

        if (stack.getItem() instanceof Shorts) {
            setAllPartsVisibility(LeftLeg.getChild("LeftLowerLeg"), false);
            setAllPartsVisibility(RightLeg.getChild("RightLowerLeg"), false);
        } else if (stack.getItem() instanceof Clothing) {
            setAllPartsVisibility(LeftLeg.getChild("LeftLowerLeg").getChild("LeftFoot"), false);
            setAllPartsVisibility(RightLeg.getChild("RightLowerLeg").getChild("RightFoot"), false);
        }
    }

    public static void prepareUnifiedLegsForArmor(ItemStack stack, ModelPart LeftLeg, ModelPart RightLeg, ModelPart Tail) {
        prepareUnifiedLegsForArmor(stack, LeftLeg, RightLeg);

        if (stack.getItem() instanceof Shorts) {
            Tail.visible = false;
        } else if (stack.getItem() instanceof Clothing) {
            Tail.visible = false;
        } else {
            Tail.visible = true;
        }
    }

    public static void addUnifiedLegs(PartDefinition partDefinition, ArmorModel layer) {
        int calfUVy = switch (layer) {
            case CLOTHING_INNER, CLOTHING_MIDDLE, ARMOR_INNER -> 20;
            default -> 16;
        };

        PartDefinition RightLeg = partDefinition.addOrReplaceChild("RightLeg", CubeListBuilder.create(), PartPose.offset(-2.5F, 10.5F, 0.0F));

        PartDefinition RightThigh_r1 = RightLeg.addOrReplaceChild("RightThigh_r1", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, layer.altDeformation), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition RightLowerLeg = RightLeg.addOrReplaceChild("RightLowerLeg", CubeListBuilder.create(), PartPose.offset(0.0F, 6.375F, -3.45F));

        PartDefinition RightCalf_r1 = RightLowerLeg.addOrReplaceChild("RightCalf_r1",
                ((CubeListBuilderExtender)CubeListBuilder.create().texOffs(0, calfUVy).addBox(-1.99F, -0.125F, -2.9F, 4.0F, 6.0F, 4.0F, layer.altDeformation))
                        .removeLastFaces(Direction.UP, Direction.DOWN).finish(), PartPose.offsetAndRotation(0.0F, -2.125F, 1.95F, 0.8727F, 0.0F, 0.0F));

        PartDefinition RightFoot = RightLowerLeg.addOrReplaceChild("RightFoot", CubeListBuilder.create(), PartPose.offset(0.0F, 0.8F, 7.175F));

        PartDefinition RightArch_r1 = RightFoot.addOrReplaceChild("RightArch_r1", CubeListBuilder.create().texOffs(2, 20).addBox(-2.0F, -8.45F, -0.725F, 4.0F, 6.0F, 3.0F, layer.altDeformation), PartPose.offsetAndRotation(0.0F, 7.075F, -4.975F, -0.3491F, 0.0F, 0.0F));

        PartDefinition RightPad = RightFoot.addOrReplaceChild("RightPad", CubeListBuilder.create().texOffs(21, 21).addBox(-2.0F, 0.0F, -2.5F, 4.0F, 2.0F, 5.0F, layer.deformation.extend(-0.25f)), PartPose.offset(0.0F, 4.325F, -4.425F));

        PartDefinition LeftLeg = partDefinition.addOrReplaceChild("LeftLeg", CubeListBuilder.create(), PartPose.offset(2.5F, 10.5F, 0.0F));

        PartDefinition LeftThigh_r1 = LeftLeg.addOrReplaceChild("LeftThigh_r1", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, layer.altDeformation).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition LeftLowerLeg = LeftLeg.addOrReplaceChild("LeftLowerLeg", CubeListBuilder.create(), PartPose.offset(0.0F, 6.375F, -3.45F));

        PartDefinition LeftCalf_r1 = LeftLowerLeg.addOrReplaceChild("LeftCalf_r1",
                ((CubeListBuilderExtender)CubeListBuilder.create().texOffs(0, calfUVy).mirror().addBox(-2.01F, -0.125F, -2.9F, 4.0F, 6.0F, 4.0F, layer.altDeformation).mirror(false))
                        .removeLastFaces(Direction.UP, Direction.DOWN).finish(), PartPose.offsetAndRotation(0.0F, -2.125F, 1.95F, 0.8727F, 0.0F, 0.0F));

        PartDefinition LeftFoot = LeftLowerLeg.addOrReplaceChild("LeftFoot", CubeListBuilder.create(), PartPose.offset(0.0F, 0.8F, 7.175F));

        PartDefinition LeftArch_r1 = LeftFoot.addOrReplaceChild("LeftArch_r1", CubeListBuilder.create().texOffs(2, 20).mirror().addBox(-2.0F, -8.45F, -0.725F, 4.0F, 6.0F, 3.0F, layer.altDeformation).mirror(false), PartPose.offsetAndRotation(0.0F, 7.075F, -4.975F, -0.3491F, 0.0F, 0.0F));

        PartDefinition LeftPad = LeftFoot.addOrReplaceChild("LeftPad", CubeListBuilder.create().texOffs(21, 21).mirror().addBox(-2.0F, 0.0F, -2.5F, 4.0F, 2.0F, 5.0F, layer.deformation.extend(-0.25f)).mirror(false), PartPose.offset(0.0F, 4.325F, -4.425F));
    }

    protected static void addUnifiedBirdLegs(PartDefinition partDefinition, ArmorModel layer) {
        int calfUVy = switch (layer) {
            case CLOTHING_INNER, CLOTHING_MIDDLE, ARMOR_INNER -> 21;
            default -> 17;
        };

        PartDefinition RightLeg = partDefinition.addOrReplaceChild("RightLeg", CubeListBuilder.create(), PartPose.offset(-2.5F, 10.5F, 0.0F));

        PartDefinition RightThigh_r1 = RightLeg.addOrReplaceChild("RightThigh_r1", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, layer.altDeformation), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition RightLowerLeg = RightLeg.addOrReplaceChild("RightLowerLeg", CubeListBuilder.create(), PartPose.offset(0.0F, 6.375F, -3.45F));

        PartDefinition RightCalf_r1 = RightLowerLeg.addOrReplaceChild("RightCalf_r1",
                ((CubeListBuilderExtender)CubeListBuilder.create().texOffs(2, calfUVy).addBox(-0.99F, 0.0168F, 0.0504F, 3.0F, 5.0F, 3.0F, layer.altDeformation.extend(0.05F)))
                        .removeLastFaces(Direction.UP, Direction.DOWN).finish(), PartPose.offsetAndRotation(-0.5F, -1.025F, 0.45F, 0.7418F, 0.0F, 0.0F));

        PartDefinition RightFoot = RightLowerLeg.addOrReplaceChild("RightFoot", CubeListBuilder.create(), PartPose.offset(0.0F, 0.875F, 6.0F));

        PartDefinition RightArch_r1 = RightFoot.addOrReplaceChild("RightArch_r1", CubeListBuilder.create().texOffs(2, 20).addBox(-1.0F, -8.2F, -0.725F, 3.0F, 6.0F, 3.0F, layer.altDeformation), PartPose.offsetAndRotation(-0.5F, 7.075F, -4.075F, -0.2618F, 0.0F, 0.0F));

        PartDefinition RightPad = RightFoot.addOrReplaceChild("RightPad", CubeListBuilder.create().texOffs(23, 21).addBox(-1.5F, 0.0F, -1.3F, 3.0F, 2.0F, 3.0F, layer.deformation.extend(-0.025F)), PartPose.offset(0.0F, 4.275F, -2.925F));

        PartDefinition LeftLeg = partDefinition.addOrReplaceChild("LeftLeg", CubeListBuilder.create(), PartPose.offset(2.5F, 10.5F, 0.0F));

        PartDefinition LeftThigh_r1 = LeftLeg.addOrReplaceChild("LeftThigh_r1", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, layer.altDeformation).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition LeftLowerLeg = LeftLeg.addOrReplaceChild("LeftLowerLeg", CubeListBuilder.create(), PartPose.offset(0.0F, 6.375F, -3.45F));

        PartDefinition LeftCalf_r1 = LeftLowerLeg.addOrReplaceChild("LeftCalf_r1",
                ((CubeListBuilderExtender)CubeListBuilder.create().texOffs(2, calfUVy).mirror().addBox(-0.99F, 0.0168F, 0.0504F, 3.0F, 5.0F, 3.0F, layer.altDeformation.extend(0.05F)).mirror(false))
                        .removeLastFaces(Direction.UP, Direction.DOWN).finish(), PartPose.offsetAndRotation(-0.5F, -1.025F, 0.45F, 0.7418F, 0.0F, 0.0F));

        PartDefinition LeftFoot = LeftLowerLeg.addOrReplaceChild("LeftFoot", CubeListBuilder.create(), PartPose.offset(0.0F, 0.875F, 6.0F));

        PartDefinition LeftArch_r1 = LeftFoot.addOrReplaceChild("LeftArch_r1", CubeListBuilder.create().texOffs(2, 20).mirror().addBox(-1.0F, -8.2F, -0.725F, 3.0F, 6.0F, 3.0F, layer.altDeformation).mirror(false), PartPose.offsetAndRotation(-0.5F, 7.075F, -4.075F, -0.2618F, 0.0F, 0.0F));

        PartDefinition LeftPad = LeftFoot.addOrReplaceChild("LeftPad", CubeListBuilder.create().texOffs(23, 21).mirror().addBox(-1.5F, 0.0F, -1.3F, 3.0F, 2.0F, 3.0F, layer.deformation.extend(-0.025F)).mirror(false), PartPose.offset(0.0F, 4.275F, -2.925F));
    }

    public static void addBreastplate(PartDefinition torso, ArmorModel layer) {
        addBreastplate(torso, layer, 0.0f, 0.0f, 0.0f);
    }

    public static void addBreastplate(PartDefinition torso, ArmorModel layer, float angle) {
        addBreastplate(torso, layer, 0.0f, 0.0f, 0.0f, angle);
    }

    public static void addBreastplate(PartDefinition torso, ArmorModel layer, float yOffset, float zOffset, float sizeOffset) {
        addBreastplate(torso, layer, yOffset, zOffset, sizeOffset, Mth.DEG_TO_RAD * -16F);
    }

    public static void addBreastplate(PartDefinition torso, ArmorModel layer, float yOffset, float zOffset, float sizeOffset, float angle) {
        switch (layer) {
            case ARMOR_OUTER -> {
                PartDefinition Plantoids = torso.addOrReplaceChild("Plantoids", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, -2.0F + zOffset));

                final var plantoidCubes = ((CubeListBuilderExtender)CubeListBuilder.create())
                        .texOffs(18, 19).mirror().addBox(-4.0F, -2.2F + yOffset, -0.8F, 8.0F, 3.0F, 2.0F, layer.dualDeformation.extend(-0.4f)).mirror(false)
                            .removeLastFaces(Direction.DOWN)
                        .texOffs(18, 22).mirror().addBox(-4.0F, 2.0F + yOffset, -0.8F, 8.0F, 1.0F, 2.0F, layer.dualDeformation.extend(-0.4f)).mirror(false)
                            .overrideLastFaceTexOffs(Direction.DOWN, 20, 23)
                            .removeLastFaces(Direction.UP);

                PartDefinition Plantoid_r1 = Plantoids.addOrReplaceChild("Plantoid_r1", plantoidCubes.finish(), PartPose.offsetAndRotation(0.0F, 2.5F - yOffset, 0.0F, angle, 0.0F, 0.0F));
            }

            case CLOTHING_INNER, CLOTHING_MIDDLE, CLOTHING_OUTER -> {
                PartDefinition Plantoids = torso.addOrReplaceChild("Plantoids", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, -2.0F + zOffset));

                final var plantoidCubes = ((CubeListBuilderExtender)CubeListBuilder.create())
                        .texOffs(18, 19).mirror().addBox(-4.0F, -2.2F + yOffset, -0.8F, 8.0F, 2.0F, 2.0F, layer.dualDeformation.extend(0.05f + sizeOffset)).mirror(false)
                            .removeLastFaces(Direction.DOWN)
                        .texOffs(18, 22).mirror().addBox(-4.0F, 0.5F + yOffset, -0.8F, 8.0F, 1.0F, 2.0F, layer.dualDeformation.extend(0.05f + sizeOffset)).mirror(false)
                            .overrideLastFaceTexOffs(Direction.DOWN, 20, 23)
                            .removeLastFaces(Direction.UP);

                PartDefinition Plantoid_r1 = Plantoids.addOrReplaceChild("Plantoid_r1", plantoidCubes.finish(), PartPose.offsetAndRotation(0.0F, 2.5F - yOffset, 0.0F, angle, 0.0F, 0.0F));
            }
        }
    }

    protected float distanceTo(@NotNull T entity, @NotNull Entity other, float partialTicks) {
        Vec3 entityPos = entity.getPosition(partialTicks);
        Vec3 otherPos = other.getPosition(partialTicks);
        float f = (float)(entityPos.x - otherPos.x);
        float f1 = (float)(entityPos.y - otherPos.y);
        float f2 = (float)(entityPos.z - otherPos.z);
        return Mth.sqrt(f * f + f1 * f1 + f2 * f2);
    }

    public static void setAllPartsVisibility(ModelPart part, boolean visible) {
        part.getAllParts().forEach(modelPart -> modelPart.visible = visible);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {}

    @Nullable
    public HelperModel getTransfurHelperModel(Limb limb) {
        return switch (limb) {
            case LEFT_LEG -> TransfurHelper.getDigitigradeLeftLeg(this.armorModel);
            case RIGHT_LEG -> TransfurHelper.getDigitigradeRightLeg(this.armorModel);
            default -> null;
        };
    }

    public void prepareVisibility(EquipmentSlot armorSlot, ItemStack item) {}
}
