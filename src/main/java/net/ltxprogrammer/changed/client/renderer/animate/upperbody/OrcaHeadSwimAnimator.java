package net.ltxprogrammer.changed.client.renderer.animate.upperbody;

import net.ltxprogrammer.changed.client.renderer.animate.LatexAnimator;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import static net.ltxprogrammer.changed.client.renderer.animate.upperbody.OrcaUpperBodySwimAnimator.*;

public class OrcaHeadSwimAnimator<T extends LatexEntity, M extends EntityModel<T>> extends AbstractHeadAnimator<T, M> {
    public OrcaHeadSwimAnimator(ModelPart head) {
        super(head);
    }

    @Override
    public LatexAnimator.AnimateStage preferredStage() {
        return LatexAnimator.AnimateStage.SWIM;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float midSegmentSway = -TORSO_SWAY_SCALE * Mth.cos(limbSwing * SWIM_RATE -
                (((float)Math.PI / 3.0F) * -0.75f));
        float headSway = -0.15F * Mth.cos(limbSwing * SWIM_RATE -
                (((float)Math.PI / 3.0F) * -1.5f));
        float headOffset = Mth.map(midSegmentSway, -0.35f, 0.35f, 2.0f, -2.0f);
        head.z += Mth.lerp(core.swimAmount, 0.0f, headOffset * SWIM_SCALE);
        head.xRot += Mth.lerp(core.swimAmount, 0.0F, headSway);
    }
}
