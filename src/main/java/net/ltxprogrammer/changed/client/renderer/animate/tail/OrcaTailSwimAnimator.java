package net.ltxprogrammer.changed.client.renderer.animate.tail;

import net.ltxprogrammer.changed.client.renderer.animate.LatexAnimator;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static net.ltxprogrammer.changed.client.renderer.animate.upperbody.SharkUpperBodySwimAnimator.SWIM_RATE;

public class OrcaTailSwimAnimator<T extends LatexEntity, M extends EntityModel<T>> extends AbstractTailAnimator<T, M> {
    public OrcaTailSwimAnimator(ModelPart tail, List<ModelPart> tailJoints) {
        super(tail, tailJoints);
    }

    @Override
    public LatexAnimator.AnimateStage preferredStage() {
        return LatexAnimator.AnimateStage.SWIM;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        tail.xRot = Mth.lerp(core.swimAmount, tail.xRot,  Mth.DEG_TO_RAD * -67.5f);
        tail.zRot = Mth.lerp(core.swimAmount, tail.zRot, 0.0f);
        tail.yRot = Mth.lerp(core.swimAmount, tail.yRot, 0.0f);
        //tail.yRot = Mth.lerp(core.swimAmount, tail.yRot, 0.25F * Mth.cos(limbSwing * 0.33333334F + (float)Math.PI));

        float offset = 0.0F;
        for (ModelPart joint : tailJoints) {
            joint.xRot = Mth.lerp(core.swimAmount, joint.xRot, -0.35F * Mth.cos(limbSwing * SWIM_RATE -
                    (((float)Math.PI / 3.0F) * offset)));
            joint.yRot = Mth.lerp(core.swimAmount, joint.yRot, 0.0f);
            joint.zRot = Mth.lerp(core.swimAmount, joint.zRot, 0.0f);
            offset += 0.75F;
        }
    }
}
