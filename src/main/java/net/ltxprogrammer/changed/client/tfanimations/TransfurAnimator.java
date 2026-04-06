package net.ltxprogrammer.changed.client.tfanimations;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.ltxprogrammer.changed.client.FormRenderHandler;
import net.ltxprogrammer.changed.client.PoseStackExtender;
import net.ltxprogrammer.changed.client.animations.AnimationContainer;
import net.ltxprogrammer.changed.client.animations.Limb;
import net.ltxprogrammer.changed.client.renderer.AdvancedHumanoidRenderer;
import net.ltxprogrammer.changed.client.renderer.accessory.TransitionalAccessory;
import net.ltxprogrammer.changed.client.renderer.layers.AccessoryLayer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexHumanoidArmorLayer;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.data.AccessorySlotContext;
import net.ltxprogrammer.changed.data.AccessorySlots;
import net.ltxprogrammer.changed.entity.AccessoryEntities;
import net.ltxprogrammer.changed.entity.LimbCoverTransition;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.extension.ChangedCompatibility;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.util.Color3;
import net.ltxprogrammer.changed.util.ReversibleKeyedMap;
import net.ltxprogrammer.changed.util.Transition;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

public abstract class TransfurAnimator {
    public static ReversibleKeyedMap<ModelPart, EntityGeometry> TRANSITIONS_CACHE = new ReversibleKeyedMap<>();

    public record ModelPose(PoseStack.Pose matrix, PartPose pose) {
        public ModelPose translate(float x, float y, float z) {
            return new ModelPose(matrix, PartPose.offsetAndRotation(
                    pose.x + x,
                    pose.y + y,
                    pose.z + z,
                    pose.xRot,
                    pose.yRot,
                    pose.zRot
            ));
        }

        public ModelPose copyRotation(ModelPart part) {
            return new ModelPose(matrix, PartPose.offsetAndRotation(
                    pose.x,
                    pose.y,
                    pose.z,
                    part.xRot,
                    part.yRot,
                    part.zRot
            ));
        }

        public ModelPose averageRotation(ModelPart part1, ModelPart part2) {
            return new ModelPose(matrix, PartPose.offsetAndRotation(
                    pose.x,
                    pose.y,
                    pose.z,
                    (part1.xRot + part2.xRot) / 2f,
                    (part1.yRot + part2.yRot) / 2f,
                    (part1.zRot + part2.zRot) / 2f
            ));
        }
    }
    public static final ModelPose NULL_POSE = new ModelPose(new PoseStack().last(), PartPose.ZERO);

    private static final Map<ModelPart, ModelPose> CAPTURED_MODELS = new HashMap<>();

    private static EntityGeometry deepCopyPart(@Nullable ModelPart part, Predicate<ModelPart> predicate, boolean copyVisibility) {
        if (part == null)
            return null;
        EntityGeometry copied = new EntityGeometry(part, predicate);
        if (!copyVisibility)
            copied.visit(visited -> visited.visible = true);
        return copied;
    }

    private static Pair<EntityGeometry, EntityGeometry> matchCubeCount(EntityGeometry begin, EntityGeometry end) {
        return matchCubeCount(begin, end, SplittingSource.empty(), SplittingSource.empty());
    }

    private static final Comparator<EntityGeometry.Cube> MASS_SORT = (left, right) -> {
        var max = left.getMax();
        var min = left.getMin();
        max.sub(min);
        float leftMass = max.x * max.y * max.z;

        max = right.getMax();
        min = right.getMin();
        max.sub(min);
        float rightMass = max.x * max.y * max.z;

        return Float.compare(rightMass, leftMass);
    };

    private static final Comparator<String> PRIORITIZE_SKELETON = (left, right) -> {
        if (EntityGeometry.isSkeletonName(left) && !EntityGeometry.isSkeletonName(right))
            return -1;
        else if (!EntityGeometry.isSkeletonName(left) && EntityGeometry.isSkeletonName(right))
            return 1;
        return left.compareTo(right);
    };

