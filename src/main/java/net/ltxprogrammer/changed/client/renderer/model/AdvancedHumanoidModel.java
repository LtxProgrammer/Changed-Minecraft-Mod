package net.ltxprogrammer.changed.client.renderer.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.client.ModelPartStem;
import net.ltxprogrammer.changed.client.PoseStackExtender;
import net.ltxprogrammer.changed.client.animations.AnimationContainer;
import net.ltxprogrammer.changed.client.renderer.accessory.WornExoskeletonRenderer;
import net.ltxprogrammer.changed.client.renderer.layers.AccessoryLayer;
import net.ltxprogrammer.changed.client.tfanimations.HelperModel;
import net.ltxprogrammer.changed.client.animations.Limb;
import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.client.tfanimations.*;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.robot.Exoskeleton;
import net.ltxprogrammer.changed.extension.ChangedCompatibility;
import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.HumanoidArm;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AdvancedHumanoidModel<T extends ChangedEntity> extends PlayerModel<T> implements AdvancedArmedModel<T>, HeadedModel, TorsoedModel {
    public static final CubeDeformation NO_DEFORMATION = CubeDeformation.NONE;
    public static final CubeDeformation TEXTURE_DEFORMATION = new CubeDeformation(-0.01F);
    protected static final ModelPart NULL_PART = new ModelPart(List.of(), Map.of());

    protected final ModelPart rootModelPart;

    public static ModelPart createNullPart(String... children) {
        return new ModelPart(List.of(), Arrays.stream(children).collect(Collectors.toMap(
                Function.identity(),
                name -> new ModelPart(List.of(), Map.of())
        )));
    }

    public AdvancedHumanoidModel(ModelPart root) {
        super(createNullPart("head", "hat", "body", "right_arm", "left_arm", "right_leg", "left_leg",
                "ear", "cloak", "left_sleeve", "right_sleeve", "left_pants", "right_pants", "jacket"), false);
        this.rootModelPart = root;
    }

    public abstract HumanoidAnimator<T, ?> getAnimator(T entity);

    public void setupHand(T entity) {
        getAnimator(entity).setupHand();
    }

    public void scaleForBody(PoseStack poseStack) {}
    public void scaleForHead(PoseStack poseStack) {}

    public void syncPropertyModel(T entity) {
        getAnimator(entity).writePropertyModel(this);
    }

    public PlayerModel<?> preparePropertyModel(T entity) {
        syncPropertyModel(entity);
        return this;
    }

    public PoseStack.Pose resetPoseStack = null;
    public void swapResetPoseStack(PoseStack poseStack) {
        // This function is to maybe reset any poseStack changes for exception case models (im looking at you centaur)
        if (resetPoseStack != null && poseStack instanceof PoseStackExtender extender) {
            var copied = extender.copyLast();
            extender.setPose(resetPoseStack);
            resetPoseStack = copied;
        }
    }

    @Override
    public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float partialTicks) {
        super.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
        this.setAllLimbsVisible(entity, true);
        this.getAnimator(entity).setupVariables(entity, partialTicks);

        if (ChangedCompatibility.isFirstPersonRendering()) {
            getHead().visible = false;
            getTorso().visible = !entity.isVisuallySwimming();
        }

        else {
            getHead().visible = true;
            getTorso().visible = true;
        }

        this.syncPropertyModel(entity);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        AnimationContainer.resetModel(this);

        this.getAnimator(entity).setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

        AnimationContainer.getForEntity(entity).ifPresent(
                animationContainer -> animationContainer.animateModel(this, entity, Mth.positiveModulo(ageInTicks, 1.0f))
        );

        Exoskeleton.getEntityExoskeleton(entity).ifPresent(pair -> {
            AccessoryLayer.getRenderer(pair.getSecond()).ifPresent(renderer -> {
                if (renderer instanceof WornExoskeletonRenderer exoRenderer) {
                    exoRenderer.getModel().animateWearerLimbs(this, pair.getFirst());
                }
            });
        });

        this.syncPropertyModel(entity);
    }

    public abstract ModelPart getArm(HumanoidArm arm);
    public abstract ModelPart getLeg(HumanoidArm leg);

    public @Nullable ModelPart getLimb(Limb limb) {
        return switch (limb) {
            case HEAD -> this.getHead();
            case HEAD2 -> {
                if (this instanceof TripleHeadedModel<?> tripleHeadedModel)
                    yield tripleHeadedModel.getCenterHead();
                if (this instanceof DoubleHeadedModel<?> doubleHeadedModel)
                    yield doubleHeadedModel.getOtherHead();
                yield null;
            }
            case HEAD3 -> {
                if (this instanceof TripleHeadedModel<?> tripleHeadedModel)
                    yield tripleHeadedModel.getOtherHead();
                yield null;
            }
            case TORSO -> this.getTorso();
            case LEFT_ARM -> this.getArm(HumanoidArm.LEFT);
            case RIGHT_ARM -> this.getArm(HumanoidArm.RIGHT);
            case LEFT_ARM2 -> {
                if (this instanceof TripleArmedModel<?> tripleArmedModel)
                    yield tripleArmedModel.getMiddleArm(HumanoidArm.LEFT);
                if (this instanceof DoubleArmedModel<?> doubleArmedModel)
                    yield doubleArmedModel.getOtherArm(HumanoidArm.LEFT);
                yield null;
            }
            case RIGHT_ARM2 -> {
                if (this instanceof TripleArmedModel<?> tripleArmedModel)
                    yield tripleArmedModel.getMiddleArm(HumanoidArm.RIGHT);
                if (this instanceof DoubleArmedModel<?> doubleArmedModel)
                    yield doubleArmedModel.getOtherArm(HumanoidArm.RIGHT);
                yield null;
            }
            case LEFT_ARM3 -> {
                if (this instanceof TripleArmedModel<?> tripleArmedModel)
                    yield tripleArmedModel.getOtherArm(HumanoidArm.LEFT);
                yield null;
            }
            case RIGHT_ARM3 -> {
                if (this instanceof TripleArmedModel<?> tripleArmedModel)
                    yield tripleArmedModel.getOtherArm(HumanoidArm.RIGHT);
                yield null;
            }
            case LEFT_LEG -> this.getLeg(HumanoidArm.LEFT);
            case RIGHT_LEG -> this.getLeg(HumanoidArm.RIGHT);
            case ABDOMEN -> {
                if (this instanceof LeglessModel leglessModel)
                    yield leglessModel.getAbdomen();
                yield null;
            }
            case LOWER_TORSO -> {
                if (this instanceof LowerTorsoedModel leglessModel)
                    yield leglessModel.getLowerTorso();
                yield null;
            }
            default -> null;
        };
    }

    @Nullable
    public HelperModel getTransfurHelperModel(Limb limb) {
        return switch (limb) {
            case HEAD -> TransfurHelper.getSnoutedHead();
            case TORSO -> TransfurHelper.getTailedTorso();
            case LEFT_LEG -> TransfurHelper.getDigitigradeLeftLeg();
            case RIGHT_LEG -> TransfurHelper.getDigitigradeRightLeg();
            case LEFT_ARM -> TransfurHelper.getBasicLeftArm();
            case RIGHT_ARM -> TransfurHelper.getBasicRightArm();
            default -> null;
        };
    }

    public boolean shouldPartTransfur(ModelPart part) {
        return true;
    }

    public boolean shouldModelSit(T entity) {
        return entity.isPassenger() && (entity.getVehicle() != null && entity.getVehicle().shouldRiderSit());
    }

    public void translateToHand(T entity, HumanoidArm arm, PoseStack poseStack) {
        this.getArm(arm).translateAndRotate(poseStack);
        poseStack.translate(0.0, (getAnimator(entity).armLength - 12.0f) / 20.0, 0.0);
    }

    @Deprecated
    private Stream<ModelPartStem> getAllPartsFor(ModelPart root) {
        return ModelPartStem.streamFromRoot(root);
    }

    public Stream<ModelPartStem> getAllParts() {
        return ModelPartStem.streamFromRoot(rootModelPart);
    }

    public Stream<ModelPart> getRootLevelLimbs() {
        return rootModelPart.children.values().stream();
    }

    public void setAllLimbsVisible(T entity, boolean visible) {
        this.getRootLevelLimbs().forEach(part -> {
            part.visible = visible;
        });
    }

    public ModelPart getRandomModelPart(RandomSource random) {
        List<ModelPart> partList = rootModelPart.getAllParts().toList();
        return partList.get(random.nextInt(partList.size()));
    }

    public static <T> T last(List<T> list) {
        return list.get(list.size()-1);
    }

    public static List<ModelPart.Cube> findLargestCube(ModelPart part) {
        ArrayList<ModelPart.Cube> list = new ArrayList<>(part.cubes);

        for (var entry : part.children.entrySet()) {
            list.addAll(findLargestCube(entry.getValue()));
        }

        list.sort((cubeA, cubeB) -> {
            float massA = (cubeA.maxX - cubeA.minX) * (cubeA.maxY - cubeA.minY) * (cubeA.maxZ - cubeA.minZ);
            float massB = (cubeB.maxX - cubeB.minX) * (cubeB.maxY - cubeB.minY) * (cubeB.maxZ - cubeB.minZ);
            return Float.compare(massB, massA);
        });

        return list;
    }

    public enum GrabState {
        EMPTY,
        REACH,
        HOLD
    }
}
