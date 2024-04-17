package net.ltxprogrammer.changed.client.tfanimations;

import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.client.renderer.model.DoubleArmedModel;
import net.ltxprogrammer.changed.client.renderer.model.LeglessModel;
import net.ltxprogrammer.changed.client.renderer.model.LowerTorsoedModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.HumanoidArm;

import java.util.Optional;
import java.util.function.Function;

public enum Limb {
    HEAD(HumanoidModel::getHead, AdvancedHumanoidModel::getHead),
    TORSO(model -> model.body, AdvancedHumanoidModel::getTorso),

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

    LEFT_LEG(model -> model.leftLeg, model -> {
        if (model instanceof LowerTorsoedModel)
            return null;
        return model.getLeg(HumanoidArm.LEFT);
    }),
    RIGHT_LEG(model -> model.rightLeg, model -> {
        if (model instanceof LowerTorsoedModel)
            return null;
        return model.getLeg(HumanoidArm.RIGHT);
    }),

    ABDOMEN(model -> model.body, model -> {
        if (model instanceof LeglessModel leglessModel)
            return leglessModel.getAbdomen();
        return null;
    }, false),

    LOWER_TORSO(model -> model.body, model -> {
        if (model instanceof LowerTorsoedModel torsoedModel)
            return torsoedModel.getLowerTorso();
        return null;
    });

    private final Function<HumanoidModel<?>, ModelPart> getModelPartFn;
    private final Function<AdvancedHumanoidModel<?>, ModelPart> getLatexModelPartFn;
    private final boolean isVanillaPart;

    Limb(Function<HumanoidModel<?>, ModelPart> getModelPartFn, Function<AdvancedHumanoidModel<?>, ModelPart> getLatexModelPartFn) {
        this.getModelPartFn = getModelPartFn;
        this.getLatexModelPartFn = getLatexModelPartFn;
        this.isVanillaPart = true;
    }

    Limb(Function<HumanoidModel<?>, ModelPart> getModelPartFn, Function<AdvancedHumanoidModel<?>, ModelPart> getLatexModelPartFn, boolean isVanillaPart) {
        this.getModelPartFn = getModelPartFn;
        this.getLatexModelPartFn = getLatexModelPartFn;
        this.isVanillaPart = isVanillaPart;
    }

    public ModelPart getModelPart(HumanoidModel<?> model) {
        return getModelPartFn.apply(model);
    }

    public Optional<ModelPart> getModelPartSafe(HumanoidModel<?> model) {
        return Optional.ofNullable(getModelPartFn.apply(model));
    }

    public ModelPart getModelPart(AdvancedHumanoidModel<?> model) {
        return getLatexModelPartFn.apply(model);
    }

    public Optional<ModelPart> getModelPartSafe(AdvancedHumanoidModel<?> model) {
        return Optional.ofNullable(getLatexModelPartFn.apply(model));
    }

    public boolean isVanillaPart() {
        return isVanillaPart;
    }

    public TransfurAnimator.ModelPose adjustModelPose(TransfurAnimator.ModelPose pose) {
        if (this == ABDOMEN)
            return pose.translate(0.0f, 12.0f, 0.0f);
        else if (this == LOWER_TORSO)
            return pose.translate(0.0f, 12.0f, 0.0f);
        else
            return pose;
    }
}
