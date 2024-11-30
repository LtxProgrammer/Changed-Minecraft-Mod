package net.ltxprogrammer.changed.client.renderer.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.HumanoidArm;

public interface TripleArmedModel<T extends ChangedEntity> extends DoubleArmedModel<T> {
    void translateToMiddleHand(T entity, HumanoidArm arm, PoseStack poseStack);

    ModelPart getMiddleArm(HumanoidArm arm);
}
