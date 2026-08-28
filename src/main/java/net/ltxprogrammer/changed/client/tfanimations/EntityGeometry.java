package net.ltxprogrammer.changed.client.tfanimations;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.ltxprogrammer.changed.client.CubeExtender;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Rotation;
import org.jetbrains.annotations.NotNull;
import org.joml.*;

import javax.annotation.Nullable;
import java.lang.Math;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Class that represents a ModelPart without any bloat from mods. Has many functions to aid in transforming
 */
public class EntityGeometry {
    // Blockbench helper part that is used to rotate cubes
    public static boolean isSkeletonName(String name) {
        return name.matches(".*_r[0-9]+$");
    }

    public static boolean isSkeletonName(String hint, String name) {
        return name.startsWith(hint + "_") && isSkeletonName(name);
    }

    /*public static class Box {
        public float xMin, xMax;
        public float yMin, yMax;
        public float zMin, zMax;

        public Box(float xMin, float yMin, float zMin, float xMax, float yMax, float zMax) {
            this.xMin = Math.min(xMin, xMax);
            this.yMin = Math.min(yMin, yMax);
            this.zMin = Math.min(zMin, zMax);
            this.xMax = Math.max(xMin, xMax);
            this.yMax = Math.max(yMin, yMax);
            this.zMax = Math.max(zMin, zMax);
        }

        public Box(Vector3fc min, Vector3fc max) {
            this(min.x(), min.y(), min.z(), max.x(), max.y(), max.z());
        }

        public Box(Cube cube) {
            this(cube.getMin(), cube.getMax());
        }

        public Vector3f getSize() {
            return new Vector3f(xMax - xMin, yMax - yMin, zMax - zMin);
        }

        public Vector3f getCenter() {
            return new Vector3f(xMax + xMin, yMax + yMin, zMax + zMin).mul(0.5f);
        }

        public Vector3f getCenter(Direction surface) {
            var size = this.getSize().mul(surface.step()).mul(0.5f);
            return this.getCenter().add(size);
        }

        public Box move(float x, float y, float z) {
            return move(x, y, z, this);
        }

        public Box move(float x, float y, float z, Box dest) {
            dest.xMin = this.xMin + x;
            dest.xMax = this.xMax + x;
            dest.yMin = this.yMin + y;
            dest.yMax = this.yMax + y;
            dest.zMin = this.zMin + z;
            dest.zMax = this.zMax + z;
            return dest;
        }

        @Override
        public String toString() {
            return String.format("Box[%f,%f,%f] {%.2f,%.2f,%.2f -> %.2f,%.2f,%.2f}",
                    xMax - xMin, yMax - yMin, zMax - zMin, xMin, yMin, zMin, xMax, yMax, zMax);
        }
    }*/

    public float x;
    public float y;
    public float z;
    public float xRot;
    public float yRot;
    public float zRot;
    public float xScale;
    public float yScale;
    public float zScale;
    public boolean visible = true;
    public final ObjectArrayList<Cube> cubes = new ObjectArrayList<>();
    public final Object2ObjectArrayMap<String, EntityGeometry> children = new Object2ObjectArrayMap<>();

    public EntityGeometry() {}

    public EntityGeometry(List<Cube> cubes, Map<String, EntityGeometry> children) {
        this.cubes.addAll(cubes);
        this.children.putAll(children);
    }

    public EntityGeometry(EntityGeometry copyFrom) {
        this(copyFrom, null);
    }

    public EntityGeometry(EntityGeometry copyFrom, @Nullable Predicate<EntityGeometry> includeChild) {
        this.x = copyFrom.x;
        this.y = copyFrom.y;
        this.z = copyFrom.z;
        this.xRot = copyFrom.xRot;
        this.yRot = copyFrom.yRot;
        this.zRot = copyFrom.zRot;
        this.xScale = copyFrom.xScale;
        this.yScale = copyFrom.yScale;
        this.zScale = copyFrom.zScale;
        this.visible = copyFrom.visible;

        copyFrom.cubes.forEach(cube -> this.cubes.add(new Cube(cube)));
        copyFrom.children.forEach((name, modelPart) -> {
            if (includeChild == null || includeChild.test(modelPart))
                this.children.put(name, new EntityGeometry(modelPart, includeChild));
        });
    }

    @Override
    public String toString() {
        return String.format("EntityGeometry[%d cubes] { %d children }",
                cubes.size(), children.size());
    }

    public EntityGeometry(ModelPart copyFrom) {
        this(copyFrom, null);
    }

