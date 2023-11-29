package net.ltxprogrammer.changed.client.renderer.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.ltxprogrammer.changed.client.ModelPartStem;
import net.ltxprogrammer.changed.client.renderer.LatexHumanoidRenderer;
import net.ltxprogrammer.changed.client.renderer.animate.LatexAnimator;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.extension.ChangedCompatibility;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public abstract class LatexHumanoidModel<T extends LatexEntity> extends EntityModel<T> implements ArmedModel, HeadedModel, TorsoedModel {
    public static final CubeDeformation NO_DEFORMATION = CubeDeformation.NONE;
    public static final CubeDeformation TEXTURE_DEFORMATION = new CubeDeformation(-0.01F);

    protected final ModelPart rootModelPart;

    public LatexHumanoidModel(ModelPart root) {
        this.rootModelPart = root;
    }

    public void prepareMobModel(LatexAnimator<T, ? extends EntityModel<T>> animator, T entity, float p_102862_, float p_102863_, float partialTicks) {
        super.prepareMobModel(entity, p_102862_, p_102863_, partialTicks);
        animator.swimAmount = entity.getSwimAmount(partialTicks);
        animator.crouching = entity.isCrouching();
        HumanoidModel.ArmPose humanoidmodel$armpose = LatexHumanoidRenderer.getArmPose(entity, InteractionHand.MAIN_HAND);
        HumanoidModel.ArmPose humanoidmodel$armpose1 = LatexHumanoidRenderer.getArmPose(entity, InteractionHand.OFF_HAND);
        if (humanoidmodel$armpose.isTwoHanded()) {
            humanoidmodel$armpose1 = entity.getOffhandItem().isEmpty() ? HumanoidModel.ArmPose.EMPTY : HumanoidModel.ArmPose.ITEM;
        }

        if (entity.getMainArm() == HumanoidArm.RIGHT) {
            animator.rightArmPose = humanoidmodel$armpose;
            animator.leftArmPose = humanoidmodel$armpose1;
        } else {
            animator.rightArmPose = humanoidmodel$armpose1;
            animator.leftArmPose = humanoidmodel$armpose;
        }

        if (ChangedCompatibility.isFirstPersonRendering()) {
            getHead().visible = false;
            getTorso().visible = !entity.isVisuallySwimming();
        }

        else {
            getHead().visible = true;
            getTorso().visible = true;
        }
    }

    public abstract ModelPart getArm(HumanoidArm arm);

    public void translateToHand(HumanoidArm p_102854_, PoseStack p_102855_) {
        this.getArm(p_102854_).translateAndRotate(p_102855_);
        if (this instanceof LatexHumanoidModelInterface modelInterface)
            p_102855_.translate(0.0, (modelInterface.getAnimator().armLength - 12.0f) / 20.0, 0.0);
    }

    private Stream<ModelPartStem> getAllPartsFor(ModelPart root) {
        return Stream.concat(Stream.of(new ModelPartStem(root)), root.children.values().stream().flatMap(this::getAllPartsFor).map(stem -> stem.withParent(root)));
    }

    public Stream<ModelPartStem> getAllParts() {
        return getAllPartsFor(rootModelPart);
    }

    public ModelPart getRandomModelPart(Random random) {
        List<ModelPart> partList = rootModelPart.getAllParts().toList();
        return partList.get(random.nextInt(partList.size()));
    }

    protected static final Vector3f HEAD_OFFSET = new Vector3f(0.0f, 26.5f, 0.0f);
    protected static final Vector3f TORSO_OFFSET = new Vector3f(0.0f, 25.5f, 0.0f);
    protected static final Vector3f RIGHT_ARM_OFFSET = new Vector3f(5.0f, 24.5f, 0.0f);
    protected static final Vector3f LEFT_ARM_OFFSET = new Vector3f(-5.0f, 24.5f, 0.0f);
    @Deprecated
    public static MeshDefinition process(MeshDefinition mesh) {
        var root = mesh.getRoot();
        var head = root.getChild("Head");
        var torso = root.getChild("Torso");
        var rightArm = root.getChild("RightArm");
        var leftArm = root.getChild("LeftArm");

        head.cubes.forEach(cube -> cube.origin.add(HEAD_OFFSET));
        torso.cubes.forEach(cube -> cube.origin.add(TORSO_OFFSET));
        rightArm.cubes.forEach(cube -> cube.origin.add(RIGHT_ARM_OFFSET));
        leftArm.cubes.forEach(cube -> cube.origin.add(LEFT_ARM_OFFSET));

        head.children.forEach((name, part) -> part.partPose = PartPose.offsetAndRotation(
                part.partPose.x + HEAD_OFFSET.x(),
                part.partPose.y + HEAD_OFFSET.y(),
                part.partPose.z + HEAD_OFFSET.z(),
                part.partPose.xRot,
                part.partPose.yRot,
                part.partPose.zRot)
        );

        torso.children.forEach((name, part) -> part.partPose = PartPose.offsetAndRotation(
                part.partPose.x + TORSO_OFFSET.x(),
                part.partPose.y + TORSO_OFFSET.y(),
                part.partPose.z + TORSO_OFFSET.z(),
                part.partPose.xRot,
                part.partPose.yRot,
                part.partPose.zRot)
        );

        rightArm.children.forEach((name, part) -> part.partPose = PartPose.offsetAndRotation(
                part.partPose.x + RIGHT_ARM_OFFSET.x(),
                part.partPose.y + RIGHT_ARM_OFFSET.y(),
                part.partPose.z + RIGHT_ARM_OFFSET.z(),
                part.partPose.xRot,
                part.partPose.yRot,
                part.partPose.zRot)
        );

        leftArm.children.forEach((name, part) -> part.partPose = PartPose.offsetAndRotation(
                part.partPose.x + LEFT_ARM_OFFSET.x(),
                part.partPose.y + LEFT_ARM_OFFSET.y(),
                part.partPose.z + LEFT_ARM_OFFSET.z(),
                part.partPose.xRot,
                part.partPose.yRot,
                part.partPose.zRot)
        );

        return mesh;
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

    public static abstract class LatexRemodel<T extends LatexEntity, M extends EntityModel<T>> extends LatexHumanoidModel<T> implements LatexHumanoidModelInterface<T, M> {
        public LatexRemodel(ModelPart root) {
            super(root);
        }

        @Override
        public final void prepareMobModel(@NotNull T p_102861_, float p_102862_, float p_102863_, float p_102864_) {
            this.prepareMobModel(getAnimator(), p_102861_, p_102862_, p_102863_, p_102864_);
        }

        @Override
        public final void setupHand() {
            getAnimator().setupHand();
        }

        @Override
        public final void setupAnim(T p_102618_, float p_102619_, float p_102620_, float p_102621_, float p_102622_, float p_102623_) {
            getAnimator().setupAnim(p_102618_, p_102619_, p_102620_, p_102621_, p_102622_, p_102623_);
        }

        @Override
        public PoseStack getPlacementCorrectors(CorrectorType type) {
            PoseStack stack = LatexHumanoidModelInterface.super.getPlacementCorrectors(type);
            if (type.isArm())
                stack.translate(0.10 * (type == CorrectorType.RIGHT_ARM ? -1 : 1), -0.20, 0.0);
            return stack;
        }
    }
}
