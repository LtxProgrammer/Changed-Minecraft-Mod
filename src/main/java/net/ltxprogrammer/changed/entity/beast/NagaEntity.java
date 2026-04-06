package net.ltxprogrammer.changed.entity.beast;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;
import org.joml.Vector3f;

public interface NagaEntity {
    class SlitherJoint<T extends Entity & NagaEntity> extends PartEntity<T> {
        public final float length;
        public Vec3 position = Vec3.ZERO;
        public Vec3 positionO = Vec3.ZERO;
        public final Vector3f surfaceNormal = new Vector3f(0.0f, 1.0f, 0.0f);
        public final Vector3f surfaceNormalO = new Vector3f(0.0f, 1.0f, 0.0f);
        private final EntityDimensions size;

        public SlitherJoint(T parentMob, float length, float height) {
            super(parentMob);
            this.length = length;
            this.size = EntityDimensions.scalable(length, height);
            this.refreshDimensions();
        }

        public float getJointX(float partialTicks) {
            return (float)Mth.lerp(partialTicks, positionO.x, position.x);
        }

        public float getJointY(float partialTicks) {
            return (float)Mth.lerp(partialTicks, positionO.y, position.y);
        }

        public float getJointZ(float partialTicks) {
            return (float)Mth.lerp(partialTicks, positionO.z, position.z);
        }

        public float getNormalX(float partialTicks) {
            return Mth.lerp(partialTicks, surfaceNormalO.x, surfaceNormal.x);
        }

        public float getNormalY(float partialTicks) {
            return Mth.lerp(partialTicks, surfaceNormalO.y, surfaceNormal.y);
        }

        public float getNormalZ(float partialTicks) {
            return Mth.lerp(partialTicks, surfaceNormalO.z, surfaceNormal.z);
        }

        public void updateBoundingBox(Vec3 nextJointPosition, Vec3 nextJointPositionO) {
            double x = this.getParent().getX();
            double y = this.getParent().getY();
            double z = this.getParent().getZ();

            this.setPos(
                    x + Mth.lerp(0.5, this.position.x, nextJointPosition.x),
                    y + Mth.lerp(0.5, this.position.y, nextJointPosition.y),
                    z + Mth.lerp(0.5, this.position.z, nextJointPosition.z)
            );
            this.xOld = x + Mth.lerp(0.5, this.positionO.x, nextJointPositionO.x);
            this.yOld = y + Mth.lerp(0.5, this.positionO.y, nextJointPositionO.y);
            this.zOld = z + Mth.lerp(0.5, this.positionO.z, nextJointPositionO.z);
            this.xo = this.xOld;
            this.yo = this.yOld;
            this.zo = this.zOld;
        }

        @Override
        protected void defineSynchedData() {

        }

        @Override
        protected void readAdditionalSaveData(CompoundTag tag) {

        }

        @Override
        protected void addAdditionalSaveData(CompoundTag tag) {

        }

        public EntityDimensions getDimensions(Pose pose) {
            return this.size;
        }
    }

    SlitherJoint getSlitherJoint(int index);

    int getSlitherJointCount();
}