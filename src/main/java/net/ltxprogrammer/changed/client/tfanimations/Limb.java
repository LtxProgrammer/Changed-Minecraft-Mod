package net.ltxprogrammer.changed.client.tfanimations;

import net.ltxprogrammer.changed.client.renderer.model.DoubleArmedModel;
import net.ltxprogrammer.changed.client.renderer.model.LatexHumanoidModel;
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
    }),
    RIGHT_ARM2(model -> model.rightArm, model -> {
        if (model instanceof DoubleArmedModel doubleArmedModel)
            return doubleArmedModel.getOtherArm(HumanoidArm.RIGHT);
        return null;
    }),

    LEFT_LEG(model -> model.leftLeg, model -> model.getLeg(HumanoidArm.LEFT)),
    RIGHT_LEG(model -> model.rightLeg, model -> model.getLeg(HumanoidArm.RIGHT));

    private final Function<HumanoidModel<?>, ModelPart> getModelPartFn;
    private final Function<LatexHumanoidModel<?>, ModelPart> getLatexModelPartFn;

    Limb(Function<HumanoidModel<?>, ModelPart> getModelPartFn, Function<LatexHumanoidModel<?>, ModelPart> getLatexModelPartFn) {
        this.getModelPartFn = getModelPartFn;
        this.getLatexModelPartFn = getLatexModelPartFn;
    }

    public ModelPart getModelPart(HumanoidModel<?> model) {
        return getModelPartFn.apply(model);
    }

    public ModelPart getModelPart(LatexHumanoidModel<?> model) {
        return getLatexModelPartFn.apply(model);
    }
}