    private static Pair<EntityGeometry, EntityGeometry> matchCubeCount(EntityGeometry begin, EntityGeometry end,
                                                                       SplittingSource beginSplittingSource, SplittingSource endSplittingSource) {
        int targetCubeCount = Math.max(begin.cubes.size(), end.cubes.size());
        int targetChildrenCount = Math.max(begin.children.size(), end.children.size());

        // Sort cubes to have consistent results across similar models
        List<EntityGeometry.Cube> beginCubesSorted = begin.cubes.stream().sorted(MASS_SORT).toList();
        List<EntityGeometry.Cube> endCubesSorted = end.cubes.stream().sorted(MASS_SORT).toList();

        List<EntityGeometry.Cube> beginResultCubes = new ArrayList<>(targetCubeCount);
        List<EntityGeometry.Cube> endResultCubes = new ArrayList<>(targetCubeCount);
        Map<String, EntityGeometry> beginResultChildren = new Object2ObjectArrayMap<>(targetChildrenCount);
        Map<String, EntityGeometry> endResultChildren = new Object2ObjectArrayMap<>(targetChildrenCount);

        SplittingSource subBeginSplittingSource = SplittingSource.forSources(
                SplittingSource.forSourceCubes(beginCubesSorted),
                beginSplittingSource
        );
        SplittingSource subEndSplittingSource = SplittingSource.forSources(
                SplittingSource.forSourceCubes(endCubesSorted),
                endSplittingSource
        );

        if (Math.min(begin.cubes.size(), end.cubes.size()) == 0) {
            if (subBeginSplittingSource.isEmpty() || subEndSplittingSource.isEmpty())
                targetCubeCount = 0;
        }

        if (targetCubeCount >= 1) {
            if (beginCubesSorted.size() == 1 && endCubesSorted.size() == 1) { // 1:1
                beginResultCubes.addAll(beginCubesSorted);
                endResultCubes.addAll(endCubesSorted);
            } else if (beginCubesSorted.size() == 1 && !endCubesSorted.isEmpty()) {
                beginResultCubes.addAll(EntityGeometry.Cube.segment(subBeginSplittingSource, endCubesSorted));
                endResultCubes.addAll(endCubesSorted);
            } else if (endCubesSorted.size() == 1 && !beginCubesSorted.isEmpty()) {
                beginResultCubes.addAll(beginCubesSorted);
                endResultCubes.addAll(EntityGeometry.Cube.segment(subEndSplittingSource, beginCubesSorted));
            } else {
                if (endCubesSorted.size() > beginCubesSorted.size()) {
                    beginResultCubes.addAll(EntityGeometry.Cube.segment(subBeginSplittingSource, endCubesSorted));
                    endResultCubes.addAll(endCubesSorted);
                } else {
                    beginResultCubes.addAll(beginCubesSorted);
                    endResultCubes.addAll(EntityGeometry.Cube.segment(subEndSplittingSource, beginCubesSorted));
                }
            }

            for (int i = 0; i < beginResultCubes.size(); i++) {
                SplittingSource source = subBeginSplittingSource.findSourceFor(beginResultCubes.get(i));
                final int myIndex = i;
                if (source != null)
                    source.setResizeConsumer((oldCube, newCube) -> beginResultCubes.set(myIndex, newCube));
            }

            for (int i = 0; i < endResultCubes.size(); i++) {
                SplittingSource source = subEndSplittingSource.findSourceFor(endResultCubes.get(i));
                final int myIndex = i;
                if (source != null)
                    source.setResizeConsumer((oldCube, newCube) -> endResultCubes.set(myIndex, newCube));
            }
        }

        if (targetChildrenCount >= 1) {
            for (var childName : begin.children.keySet().stream().sorted(PRIORITIZE_SKELETON).toList()) {
                var child = begin.children.get(childName);
                if (endResultChildren.containsKey(childName))
                    continue;

                // TODO maybe fuzz for similar children

                if (end.children.containsKey(childName)) {
                    var pair = matchCubeCount(child, end.children.get(childName));
                    beginResultChildren.put(childName, pair.getFirst());
                    endResultChildren.put(childName, pair.getSecond());
                    continue;
                }

                // Similar not found, creating new child

                var pair = matchCubeCount(
                        new EntityGeometry(child).embedPositioning(/* Discard rotation, flatten position */),
                        new EntityGeometry(/* Empty Part */),
                        SplittingSource.empty(), subEndSplittingSource);
                beginResultChildren.put(childName, child);
                endResultChildren.put(childName, pair.getSecond());
            }

            for (var childName : end.children.keySet().stream().sorted(PRIORITIZE_SKELETON).toList()) {
                var child = end.children.get(childName);
                if (beginResultChildren.containsKey(childName))
                    continue;

                if (begin.children.containsKey(childName)) {
                    var pair = matchCubeCount(begin.children.get(childName), child);
                    beginResultChildren.put(childName, pair.getFirst());
                    endResultChildren.put(childName, pair.getSecond());
                    continue;
                }

                // Similar not found, creating new child

                var pair = matchCubeCount(
                        new EntityGeometry(/* Empty Part */),
                        new EntityGeometry(child).embedPositioning(/* Discard rotation, flatten position */),
                        subBeginSplittingSource, SplittingSource.empty());
                beginResultChildren.put(childName, pair.getFirst());
                endResultChildren.put(childName, child);
            }
        }

        if (beginResultCubes.size() != targetCubeCount || endResultCubes.size() != targetCubeCount)
            throw new IllegalStateException("Begin and ending cubes aren't both equal to target count");

        var beginResult = new EntityGeometry(beginResultCubes, beginResultChildren);
        var endResult = new EntityGeometry(endResultCubes, endResultChildren);

        for (int i = 0; i < beginResult.cubes.size(); i++) { // Update resize consumers to new lists
            SplittingSource source = subBeginSplittingSource.findSourceFor(beginResult.cubes.get(i));
            final int myIndex = i;
            if (source != null)
                source.setResizeConsumer((oldCube, newCube) -> beginResult.cubes.set(myIndex, newCube));
        }

        for (int i = 0; i < endResult.cubes.size(); i++) {
            SplittingSource source = subEndSplittingSource.findSourceFor(endResult.cubes.get(i));
            final int myIndex = i;
            if (source != null)
                source.setResizeConsumer((oldCube, newCube) -> endResult.cubes.set(myIndex, newCube));
        }

        return Pair.of(beginResult, endResult);
    }

