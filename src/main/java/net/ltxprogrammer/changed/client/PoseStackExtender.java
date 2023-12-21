package net.ltxprogrammer.changed.client;

import com.mojang.blaze3d.vertex.PoseStack;

import java.util.function.Consumer;
import java.util.function.Function;

public interface PoseStackExtender {
    /**
     * Calls consumer with the second to last pose. The pose given to consumer is a copy, and does not affect the poseStack
     * @param consumer
     */
    <T> T popAndRepush(Function<PoseStack.Pose, T> consumer);

    PoseStack.Pose first();
}