    public EntityGeometry(ModelPart copyFrom, @Nullable Predicate<ModelPart> includeChild) {
        this.x = copyFrom.x;
        this.y = copyFrom.y;
        this.z = copyFrom.z;
        this.xRot = copyFrom.xRot;
        this.yRot = copyFrom.yRot;
        this.zRot = copyFrom.zRot;
        this.xScale = copyFrom.xScale;
        this.yScale = copyFrom.yScale;
        this.zScale = copyFrom.zScale;

        copyFrom.cubes.forEach(cube -> this.cubes.add(new Cube(cube)));
        copyFrom.children.forEach((name, modelPart) -> {
            if (includeChild == null || includeChild.test(modelPart))
                this.children.put(name, new EntityGeometry(modelPart, includeChild));
        });
    }

    public void visit(Consumer<? super EntityGeometry> visitor) {
        visitor.accept(this);
        children.values().forEach(visitor);
    }

    public PartPose storePose() {
        return PartPose.offsetAndRotation(x, y, z, xRot, yRot, zRot);
    }

    public void loadPose(PartPose pose) {
        this.x = pose.x;
        this.y = pose.y;
        this.z = pose.z;
        this.xRot = pose.xRot;
        this.yRot = pose.yRot;
        this.zRot = pose.zRot;
    }

    public void copyPoseFrom(EntityGeometry part) {
        this.xRot = part.xRot;
        this.yRot = part.yRot;
        this.zRot = part.zRot;
        this.x = part.x;
        this.y = part.y;
        this.z = part.z;
        this.xScale = part.xScale;
        this.yScale = part.yScale;
        this.zScale = part.zScale;
    }

    public void copyPoseTreeFrom(EntityGeometry part) {
        this.copyPoseFrom(part);

        for (var child : part.children.entrySet()) {
            if (hasChild(child.getKey())) {
                getChild(child.getKey()).copyPoseTreeFrom(child.getValue());
            }
        }
    }

    public void copyPoseFrom(ModelPart part) {
        this.xRot = part.xRot;
        this.yRot = part.yRot;
        this.zRot = part.zRot;
        this.x = part.x;
        this.y = part.y;
        this.z = part.z;
        this.xScale = part.xScale;
        this.yScale = part.yScale;
        this.zScale = part.zScale;
    }

    public boolean hasChild(String name) {
        return this.children.containsKey(name);
    }

    public EntityGeometry getChild(String name) {
        EntityGeometry part = this.children.get(name);
        if (part == null) {
            throw new NoSuchElementException("Can't find part " + name);
        } else {
            return part;
        }
    }