    private static EntityGeometry.Vertex lerpVertex(EntityGeometry.Vertex a, EntityGeometry.Vertex b, float lerp) {
        return new EntityGeometry.Vertex(
                Mth.lerp(lerp, a.pos.x(), b.pos.x()),
                Mth.lerp(lerp, a.pos.y(), b.pos.y()),
                Mth.lerp(lerp, a.pos.z(), b.pos.z()),
                Mth.lerp(lerp, a.u, b.u),
                Mth.lerp(lerp, a.v, b.v)
        );
    }

    private static final float GOOP_CUBE_WIDTH = 16.0f;
    private static final float GOOP_CUBE_HEIGHT = 16.0f;

    private static EntityGeometry.Polygon lerpPolygon(@NotNull EntityGeometry.Polygon a, @NotNull EntityGeometry.Polygon b, float lerp, boolean remapUV) {
        EntityGeometry.Polygon ret = new EntityGeometry.Polygon(a);

        for (int i = 0; i < ret.vertices.length; ++i) {
            ret.vertices[i] = lerpVertex(a.vertices[i], b.vertices[i], lerp);
        }

        if (remapUV) {
            float polygonWidth;
            float polygonHeight;

            if (Mth.abs(a.normal.x()) > 0f) {
                polygonWidth = Mth.abs(ret.vertices[1].pos.z() - ret.vertices[0].pos.z());
                polygonHeight = Mth.abs(ret.vertices[2].pos.y() - ret.vertices[1].pos.y());
            } else if (Mth.abs(a.normal.y()) > 0f) {
                polygonWidth = Mth.abs(ret.vertices[1].pos.x() - ret.vertices[0].pos.x());
                polygonHeight = Mth.abs(ret.vertices[2].pos.z() - ret.vertices[1].pos.z());
            } else {
                polygonWidth = Mth.abs(ret.vertices[1].pos.x() - ret.vertices[0].pos.x());
                polygonHeight = Mth.abs(ret.vertices[2].pos.y() - ret.vertices[1].pos.y());
            }

            polygonWidth /= GOOP_CUBE_WIDTH;
            polygonHeight /= GOOP_CUBE_HEIGHT;

            ret.vertices[0] = ret.vertices[0].remap(polygonWidth, 0.0f);
            ret.vertices[1] = ret.vertices[1].remap(0.0f, 0.0f);
            ret.vertices[2] = ret.vertices[2].remap(0.0f, polygonHeight);
            ret.vertices[3] = ret.vertices[3].remap(polygonWidth, polygonHeight);
        }

        return ret;
    }

    private static EntityGeometry.Cube lerpCube(@NotNull EntityGeometry.Cube a, @NotNull EntityGeometry.Cube b, float lerp, boolean remapUV) {
        EntityGeometry.Cube ret = new EntityGeometry.Cube(a);

        for (var normal : Direction.values()) {
            var faceA = a.getFace(normal);
            var faceB = b.getFace(normal);
            if (faceA == null || faceB == null) {
                ret.removeFace(normal);
                continue;
            }

            ret.putFace(lerpPolygon(faceA, faceB, lerp, remapUV));
        }

        return ret;
    }

    private static EntityGeometry lerpModelPart(@NotNull EntityGeometry a, @NotNull EntityGeometry b, float lerp, boolean remapUV) {
        List<EntityGeometry.Cube> copiedCubes = new ArrayList<>();
        for (int i = 0; i < a.cubes.size(); ++i)
            copiedCubes.add(lerpCube(a.cubes.get(i), b.cubes.get(i), lerp, remapUV));
        Map<String, EntityGeometry> copiedChildren = new HashMap<>();
        for (var k : a.children.keySet())
            copiedChildren.put(k, lerpModelPart(a.children.get(k), b.children.get(k), lerp, remapUV));

        var lerped = new EntityGeometry(copiedCubes, copiedChildren);
        lerped.x = Mth.lerp(lerp, a.x, b.x);
        lerped.y = Mth.lerp(lerp, a.y, b.y);
        lerped.z = Mth.lerp(lerp, a.z, b.z);
        lerped.xRot = Mth.lerp(lerp, a.xRot, b.xRot);
        lerped.yRot = Mth.lerp(lerp, a.yRot, b.yRot);
        lerped.zRot = Mth.lerp(lerp, a.zRot, b.zRot);
        lerped.visible = a.visible && b.visible;
        return lerped;
    }

    private static Pair<EntityGeometry, EntityGeometry> createTransitionModels(EntityGeometry before, EntityGeometry after) {
        return matchCubeCount(before, after);

        // TODO Undo EntityGeometry.embedPositioning()
    }

