package net.ltxprogrammer.changed.client.renderer.animate.upperbody;

import net.ltxprogrammer.changed.client.renderer.animate.LatexAnimator;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import static net.ltxprogrammer.changed.client.renderer.animate.upperbody.SharkUpperBodySwimAnimator.*;
import static net.ltxprogrammer.changed.client.renderer.animate.wing.DragonWingCreativeFlyAnimator.BODY_FLY_SCALE;
import static net.ltxprogrammer.changed.client.renderer.animate.wing.DragonWingCreativeFlyAnimator.WING_FLAP_RATE;

public class DragonHeadCreativeFlyAnimator<T extends LatexEntity, M extends EntityModel<T>> extends AbstractHeadAnimator<T, M> {
    public DragonHeadCreativeFlyAnimator(ModelPart head) {
        super(head);
    }

    @Override
    public LatexAnimator.AnimateStage preferredStage() {
        return LatexAnimator.AnimateStage.CREATIVE_FLY;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float flapAmount = Mth.cos(ageInTicks * WING_FLAP_RATE);
        flapAmount = Mth.map(flapAmount * flapAmount, 0.0f, 1.0f, -BODY_FLY_SCALE, BODY_FLY_SCALE);
        head.y += Mth.lerp(core.flyAmount, 0.0f, flapAmount);
    }
}