    public void setPos(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void setRotation(float xRot, float yRot, float zRot) {
        this.xRot = xRot;
        this.yRot = yRot;
        this.zRot = zRot;
    }

    public void setScale(float xScale, float yScale, float zScale) {
        this.xScale = xScale;
        this.yScale = yScale;
        this.zScale = zScale;
    }

    public void render(PoseStack poseStack, VertexConsumer vertexConsumer, int overlay, int lightCoords) {
        this.render(poseStack, vertexConsumer, overlay, lightCoords, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    public void render(PoseStack poseStack, VertexConsumer vertexConsumer, int overlay, int lightCoords, float red, float green, float blue, float alpha) {
        if (this.visible) {
            if (!this.cubes.isEmpty() || !this.children.isEmpty()) {
                poseStack.pushPose();
                this.translateAndRotate(poseStack);
                this.compile(poseStack.last(), vertexConsumer, overlay, lightCoords, red, green, blue, alpha);

                for(EntityGeometry part : this.children.values()) {
                    part.render(poseStack, vertexConsumer, overlay, lightCoords, red, green, blue, alpha);
                }

                poseStack.popPose();
            }
        }
    }

    public void translateAndRotate(PoseStack poseStack) {
        poseStack.translate(this.x / 16.0F, this.y / 16.0F, this.z / 16.0F);
        if (this.xRot != 0.0F || this.yRot != 0.0F || this.zRot != 0.0F) {
            poseStack.mulPose((new Quaternionf()).rotationZYX(this.zRot, this.yRot, this.xRot));
        }

        if (this.xScale != 1.0F || this.yScale != 1.0F || this.zScale != 1.0F) {
            poseStack.scale(this.xScale, this.yScale, this.zScale);
        }
    }

    private void compile(PoseStack.Pose pose, VertexConsumer vertexConsumer, int overlay, int lightCoords, float red, float green, float blue, float alpha) {
        for(Cube cube : this.cubes) {
            cube.compile(pose, vertexConsumer, overlay, lightCoords, red, green, blue, alpha);
        }

    }

    public EntityGeometry offsetSize(float x, float y, float z) {
        visit(part -> {
            for (var cube : part.cubes)
                cube.offsetSize(x, y, z);
        });
        return this;
    }

    public EntityGeometry move(float x, float y, float z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    public EntityGeometry collapseSimple() {
        // Blockbench models sometimes create part "joints" that are just positional/rotational offsets, and don't (typically) animate
        this.children.forEach((name, part) -> {
            part.collapseSimple();
        });

        boolean collapsed;
        do {
            collapsed = false;
            var preModSet = Set.copyOf(children.keySet());
            for (var childName : preModSet) {
                var childPart = children.get(childName);
                if (isSkeletonName(childName)) {
                    if (childPart.xRot == 0.0f && childPart.yRot == 0.0f && childPart.zRot == 0.0f) {
                        childPart.cubes.forEach(cube -> {
                            this.cubes.add(cube.move(childPart.x, childPart.y, childPart.z));
                        });
                        childPart.children.forEach((name, part) -> {
                            this.children.put(childName + "/" + name, part.move(childPart.x, childPart.y, childPart.z));
                        });

                        children.remove(childName);
                        collapsed = true;
                    }
                }
            }
        } while (collapsed);

        return this;
    }

    /**
     * Discards child rotation, and applied positioning to cubes
     * @return this
     */
    public EntityGeometry embedPositioning() {
        this.xRot = 0.0f;
        this.yRot = 0.0f;
        this.zRot = 0.0f;

        for (var cube : this.cubes) {
            cube.move(this.x, this.y, this.z);
        }

        for (var child : this.children.values()) {
            child.x += this.x;
            child.y += this.y;
            child.z += this.z;
        }

        this.x = 0.0f;
        this.y = 0.0f;
        this.z = 0.0f;

        this.children.forEach((name, part) -> {
            part.embedPositioning();
        });

        return this;
    }

    public static class Vertex {
        public final Vector3f pos;
        public float u;
        public float v;

        public Vertex(Vertex copyFrom) {
            this.pos = new Vector3f(copyFrom.pos);
            this.u = copyFrom.u;
            this.v = copyFrom.v;
        }

        public Vertex(ModelPart.Vertex copyFrom) {
            this.pos = new Vector3f(copyFrom.pos);
            this.u = copyFrom.u;
            this.v = copyFrom.v;
        }

        public Vertex(float x, float y, float z, float u, float v) {
            this(new Vector3f(x, y, z), u, v);
        }

        public Vertex remap(float u, float v) {
            return new Vertex(this.pos, u, v);
        }

        public Vertex(Vector3f pos, float u, float v) {
            this.pos = pos;
            this.u = u;
            this.v = v;
        }
    }

    public static class Polygon {
        public Vertex[] vertices;
        public Vector3f normal;

        public enum Edge {
            UP, RIGHT, DOWN, LEFT;

            public Edge getOpposite() {
                return switch (this) {
                    case UP -> DOWN;
                    case DOWN -> UP;
                    case LEFT -> RIGHT;
                    case RIGHT -> LEFT;
                };
            }

            public static @Nullable Edge fromFaceAndDirection(Direction face, Direction direction) {
                if (face.getAxis() == direction.getAxis())
                    return null;
                if (face.getAxis().isHorizontal()) {
                    direction = switch (face) {
                        case NORTH -> Rotation.CLOCKWISE_180.rotate(direction);
                        case EAST -> Rotation.CLOCKWISE_90.rotate(direction);
                        case WEST -> Rotation.COUNTERCLOCKWISE_90.rotate(direction);
                        default -> direction;
                    };

                    return switch (direction) {
                        case UP -> Edge.UP;
                        case DOWN -> Edge.DOWN;
                        case EAST -> Edge.RIGHT;
                        case WEST -> Edge.LEFT;
                        default -> null;
                    };
                } else {
                    if (face == Direction.UP) {
                        return switch (direction) {
                            case NORTH -> Edge.DOWN;
                            case SOUTH -> Edge.UP;
                            case EAST -> Edge.LEFT;
                            case WEST -> Edge.RIGHT;
                            default -> null;
                        };
                    } else if (face == Direction.DOWN) {
                        return switch (direction) {
                            case NORTH -> Edge.DOWN;
                            case SOUTH -> Edge.UP;
                            case EAST -> Edge.RIGHT;
                            case WEST -> Edge.LEFT;
                            default -> null;
                        };
                    }
                }
                return null;
            }
        }

        public Polygon(Polygon copyFrom) {
            this.vertices = new Vertex[copyFrom.vertices.length];
            int i = 0;
            for (var poly : copyFrom.vertices) {
                this.vertices[i++] = new Vertex(poly);
            }
            this.normal = new Vector3f(copyFrom.normal);
        }

        public Polygon(ModelPart.Polygon copyFrom) {
            this.vertices = new Vertex[copyFrom.vertices.length];
            int i = 0;
            for (var poly : copyFrom.vertices) {
                this.vertices[i++] = new Vertex(poly);
            }
            this.normal = new Vector3f(copyFrom.normal);
        }

        public Polygon growEdge(@NotNull Edge edge, float offset) {
            var face = Direction.getNearest(normal.x, normal.y, normal.z);
            Vector3f downDir = new Vector3f(0.0f, -1.0f, 0.0f), rightDir = new Vector3f(1.0f, 0.0f, 0.0f);

            Vertex topLeft = this.vertices[0], topRight = this.vertices[0], bottomLeft = this.vertices[0], bottomRight = this.vertices[0];
            switch (face.getAxis()) {
                case X -> {
                    for (var vert : this.vertices) {
                        if (vert.pos.y >= topLeft.pos.y && vert.pos.z * face.getAxisDirection().getStep() <= topLeft.pos.z * face.getAxisDirection().getStep())
                            topLeft = vert;
                        if (vert.pos.y >= topRight.pos.y && vert.pos.z * face.getAxisDirection().getStep() >= topRight.pos.z * face.getAxisDirection().getStep())
                            topRight = vert;
                        if (vert.pos.y <= bottomLeft.pos.y && vert.pos.z * face.getAxisDirection().getStep() <= bottomLeft.pos.z * face.getAxisDirection().getStep())
                            bottomLeft = vert;
                        if (vert.pos.y <= bottomRight.pos.y && vert.pos.z * face.getAxisDirection().getStep() >= bottomRight.pos.z * face.getAxisDirection().getStep())
                            bottomRight = vert;
                    }

                    rightDir.set(0.0f, 0.0f, face.getAxisDirection().getStep());
                }
                case Z -> {
                    for (var vert : this.vertices) {
                        if (vert.pos.y >= topLeft.pos.y && vert.pos.x * face.getAxisDirection().getStep() >= topLeft.pos.x * face.getAxisDirection().getStep())
                            topLeft = vert;
                        if (vert.pos.y >= topRight.pos.y && vert.pos.x * face.getAxisDirection().getStep() <= topRight.pos.x * face.getAxisDirection().getStep())
                            topRight = vert;
                        if (vert.pos.y <= bottomLeft.pos.y && vert.pos.x * face.getAxisDirection().getStep() >= bottomLeft.pos.x * face.getAxisDirection().getStep())
                            bottomLeft = vert;
                        if (vert.pos.y <= bottomRight.pos.y && vert.pos.x * face.getAxisDirection().getStep() <= bottomRight.pos.x * face.getAxisDirection().getStep())
                            bottomRight = vert;
                    }

                    rightDir.set(face.getAxisDirection().getStep(), 0.0f, 0.0f);
                }
                case Y -> {
                    for (var vert : this.vertices) {
                        if (vert.pos.z <= topLeft.pos.z && vert.pos.x <= topLeft.pos.x)
                            topLeft = vert;
                        if (vert.pos.z <= topRight.pos.z && vert.pos.x >= topRight.pos.x)
                            topRight = vert;
                        if (vert.pos.z >= bottomLeft.pos.z && vert.pos.x <= bottomLeft.pos.x)
                            bottomLeft = vert;
                        if (vert.pos.z >= bottomRight.pos.z && vert.pos.x >= bottomRight.pos.x)
                            bottomRight = vert;
                    }

                    downDir.set(0.0f, 0.0f, face.getAxisDirection().getStep());
                }
            }

            switch (edge) { // TODO UV
                case UP -> {
                    downDir.mul(-offset, -offset, -offset);
                    topLeft.pos.add(downDir);
                    topRight.pos.add(downDir);
                }
                case DOWN -> {
                    downDir.mul(offset, offset, offset);
                    bottomLeft.pos.add(downDir);
                    bottomRight.pos.add(downDir);
                }
                case LEFT -> {
                    rightDir.mul(-offset, -offset, -offset);
                    topLeft.pos.add(rightDir);
                    bottomLeft.pos.add(rightDir);
                }
                case RIGHT -> {
                    rightDir.mul(offset, offset, offset);
                    topRight.pos.add(rightDir);
                    bottomRight.pos.add(rightDir);
                }
            }

            return this;
        }

        public Polygon shrinkEdge(@NotNull Edge edge, float offset) {
            return growEdge(edge, -offset);
        }

        public Polygon move(Vector3fc vector3fc) {
            return move(vector3fc.x(), vector3fc.y(), vector3fc.z(), this);
        }

        public Polygon move(Vector3fc vector3fc, Polygon dest) {
            return move(vector3fc.x(), vector3fc.y(), vector3fc.z(), dest);
        }

        public Polygon move(float x, float y, float z) {
            return move(x, y, z, this);
        }

        public Polygon move(float x, float y, float z, Polygon dest) {
            for (int i = 0; i < this.vertices.length; ++i) {
                dest.vertices[i].pos.x = this.vertices[i].pos.x + x;
                dest.vertices[i].pos.y = this.vertices[i].pos.y + y;
                dest.vertices[i].pos.z = this.vertices[i].pos.z + z;
            }

            return dest;
        }
    }

    public static class Cube {
        public Polygon[] polygons;

        public Cube(Polygon[] copyPolys) {
            this.polygons = new Polygon[copyPolys.length];
            int i = 0;
            for (var poly : copyPolys) {
                this.polygons[i++] = new Polygon(poly);
            }
        }

        public Cube(Cube copyFrom) {
            final var copyPolys = copyFrom.polygons;
            this.polygons = new Polygon[copyPolys.length];
            int i = 0;
            for (var poly : copyPolys) {
                this.polygons[i++] = new Polygon(poly);
            }
        }

        public Cube(ModelPart.Cube copyFrom) {
            final var copyPolys = ((CubeExtender)copyFrom).getPolygons();
            this.polygons = new Polygon[copyPolys.length];
            int i = 0;
            for (var poly : copyPolys) {
                this.polygons[i++] = new Polygon(poly);
            }
        }

        @Override
        public String toString() {
            var min = this.getMin();
            var max = this.getMax();
            var size = new Vector3f();
            max.sub(min, size);
            return String.format("Cube[%f,%f,%f] {%.2f,%.2f,%.2f -> %.2f,%.2f,%.2f}",
                    size.x, size.y, size.z, min.x, min.y, min.z, max.x, max.y, max.z);
        }

        @Nullable
        public Polygon getFace(Direction normal) {
            for (Polygon poly : polygons) {
                if (poly.normal.dot(normal.step()) >= 0.95f)
                    return poly;
            }
            return null;
        }

        public void putFace(Polygon polygon) {
            for (int i = 0; i < polygons.length; i++) {
                if (polygons[i].normal.dot(polygon.normal) >= 0.95f) {
                    polygons[i] = polygon;
                    return;
                }
            }
        }

        public void removeFace(Direction normal) {
            for (int i = 0; i < polygons.length; i++) {
                if (polygons[i].normal.dot(normal.step()) >= 0.95f) {
                    var replacement = new Polygon[polygons.length - 1];

                    for (int x = 0; x < replacement.length; x++) {
                        replacement[x] = polygons[x < i ? x : (x + 1)];
                    }

                    polygons = replacement;
                    return;
                }
            }
        }

        public Cube clampToFit(Cube clampBy) {
            return clampToFit(clampBy, this);
        }

        public Cube clampToFit(Cube clampBy, Cube dest) {
            return clampToFit(clampBy.getMin(), clampBy.getMax(), this);
        }

        public Cube clampToFit(Vector3fc clampMin, Vector3fc clampMax) {
            return clampToFit(clampMin, clampMax, this);
        }

        public Cube clampToFit(Vector3fc clampMin, Vector3fc clampMax, Cube dest) {
            Vector3f thisMin = this.getMin();
            Vector3f thisMax = this.getMax();
            Vector3f thisSize = new Vector3f();
            Vector3f thisCenter = new Vector3f();

            thisMax.sub(thisMin, thisSize);
            thisSize.mul(0.5f, thisCenter).add(thisMin);

            Vector3f sizeClamp = new Vector3f();
            clampMax.sub(clampMin, sizeClamp);
            var result = clampToFitSize(sizeClamp, dest);
            Vector3f resultSize = new Vector3f();
            Vector3f resultCenter = new Vector3f();
            result.getMax().sub(result.getMin(), resultSize);
            resultSize.mul(0.5f, resultCenter).add(result.getMin());

            Vector3f clampCenter = new Vector3f();
            clampMax.add(clampMin, clampCenter).mul(0.5f);

            Vector3f wantedMovement = new Vector3f(); // How much the cube wants to move
            clampCenter.sub(resultCenter, wantedMovement);
            Vector3f allowedMovement = new Vector3f(); // How much the cube can move in any direction
            thisSize.sub(resultSize, allowedMovement).mul(0.5f);

            Vector3f appliedMovement = new Vector3f(
                    Mth.clamp(wantedMovement.x, -allowedMovement.x, allowedMovement.x),
                    Mth.clamp(wantedMovement.y, -allowedMovement.y, allowedMovement.y),
                    Mth.clamp(wantedMovement.z, -allowedMovement.z, allowedMovement.z)
            );

            // TODO remap UV for movement applied

            return result.move(appliedMovement);
        }

        public Cube clampToFitSize(Vector3fc sizeClamp) {
            return clampToFitSize(sizeClamp, this);
        }

        /**
         * Shrinks this cube from the center to fit sizeClamp, and stores the result in dest
         * @param sizeClamp
         * @param dest
         * @return
         */
        public Cube clampToFitSize(Vector3fc sizeClamp, Cube dest) {
            Vector3f thisMin = this.getMin();
            Vector3f thisMax = this.getMax();
            Vector3f thisSize = new Vector3f();
            Vector3f thisCenter = new Vector3f();

            thisMax.sub(thisMin, thisSize);
            thisSize.mul(0.5f, thisCenter).add(thisMin);

            Vector3f deltaSize = new Vector3f();
            sizeClamp.sub(thisSize, deltaSize);
            deltaSize.set(Math.min(deltaSize.x, 0.0f), Math.min(deltaSize.y, 0.0f), Math.min(deltaSize.z, 0.0f));
            Vector3f scalarSize = new Vector3f();
            sizeClamp.div(thisSize, scalarSize);
            scalarSize.set(Math.min(scalarSize.x, 1.0f), Math.min(scalarSize.y, 1.0f), Math.min(scalarSize.z, 1.0f));

            for (Direction normal : Direction.values()) {
                Polygon thisFace = this.getFace(normal);
                Polygon destFace = dest.getFace(normal);
                if (thisFace == null || destFace == null)
                    continue;

                float minU = Float.MAX_VALUE;
                float maxU = -Float.MAX_VALUE;
                float minV = Float.MAX_VALUE;
                float maxV = -Float.MAX_VALUE;
                for (int i = 0; i < thisFace.vertices.length; ++i) {
                    if (thisFace.vertices[i].u < minU)
                        minU = thisFace.vertices[i].u;
                    if (thisFace.vertices[i].v < minV)
                        minV = thisFace.vertices[i].v;

                    if (thisFace.vertices[i].u > maxU)
                        maxU = thisFace.vertices[i].u;
                    if (thisFace.vertices[i].v > maxV)
                        maxV = thisFace.vertices[i].v;
                }

                float avgU = (minU + maxU) * 0.5f;
                float avgV = (minV + maxV) * 0.5f;

                for (int i = 0; i < thisFace.vertices.length; ++i) {
                    destFace.vertices[i].pos.x = thisCenter.x + ((thisFace.vertices[i].pos.x - thisCenter.x) * scalarSize.x);
                    destFace.vertices[i].pos.y = thisCenter.y + ((thisFace.vertices[i].pos.y - thisCenter.y) * scalarSize.y);
                    destFace.vertices[i].pos.z = thisCenter.z + ((thisFace.vertices[i].pos.z - thisCenter.z) * scalarSize.z);

                    float su;
                    float sv;
                    switch (normal) {
                        case NORTH, SOUTH -> { su = scalarSize.x; sv = scalarSize.y; }
                        case EAST, WEST -> { su = scalarSize.z; sv = scalarSize.y; }
                        case UP, DOWN -> { su = scalarSize.x; sv = scalarSize.z; }
                        default -> { su = 0.0f; sv = 0.0f; }
                    }

                    destFace.vertices[i].u = avgU + ((thisFace.vertices[i].u - avgU) * su);
                    destFace.vertices[i].v = avgV + ((thisFace.vertices[i].v - avgV) * sv);
                }
            }

            return dest;
        }

        public Cube scale(float x, float y, float z) {
            return scale(x, y, z, this);
        }

        public Cube scale(float x, float y, float z, Cube dest) {
            for (Direction normal : Direction.values()) {
                Polygon thisFace = this.getFace(normal);
                Polygon destFace = dest.getFace(normal);
                if (thisFace == null || destFace == null)
                    continue;

                for (int i = 0; i < thisFace.vertices.length; ++i) {
                    destFace.vertices[i].pos.x = thisFace.vertices[i].pos.x * x;
                    destFace.vertices[i].pos.y = thisFace.vertices[i].pos.y * y;
                    destFace.vertices[i].pos.z = thisFace.vertices[i].pos.z * z;
                }
            }

            return dest;
        }

        public Cube offsetSize(float x, float y, float z) {
            return offsetSize(x, y, z, this);
        }

        public Cube offsetSize(float x, float y, float z, Cube dest) {
            Vector3f thisMin = this.getMin();
            Vector3f thisMax = this.getMax();
            Vector3f thisSize = new Vector3f();
            Vector3f thisCenter = new Vector3f();

            thisMax.sub(thisMin, thisSize);
            thisSize.mul(0.5f, thisCenter).add(thisMin);

            for (Direction normal : Direction.values()) {
                Polygon thisFace = this.getFace(normal);
                Polygon destFace = dest.getFace(normal);
                if (thisFace == null || destFace == null)
                    continue;

                for (int i = 0; i < thisFace.vertices.length; ++i) {
                    destFace.vertices[i].pos.x = thisFace.vertices[i].pos.x + (thisFace.vertices[i].pos.x < thisCenter.x ? -x : x);
                    destFace.vertices[i].pos.y = thisFace.vertices[i].pos.y + (thisFace.vertices[i].pos.y < thisCenter.y ? -y : y);
                    destFace.vertices[i].pos.z = thisFace.vertices[i].pos.z + (thisFace.vertices[i].pos.z < thisCenter.z ? -z : z);
                }
            }

            return dest;
        }

        public Cube move(Vector3fc delta) {
            return move(delta.x(), delta.y(), delta.z(), this);
        }

        public Cube move(Vector3fc delta, Cube dest) {
            return move(delta.x(), delta.y(), delta.z(), dest);
        }

        public Cube move(float x, float y, float z) {
            return move(x, y, z, this);
        }

        public Cube move(float x, float y, float z, Cube dest) {
            for (Direction normal : Direction.values()) {
                Polygon thisFace = this.getFace(normal);
                Polygon destFace = dest.getFace(normal);
                if (thisFace == null || destFace == null)
                    continue;

                thisFace.move(x, y, z, destFace);
            }

            return dest;
        }

        public static class SegmentCompute {
            public static float EPSILON = 0.00001f;

            public Cube source;
            public Vector3fc min, max;
            public boolean resolved = false;

            public SegmentCompute(Cube source) {
                this.source = source;
                this.min = source.getMin();
                this.max = source.getMax();
            }

            @Override
            public String toString() {
                var size = new Vector3f();
                max.sub(min, size);
                return String.format("Cube.SegmentCompute[%f,%f,%f] {%.2f,%.2f,%.2f -> %.2f,%.2f,%.2f}",
                        size.x, size.y, size.z, min.x(), min.y(), min.z(), max.x(), max.y(), max.z());
            }

            public Vector3f getSize() {
                var r = new Vector3f();
                return max.sub(min, r);
            }

            public Vector3f getCenter() {
                var r = new Vector3f();
                return max.add(min, r).mul(0.5f);
            }

            public Vector3f getCenter(Direction surface) {
                var r = new Vector3f();
                var size = this.getSize().mul(surface.step()).mul(0.5f);
                return max.add(min, r).mul(0.5f).add(size);
            }

            public @Nullable Direction.Axis getBestAxisForSplit(Cube other) {
                return getBestAxisForSplit(new SegmentCompute(other));
            }

            public @Nullable Direction.Axis getBestAxisForSplit(SegmentCompute other) {
                var thisCenter = this.getCenter();
                var otherCenter = other.getCenter();

                Direction.Axis centerAxis = null;
                float dx = Mth.abs(thisCenter.x - otherCenter.x);
                float dy = Mth.abs(thisCenter.y - otherCenter.y);
                float dz = Mth.abs(thisCenter.z - otherCenter.z);
                if (dz < dy && dx < dy)
                    centerAxis = Direction.Axis.Y;
                else if (dy < dz && dx < dz)
                    centerAxis = Direction.Axis.Z;
                else if (dy < dx && dz < dx)
                    centerAxis = Direction.Axis.X;

                /*if (centerAxis != null) {
                    TODO maybe fail axis if part is too misaligned
                }*/

                return centerAxis;
            }

            public float rateSimilarity(Cube other) {
                return rateSimilarity(new SegmentCompute(other));
            }

            public float rateSimilarity(SegmentCompute other) {
                float lowest = Float.MAX_VALUE;
                for (var normal : Direction.values()) {
                    float distance = this.getCenter(normal).distanceSquared(other.getCenter(normal));
                    if (distance < lowest)
                        lowest = distance;
                }

                return lowest;
            }
        }

        public Cube growFace(@NotNull Direction targetFace, float offset) {
            for (var normal : Direction.values()) {
                if (normal == targetFace.getOpposite()) continue;

                var face = this.getFace(normal);
                if (face == null) continue;

                if (normal == targetFace) {
                    face.move(targetFace.step().mul(offset));
                } else {
                    var edge = Polygon.Edge.fromFaceAndDirection(normal, targetFace);
                    if (edge != null)
                        face.growEdge(edge, offset);
                }
            }

            return this;
        }

        public Cube shrinkFace(@NotNull Direction targetFace, float offset) {
            return growFace(targetFace, -offset);
        }

        public Pair<Cube, Cube> splitOnAxis(@NotNull Direction.Axis onAxis) {
            return splitOnAxis(onAxis, 0.5f);
        }

        /**
         * Creates a pair of cubes that take the same space as this cube, split along an axis and a distrobution
         * @param onAxis the axis to split upon
         * @param distribution point from 0 to 1 that draws the line where to split, resulting in a (distribution):(1 - distribution) split
         * @return A pair of cubes. The first cube is position lower on the axis, and the second, higher.
         */
        public Pair<Cube, Cube> splitOnAxis(@NotNull Direction.Axis onAxis, float distribution) {
            Vector3f thisMin = this.getMin();
            Vector3f thisMax = this.getMax();
            Vector3f thisSize = new Vector3f();

            thisMax.sub(thisMin, thisSize);

            Vector3f deltaPositionFirst = new Vector3f();
            thisSize.mul(1.0f - distribution, deltaPositionFirst);

            Vector3f deltaPositionSecond = new Vector3f();
            thisSize.mul(distribution, deltaPositionSecond);

            var copyFirst = new Cube(this);
            var copySecond = new Cube(this);

            Direction dirFirst = Direction.fromAxisAndDirection(onAxis, Direction.AxisDirection.POSITIVE);
            Direction dirSecond = Direction.fromAxisAndDirection(onAxis, Direction.AxisDirection.NEGATIVE);

            copyFirst.shrinkFace(dirFirst, (float)onAxis.choose(deltaPositionFirst.x, deltaPositionFirst.y, deltaPositionFirst.z));
            copySecond.shrinkFace(dirSecond, (float)onAxis.choose(deltaPositionSecond.x, deltaPositionSecond.y, deltaPositionSecond.z));

            return Pair.of(copyFirst, copySecond);
        }

        public List<Cube> segment(List<Cube> toMatch) {
            return segment(toMatch, new ArrayList<>(toMatch.size()));
        }

        public List<Cube> segment(List<Cube> toMatch, List<Cube> output) {
            if (toMatch.size() == 1)
                output.add(new Cube(this));

            SplittingSource splittingCubes = SplittingSource.forCube(this, (t, u) -> {});
            return segment(splittingCubes, toMatch, new ArrayList<>(toMatch.size()));
        }

        public static List<Cube> segment(SplittingSource splittingCubes, List<Cube> toMatch) {
            return segment(splittingCubes, toMatch, new ArrayList<>(toMatch.size()));
        }

        public static List<Cube> segment(SplittingSource splittingCubes, List<Cube> toMatch, List<Cube> output) {
            for (int i = 0; i < toMatch.size(); i++) {
                var match = toMatch.get(i);
                final int myIndex = i;
                output.add(splittingCubes.splitFor(match.getMin(), match.getMax(), (oldCube, newCube) -> {
                    output.set(myIndex, newCube);
                }));
            }

            return output;
        }

        public static List<SegmentCompute> segmentDeferred(Set<Cube> availableSpace, List<Cube> toMatch) {
            return segmentDeferred(availableSpace, toMatch, new ArrayList<>(toMatch.size()));
        }

        public static List<SegmentCompute> segmentDeferred(Set<Cube> availableSpace, List<Cube> toMatch, List<SegmentCompute> output) {


            return output;
        }

        public Vector3f getMin() {
            float minX = Float.MAX_VALUE;
            float minY = Float.MAX_VALUE;
            float minZ = Float.MAX_VALUE;
            for (var poly : polygons) {
                for (var vert : poly.vertices) {
                    if (vert.pos.x < minX)
                        minX = vert.pos.x;
                    if (vert.pos.y < minY)
                        minY = vert.pos.y;
                    if (vert.pos.z < minZ)
                        minZ = vert.pos.z;
                }
            }

            return new Vector3f(minX, minY, minZ);
        }

        public Vector3f getMax() {
            float maxX = -Float.MAX_VALUE;
            float maxY = -Float.MAX_VALUE;
            float maxZ = -Float.MAX_VALUE;
            for (var poly : polygons) {
                for (var vert : poly.vertices) {
                    if (vert.pos.x > maxX)
                        maxX = vert.pos.x;
                    if (vert.pos.y > maxY)
                        maxY = vert.pos.y;
                    if (vert.pos.z > maxZ)
                        maxZ = vert.pos.z;
                }
            }

            return new Vector3f(maxX, maxY, maxZ);
        }

        public void compile(PoseStack.Pose pose, VertexConsumer vertexConsumer, int overlay, int lightCoords, float red, float green, float blue, float alpha) {
            Matrix4f matrix4f = pose.pose();
            Matrix3f matrix3f = pose.normal();

            for(Polygon polygon : this.polygons) {
                Vector3f orientedNormal = matrix3f.transform(new Vector3f(polygon.normal));
                float nx = orientedNormal.x();
                float ny = orientedNormal.y();
                float nz = orientedNormal.z();

                for(Vertex vertex : polygon.vertices) {
                    float x = vertex.pos.x() / 16.0F;
                    float y = vertex.pos.y() / 16.0F;
                    float z = vertex.pos.z() / 16.0F;
                    Vector4f vector4f = matrix4f.transform(new Vector4f(x, y, z, 1.0F));
                    vertexConsumer.vertex(vector4f.x(), vector4f.y(), vector4f.z(), red, green, blue, alpha, vertex.u, vertex.v, lightCoords, overlay, nx, ny, nz);
                }
            }
        }
    }
}
