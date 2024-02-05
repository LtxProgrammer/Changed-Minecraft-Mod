package net.ltxprogrammer.changed.client.tfanimations;

import net.ltxprogrammer.changed.client.renderer.model.DoubleArmedModel;
import net.ltxprogrammer.changed.client.renderer.model.LatexHumanoidModel;
import net.ltxprogrammer.changed.client.renderer.model.LeglessModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.HumanoidArm;

import java.util.function.Function;

public enum Limb {
    HEAD(HumanoidModel::getHead, LatexHumanoidModel::getHead),
    TORSO(model -> model.body, LatexHumanoidModel::getTorso),

    LEFT_ARM(model -> model.leftArm, model -> model.getArm(HumanoidArm.LEFT)),
    RIGHT_ARM(model -> model.rightArm, model -> model.getArm(HumanoidArm.RIGHT)),

    LEFT_ARM2(model -> model.leftArm, model -> {
        if (model instanceof DoubleArmedModel doubleArmedModel)
            return doubleArmedModel.getOtherArm(HumanoidArm.LEFT);
        return null;
    }, false),
    RIGHT_ARM2(model -> model.rightArm, model -> {
        if (model instanceof DoubleArmedModel doubleArmedModel)
            return doubleArmedModel.getOtherArm(HumanoidArm.RIGHT);
        return null;
    }, false),

    LEFT_LEG(model -> model.leftLeg, model -> model.getLeg(HumanoidArm.LEFT)),
    RIGHT_LEG(model -> model.rightLeg, model -> model.getLeg(HumanoidArm.RIGHT)),

    ABDOMEN(model -> model.rightLeg, model -> {
        if (model instanceof LeglessModel leglessModel)
            return leglessModel.getAbdomen();
        return null;
    }, false);

    private final Function<HumanoidModel<?>, ModelPart> getModelPartFn;
    private final Function<LatexHumanoidModel<?>, ModelPart> getLatexModelPartFn;
    private final boolean isVanillaPart;

    Limb(Function<HumanoidModel<?>, ModelPart> getModelPartFn, Function<LatexHumanoidModel<?>, ModelPart> getLatexModelPartFn) {
        this.getModelPartFn = getModelPartFn;
        this.getLatexModelPartFn = getLatexModelPartFn;
        this.isVanillaPart = true;
    }

    Limb(Function<HumanoidModel<?>, ModelPart> getModelPartFn, Function<LatexHumanoidModel<?>, ModelPart> getLatexModelPartFn, boolean isVanillaPart) {
        this.getModelPartFn = getModelPartFn;
        this.getLatexModelPartFn = getLatexModelPartFn;
        this.isVanillaPart = isVanillaPart;
    }

    public ModelPart getModelPart(HumanoidModel<?> model) {
        return getModelPartFn.apply(model);
    }

    public ModelPart getModelPart(LatexHumanoidModel<?> model) {
        return getLatexModelPartFn.apply(model);
    }

    public boolean isVanillaPart() {
        return isVanillaPart;
    }

    public TransfurAnimator.ModelPose adjustModelPose(TransfurAnimator.ModelPose pose) {
        if (this == ABDOMEN)
            return pose.translate(2.0f, 0.0f, 0.0f);
        else
            return pose;
    }
}
