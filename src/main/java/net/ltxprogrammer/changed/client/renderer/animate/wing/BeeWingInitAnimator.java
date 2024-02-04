package net.ltxprogrammer.changed.client.renderer.animate.wing;

import net.ltxprogrammer.changed.client.renderer.animate.LatexAnimator;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class BeeWingInitAnimator<T extends LatexEntity, M extends EntityModel<T>> extends LatexAnimator.Animator<T, M> {
    private final ModelPart leftWingRoot;
    private final ModelPart rightWingRoot;

    public BeeWingInitAnimator(ModelPart leftWingRoot, ModelPart rightWingRoot) {
        this.leftWingRoot = leftWingRoot;
        this.rightWingRoot = rightWingRoot;
    }

    @Override
    public LatexAnimator.AnimateStage preferredStage() {
        return LatexAnimator.AnimateStage.INIT;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float wingRootYAgeLerp = Mth.lerp(core.ageLerp, 0.174532f, 0.3490659f);
        float wingRootZAgeLerp = Mth.lerp(core.ageLerp, 0.174532f, 0.2617994f);

        leftWingRoot.xRot = 0.0f;
        leftWingRoot.yRot = -wingRootYAgeLerp;
        leftWingRoot.zRot = -wingRootZAgeLerp;
        rightWingRoot.xRot = 0.0f;
        rightWingRoot.yRot = wingRootYAgeLerp;
        rightWingRoot.zRot = wingRootZAgeLerp;
    }
}