    private static EntityGeometry transitionModelPart(
            ModelPart beforeCache, ModelPart afterCache,
            EntityGeometry before, EntityGeometry after, float lerp, boolean remapUV, boolean copyAfterVisibility) {
        var pair = TRANSITIONS_CACHE.computeIfAbsent(
                beforeCache, afterCache,
                (left, right) -> createTransitionModels(before, after));

        var transitionBefore = pair.getFirst();
        var transitionAfter = pair.getSecond();

        transitionBefore.copyPoseTreeFrom(before);
        transitionAfter.copyPoseTreeFrom(after);

        return lerpModelPart(transitionBefore, transitionAfter, lerp, remapUV);
    }

    private static Matrix4f lerpMatrix(Matrix4f a, Matrix4f b, float lerp) {
        Matrix4f out = new Matrix4f(a);
        out.lerp(b, lerp);
        return out;
    }

    private static Matrix3f lerpMatrix(Matrix3f a, Matrix3f b, float lerp) {
        Matrix3f out = new Matrix3f(a);
        out.lerp(b, lerp);
        return out;
    }

    private static float wrapRadians(float angle) {
        float f = angle % Mth.TWO_PI;
        if (f >= Mth.PI) {
            f -= Mth.TWO_PI;
        }

        if (f < -Mth.PI) {
            f += Mth.TWO_PI;
        }

        return f;
    }

    private static float rotLerp(float lerp, float r0, float r1) {
        return r0 + lerp * wrapRadians(r1 - r0);
    }

    public static PartPose lerpPartPose(PartPose before, PartPose after, float lerp) {
        return PartPose.offsetAndRotation(
                Mth.lerp(lerp, before.x, after.x),
                Mth.lerp(lerp, before.y, after.y),
                Mth.lerp(lerp, before.z, after.z),
                rotLerp(lerp, before.xRot, after.xRot),
                rotLerp(lerp, before.yRot, after.yRot),
                rotLerp(lerp, before.zRot, after.zRot)
        );
    }

    private static ModelPose transitionModelPose(ModelPose before, ModelPose after, float lerp) {
        var tmp = new PoseStack();
        tmp.pushPose();

        Matrix4f m = lerpMatrix(before.matrix.pose(), after.matrix.pose(), lerp);
        Matrix3f n = lerpMatrix(before.matrix.normal(), after.matrix.normal(), lerp);
        ((PoseStackExtender)tmp).setPose(m, n);

        return new ModelPose(tmp.last(), lerpPartPose(before.pose, after.pose, lerp));
    }

    private static Optional<HelperModel> maybeReplaceWithHelper(AdvancedHumanoidModel<?> afterModel, Limb limb) {
        return Optional.ofNullable(afterModel.getTransfurHelperModel(limb));
    }

    private static void renderMorphedLimb(LivingEntity entity, Limb limb, HumanoidModel<?> beforeModel, AdvancedHumanoidModel<?> afterModel, float partialTicks,
                                          float morphProgress, Color3 color, float alpha, PoseStack stack, MultiBufferSource buffer, int light,
                                          @Nullable ResourceLocation texture, boolean listenToAfterVisible, boolean applyAnimation) {
        ModelPart before = limb.getModelPart(beforeModel);
        final ModelPart after = limb.getModelPart(afterModel);
        if (before == null || after == null)
            return;
        if (!before.visible)
            return;

        ModelPose beforePose = CAPTURED_MODELS.getOrDefault(before, NULL_POSE);
        final ModelPose afterPose = CAPTURED_MODELS.getOrDefault(after, NULL_POSE);
        if (afterPose == NULL_POSE) return;

        {
            var helper = maybeReplaceWithHelper(afterModel, limb);
            final var beforePoseCopy = beforePose;
            beforePose = helper.map(h -> h.prepareModelPart(beforePoseCopy, beforeModel)).orElse(beforePose);
            before = helper.map(HelperModel::getModelPart).orElse(before);
        }

        final EntityGeometry afterCopied = deepCopyPart(limb.getModelPart(afterModel), afterModel::shouldPartTransfur, listenToAfterVisible);
        final EntityGeometry transitionPart = transitionModelPart(
                before, after,
                new EntityGeometry(before).collapseSimple(), afterCopied.collapseSimple(),
                morphProgress, texture == null, listenToAfterVisible);
        final ModelPose transitionPose = transitionModelPose(beforePose, afterPose, morphProgress);

        if (texture == null)
            texture = LimbCoverTransition.LATEX_CUBE;

        final var vertexConsumer = buffer.getBuffer(
                alpha >= 1f ? RenderType.entityCutoutNoCull(texture)
                        : RenderType.entityTranslucent(texture)
        );

        final int overlay = LivingEntityRenderer.getOverlayCoords(entity, 0.0f);

        stack.pushPose();
        ((PoseStackExtender)stack).setPose(transitionPose.matrix);

        transitionPart.loadPose(transitionPose.pose);

        // TODO apply LimbExtension stuff
        if (applyAnimation) {
            AnimationContainer.getForEntity(entity).ifPresent(container -> {
                container.getOrderedAnimations().forEach(instance -> {
                    transitionPart.loadPose(instance.animatePartAs(limb, transitionPart.storePose(), partialTicks));
                });
            });
        }

        transitionPart.render(stack, vertexConsumer, light, overlay, color.red(), color.green(), color.blue(), alpha);

        stack.popPose();
    }

