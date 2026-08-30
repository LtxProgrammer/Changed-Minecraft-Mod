package net.ltxprogrammer.changed.extension.vivecraft;

import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.entity.ChangedEntity;

public interface RendererScaleAccessor<T extends ChangedEntity> {
    void vivecraft$scale(T entity, PoseStack poseStack, float partialTick);
}
