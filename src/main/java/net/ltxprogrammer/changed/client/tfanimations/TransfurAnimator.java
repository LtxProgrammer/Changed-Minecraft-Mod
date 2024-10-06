package net.ltxprogrammer.changed.client.tfanimations;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import net.ltxprogrammer.changed.client.CubeExtender;
import net.ltxprogrammer.changed.client.FormRenderHandler;
import net.ltxprogrammer.changed.client.PoseStackExtender;
import net.ltxprogrammer.changed.client.renderer.AdvancedHumanoidRenderer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexHumanoidArmorLayer;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.entity.LimbCoverTransition;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.extension.ChangedCompatibility;
import net.ltxprogrammer.changed.util.Color3;
import net.ltxprogrammer.changed.util.Transition;
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

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class TransfurAnimator {
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

    private static ModelPart.Cube copyCube(ModelPart.Cube cube) {
        ModelPart.Cube newCube = new ModelPart.Cube(0, 0, cube.minX, cube.minY, cube.minZ,
                cube.maxX - cube.minX, cube.maxY - cube.minY, cube.maxZ - cube.minZ,
                0.0f, 0.0f, 0.0f, false, 1.0f, 1.0f);
        ((CubeExtender)newCube).copyPolygonsFrom(cube);
        return newCube;
    }

    private static ModelPart deepCopyPart(@Nullable ModelPart part, boolean copyVisibility) {
        if (part == null)
            return null;
        ModelPart copied = new ModelPart(
                part.cubes.stream().map(TransfurAnimator::copyCube).toList(),
                part.children.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> deepCopyPart(entry.getValue(), copyVisibility))));
        copied.loadPose(part.storePose());
        if (copyVisibility)
            copied.visible = part.visible;
        return copied;
    }

    private static ModelPart deepCopyPart(@Nullable ModelPart part, Predicate<ModelPart> predicate, boolean copyVisibility) {
        if (part == null)
            return null;
        ModelPart copied = new ModelPart(
                part.cubes.stream().map(TransfurAnimator::copyCube).toList(),
                part.children.entrySet().stream().filter(entry -> predicate.test(entry.getValue())).collect(Collectors.toMap(Map.Entry::getKey, entry -> deepCopyPart(entry.getValue(), predicate, copyVisibility))));
        copied.loadPose(part.storePose());
        if (copyVisibility)
            copied.visible = part.visible;
        return copied;
    }

    private static ModelPart.Cube clampCube(ModelPart.Cube a, @Nullable ModelPart.Cube clampBy) {
        if (clampBy == null)
            clampBy = a;

        float minX = Mth.clamp(a.minX, clampBy.minX, clampBy.maxX);
        float minY = Mth.clamp(a.minY, clampBy.minY, clampBy.maxY);
        float minZ = Mth.clamp(a.minZ, clampBy.minZ, clampBy.maxZ);

        float maxX = Mth.clamp(a.maxX, clampBy.minX, clampBy.maxX);
        float maxY = Mth.clamp(a.maxY, clampBy.minY, clampBy.maxY);
        float maxZ = Mth.clamp(a.maxZ, clampBy.minZ, clampBy.maxZ);

        ModelPart.Cube newCube = new ModelPart.Cube(0, 0, minX, minY, minZ,
                maxX - minX, maxY - minY, maxZ - minZ,
                0.0f, 0.0f, 0.0f, false, 1.0f, 1.0f);
        var polyCube = ((CubeExtender)newCube).getPolygons();
        var polyClamp = ((CubeExtender)clampBy).getPolygons();
        for (int i = 0; i < polyCube.length && i < polyClamp.length; ++i) {
            for (int v = 0; v < polyCube[i].vertices.length && v < polyClamp[i].vertices.length; ++v) {
                polyCube[i].vertices[v] = polyCube[i].vertices[v].remap(polyClamp[i].vertices[v].u, polyClamp[i].vertices[v].v);
            }
        }
        return newCube;
    }

    private static ModelPart replaceCubesAndZeroParts(ModelPart part, ModelPart.Cube cube) {
        ModelPart ret = new ModelPart(
                part.cubes.stream().map(otherCube -> clampCube(otherCube, cube)).toList(),
                part.children.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> replaceCubesAndZeroParts(entry.getValue(), cube))));
        ret.loadPose(PartPose.ZERO);
        return ret;
    }

    private static ModelPart shallowCopyPart(ModelPart part) {
        return new ModelPart(part.cubes.stream().map(TransfurAnimator::copyCube).toList(), Map.of());
    }

    private static ModelPart matchCubeCount(ModelPart to, ModelPart from, ModelPart.Cube copyWith, boolean copyVisibility) {
        List<ModelPart.Cube> cubes = new ArrayList<>();

        final int targetCubeCount = Math.max(to.cubes.size(), from.cubes.size());
        int cubeCount = 0;

        for (var cube : to.cubes) {
            if (cubeCount >= targetCubeCount) break;

            cubes.add(copyCube(cube));
            cubeCount++;
        }

        for (var cube : from.cubes) {
            if (cubeCount >= targetCubeCount) break;

            cubes.add(clampCube(cube, copyWith));
            cubeCount++;
        }

        cubes.sort((c1, c2) -> {
            int yCompare = Float.compare(c1.maxY, c2.maxY);
            int xCompare = Float.compare(c1.maxX, c2.maxX);
            int zCompare = Float.compare(c1.maxZ, c2.maxZ);

            if (yCompare == 0) {
                if (xCompare == 0)
                    return zCompare;
                return xCompare;
            }
            return yCompare;
        });

        Map<String, ModelPart> children = new HashMap<>();

        for (var k : to.children.keySet()) {
            children.put(k, deepCopyPart(to.children.get(k), copyVisibility));
        }

        ModelPart.Cube copyOverride = !to.cubes.isEmpty() ? to.cubes.get(0) : copyWith;

        for (var k : from.children.keySet()) {
            if (to.children.containsKey(k)) {
                var model = matchCubeCount(to.children.get(k), from.children.get(k), copyOverride, copyVisibility);
                model.loadPose(to.children.get(k).storePose());
                children.put(k, model);
            } else {
                children.put(k, replaceCubesAndZeroParts(from.children.get(k), copyOverride));
            }
        }

        final ModelPart matched = new ModelPart(cubes, children);
        if (copyVisibility)
            matched.visible = to.visible;
        return matched;
    }

    private static ModelPart matchCubeCount(ModelPart to, ModelPart from, boolean copyVisibility) {
        return matchCubeCount(to, from, findCube(to), copyVisibility);
    }

    private static ModelPart.Vertex lerpVertex(ModelPart.Vertex a, ModelPart.Vertex b, float lerp) {
        return new ModelPart.Vertex(
                Mth.lerp(lerp, a.pos.x(), b.pos.x()),
                Mth.lerp(lerp, a.pos.y(), b.pos.y()),
                Mth.lerp(lerp, a.pos.z(), b.pos.z()),
                Mth.lerp(lerp, a.u, b.u),
                Mth.lerp(lerp, a.v, b.v)
        );
    }

    private static final float GOOP_CUBE_WIDTH = 16.0f;
    private static final float GOOP_CUBE_HEIGHT = 16.0f;

    private static ModelPart.Polygon lerpPolygon(ModelPart.Polygon a, ModelPart.Polygon b, float lerp, boolean remapUV) {
        ModelPart.Vertex[] copied = new ModelPart.Vertex[a.vertices.length];
        System.arraycopy(a.vertices, 0, copied, 0, a.vertices.length);
        ModelPart.Polygon ret = new ModelPart.Polygon(copied, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, false,
                Direction.getNearest(a.normal.x(), a.normal.y(), a.normal.z()));

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

    private static ModelPart.Cube lerpCube(ModelPart.Cube a, ModelPart.Cube b, float lerp, boolean remapUV) {
        float lerpMinX = Mth.lerp(lerp, a.minX, b.minX);
        float lerpMinY = Mth.lerp(lerp, a.minY, b.minY);
        float lerpMinZ = Mth.lerp(lerp, a.minZ, b.minZ);
        float lerpMaxX = Mth.lerp(lerp, a.maxX, b.maxX);
        float lerpMaxY = Mth.lerp(lerp, a.maxY, b.maxY);
        float lerpMaxZ = Mth.lerp(lerp, a.maxZ, b.maxZ);

        ModelPart.Cube ret = new ModelPart.Cube(0, 0, lerpMinX, lerpMinY, lerpMinZ, lerpMaxX - lerpMinX, lerpMaxY - lerpMinY, lerpMaxZ - lerpMinZ,
                0.0f, 0.0f, 0.0f, false, 0.0f, 0.0f);

        final var polyA = ((CubeExtender)a).getPolygons();
        final var polyB = ((CubeExtender)b).getPolygons();
        final var polyR = ((CubeExtender)ret).getPolygons();

        for (int i = 0; i < polyR.length; ++i) {
            polyR[i] = lerpPolygon(polyA[i], polyB[i], lerp, remapUV);
        }

        return ret;
    }

    private static ModelPart lerpModelPart(ModelPart a, ModelPart b, float lerp, boolean remapUV) {
        List<ModelPart.Cube> copiedCubes = new ArrayList<>();
        for (int i = 0; i < a.cubes.size(); ++i)
            copiedCubes.add(lerpCube(a.cubes.get(i), b.cubes.get(i), lerp, remapUV));
        Map<String, ModelPart> copiedChildren = new HashMap<>();
        for (var k : a.children.keySet())
            copiedChildren.put(k, lerpModelPart(a.children.get(k), b.children.get(k), lerp, remapUV));

        var lerped = new ModelPart(copiedCubes, copiedChildren);
        lerped.x = Mth.lerp(lerp, a.x, b.x);
        lerped.y = Mth.lerp(lerp, a.y, b.y);
        lerped.z = Mth.lerp(lerp, a.z, b.z);
        lerped.xRot = Mth.lerp(lerp, a.xRot, b.xRot);
        lerped.yRot = Mth.lerp(lerp, a.yRot, b.yRot);
        lerped.zRot = Mth.lerp(lerp, a.zRot, b.zRot);
        lerped.visible = a.visible && b.visible;
        return lerped;
    }

    private static ModelPart.Cube findCube(ModelPart part) {
        final AtomicReference<ModelPart.Cube> cubeReturn = new AtomicReference<>(null);

        part.visit(new PoseStack(), (pose, name, id, cube) -> {
            cubeReturn.compareAndSet(null, cube);
        });

        return cubeReturn.getAcquire();
    }

    private static ModelPart transitionModelPart(ModelPart before, ModelPart after, float lerp, boolean remapUV, boolean copyAfterVisibility) {
        ModelPart beforeCopy = matchCubeCount(deepCopyPart(before, false), after, false);
        ModelPart afterCopy = matchCubeCount(deepCopyPart(after, copyAfterVisibility), before, copyAfterVisibility);

        return lerpModelPart(beforeCopy, afterCopy, lerp, remapUV);
    }

    private static Matrix4f lerpMatrix(Matrix4f a, Matrix4f b, float lerp) {
        a.multiply(1.0f - lerp);
        b.multiply(lerp);
        a.add(b);
        return a;
    }

    private static Matrix3f lerpMatrix(Matrix3f a, Matrix3f b, float lerp) {
        a.mul(1.0f - lerp);
        b.mul(lerp);
        a.add(b);
        return a;
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

        Matrix4f m = lerpMatrix(before.matrix.pose().copy(), after.matrix.pose().copy(), lerp);
        Matrix3f n = lerpMatrix(before.matrix.normal().copy(), after.matrix.normal().copy(), lerp);
        ((PoseStackExtender)tmp).setPose(m, n);

        return new ModelPose(tmp.last(), lerpPartPose(before.pose, after.pose, lerp));
    }

    private static Optional<HelperModel> maybeReplaceWithHelper(AdvancedHumanoidModel<?> afterModel, Limb limb) {
        return Optional.ofNullable(afterModel.getTransfurHelperModel(limb));
    }

    private static void renderMorphedLimb(LivingEntity entity, Limb limb, HumanoidModel<?> beforeModel, AdvancedHumanoidModel<?> afterModel,
                                          float morphProgress, Color3 color, float alpha, PoseStack stack, MultiBufferSource buffer, int light,
                                          @Nullable ResourceLocation texture, boolean listenToAfterVisible) {
        ModelPart before = limb.getModelPart(beforeModel);
        final ModelPart after = limb.getModelPart(afterModel);
        if (before == null || after == null)
            return;
        if (!before.visible)
            return;

        ModelPose beforePose = CAPTURED_MODELS.getOrDefault(before, NULL_POSE);
        final ModelPose afterPose = CAPTURED_MODELS.getOrDefault(after, NULL_POSE);

        {
            var helper = maybeReplaceWithHelper(afterModel, limb);
            final var beforePoseCopy = beforePose;
            beforePose = helper.map(h -> h.prepareModelPart(beforePoseCopy, beforeModel)).orElse(beforePose);
            before = helper.map(HelperModel::getModelPart).orElse(before);
        }

        final ModelPart afterCopied = deepCopyPart(limb.getModelPart(afterModel), afterModel::shouldPartTransfur, listenToAfterVisible);
        final ModelPart transitionPart = transitionModelPart(before, afterCopied, morphProgress, texture == null, listenToAfterVisible);
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

        transitionPart.render(stack, vertexConsumer, light, overlay, color.red(), color.green(), color.blue(), alpha);
        //transitionPart.loadPose(beforePose.pose);

        stack.popPose();
    }

    public static void renderMorphedEntity(LivingEntity entity, HumanoidModel<?> beforeModel, AdvancedHumanoidModel<?> afterModel, float morphProgress, Color3 color, float alpha, PoseStack stack, MultiBufferSource buffer, int light, @Nullable ResourceLocation texture, boolean listenToAfterVisible) {
        Arrays.stream(Limb.values()).forEach(limb -> {
            if (ChangedCompatibility.isFirstPersonRendering() && limb == Limb.HEAD)
                return;
            if (ChangedCompatibility.isFirstPersonRendering() && entity.isSwimming() && limb == Limb.TORSO)
                return;

            renderMorphedLimb(entity, limb, beforeModel, afterModel, morphProgress, color, alpha, stack, buffer, light, texture, listenToAfterVisible);
        });
    }

    public static float getPreMorphProgression(float transfurProgression) {
        return Transition.easeInOutSine(Mth.clamp(Mth.map(transfurProgression, 0.2f, 0.45f, 0.0f, 1.0f), 0.0f, 1.0f));
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

    private static ModelPart extendModelPartCubes(ModelPart part, float x, float y, float z) {
        part.cubes.forEach(cube -> ((CubeExtender)cube).extendCube(x, y, z));
        part.children.values().forEach(child -> extendModelPartCubes(child, x, y, z));

        return part;
    }

    private static void renderCoveringLimb(LivingEntity entity, TransfurVariantInstance<?> variant, float coverProgress, float coverAlpha, ModelPart part, Limb limb, PoseStack stack, MultiBufferSource buffer, int light, float partialTick) {
        final float progress = switch (limb) {
            case HEAD -> variant.transfurContext.cause.getHeadProgress(coverProgress);
            case TORSO -> variant.transfurContext.cause.getTorsoProgress(coverProgress);
            case LEFT_ARM -> variant.transfurContext.cause.getLeftArmProgress(coverProgress);
            case RIGHT_ARM -> variant.transfurContext.cause.getRightArmProgress(coverProgress);
            case LEFT_LEG -> variant.transfurContext.cause.getLeftLegProgress(coverProgress);
            case RIGHT_LEG -> variant.transfurContext.cause.getRightLegProgress(coverProgress);
            default -> 1.0f;
        };

        final LimbCoverTransition transition = switch (limb) {
            case HEAD -> variant.transfurContext.cause.getHeadTransition();
            case TORSO -> variant.transfurContext.cause.getTorsoTransition();
            case LEFT_ARM -> variant.transfurContext.cause.getLeftArmTransition();
            case RIGHT_ARM -> variant.transfurContext.cause.getRightArmTransition();
            case LEFT_LEG -> variant.transfurContext.cause.getLeftLegTransition();
            case RIGHT_LEG -> variant.transfurContext.cause.getRightLegTransition();
            default -> LimbCoverTransition.INSTANT;
        };

        if (progress <= 0f)
            return;

        final float shrink = (coverAlpha - 1.0f) * 0.5f;

        final ModelPart copiedPart = extendModelPartCubes(deepCopyPart(part, false), shrink, shrink, shrink);
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
        copiedPart.render(stack, vertexConsumer, light, LivingEntityRenderer.getOverlayCoords(entity, 0.0f), color.red(), color.green(), color.blue(), alpha);

        stack.popPose();
    }

    private static Optional<HumanoidArmorLayer<?,?,?>> findArmorLayer(LivingEntityRenderer<?,?> renderer) {
        for (var layer : renderer.layers)
            if (layer instanceof HumanoidArmorLayer<?,?,?> armorLayer)
                return Optional.of(armorLayer);
        return Optional.empty();
    }

    public static void renderTransfurringPlayer(Player player, TransfurVariantInstance<?> variant, PoseStack stack, MultiBufferSource buffer, int light, float partialTick) {
        final Minecraft minecraft = Minecraft.getInstance();
        final EntityRenderDispatcher dispatcher = minecraft.getEntityRenderDispatcher();
        final var playerRenderer = dispatcher.getRenderer(player);
        final var latexRenderer = dispatcher.getRenderer(variant.getChangedEntity());

        if (!(playerRenderer instanceof LivingEntityRenderer<?,?> livingPlayerRenderer)) return;
        if (!(livingPlayerRenderer.getModel() instanceof HumanoidModel<?> playerHumanoidModel)) return;

        if (!(latexRenderer instanceof AdvancedHumanoidRenderer<?,?,?> latexHumanoidRenderer)) return;

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
                FormRenderHandler.renderLiving(variant.getChangedEntity(), stack, buffer, light, partialTick);
        }

        if (coverAlpha > 0f) {
            if (!ChangedCompatibility.isFirstPersonRendering()) {
                renderCoveringLimb(player, variant, coverProgress, 1.0f, playerHumanoidModel.head, Limb.HEAD, stack, buffer, light, partialTick);
                renderCoveringLimb(player, variant, coverProgress, coverAlpha, playerHumanoidModel.hat, Limb.HEAD, stack, buffer, light, partialTick);
            }
            if (!(ChangedCompatibility.isFirstPersonRendering() && player.isSwimming()))
                renderCoveringLimb(player, variant, coverProgress, 1.0f, playerHumanoidModel.body, Limb.TORSO, stack, buffer, light, partialTick);
            renderCoveringLimb(player, variant, coverProgress, 1.0f, playerHumanoidModel.leftArm, Limb.LEFT_ARM, stack, buffer, light, partialTick);
            renderCoveringLimb(player, variant, coverProgress, 1.0f, playerHumanoidModel.rightArm, Limb.RIGHT_ARM, stack, buffer, light, partialTick);
            renderCoveringLimb(player, variant, coverProgress, 1.0f, playerHumanoidModel.leftLeg, Limb.LEFT_LEG, stack, buffer, light, partialTick);
            renderCoveringLimb(player, variant, coverProgress, 1.0f, playerHumanoidModel.rightLeg, Limb.RIGHT_LEG, stack, buffer, light, partialTick);
            if (playerHumanoidModel instanceof PlayerModel<?> playerModel) {
                if (!(ChangedCompatibility.isFirstPersonRendering() && player.isSwimming()))
                    renderCoveringLimb(player, variant, coverProgress, coverAlpha, playerModel.jacket, Limb.TORSO, stack, buffer, light, partialTick);
                renderCoveringLimb(player, variant, coverProgress, coverAlpha, playerModel.leftSleeve, Limb.LEFT_ARM, stack, buffer, light, partialTick);
                renderCoveringLimb(player, variant, coverProgress, coverAlpha, playerModel.rightSleeve, Limb.RIGHT_ARM, stack, buffer, light, partialTick);
                renderCoveringLimb(player, variant, coverProgress, coverAlpha, playerModel.leftPants, Limb.LEFT_LEG, stack, buffer, light, partialTick);
                renderCoveringLimb(player, variant, coverProgress, coverAlpha, playerModel.rightPants, Limb.RIGHT_LEG, stack, buffer, light, partialTick);
            }
        }

        if (morphAlpha > 0f) {
            final var colors = variant.getTransfurColor();
            renderMorphedEntity(player, playerHumanoidModel, latexHumanoidRenderer.getModel(variant.getChangedEntity()),
                    morphProgress, colors, morphAlpha, stack, buffer, light, null, false);
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
                    var afterModel = latexHumanoidRenderer.getArmorLayer().getArmorModel(armorSlot);
                    afterModel.prepareVisibility(armorSlot, item);
                    renderMorphedEntity(player,
                            model,
                            afterModel,
                            morphProgress, Color3.WHITE, 1f, stack, buffer, light,
                            texture, true);
                    afterModel.unprepareVisibility(armorSlot, item);
                });
            });
        }
    }

    public static void renderTransfurringArm(Player player, HumanoidArm arm, TransfurVariantInstance<?> variant, PoseStack stack, MultiBufferSource buffer, int light, float partialTick, @Nullable ResourceLocation texture) {
        final Minecraft minecraft = Minecraft.getInstance();
        final EntityRenderDispatcher dispatcher = minecraft.getEntityRenderDispatcher();
        final var playerRenderer = dispatcher.getRenderer(player);
        final var latexRenderer = dispatcher.getRenderer(variant.getChangedEntity());

        if (!(playerRenderer instanceof LivingEntityRenderer<?,?> livingPlayerRenderer)) return;
        if (!(livingPlayerRenderer.getModel() instanceof HumanoidModel<?> playerHumanoidModel)) return;

        if (!(latexRenderer instanceof AdvancedHumanoidRenderer<?,?,?> latexHumanoidRenderer)) return;

        final float transfurProgression = variant.getTransfurProgression(partialTick);
        final float coverProgress = getCoverProgression(transfurProgression);
        final float coverAlpha = Transition.easeInOutSine(getCoverAlpha(transfurProgression));
        final float morphProgress = variant.getMorphProgression(partialTick);
        final float morphAlpha = Transition.easeInOutSine(getMorphAlpha(transfurProgression));

        if (morphAlpha < 1f) { // Render normal living entity, when they are still seen
            if (coverProgress < 1f) { // Render player, being covered
                forceRenderPlayer = true;
                FormRenderHandler.renderHand(player, arm, stack, buffer, light, partialTick);
                ChangedCompatibility.forceIsFirstPersonRenderingToFrozen();
                forceRenderPlayer = false;
            } else if (morphProgress > 0.5f) // Render latex at the end
                FormRenderHandler.renderHand(variant.getChangedEntity(), arm, stack, buffer, light, partialTick);
        }

        Limb limb = arm == HumanoidArm.LEFT ? Limb.LEFT_ARM : Limb.RIGHT_ARM;

        if (coverAlpha > 0f) {
            switch (arm) {
                case RIGHT -> renderCoveringLimb(player, variant, coverProgress, 1.0f, playerHumanoidModel.rightArm, Limb.RIGHT_ARM, stack, buffer, light, partialTick);
                case LEFT -> renderCoveringLimb(player, variant, coverProgress, 1.0f, playerHumanoidModel.leftArm, Limb.LEFT_ARM, stack, buffer, light, partialTick);
            }

            if (playerHumanoidModel instanceof PlayerModel<?> playerModel) {
                switch (arm) {
                    case RIGHT -> renderCoveringLimb(player, variant, coverProgress, 1.0f, playerModel.rightSleeve, Limb.RIGHT_ARM, stack, buffer, light, partialTick);
                    case LEFT -> renderCoveringLimb(player, variant, coverProgress, 1.0f, playerModel.leftSleeve, Limb.LEFT_ARM, stack, buffer, light, partialTick);
                }
            }
        }

        if (morphAlpha <= 0f)
            return; // Don't bother rendering

        final var color = variant.getTransfurColor();
        renderMorphedLimb(player, limb, playerHumanoidModel, latexHumanoidRenderer.getModel(variant.getChangedEntity()),
                morphProgress, color, morphAlpha, stack, buffer, light, texture, false);
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
        if (layer instanceof LatexHumanoidArmorLayer<?,?,?>)
            return true;
        return false;
    }
}
