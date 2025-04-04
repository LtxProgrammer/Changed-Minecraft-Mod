package net.ltxprogrammer.changed.client.animations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.ltxprogrammer.changed.client.renderer.model.*;
import net.ltxprogrammer.changed.entity.VisionType;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.HumanoidArm;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;

public enum Limb implements StringRepresentable {
    HEAD("head", HumanoidModel::getHead, AdvancedHumanoidModel::getHead),
    HEAD2("head2", HumanoidModel::getHead, model -> {
        if (model instanceof DoubleHeadedModel<?> doubleHeadedModel)
            return doubleHeadedModel.getOtherHead();
        return null;
    }),

    TORSO("torso", model -> model.body, AdvancedHumanoidModel::getTorso),

    LEFT_ARM("left_arm", model -> model.leftArm, model -> model.getArm(HumanoidArm.LEFT)),
    RIGHT_ARM("right_arm", model -> model.rightArm, model -> model.getArm(HumanoidArm.RIGHT)),

    LEFT_ARM2("left_arm2", model -> model.leftArm, model -> {
        if (model instanceof TripleArmedModel<?> tripleArmedModel)
            return tripleArmedModel.getMiddleArm(HumanoidArm.LEFT);
        if (model instanceof DoubleArmedModel<?> doubleArmedModel)
            return doubleArmedModel.getOtherArm(HumanoidArm.LEFT);
        return null;
    }, false),
    RIGHT_ARM2("right_arm2", model -> model.rightArm, model -> {
        if (model instanceof TripleArmedModel<?> tripleArmedModel)
            return tripleArmedModel.getMiddleArm(HumanoidArm.RIGHT);
        if (model instanceof DoubleArmedModel<?> doubleArmedModel)
            return doubleArmedModel.getOtherArm(HumanoidArm.RIGHT);
        return null;
    }, false),

    LEFT_ARM3("left_arm3", model -> model.leftArm, model -> {
        if (model instanceof TripleArmedModel<?> doubleArmedModel)
            return doubleArmedModel.getOtherArm(HumanoidArm.LEFT);
        return null;
    }, false),
    RIGHT_ARM3("right_arm3", model -> model.rightArm, model -> {
        if (model instanceof TripleArmedModel<?> doubleArmedModel)
            return doubleArmedModel.getOtherArm(HumanoidArm.RIGHT);
        return null;
    }, false),

    LEFT_LEG("left_leg", model -> model.leftLeg, model -> {
        if (model instanceof LowerTorsoedModel)
            return null;
        return model.getLeg(HumanoidArm.LEFT);
    }),
    RIGHT_LEG("right_leg", model -> model.rightLeg, model -> {
        if (model instanceof LowerTorsoedModel)
            return null;
        return model.getLeg(HumanoidArm.RIGHT);
    }),

    ABDOMEN("abdomen", model -> model.body, model -> {
        if (model instanceof LeglessModel leglessModel)
            return leglessModel.getAbdomen();
        return null;
    }, false),

    LOWER_TORSO("lower_torso", model -> model.body, model -> {
        if (model instanceof LowerTorsoedModel torsoedModel)
            return torsoedModel.getLowerTorso();
        return null;
    });

    public static final Codec<Limb> CODEC = Codec.STRING.comapFlatMap(Limb::fromSerial, Limb::getSerializedName);

    @Override
    public @NotNull String getSerializedName() {
        return serialName;
    }

    public static DataResult<Limb> fromSerial(String name) {
        return Arrays.stream(values()).filter(type -> type.serialName.equals(name))
                .findFirst().map(DataResult::success).orElseGet(() -> DataResult.error(name + " is not a valid Limb"));
    }

    private final String serialName;
    private final Function<HumanoidModel<?>, ModelPart> getModelPartFn;
    private final Function<AdvancedHumanoidModel<?>, ModelPart> getLatexModelPartFn;
    private final boolean isVanillaPart;

    Limb(String serialName, Function<HumanoidModel<?>, ModelPart> getModelPartFn, Function<AdvancedHumanoidModel<?>, ModelPart> getLatexModelPartFn) {
        this.serialName = serialName;
        this.getModelPartFn = getModelPartFn;
        this.getLatexModelPartFn = getLatexModelPartFn;
        this.isVanillaPart = true;
    }

    Limb(String serialName, Function<HumanoidModel<?>, ModelPart> getModelPartFn, Function<AdvancedHumanoidModel<?>, ModelPart> getLatexModelPartFn, boolean isVanillaPart) {
        this.serialName = serialName;
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

    public ModelPart getModelPart(EntityModel<?> model) {
        if (model instanceof HumanoidModel<?> humanoidModel)
            return getModelPart(humanoidModel);
        else if (model instanceof AdvancedHumanoidModel<?> advancedHumanoidModel)
            return getModelPart(advancedHumanoidModel);
        return null;
    }

    public Optional<ModelPart> getModelPartSafe(EntityModel<?> model) {
        return Optional.ofNullable(getModelPart(model));
    }

    public boolean isVanillaPart() {
        return isVanillaPart;
    }
}