    public static void renderMorphedEntity(LivingEntity entity, HumanoidModel<?> beforeModel, AdvancedHumanoidModel<?> afterModel, float partialTicks, float morphProgress, Color3 color, float alpha, PoseStack stack, MultiBufferSource buffer, int light, @Nullable ResourceLocation texture, boolean listenToAfterVisible) {
        Arrays.stream(Limb.values()).forEach(limb -> {
            if (ChangedCompatibility.isFirstPersonRendering() && limb == Limb.HEAD)
                return;
            if (ChangedCompatibility.isFirstPersonRendering() && entity.isSwimming() && limb == Limb.TORSO)
                return;

            try {
                renderMorphedLimb(entity, limb, beforeModel, afterModel, partialTicks, morphProgress, color, alpha, stack, buffer, light, texture, listenToAfterVisible, true);
            } catch (Exception e) {
                CrashReport report = CrashReport.forThrowable(e, "Rendering transfurring entity's limb");
                CrashReportCategory category = report.addCategory("Limb being renderered");
                category.setDetail("Limb Name", limb.getSerializedName());
                throw new ReportedException(report);
            }
        });
    }

    public static float getPreMorphProgression(float transfurProgression) {
        return Transition.easeInOutSine(Mth.clamp(Mth.map(transfurProgression, 0.2f, 0.35f, 0.0f, 1.0f), 0.0f, 1.0f));
    }

    public static float getCoverProgression(float transfurProgression) {
        return Mth.clamp(Mth.map(transfurProgression, 0.0f, 0.33f, 0.0f, 1.0f), 0.0f, 1.0f);
    }

    public static float getCoverAlpha(float transfurProgression) {
        return Mth.clamp(Mth.map(transfurProgression, 0.33f, 0.45f, 1.0f, 0.0f), 0.0f, 1.0f);
    }

    public static float getMorphAlpha(float transfurProgression) {
        if (transfurProgression < 0.5f)
            return Mth.clamp(Mth.map(transfurProgression, 0.35f, 0.45f, 0.0f, 1.0f), 0.0f, 1.0f);
        else
            return Mth.clamp(Mth.map(transfurProgression, 0.8f, 0.85f, 1.0f, 0.0f), 0.0f, 1.0f);
    }

    private static void renderCoveringLimb(LivingEntity entity, TransfurVariantInstance<?> variant, float partialTicks, float coverProgress, float coverAlpha, ModelPart part, Limb limb, PoseStack stack, MultiBufferSource buffer, int light, boolean applyAnimation) {
        final float progress = switch (limb) {
            case HEAD -> variant.transfurContext.cause().getHeadProgress(coverProgress);
            case TORSO -> variant.transfurContext.cause().getTorsoProgress(coverProgress);
            case LEFT_ARM -> variant.transfurContext.cause().getLeftArmProgress(coverProgress);
            case RIGHT_ARM -> variant.transfurContext.cause().getRightArmProgress(coverProgress);
            case LEFT_LEG -> variant.transfurContext.cause().getLeftLegProgress(coverProgress);
            case RIGHT_LEG -> variant.transfurContext.cause().getRightLegProgress(coverProgress);
            default -> 1.0f;
        };

        final LimbCoverTransition transition = switch (limb) {
            case HEAD -> variant.transfurContext.cause().getHeadTransition();
            case TORSO -> variant.transfurContext.cause().getTorsoTransition();
            case LEFT_ARM -> variant.transfurContext.cause().getLeftArmTransition();
            case RIGHT_ARM -> variant.transfurContext.cause().getRightArmTransition();
            case LEFT_LEG -> variant.transfurContext.cause().getLeftLegTransition();
            case RIGHT_LEG -> variant.transfurContext.cause().getRightLegTransition();
            default -> LimbCoverTransition.INSTANT;
        };

        if (progress <= 0f)
            return;

        final float shrink = (coverAlpha - 1.0f) * 0.5f;

        final EntityGeometry copiedPart = deepCopyPart(part, pred -> true, false).offsetSize(shrink, shrink, shrink);
        final ModelPose pose = CAPTURED_MODELS.getOrDefault(part, NULL_POSE);

        stack.pushPose();
        ((PoseStackExtender)stack).setPose(pose.matrix);
        stack.scale(1.005f, 1.005f, 1.005f);
        stack.translate(0.0f, -0.0025f, 0.0f);

        final float alpha = transition.getAlphaForProgress(progress);
        final var vertexConsumer = buffer.getBuffer(alpha >= 1f ? RenderType.entityCutoutNoCull(
                        transition.getTextureForProgress(progress)) : RenderType.entityTranslucent(transition.getTextureForProgress(progress))
        );
        final Color3 color = variant.getTransfurColor();

        copiedPart.loadPose(pose.pose);
        if (applyAnimation) {
            AnimationContainer.getForEntity(entity).ifPresent(container -> {
                container.getOrderedAnimations().forEach(instance -> {
                    copiedPart.loadPose(instance.animatePartAs(limb, copiedPart.storePose(), partialTicks));
                });
            });
        }
        copiedPart.render(stack, vertexConsumer, light, LivingEntityRenderer.getOverlayCoords(entity, 0.0f), color.red(), color.green(), color.blue(), alpha);

        stack.popPose();
    }

