package net.ltxprogrammer.changed.client.renderer.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.client.ClientLivingEntityExtender;
import net.ltxprogrammer.changed.client.ModelPartStem;
import net.ltxprogrammer.changed.client.PoseStackExtender;
import net.ltxprogrammer.changed.client.renderer.ExoskeletonRenderer;
import net.ltxprogrammer.changed.client.tfanimations.HelperModel;
import net.ltxprogrammer.changed.client.animations.Limb;
import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.client.tfanimations.*;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.robot.Exoskeleton;
import net.ltxprogrammer.changed.extension.ChangedCompatibility;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.util.Mth;
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

    public void syncPropertyModel(T entity) {
        if (this instanceof AdvancedHumanoidModelInterface modelInterface)
            modelInterface.getAnimator(entity).writePropertyModel(this);
    }

    public PlayerModel<?> preparePropertyModel(T entity) {
        syncPropertyModel(entity);
        return this;
    }

    public void prepareMobModel(HumanoidAnimator<T, ? extends EntityModel<T>> animator, T entity, float p_102862_, float p_102863_, float partialTicks) {
        super.prepareMobModel(entity, p_102862_, p_102863_, partialTicks);
        this.setAllLimbsVisible(entity, true);
        animator.setupVariables(entity, partialTicks);

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
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        ((ClientLivingEntityExtender)entity.maybeGetUnderlying()).getOrderedAnimations().forEach(instance -> {
            instance.animate(this, Mth.positiveModulo(ageInTicks, 1.0f));
        });

        this.syncPropertyModel(entity);
    }

    public abstract ModelPart getArm(HumanoidArm arm);
    public abstract ModelPart getLeg(HumanoidArm leg);

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

    public void translateToHand(T entity, HumanoidArm arm, PoseStack poseStack) {
        this.getArm(arm).translateAndRotate(poseStack);
        if (this instanceof AdvancedHumanoidModelInterface modelInterface)
            poseStack.translate(0.0, (modelInterface.getAnimator(entity).armLength - 12.0f) / 20.0, 0.0);
    }

    private Stream<ModelPartStem> getAllPartsFor(ModelPart root) {
        return Stream.concat(Stream.of(new ModelPartStem(root)), root.children.values().stream().flatMap(this::getAllPartsFor).map(stem -> stem.withParent(root)));
    }

    public Stream<ModelPartStem> getAllParts() {
        return getAllPartsFor(rootModelPart);
    }

    public Stream<ModelPart> getRootLevelLimbs() {
        return rootModelPart.children.values().stream();
    }

    public void setAllLimbsVisible(T entity, boolean visible) {
        this.getRootLevelLimbs().forEach(part -> {
            part.visible = visible;
        });
    }

    public ModelPart getRandomModelPart(Random random) {
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
