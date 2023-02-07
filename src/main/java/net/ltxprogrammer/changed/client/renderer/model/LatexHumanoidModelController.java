package net.ltxprogrammer.changed.client.renderer.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class LatexHumanoidModelController {
    public final ModelPart Head, Torso, LeftArm, RightArm, LeftLeg, RightLeg;
    @Nullable
    public final ModelPart Tail, LeftWing, RightWing, LeftArm2, RightArm2, LeftArm3, RightArm3, LowerTorso, LeftLeg2, RightLeg2, Abdomen, LowerAbdomen;
    public final List<ModelPart> TailJoints;
    public final boolean hasTail, hasWings, hasArms2, hasArms3, hasLegs2, hasLegs;
    public final EntityModel<?> entityModel;
    public final float hipOffset;
    public final float forewardOffset;
    public final float legLength;
    public final float armLength;
    public final float torsoWidth;
    public final float torsoLength;
    public final boolean swimTail;
    public HumanoidModel.ArmPose leftArmPose = HumanoidModel.ArmPose.EMPTY;
    public HumanoidModel.ArmPose rightArmPose = HumanoidModel.ArmPose.EMPTY;
    public boolean crouching;
    public float swimAmount;

    public LatexHumanoidModelController(EntityModel entityModel, float hipOffset, float forewardOffset, float legLength, float armLength, float torsoWidth, float torsoLength, boolean swimTail, ModelPart head, ModelPart torso, @Nullable ModelPart tail, ModelPart rightArm, ModelPart leftArm, ModelPart rightLeg, ModelPart leftLeg,
                                        @Nullable ModelPart rightWing, @Nullable ModelPart leftWing, @Nullable ModelPart rightArm2, @Nullable ModelPart leftArm2, @Nullable ModelPart rightArm3, @Nullable ModelPart leftArm3, @Nullable ModelPart lowerTorso, @Nullable ModelPart rightLeg2, @Nullable ModelPart leftLeg2,
                                        @Nullable ModelPart abdomen, @Nullable ModelPart lowerAbdomen, @Nullable List<ModelPart> tailJoints) {
        this.entityModel = entityModel;
        this.hipOffset = hipOffset;
        this.forewardOffset = forewardOffset;
        this.legLength = legLength;
        this.armLength = armLength;
        this.torsoWidth = torsoWidth;
        this.torsoLength = torsoLength;
        this.swimTail = swimTail;
        Head = head;
        Torso = torso;
        Tail = tail;
        hasTail = Tail != null;
        LeftArm = leftArm;
        RightArm = rightArm;
        LeftLeg = leftLeg;
        RightLeg = rightLeg;
        LeftWing = leftWing;
        RightWing = rightWing;
        hasWings = LeftWing != null && RightWing != null;
        LeftArm2 = rightArm2;
        RightArm2 = leftArm2;
        hasArms2 = LeftArm2 != null && RightArm2 != null;
        LeftArm3 = rightArm3;
        RightArm3 = leftArm3;
        hasArms3 = LeftArm3 != null && RightArm3 != null;
        LowerTorso = lowerTorso;
        LeftLeg2 = rightLeg2;
        RightLeg2 = leftLeg2;
        hasLegs2 = LowerTorso != null && LeftLeg2 != null && RightLeg2 != null;
        Abdomen = abdomen;
        LowerAbdomen = lowerAbdomen;
        hasLegs = Abdomen == null && LowerAbdomen == null;
        TailJoints = tailJoints;
    }

    public static List<ModelPart.Cube> findLargestCube(ModelPart part) {
        ArrayList<ModelPart.Cube> list = new ArrayList<>(part.cubes);

        for (var entry : part.children.entrySet()) {
            list.addAll(findLargestCube(entry.getValue()));
        }

        list.sort((cubeA, cubeB) -> {
            float massA = (cubeA.maxX - cubeA.minX) * (cubeA.maxY - cubeA.minY) * (cubeA.maxZ - cubeA.minZ);
            float massB = (cubeB.maxX - cubeB.minX) * (cubeB.maxY - cubeB.minY) * (cubeB.maxZ - cubeB.minZ);
            return Float.compare(massB, massA);
        });

        return list;
    }

    public static class Builder {
        private final ModelPart Head, Torso, Tail, LeftArm, RightArm, LeftLeg, RightLeg;
        private ModelPart LeftWing, RightWing, LeftArm2, RightArm2, LeftArm3, RightArm3, LowerTorso, LeftLeg2, RightLeg2, Abdomen, LowerAbdomen;
        public List<ModelPart> TailJoints = new ArrayList<>();
        private final EntityModel<?> entityModel;
        private float hipOffset = -2.0F;
        private float forewardOffset = 0.0F;
        private float legLength = 0.0F;
        private float armLength = 0.5F;
        private float torsoWidth = 0.0F;
        private float torsoLength = 0.0F;
        private boolean swimTail = false;

        public Builder(EntityModel model, ModelPart head, ModelPart torso, ModelPart tail, ModelPart rightArm, ModelPart leftArm, ModelPart rightLeg, ModelPart leftLeg) {
            this.Head = head;
            this.Torso = torso;
            this.Tail = tail;
            this.RightArm = rightArm;
            this.RightLeg = rightLeg;
            this.LeftArm = leftArm;
            this.LeftLeg = leftLeg;
            this.entityModel = model;

            if (leftLeg != null)
                this.legLength = (leftLeg.y - 10.0f) * (4.0f / 3.0f) - (hipOffset + 2.0f);
            if (leftArm != null)
                this.torsoWidth = Math.max(leftArm.x - 5.0f, 0.0f);
        }

        public static Builder of(EntityModel model, ModelPart head, ModelPart torso, ModelPart tail, ModelPart rightArm, ModelPart leftArm, ModelPart rightLeg, ModelPart leftLeg) {
            return new Builder(model, head, torso, tail, rightArm, leftArm, rightLeg, leftLeg);
        }

        public Builder tailAidsInSwim() {
            this.swimTail = true; return this;
        }

        public Builder hipOffset(float f) {
            if (LeftLeg != null)
                this.legLength = (LeftLeg.y - 10.0f) * (4.0f / 3.0f) - (f + 2.0f);
            this.hipOffset = f; return this;
        }

        public Builder legLengthOffset(float f) {
            this.legLength = f; return this;
        }

        public Builder forewardOffset(float f) {
            this.forewardOffset = f; return this;
        }

        public Builder armLengthOffset(float f) {
            this.armLength = f; return this;
        }

        public Builder torsoLengthOffset(float f) {
            this.torsoLength = f; return this;
        }

        public Builder wings(ModelPart rightWing, ModelPart leftWing) {
            this.RightWing = rightWing; this.LeftWing = leftWing; return this;
        }

        public Builder arms2(ModelPart rightArm2, ModelPart leftArm2) {
            this.RightArm2 = rightArm2; this.LeftArm2 = leftArm2; return this;
        }

        public Builder arms3(ModelPart rightArm3, ModelPart leftArm3) {
            this.RightArm3 = rightArm3; this.LeftArm3 = leftArm3; return this;
        }

        public Builder legs2(ModelPart lowerTorso, ModelPart rightLeg2, ModelPart leftLeg2) {
            this.LowerTorso = lowerTorso;
            this.RightLeg2 = rightLeg2; this.LeftLeg2 = leftLeg2; return this;
        }

        public Builder noLegs(ModelPart abdomen, ModelPart lowerAbdomen) {
            this.Abdomen = abdomen;
            this.LowerAbdomen = lowerAbdomen;
            return this;
        }

        public Builder tailJoints(List<ModelPart> tailJoints) {
            this.TailJoints = tailJoints;
            return this;
        }

        public LatexHumanoidModelController build() {
            return new LatexHumanoidModelController(entityModel, hipOffset, forewardOffset, legLength, armLength, torsoWidth, torsoLength, swimTail, Head, Torso, Tail, RightArm, LeftArm, RightLeg, LeftLeg,
                    LeftWing, RightWing, LeftArm2, RightArm2, LeftArm3, RightArm3, LowerTorso, LeftLeg2, RightLeg2, Abdomen, LowerAbdomen, TailJoints);
        }
    }

    public void setupHand() {
        this.RightArm.x += 3F;
        this.LeftArm.x += -3F;
        this.RightArm.z += -1F - forewardOffset;
        this.LeftArm.z += -1F - forewardOffset;
        if (this.hasArms2) {
            this.RightArm2.x += 3F;
            this.LeftArm2.x += -3F;
            this.RightArm2.z += -1F - forewardOffset;
            this.LeftArm2.z += -1F - forewardOffset;
        }
        if (this.hasArms3) {
            this.RightArm3.x += 3F;
            this.LeftArm3.x += -3F;
            this.RightArm3.z += -1F - forewardOffset;
            this.LeftArm3.z += -1F - forewardOffset;
        }
    }

    public void setupAnim(@NotNull LatexEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        boolean flag = entity.getFallFlyingTicks() > 4;
        boolean flag1 = entity.isVisuallySwimming();
        this.Head.yRot = netHeadYaw * ((float)Math.PI / 180F);
        if (flag) {
            this.Head.xRot = (-(float)Math.PI / 4F);
        } else if (this.swimAmount > 0.0F) {
            if (flag1) {
                if (hasTail && swimTail) {
                    this.Head.xRot = this.rotlerpRad(this.swimAmount, this.Head.xRot, (-(float)Math.PI / 2.8F));
                }
                else {
                    this.Head.xRot = this.rotlerpRad(this.swimAmount, this.Head.xRot, (-(float)Math.PI / 4F));
                }
            } else {
                this.Head.xRot = this.rotlerpRad(this.swimAmount, this.Head.xRot, headPitch * ((float)Math.PI / 180F));
            }
        } else {
            this.Head.xRot = headPitch * ((float)Math.PI / 180F);
        }

        this.Torso.yRot = 0.0F;
        this.RightArm.z = forewardOffset;
        this.RightArm.x = -5.0F - torsoWidth;
        this.LeftArm.z = forewardOffset;
        this.LeftArm.x = 5.0F + torsoWidth;
        float f = 1.0F;
        if (flag) {
            f = (float)entity.getDeltaMovement().lengthSqr();
            f /= 0.2F;
            f *= f * f;
        }

        if (f < 1.0F) {
            f = 1.0F;
        }

        this.RightArm.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 2.0F * limbSwingAmount * 0.5F / f;
        this.LeftArm.xRot = Mth.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F / f;
        this.RightArm.zRot = 0.0F;
        this.LeftArm.zRot = 0.0F;
        this.RightLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount / f;
        this.LeftLeg.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount / f;
        this.RightLeg.yRot = 0.0F;
        this.LeftLeg.yRot = 0.0F;
        this.RightLeg.zRot = 0.0F;
        this.LeftLeg.zRot = 0.0F;
        if (this.hasLegs2) {
            this.RightLeg2.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount / f;
            this.LeftLeg2.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount / f;
            this.RightLeg2.yRot = 0.0F;
            this.LeftLeg2.yRot = 0.0F;
            this.RightLeg2.zRot = 0.0F;
            this.LeftLeg2.zRot = 0.0F;
        }
        if (this.hasTail) {
            this.Tail.xRot = 0.0F;
            this.Tail.yRot = Mth.cos(limbSwing * 0.6662F) * (entity.isInWaterOrBubble() && swimTail ? 0.18F : 0.125F) * limbSwingAmount / f;
            this.Tail.zRot = 0.0F;
        }
        if (!this.hasLegs) { // TODO better slither
            float slitherAmount = Math.min(0.5F, limbSwingAmount);
            this.Abdomen.xRot = (float) Math.toRadians(-12.5);
            this.Abdomen.yRot = Mth.cos(limbSwing * 0.33333334F + ((float)Math.PI / 2.0F)) * 0.6F * slitherAmount;
            this.Abdomen.zRot = Mth.cos(limbSwing * 0.33333334F) * 0.6F * slitherAmount;
            this.LowerAbdomen.xRot = (float) Math.toRadians(50);
            this.LowerAbdomen.yRot = this.Abdomen.yRot;
            this.LowerAbdomen.zRot = this.Abdomen.zRot;
            this.Tail.xRot = (float) Math.toRadians(40);
            this.Tail.yRot = 0.0F;
            this.Tail.zRot = 0.0F;

            float offset = 0.0F;
            for (ModelPart joint : this.TailJoints) {
                joint.xRot = 0.0F;
                joint.zRot = Mth.cos((limbSwing * 0.33333334F) - (((float)Math.PI / 2.0F) * offset)) * 0.3F * slitherAmount;
                joint.yRot = 0.0F;
                offset += 1.0F;
            }
        }

        if (this.RightWing != null) { this.RightWing.zRot = 0.0F; }
        if (this.LeftWing != null) { this.LeftWing.zRot = 0.0F; }

        if (entityModel.riding) {
            this.RightArm.xRot += (-(float)Math.PI / 5F);
            this.LeftArm.xRot += (-(float)Math.PI / 5F);
            this.RightLeg.xRot = -1.4137167F;
            this.RightLeg.yRot = ((float)Math.PI / 10F);
            this.RightLeg.zRot = 0.07853982F;
            this.LeftLeg.xRot = -1.4137167F;
            this.LeftLeg.yRot = (-(float)Math.PI / 10F);
            this.LeftLeg.zRot = -0.07853982F;
            if (this.hasLegs2) {
                this.RightLeg2.xRot = -1.4137167F;
                this.RightLeg2.yRot = ((float)Math.PI / 10F);
                this.RightLeg2.zRot = 0.07853982F;
                this.LeftLeg2.xRot = -1.4137167F;
                this.LeftLeg2.yRot = (-(float)Math.PI / 10F);
                this.LeftLeg2.zRot = -0.07853982F;
            }
            if (this.hasTail)
                this.Tail.xRot = 0.005F;
        }

        if (entity.isSleeping()) {
            if (!this.hasLegs) {
                this.Abdomen.xRot = 0.0F;
                this.LowerAbdomen.xRot = 0.0F;
                this.Tail.xRot = 0.0F;
                for (ModelPart joint : this.TailJoints) {
                    joint.zRot = 0.0F;
                }
            }
            else if (this.hasTail)
                this.Tail.xRot = -1.4137167F;
        }

        this.RightArm.yRot = 0.0F;
        this.LeftArm.yRot = 0.0F;
        boolean flag2 = entity.getMainArm() == HumanoidArm.RIGHT;
        if (entity.isUsingItem()) {
            boolean flag3 = entity.getUsedItemHand() == InteractionHand.MAIN_HAND;
            if (flag3 == flag2) {
                this.poseRightArm(entity);
            } else {
                this.poseLeftArm(entity);
            }
        } else {
            boolean flag4 = flag2 ? this.leftArmPose.isTwoHanded() : this.rightArmPose.isTwoHanded();
            if (flag2 != flag4) {
                this.poseLeftArm(entity);
                this.poseRightArm(entity);
            } else {
                this.poseRightArm(entity);
                this.poseLeftArm(entity);
            }
        }

        this.setupAttackAnimation(entity, ageInTicks);
        if (this.crouching) {
            if (this.hasTail)
                this.Tail.xRot -= 0.3F;
            if (!this.hasLegs2) {
                this.Torso.xRot = 0.5F;
                this.RightArm.xRot += 0.4F;
                this.LeftArm.xRot += 0.4F;
                this.RightLeg.z = 4.0F + forewardOffset + (torsoLength / 1.83048772171f);
                this.LeftLeg.z = 4.0F + forewardOffset + (torsoLength / 1.83048772171f);
            }
            this.RightLeg.y = 12.2F + hipOffset;
            this.LeftLeg.y = 12.2F + hipOffset;
            this.Head.y = 4.2F + hipOffset + legLength;
            this.Torso.y = 3.2F + hipOffset + legLength;
            this.LeftArm.y = 5.2F + hipOffset + (hasArms2 ? 4.0F : 0.0F) + legLength;
            this.RightArm.y = 5.2F + hipOffset + (hasArms2 ? 4.0F : 0.0F) + legLength;
            if (this.hasLegs2) {
                this.RightLeg2.y = 12.2F + hipOffset;
                this.LeftLeg2.y = 12.2F + hipOffset;
            }

            if (!this.hasLegs) {
                this.Abdomen.y = 12.2F + hipOffset;
                this.Abdomen.z = 4.0F + forewardOffset;
                this.LowerAbdomen.xRot = (float) Math.toRadians(75);
            }
        } else {
            this.Torso.xRot = 0.0F;
            this.RightLeg.z = 0.1F + forewardOffset;
            this.LeftLeg.z = 0.1F + forewardOffset;
            this.RightLeg.y = 12.0F + hipOffset;
            this.LeftLeg.y = 12.0F + hipOffset;
            this.Head.y = 0.0F + hipOffset + legLength;
            this.Torso.y = 0.0F + hipOffset + legLength;
            this.LeftArm.y = 2.0F + hipOffset + (hasArms2 ? 2.0F : 0.0F) + legLength;
            this.RightArm.y = 2.0F + hipOffset + (hasArms2 ? 2.0F : 0.0F) + legLength;
            if (this.hasLegs2) {
                this.RightLeg2.y = 12.0F + hipOffset;
                this.LeftLeg2.y = 12.0F + hipOffset;
            }

            if (!this.hasLegs) {
                this.Abdomen.y = 12.0F + hipOffset;
                this.Abdomen.z = 0.1F + forewardOffset;
            }
        }

        if (this.hasArms2) {
            this.RightArm2.z = 0.0F;
            this.RightArm2.y = this.RightArm.y - 2.0F;
            this.RightArm2.x = -5.0F - torsoWidth;
            this.LeftArm2.z = 0.0F;
            this.LeftArm2.y = this.LeftArm.y - 2.0F;
            this.LeftArm2.x = 5.0F + torsoWidth;
        }
        if (this.hasArms3) {
            this.RightArm3.z = 0.0F;
            this.RightArm3.y = this.RightArm.y + 2.0F;
            this.RightArm3.x = -5.0F - torsoWidth;
            this.LeftArm3.z = 0.0F;
            this.LeftArm3.y = this.LeftArm.y + 2.0F;
            this.LeftArm3.x = 5.0F + torsoWidth;
        }

        if (this.rightArmPose != HumanoidModel.ArmPose.SPYGLASS) {
            AnimationUtils.bobModelPart(this.RightArm, ageInTicks, 1.0F);
        }

        if (this.leftArmPose != HumanoidModel.ArmPose.SPYGLASS) {
            AnimationUtils.bobModelPart(this.LeftArm, ageInTicks, -1.0F);
        }

        if (entity.isFallFlying()) {
            float f1 = (float)entity.getFallFlyingTicks();
            float f2 = Mth.clamp(f1 * f1 / 100.0F, 0.0F, 1.0F);
            if (this.hasTail && this.hasLegs)
                this.Tail.xRot = Mth.lerp(f2, this.Tail.xRot, -1.0f);

            if (this.RightWing != null) {
                this.RightWing.zRot = Mth.lerp(f2, this.RightWing.zRot, -0.8f);
            }

            if (this.LeftWing != null) {
                this.LeftWing.zRot = Mth.lerp(f2, this.LeftWing.zRot, 0.8f);
            }

            if (!this.hasLegs) {
                this.Abdomen.xRot = Mth.lerp(f2, this.Abdomen.xRot, 0.0f);
                this.LowerAbdomen.xRot = Mth.lerp(f2, this.LowerAbdomen.xRot, 0.0f);
                this.Tail.xRot = Mth.lerp(f2, this.Tail.xRot, 0.0f);
            }
        }

        if (this.swimAmount > 0.0F) {
            float f5 = limbSwing % 26.0F;
            HumanoidArm humanoidarm = this.getAttackArm(entity);
            float f1 = humanoidarm == HumanoidArm.RIGHT && entityModel.attackTime > 0.0F ? 0.0F : this.swimAmount;
            float f2 = humanoidarm == HumanoidArm.LEFT && entityModel.attackTime > 0.0F ? 0.0F : this.swimAmount;
            if (!entity.isUsingItem() && !swimTail) {
                if (f5 < 14.0F) {
                    this.LeftArm.xRot = this.rotlerpRad(f2, this.LeftArm.xRot, 0.0F);
                    this.RightArm.xRot = Mth.lerp(f1, this.RightArm.xRot, 0.0F);
                    this.LeftArm.yRot = this.rotlerpRad(f2, this.LeftArm.yRot, (float)Math.PI);
                    this.RightArm.yRot = Mth.lerp(f1, this.RightArm.yRot, (float)Math.PI);
                    this.LeftArm.zRot = this.rotlerpRad(f2, this.LeftArm.zRot, (float)Math.PI + 1.8707964F * this.quadraticArmUpdate(f5) / this.quadraticArmUpdate(14.0F));
                    this.RightArm.zRot = Mth.lerp(f1, this.RightArm.zRot, (float)Math.PI - 1.8707964F * this.quadraticArmUpdate(f5) / this.quadraticArmUpdate(14.0F));
                } else if (f5 >= 14.0F && f5 < 22.0F) {
                    float f6 = (f5 - 14.0F) / 8.0F;
                    this.LeftArm.xRot = this.rotlerpRad(f2, this.LeftArm.xRot, ((float)Math.PI / 2F) * f6);
                    this.RightArm.xRot = Mth.lerp(f1, this.RightArm.xRot, ((float)Math.PI / 2F) * f6);
                    this.LeftArm.yRot = this.rotlerpRad(f2, this.LeftArm.yRot, (float)Math.PI);
                    this.RightArm.yRot = Mth.lerp(f1, this.RightArm.yRot, (float)Math.PI);
                    this.LeftArm.zRot = this.rotlerpRad(f2, this.LeftArm.zRot, 5.012389F - 1.8707964F * f6);
                    this.RightArm.zRot = Mth.lerp(f1, this.RightArm.zRot, 1.2707963F + 1.8707964F * f6);
                } else if (f5 >= 22.0F && f5 < 26.0F) {
                    float f3 = (f5 - 22.0F) / 4.0F;
                    this.LeftArm.xRot = this.rotlerpRad(f2, this.LeftArm.xRot, ((float)Math.PI / 2F) - ((float)Math.PI / 2F) * f3);
                    this.RightArm.xRot = Mth.lerp(f1, this.RightArm.xRot, ((float)Math.PI / 2F) - ((float)Math.PI / 2F) * f3);
                    this.LeftArm.yRot = this.rotlerpRad(f2, this.LeftArm.yRot, (float)Math.PI);
                    this.RightArm.yRot = Mth.lerp(f1, this.RightArm.yRot, (float)Math.PI);
                    this.LeftArm.zRot = this.rotlerpRad(f2, this.LeftArm.zRot, (float)Math.PI);
                    this.RightArm.zRot = Mth.lerp(f1, this.RightArm.zRot, (float)Math.PI);
                }
            }

            float f7 = 0.3F;
            float f4 = 0.33333334F;
            this.LeftLeg.xRot = Mth.lerp(this.swimAmount, this.LeftLeg.xRot, 0.3F * Mth.cos(limbSwing * 0.33333334F + (float)Math.PI));
            this.RightLeg.xRot = Mth.lerp(this.swimAmount, this.RightLeg.xRot, 0.3F * Mth.cos(limbSwing * 0.33333334F));

            if (this.hasTail && this.hasLegs) {
                this.Tail.xRot = Mth.lerp(this.swimAmount, this.Tail.xRot, -1.1f);//78f);
                float oldTailzRot = this.Tail.zRot;
                this.Tail.zRot = Mth.lerp(this.swimAmount, 0.0F, this.Tail.yRot);
                this.Tail.yRot = Mth.lerp(this.swimAmount, oldTailzRot, 0.0F);

                if (swimTail) {
                    this.Tail.zRot = Mth.lerp(this.swimAmount, this.Tail.zRot, 0.25F * Mth.cos(limbSwing * 0.33333334F + (float)Math.PI));
                    this.LeftArm.xRot = Mth.lerp(this.swimAmount, this.LeftArm.xRot, 0.3F * Mth.cos(limbSwing * 0.33333334F));
                    this.RightArm.xRot = Mth.lerp(this.swimAmount, this.RightArm.xRot, 0.3F * Mth.cos(limbSwing * 0.33333334F + (float)Math.PI));
                }
            }

            else if (!this.hasLegs) {
                this.Abdomen.xRot = Mth.lerp(f2, this.Abdomen.xRot, 0.0f);
                this.Abdomen.yRot = Mth.lerp(f2, this.Abdomen.yRot, 0.0f);
                this.Abdomen.zRot = Mth.lerp(this.swimAmount, this.Abdomen.zRot, 0.35F * Mth.cos(limbSwing * 0.33333334F));
                this.LowerAbdomen.xRot = Mth.lerp(f2, this.LowerAbdomen.xRot, 0.0f);
                this.LowerAbdomen.yRot = Mth.lerp(f2, this.LowerAbdomen.yRot, 0.0f);
                this.LowerAbdomen.zRot = Mth.lerp(this.swimAmount, this.LowerAbdomen.zRot, 0.35F * Mth.cos(limbSwing * 0.33333334F - ((float)Math.PI / 3.0F)));
                this.Tail.xRot = Mth.lerp(f2, this.Tail.xRot, 0.0f);
                this.Tail.yRot = Mth.lerp(f2, this.Tail.yRot, 0.0f);
                this.Tail.zRot = Mth.lerp(this.swimAmount, this.Tail.zRot, 0.35F * Mth.cos(limbSwing * 0.33333334F - ((float)Math.PI / 1.5F)));

                float offset = 0.0F;
                for (ModelPart joint : this.TailJoints) {
                    offset += 1.0F;
                    joint.xRot = 0.0F;
                    joint.zRot = 0.0F;
                    joint.yRot = Mth.lerp(this.swimAmount, joint.yRot, 0.35F * Mth.cos(limbSwing * 0.33333334F - (((float)Math.PI / 3.0F) * offset)));
                }

                if (swimTail) {
                    this.LeftArm.xRot = Mth.lerp(this.swimAmount, this.LeftArm.xRot, 0.3F * Mth.cos(limbSwing * 0.33333334F));
                    this.RightArm.xRot = Mth.lerp(this.swimAmount, this.RightArm.xRot, 0.3F * Mth.cos(limbSwing * 0.33333334F + (float)Math.PI));
                }
            }
        }

        if (this.hasArms2) {
            this.RightArm2.xRot = this.RightArm.xRot * 0.88F;
            this.RightArm2.yRot = this.RightArm.yRot * 0.88F;
            this.RightArm2.zRot = this.RightArm.zRot * 0.88F;
            this.LeftArm2.xRot = this.LeftArm.xRot * 0.88F;
            this.LeftArm2.yRot = this.LeftArm.yRot * 0.88F;
            this.LeftArm2.zRot = this.LeftArm.zRot * 0.88F;
        }

        if (this.hasArms3) {
            this.RightArm3.xRot = this.RightArm.xRot * 0.88F;
            this.RightArm3.yRot = this.RightArm.yRot * 0.88F;
            this.RightArm3.zRot = this.RightArm.zRot * 0.88F;
            this.LeftArm3.xRot = this.LeftArm.xRot * 0.88F;
            this.LeftArm3.yRot = this.LeftArm.yRot * 0.88F;
            this.LeftArm3.zRot = this.LeftArm.zRot * 0.88F;
        }
    }

    private void poseRightArm(LatexEntity p_102876_) {
        switch(this.rightArmPose) {
            case EMPTY:
                this.RightArm.yRot = 0.0F;
                break;
            case BLOCK:
                this.RightArm.xRot = this.RightArm.xRot * 0.5F - 0.9424779F;
                this.RightArm.yRot = (-(float)Math.PI / 6F);
                break;
            case ITEM:
                this.RightArm.xRot = this.RightArm.xRot * 0.5F - ((float)Math.PI / 10F);
                this.RightArm.yRot = 0.0F;
                break;
            case THROW_SPEAR:
                this.RightArm.xRot = this.RightArm.xRot * 0.5F - (float)Math.PI;
                this.RightArm.yRot = 0.0F;
                break;
            case BOW_AND_ARROW:
                this.RightArm.yRot = -0.1F + this.Head.yRot;
                this.LeftArm.yRot = 0.1F + this.Head.yRot + 0.4F;
                this.RightArm.xRot = (-(float)Math.PI / 2F) + this.Head.xRot;
                this.LeftArm.xRot = (-(float)Math.PI / 2F) + this.Head.xRot;
                break;
            case CROSSBOW_CHARGE:
                AnimationUtils.animateCrossbowCharge(this.RightArm, this.LeftArm, p_102876_, true);
                break;
            case CROSSBOW_HOLD:
                AnimationUtils.animateCrossbowHold(this.RightArm, this.LeftArm, this.Head, true);
                break;
            case SPYGLASS:
                this.RightArm.xRot = Mth.clamp(this.Head.xRot - 1.9198622F - (p_102876_.isCrouching() ? 0.2617994F : 0.0F), -2.4F, 3.3F);
                this.RightArm.yRot = this.Head.yRot - 0.2617994F;
        }

    }

    private void poseLeftArm(LatexEntity p_102879_) {
        switch(this.leftArmPose) {
            case EMPTY:
                this.LeftArm.yRot = 0.0F;
                break;
            case BLOCK:
                this.LeftArm.xRot = this.LeftArm.xRot * 0.5F - 0.9424779F;
                this.LeftArm.yRot = ((float)Math.PI / 6F);
                break;
            case ITEM:
                this.LeftArm.xRot = this.LeftArm.xRot * 0.5F - ((float)Math.PI / 10F);
                this.LeftArm.yRot = 0.0F;
                break;
            case THROW_SPEAR:
                this.LeftArm.xRot = this.LeftArm.xRot * 0.5F - (float)Math.PI;
                this.LeftArm.yRot = 0.0F;
                break;
            case BOW_AND_ARROW:
                this.RightArm.yRot = -0.1F + this.Head.yRot - 0.4F;
                this.LeftArm.yRot = 0.1F + this.Head.yRot;
                this.RightArm.xRot = (-(float)Math.PI / 2F) + this.Head.xRot;
                this.LeftArm.xRot = (-(float)Math.PI / 2F) + this.Head.xRot;
                break;
            case CROSSBOW_CHARGE:
                AnimationUtils.animateCrossbowCharge(this.RightArm, this.LeftArm, p_102879_, false);
                break;
            case CROSSBOW_HOLD:
                AnimationUtils.animateCrossbowHold(this.RightArm, this.LeftArm, this.Head, false);
                break;
            case SPYGLASS:
                this.LeftArm.xRot = Mth.clamp(this.Head.xRot - 1.9198622F - (p_102879_.isCrouching() ? 0.2617994F : 0.0F), -2.4F, 3.3F);
                this.LeftArm.yRot = this.Head.yRot + 0.2617994F;
        }
    }

    private float quadraticArmUpdate(float p_102834_) {
        return -65.0F * p_102834_ + p_102834_ * p_102834_;
    }

    public void translateToHand(HumanoidArm p_102854_, boolean alternate, PoseStack p_102855_) {
        this.getArm(p_102854_, alternate).translateAndRotate(p_102855_);
    }

    protected ModelPart getArm(HumanoidArm p_102852_, boolean alternate) {
        if (!alternate)
            return p_102852_ == HumanoidArm.LEFT ? this.LeftArm : this.RightArm;
        else
            return p_102852_ == HumanoidArm.LEFT ? this.LeftArm2 : this.RightArm2;
    }

    public ModelPart getHead() {
        return this.Head;
    }

    private HumanoidArm getAttackArm(LatexEntity p_102857_) {
        HumanoidArm humanoidarm = p_102857_.getMainArm();
        return p_102857_.swingingArm == InteractionHand.MAIN_HAND ? humanoidarm : humanoidarm.getOpposite();
    }

    protected void setupAttackAnimation(LatexEntity p_102858_, float p_102859_) {
        if (!(entityModel.attackTime <= 0.0F)) {
            HumanoidArm humanoidarm = this.getAttackArm(p_102858_);
            ModelPart modelpart = this.getArm(humanoidarm, false);
            float f = entityModel.attackTime;
            if (!this.hasLegs2)
                this.Torso.yRot = Mth.sin(Mth.sqrt(f) * ((float)Math.PI * 2F)) * 0.2F;
            else
                this.Torso.yRot = 0.0F;
            if (humanoidarm == HumanoidArm.LEFT) {
                this.Torso.yRot *= -1.0F;
            }

            this.RightArm.z = Mth.sin(this.Torso.yRot) * 5.0F + forewardOffset;
            this.RightArm.x = -Mth.cos(this.Torso.yRot) * 5.0F - torsoWidth;
            this.LeftArm.z = -Mth.sin(this.Torso.yRot) * 5.0F + forewardOffset;
            this.LeftArm.x = Mth.cos(this.Torso.yRot) * 5.0F + torsoWidth;
            this.RightArm.yRot += this.Torso.yRot;
            this.LeftArm.yRot += this.Torso.yRot;
            this.LeftArm.xRot += this.Torso.yRot;
            f = 1.0F - entityModel.attackTime;
            f *= f;
            f *= f;
            f = 1.0F - f;
            float f1 = Mth.sin(f * (float)Math.PI);
            float f2 = Mth.sin(entityModel.attackTime * (float)Math.PI) * -(this.Head.xRot - 0.7F) * 0.75F;
            modelpart.xRot = (float)((double)modelpart.xRot - ((double)f1 * 1.2D + (double)f2));
            modelpart.yRot += this.Torso.yRot * 2.0F;
            modelpart.zRot += Mth.sin(entityModel.attackTime * (float)Math.PI) * -0.4F;
        }
    }

    protected float rotlerpRad(float p_102836_, float p_102837_, float p_102838_) {
        float f = (p_102838_ - p_102837_) % ((float)Math.PI * 2F);
        if (f < -(float)Math.PI) {
            f += ((float)Math.PI * 2F);
        }

        if (f >= (float)Math.PI) {
            f -= ((float)Math.PI * 2F);
        }

        return p_102837_ + p_102836_ * f;
    }
}