    private static Optional<HumanoidArmorLayer<?,?,?>> findArmorLayer(LivingEntityRenderer<?,?> renderer) {
        for (var layer : renderer.layers)
            if (layer instanceof HumanoidArmorLayer<?,?,?> armorLayer)
                return Optional.of(armorLayer);
        return Optional.empty();
    }

    private static Optional<AccessoryLayer<?,?>> findAccessoryLayer(LivingEntityRenderer<?,?> renderer) {
        for (var layer : renderer.layers)
            if (layer instanceof AccessoryLayer<?,?> accessoryLayer)
                return Optional.of(accessoryLayer);
        return Optional.empty();
    }

    public static void renderTransfurringPlayer(Player player, TransfurVariantInstance<?> variant, PoseStack stack, MultiBufferSource buffer, int light, float partialTick) {
        final Minecraft minecraft = Minecraft.getInstance();
        final EntityRenderDispatcher dispatcher = minecraft.getEntityRenderDispatcher();
        final var playerRenderer = dispatcher.getRenderer(player);
        final var latexRenderer = dispatcher.getRenderer(variant.getChangedEntity());

        if (!(playerRenderer instanceof LivingEntityRenderer<?,?> livingPlayerRenderer)) return;
        if (!(livingPlayerRenderer.getModel() instanceof HumanoidModel<?> playerHumanoidModel)) return;

        if (!(latexRenderer instanceof AdvancedHumanoidRenderer latexHumanoidRenderer)) return;

        int variantLight = FormRenderHandler.maxPackedLight(light, latexHumanoidRenderer.getPackedLightCoords(variant.getChangedEntity(), partialTick));

        final float transfurProgression = variant.getTransfurProgression(partialTick);
        final float coverProgress = getCoverProgression(transfurProgression);
        final float coverAlpha = Transition.easeInOutSine(getCoverAlpha(transfurProgression));
        final float morphProgress = variant.getMorphProgression(partialTick);
        final float morphAlpha = Transition.easeInOutSine(getMorphAlpha(transfurProgression));

        if (morphAlpha < 1f) { // Render normal living entity, when they are still seen
            if (coverProgress < 1f) { // Render player, being covered
                forceRenderPlayer = true;
                FormRenderHandler.renderLiving(player, stack, buffer, light, partialTick);
                ChangedCompatibility.forceIsFirstPersonRenderingToFrozen();
                forceRenderPlayer = false;
            } else if (morphProgress > 0.5f) // Render latex at the end
                FormRenderHandler.renderLiving(variant.getChangedEntity(), stack, buffer, variantLight, partialTick);
        }

        if (coverAlpha > 0f) {
            if (!ChangedCompatibility.isFirstPersonRendering()) {
                renderCoveringLimb(player, variant, partialTick, coverProgress, 1.0f, playerHumanoidModel.head, Limb.HEAD, stack, buffer, variantLight, true);
                renderCoveringLimb(player, variant, partialTick, coverProgress, coverAlpha, playerHumanoidModel.hat, Limb.HEAD, stack, buffer, variantLight, true);
            }
            if (!(ChangedCompatibility.isFirstPersonRendering() && player.isSwimming()))
                renderCoveringLimb(player, variant, partialTick, coverProgress, 1.0f, playerHumanoidModel.body, Limb.TORSO, stack, buffer, variantLight, true);
            renderCoveringLimb(player, variant, partialTick, coverProgress, 1.0f, playerHumanoidModel.leftArm, Limb.LEFT_ARM, stack, buffer, variantLight, true);
            renderCoveringLimb(player, variant, partialTick, coverProgress, 1.0f, playerHumanoidModel.rightArm, Limb.RIGHT_ARM, stack, buffer, variantLight, true);
            renderCoveringLimb(player, variant, partialTick, coverProgress, 1.0f, playerHumanoidModel.leftLeg, Limb.LEFT_LEG, stack, buffer, variantLight, true);
            renderCoveringLimb(player, variant, partialTick, coverProgress, 1.0f, playerHumanoidModel.rightLeg, Limb.RIGHT_LEG, stack, buffer, variantLight, true);
            if (playerHumanoidModel instanceof PlayerModel<?> playerModel) {
                if (!(ChangedCompatibility.isFirstPersonRendering() && player.isSwimming()))
                    renderCoveringLimb(player, variant, partialTick, coverProgress, coverAlpha, playerModel.jacket, Limb.TORSO, stack, buffer, variantLight, true);
                renderCoveringLimb(player, variant, partialTick, coverProgress, coverAlpha, playerModel.leftSleeve, Limb.LEFT_ARM, stack, buffer, variantLight, true);
                renderCoveringLimb(player, variant, partialTick, coverProgress, coverAlpha, playerModel.rightSleeve, Limb.RIGHT_ARM, stack, buffer, variantLight, true);
                renderCoveringLimb(player, variant, partialTick, coverProgress, coverAlpha, playerModel.leftPants, Limb.LEFT_LEG, stack, buffer, variantLight, true);
                renderCoveringLimb(player, variant, partialTick, coverProgress, coverAlpha, playerModel.rightPants, Limb.RIGHT_LEG, stack, buffer, variantLight, true);
            }
        }

        int coverLight = FormRenderHandler.lerpPackedLight(light, variantLight, coverProgress);

        if (morphAlpha > 0f) {
            final var colors = variant.getTransfurColor();
            try {
                renderMorphedEntity(player, playerHumanoidModel, latexHumanoidRenderer.getModel(variant.getChangedEntity()),
                        partialTick, morphProgress, colors, morphAlpha, stack, buffer, variantLight, null, false);
            } catch (Exception e) {
                CrashReport report = CrashReport.forThrowable(e, "Rendering entity partially transfurred");
                throw new ReportedException(report);
            }
        }

        if (coverProgress >= 1f) {
            findArmorLayer(livingPlayerRenderer).ifPresent(armorLayer -> {
                Arrays.stream(EquipmentSlot.values()).filter(slot -> slot.getType() == EquipmentSlot.Type.ARMOR).forEach(armorSlot -> {
                    var item = player.getItemBySlot(armorSlot);
                    ResourceLocation texture = null;
                    if (item.getItem() instanceof ArmorItem)
                        texture = armorLayer.getArmorResource(player, item, armorSlot, null);

                    if (texture == null)
                        return;

                    var model = armorLayer.getArmorModel(armorSlot);
                    ((HumanoidArmorLayer) armorLayer).setPartVisibility((HumanoidModel) model, armorSlot);
                    var afterModel = latexHumanoidRenderer.getArmorLayer().getArmorModel(variant.getChangedEntity(), armorSlot);
                    try {
                        afterModel.prepareVisibility(armorSlot, item);
                        renderMorphedEntity(player,
                                model,
                                afterModel,
                                partialTick, morphProgress, Color3.WHITE, 1f, stack, buffer, coverLight,
                                texture, true);
                    } catch (Exception e) {
                        CrashReport report = CrashReport.forThrowable(e, "Rendering transfurring entity's armor");
                        CrashReportCategory category = report.addCategory("Armor being rendered");
                        category.setDetail("Armor Item", item);
                        category.setDetail("Armor Slot", armorSlot);
                        throw new ReportedException(report);
                    }
                });
            });

            final var slotTypePredicate = AccessoryEntities.INSTANCE.canEntityTypeUseSlot(AccessoryEntities.getApparentEntityType(variant.getChangedEntity()));
            findAccessoryLayer(livingPlayerRenderer).flatMap(accessoryLayer -> AccessorySlots.getForEntity(player))
                    .ifPresent(slots -> slots.forEachSlot((slotType, itemStack) -> {
                        if (itemStack.isEmpty())
                            return;
                        if (!slotTypePredicate.test(slotType) || !slotType.canHoldItem(itemStack, player))
                            return; // Ensure lag doesn't crash with an invalid slot

                        var slotContextPlayer = new AccessorySlotContext<>(player, slotType, itemStack);
                        var slotContextVariant = new AccessorySlotContext<>(variant.getChangedEntity(), slotType, itemStack);

                        AccessoryLayer.getRenderer(itemStack.getItem()).ifPresent(renderer -> {
                            if (renderer instanceof TransitionalAccessory transitionalAccessory) {
                                final var texture = transitionalAccessory.getModelTexture(slotContextVariant);
                                final var before = transitionalAccessory.getBeforeModel(slotContextPlayer, livingPlayerRenderer);
                                if (texture.isEmpty() || before.isEmpty())
                                    return;

                                transitionalAccessory.getAfterModels(slotContextPlayer, latexHumanoidRenderer).forEach(after -> {
                                    try {
                                        renderMorphedEntity(player,
                                                before.get(),
                                                after,
                                                partialTick, morphProgress, Color3.WHITE, 1f, stack, buffer, coverLight,
                                                texture.get(), true);
                                    } catch (Exception e) {
                                        CrashReport report = CrashReport.forThrowable(e, "Rendering transfurring entity's accessories");
                                        CrashReportCategory category = report.addCategory("Accessory being rendered");
                                        category.setDetail("Accessory Item", itemStack);
                                        category.setDetail("Accessory Slot", ChangedRegistry.ACCESSORY_SLOTS.getKey(slotType));
                                        throw new ReportedException(report);
                                    }
                                });
                            }
                        });
            }));
        }
    }

