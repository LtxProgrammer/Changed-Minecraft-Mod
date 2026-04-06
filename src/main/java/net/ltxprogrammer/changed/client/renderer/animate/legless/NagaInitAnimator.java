package net.ltxprogrammer.changed.client.renderer.animate.legless;

import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.beast.NagaEntity;
import net.ltxprogrammer.changed.util.CameraUtil;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Vector3f;

import java.util.List;

public class NagaInitAnimator<T extends ChangedEntity & NagaEntity, M extends AdvancedHumanoidModel<T>> extends AbstractNagaAnimator<T, M> {
    protected final Matrix3f cumulativeRotation = new Matrix3f();
    protected final Matrix3f inverseCumulativeRotation = new Matrix3f();
    protected final Matrix3f localRotation = new Matrix3f();

    public NagaInitAnimator(ModelPart waist, ModelPart upperAbdomen, ModelPart lowerAbdomen, ModelPart tailStart, List<ModelPart> tailJoints) {
        super(waist, upperAbdomen, lowerAbdomen, tailStart, tailJoints);
    }

    @Override
    public HumanoidAnimator.AnimateStage preferredStage() {
        return HumanoidAnimator.AnimateStage.INIT;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float yBodyRot = Mth.DEG_TO_RAD * Mth.rotLerp(core.partialTicks, entity.yBodyRotO, entity.yBodyRot);
        float yTailRot;

        {
            var slitherJoint = entity.getSlitherJoint(0);
            var nextJoint = entity.getSlitherJoint(1);

            Vector3f facing = new Vector3f(
                    nextJoint.getJointX(core.partialTicks),
                    nextJoint.getJointY(core.partialTicks),
                    nextJoint.getJointZ(core.partialTicks)
            ).sub(
                    slitherJoint.getJointX(core.partialTicks),
                    slitherJoint.getJointY(core.partialTicks),
                    slitherJoint.getJointZ(core.partialTicks)
            ).normalize().mul(1f, -1f, -1f);

            yTailRot = (float) Math.atan2(facing.x, facing.z);
        }

        float dyBodyRot = yBodyRot - yTailRot;
        dyBodyRot = Mth.sign(dyBodyRot) * ((Mth.abs(dyBodyRot) + Mth.PI) % Mth.TWO_PI - Mth.PI);


        float slitherAngle;
        {
            var slitherJoint = entity.getSlitherJoint(0);

            Vector3f abdomenAngle = new Vector3f(0f, core.legLength / 16.0f, 0f).sub(
                    slitherJoint.getJointX(core.partialTicks),
                    slitherJoint.getJointY(core.partialTicks),
                    slitherJoint.getJointZ(core.partialTicks)
            ).normalize();

            float f1 = -(yBodyRot + Mth.HALF_PI);
            slitherAngle = (float) -Math.acos(abdomenAngle.dot(0f, 1f, 0f)) *
                    Mth.sign(abdomenAngle.dot(Mth.sin(f1), 0f, Mth.cos(f1)));
        }

        waist.x = 0f;
        waist.y = core.calculateLegPositionY();
        waist.z = 0f;
        tailStart.xRot = 0f;
        tailStart.zRot = 0f;
        tailStart.yRot = 0f;

        tailJoints.forEach(joint -> {
            joint.xRot = 0f;
            joint.yRot = 0f;
            joint.zRot = 0f;
        });

        cumulativeRotation.identity();
        cumulativeRotation.rotateY(yBodyRot);

        localRotation.rotationZYX(0f, yBodyRot - dyBodyRot * 0.25f, 0f);
        cumulativeRotation.invert(inverseCumulativeRotation);
        inverseCumulativeRotation.mul(localRotation, localRotation);
        CameraUtil.decomposeZYX(localRotation, waist);
        cumulativeRotation.mul(localRotation);

        localRotation.rotationY(yBodyRot - dyBodyRot * 0.5f).rotateZ(slitherAngle).rotateX(Mth.DEG_TO_RAD * -20.0f);
        cumulativeRotation.invert(inverseCumulativeRotation);
        inverseCumulativeRotation.mul(localRotation, localRotation);
        CameraUtil.decomposeZYX(localRotation, upperAbdomen);
        cumulativeRotation.mul(localRotation);

        localRotation.rotationY(yBodyRot - dyBodyRot * 0.75f).rotateZ(slitherAngle).rotateX(Mth.DEG_TO_RAD * 50.0f);
        cumulativeRotation.invert(inverseCumulativeRotation);
        inverseCumulativeRotation.mul(localRotation, localRotation);
        CameraUtil.decomposeZYX(localRotation, lowerAbdomen);
        cumulativeRotation.mul(localRotation);

        for (int i = 0; i < entity.getSlitherJointCount() - 1 && i < tailJoints.size() + 1; ++i) {
            var modelPart = i == 0 ? tailStart : tailJoints.get(i - 1);
            var slitherJoint = entity.getSlitherJoint(i);
            var nextJoint = entity.getSlitherJoint(i + 1);

            Vector3f facing = new Vector3f(
                    nextJoint.getJointX(core.partialTicks),
                    nextJoint.getJointY(core.partialTicks),
                    nextJoint.getJointZ(core.partialTicks)
            ).sub(
                    slitherJoint.getJointX(core.partialTicks),
                    slitherJoint.getJointY(core.partialTicks),
                    slitherJoint.getJointZ(core.partialTicks)
            ).normalize().mul(1f, -1f, -1f);
            Vector3f up = new Vector3f(
                    nextJoint.getNormalX(core.partialTicks),
                    nextJoint.getNormalY(core.partialTicks),
                    nextJoint.getNormalZ(core.partialTicks)
            ).add(
                    slitherJoint.getNormalX(core.partialTicks),
                    slitherJoint.getNormalY(core.partialTicks),
                    slitherJoint.getNormalZ(core.partialTicks)
            ).normalize().mul(1f, -1f, 1f);

            Vector3f right = new Vector3f();
            facing.cross(up, right).normalize();
            right.cross(facing, up);

            localRotation.setColumn(0/* X Axis */, right);
            localRotation.setColumn(1/* Y Axis */, facing);
            localRotation.setColumn(2/* Z Axis */, up);

            cumulativeRotation.invert(inverseCumulativeRotation);
            inverseCumulativeRotation.mul(localRotation, localRotation);

            CameraUtil.decomposeZYX(localRotation, modelPart);

            cumulativeRotation.mul(localRotation);
        }
    }
}
