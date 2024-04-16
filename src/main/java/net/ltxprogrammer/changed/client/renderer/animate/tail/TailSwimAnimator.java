package net.ltxprogrammer.changed.client.renderer.animate.tail;

import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TailSwimAnimator<T extends ChangedEntity, M extends EntityModel<T>> extends AbstractTailAnimator<T, M> {
    public TailSwimAnimator(ModelPart tail, List<ModelPart> tailJoints) {
        super(tail, tailJoints);
    }

    @Override
    public HumanoidAnimator.AnimateStage preferredStage() {
        return HumanoidAnimator.AnimateStage.SWIM;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        tail.xRot = Mth.lerp(core.swimAmount, tail.xRot, -1.1f);
        float oldZ = tail.zRot;
        tail.zRot = Mth.lerp(core.swimAmount, 0.0F, tail.yRot);
        tail.yRot = Mth.lerp(core.swimAmount, oldZ, 0.0F);
    }
}