    public static void renderTransfurringArm(Player player, HumanoidArm arm, PartPose armPose, TransfurVariantInstance<?> variant, PoseStack stack, MultiBufferSource buffer, int light, float partialTick, @Nullable ResourceLocation texture) {
        final Minecraft minecraft = Minecraft.getInstance();
        final EntityRenderDispatcher dispatcher = minecraft.getEntityRenderDispatcher();
        final var playerRenderer = dispatcher.getRenderer(player);
        final var latexRenderer = dispatcher.getRenderer(variant.getChangedEntity());

        if (!(playerRenderer instanceof LivingEntityRenderer<?,?> livingPlayerRenderer)) return;
        if (!(livingPlayerRenderer.getModel() instanceof HumanoidModel<?> playerHumanoidModel)) return;

        if (!(latexRenderer instanceof AdvancedHumanoidRenderer<?,?> latexHumanoidRenderer)) return;

        int variantLight = FormRenderHandler.maxPackedLight(light, ((AdvancedHumanoidRenderer)latexHumanoidRenderer).getPackedLightCoords(variant.getChangedEntity(), partialTick));

        final float transfurProgression = variant.getTransfurProgression(partialTick);
        final float coverProgress = getCoverProgression(transfurProgression);
        final float coverAlpha = Transition.easeInOutSine(getCoverAlpha(transfurProgression));
        final float morphProgress = variant.getMorphProgression(partialTick);
        final float morphAlpha = Transition.easeInOutSine(getMorphAlpha(transfurProgression));

        if (morphAlpha < 1f) { // Render normal living entity, when they are still seen
            if (coverProgress < 1f) { // Render player, being covered
                forceRenderPlayer = true;
                FormRenderHandler.renderHand(player, arm, armPose, stack, buffer, light, partialTick);
                ChangedCompatibility.forceIsFirstPersonRenderingToFrozen();
                forceRenderPlayer = false;
            } else if (morphProgress > 0.5f) // Render latex at the end
                FormRenderHandler.renderHand(variant.getChangedEntity(), arm, armPose, stack, buffer, variantLight, partialTick);
        }

        Limb limb = arm == HumanoidArm.LEFT ? Limb.LEFT_ARM : Limb.RIGHT_ARM;

        if (coverAlpha > 0f) {
            switch (arm) {
                case RIGHT -> renderCoveringLimb(player, variant, partialTick, coverProgress, 1.0f, playerHumanoidModel.rightArm, Limb.RIGHT_ARM, stack, buffer, variantLight, false);
                case LEFT -> renderCoveringLimb(player, variant, partialTick, coverProgress, 1.0f, playerHumanoidModel.leftArm, Limb.LEFT_ARM, stack, buffer, variantLight, false);
            }

            if (playerHumanoidModel instanceof PlayerModel<?> playerModel) {
                switch (arm) {
                    case RIGHT -> renderCoveringLimb(player, variant, partialTick, coverProgress, 1.0f, playerModel.rightSleeve, Limb.RIGHT_ARM, stack, buffer, variantLight, false);
                    case LEFT -> renderCoveringLimb(player, variant, partialTick, coverProgress, 1.0f, playerModel.leftSleeve, Limb.LEFT_ARM, stack, buffer, variantLight, false);
                }
            }
        }

        if (morphAlpha <= 0f)
            return; // Don't bother rendering

        final var color = variant.getTransfurColor();
        try {
            renderMorphedLimb(player, limb, playerHumanoidModel, latexHumanoidRenderer.getModel(variant.getChangedEntity()),
                    partialTick, morphProgress, color, morphAlpha, stack, buffer, light, texture, false, false);
        } catch (Exception e) {
            CrashReport report = CrashReport.forThrowable(e, "Rendering transfurring entity's arm");
            CrashReportCategory category = report.addCategory("Limb being rendered");
            category.setDetail("Limb Name", limb.getSerializedName());
            throw new ReportedException(report);
        }
    }

    private static boolean capturingPose = false;
    private static boolean forceRenderPlayer = false;

    public static void startCapture() {
        capturingPose = true;
        CAPTURED_MODELS.clear();
    }

    public static void endCapture() {
        capturingPose = false;
    }

    public static boolean shouldRenderHuman() {
        return capturingPose || forceRenderPlayer;
    }

    public static boolean isCapturing() {
        return capturingPose;
    }

    public static boolean capture(ModelPart part, PoseStack pose) {
        if (!capturingPose)
            return false;

        if (!CAPTURED_MODELS.containsKey(part)) {
            CAPTURED_MODELS.put(part, new ModelPose(((PoseStackExtender)pose).copyLast(), part.storePose()));
        }

        return true;
    }

    public static <T extends LivingEntity, M extends EntityModel<T>> boolean isLayerAllowed(RenderLayer<T, M> layer) {
        if (layer instanceof HumanoidArmorLayer<?,?,?>)
            return true;
        if (layer instanceof LatexHumanoidArmorLayer<?,?>)
            return true;
        if (layer instanceof AccessoryLayer<?,?>)
            return true;
        return false;
    }
}
