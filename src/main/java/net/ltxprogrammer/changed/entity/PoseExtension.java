package net.ltxprogrammer.changed.entity;

import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;

public class PoseExtension {
    private final Pose underlyingPose;

    private final boolean usePoseDimensions;

    protected PoseExtension(Pose underlyingPose, boolean usePoseDimensions) {
        this.underlyingPose = underlyingPose;
        this.usePoseDimensions = usePoseDimensions;
    }

    @Override
    public String toString() {
        var key = ChangedRegistry.POSE_EXTENSION.getKey(this);
        if (key != null)
            return key.toString();
        else
            return super.toString();
    }

    public Pose getUnderlyingPose() {
        return underlyingPose;
    }

    public EntityDimensions getDimensions(ChangedEntity entity, EntityDimensions core, EntityDimensions poseDimensions) {
        return usePoseDimensions ? poseDimensions : core;
    }

    protected static class FixedSize extends PoseExtension {
        private final EntityDimensions dimensions;

        protected FixedSize(Pose underlyingPose, float width, float height) {
            super(underlyingPose, false);
            this.dimensions = EntityDimensions.fixed(width, height);
        }

        @Override
        public EntityDimensions getDimensions(ChangedEntity entity, EntityDimensions core, EntityDimensions poseDimensions) {
            return dimensions;
        }
    }

    protected static class ComputedSize extends PoseExtension {
        private final boolean scalable;
        private final float widthScale;
        private final float widthOffset;
        private final float heightScale;
        private final float heightOffset;

        private ComputedSize(Pose underlyingPose, boolean usePoseDimensions, boolean scalable, float widthScale, float widthOffset, float heightScale, float heightOffset) {
            super(underlyingPose, usePoseDimensions);
            this.scalable = scalable;
            this.widthScale = widthScale;
            this.widthOffset = widthOffset;
            this.heightScale = heightScale;
            this.heightOffset = heightOffset;
        }

        @Override
        public EntityDimensions getDimensions(ChangedEntity entity, EntityDimensions core, EntityDimensions poseDimensions) {
            EntityDimensions ref = super.getDimensions(entity, core, poseDimensions);

            return new EntityDimensions(
                    ref.width * widthScale + widthOffset,
                    ref.height * heightScale + heightOffset,
                    scalable
            );
        }
    }

    public static PoseExtension passthrough(Pose underlyingPose) {
        return new PoseExtension(underlyingPose, true);
    }

    public static PoseExtension computed(Pose underlyingPose, boolean usePoseDimensions, float widthScale, float widthOffset, float heightScale, float heightOffset) {
        return new ComputedSize(underlyingPose, usePoseDimensions, true, widthScale, widthOffset, heightScale, heightOffset);
    }

    public static PoseExtension computedFromCore(Pose underlyingPose, float widthScale, float widthOffset, float heightScale, float heightOffset) {
        return new ComputedSize(underlyingPose, false, true, widthScale, widthOffset, heightScale, heightOffset);
    }

    public static PoseExtension computedFromPose(Pose underlyingPose, float widthScale, float widthOffset, float heightScale, float heightOffset) {
        return new ComputedSize(underlyingPose, true, true, widthScale, widthOffset, heightScale, heightOffset);
    }

    public static PoseExtension fixed(Pose underlyingPose, float width, float height) {
        return new FixedSize(underlyingPose, width, height);
    }
}
