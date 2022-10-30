package net.ltxprogrammer.changed.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Vector3f;
import net.ltxprogrammer.changed.util.CollectionUtil;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.core.Direction;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Function;

public class DelayLoadedModel {
    private static Vector3f vec3fFromJson(JsonArray triple) {
        return new Vector3f(triple.get(0).getAsFloat(), triple.get(1).getAsFloat(), triple.get(2).getAsFloat());
    }

    protected record UV(int left, int top, int right, int bottom) {
        public static UV parse(JsonObject object) {
            JsonArray array = object.get("uv").getAsJsonArray();
            return new UV(array.get(0).getAsInt(), array.get(1).getAsInt(), array.get(2).getAsInt(), array.get(3).getAsInt());
        }
    }

    protected record Part(String name, Vector3f from, Vector3f to, float inflate, Vector3f rotation, Vector3f origin, boolean mirrored, Map<Direction, UV> faces, UUID uuid) {
        public static Part parse(JsonObject object) {
            String name = object.get("name").getAsString();
            Vector3f from = vec3fFromJson(object.get("from").getAsJsonArray());
            Vector3f to = vec3fFromJson(object.get("to").getAsJsonArray());
            float inflate = object.has("inflate") ? object.get("inflate").getAsFloat() : 0.0f;
            Vector3f rotation = object.has("rotation") ? vec3fFromJson(object.get("rotation").getAsJsonArray()) : Vector3f.ZERO;
            Vector3f origin = vec3fFromJson(object.get("origin").getAsJsonArray());
            Map<Direction, UV> faces = new HashMap<>();
            JsonObject facesJson = object.get("faces").getAsJsonObject();
            facesJson.keySet().forEach((face) -> faces.put(Direction.byName(face), UV.parse(facesJson.get(face).getAsJsonObject())));
            UUID uuid = UUID.fromString(object.get("uuid").getAsString());

            if (object.has("uv_offset")) {
                return new Part(name, from, to, inflate, rotation, origin,
                        object.get("uv_offset").getAsJsonArray().get(0).getAsInt() != faces.get(Direction.EAST).left, faces, uuid);
            }

            else
                return new Part(name, from, to, inflate, rotation, origin, false, faces, uuid);
        }
    }

    protected record OutlineParent(Vector3f origin, UUID uuid) {
        public static OutlineParent of(Vector3f origin, UUID uuid) { return new OutlineParent(origin, uuid); }
    }

    protected record Outline(OutlineParent parent, String name, UUID uuid, Vector3f origin, Vector3f rotation, Set<UUID> childrenParts, Set<Outline> childrenOutline,
                             boolean rotationSubgroup) {
        public OutlineParent asParent() {
            return OutlineParent.of(origin, uuid);
        }

        public static Outline parse(final OutlineParent parent, JsonObject object) {
            if (object.has("outliner")) {
                Set<UUID> childrenParts = new HashSet<>();
                Set<Outline> childrenOutline = new HashSet<>();

                object.get("outliner").getAsJsonArray().forEach((element) -> {
                    if (element.isJsonPrimitive()) {
                        childrenParts.add(UUID.fromString(element.getAsString()));
                    }

                    else {
                        childrenOutline.add(Outline.parse(null, element.getAsJsonObject()));
                    }
                });

                return new Outline(parent, "ROOT", UUID.randomUUID(), new Vector3f(0, 0, 0), new Vector3f(0, 0, 0),
                        childrenParts, childrenOutline, false);
            }

            else {
                String name = object.get("name").getAsString();
                Vector3f origin = vec3fFromJson(object.get("origin").getAsJsonArray());
                Vector3f rotation = object.has("rotation") ? vec3fFromJson(object.get("rotation").getAsJsonArray()) : new Vector3f(0, 0, 0);

                Set<UUID> childrenParts = new HashSet<>();
                Set<Outline> childrenOutline = new HashSet<>();
                UUID uuid = UUID.fromString(object.get("uuid").getAsString());
                OutlineParent thisAsParent = OutlineParent.of(origin, uuid);

                object.get("children").getAsJsonArray().forEach((element) -> {
                    if (element.isJsonPrimitive()) {
                        childrenParts.add(UUID.fromString(element.getAsString()));
                    }

                    else {
                        childrenOutline.add(Outline.parse(thisAsParent, element.getAsJsonObject()));
                    }
                });

                return new Outline(parent, name, uuid, origin, rotation, childrenParts, childrenOutline, false);
            }
        }
    }

    private final int textureWidth, textureHeight;
    private final Map<UUID, Part> parts;
    private final Outline outline;

    public DelayLoadedModel(Map<UUID, Part> parts, Outline outline, int tWidth, int tHeight) {
        this.textureWidth = tWidth;
        this.textureHeight = tHeight;
        this.parts = parts;
        this.outline = outline;
    }

    public static DelayLoadedModel parse(JsonObject root) {
        JsonObject resolution = root.get("resolution").getAsJsonObject();
        int textureWidth = resolution.get("width").getAsInt();
        int textureHeight = resolution.get("height").getAsInt();

        JsonArray elements = root.get("elements").getAsJsonArray();

        Map<UUID, Part> parts = new HashMap<>();
        elements.forEach((element) -> {
            Part nPart = Part.parse(element.getAsJsonObject());
            parts.put(nPart.uuid(), nPart);
        });

        return new DelayLoadedModel(parts, Outline.parse(null, root), textureWidth, textureHeight);
    }
    public int getTexX(Part part) {
        return part.mirrored ? part.faces.get(Direction.WEST).right : part.faces.get(Direction.EAST).left;
    }

    public int getTexY(Part part) {
        return part.faces.get(Direction.DOWN).top;
    }

    public boolean significantRotation(Vector3f rotation) {
        return Math.abs(rotation.x()) > 0.0001f || Math.abs(rotation.y()) > 0.0001f || Math.abs(rotation.z()) > 0.0001f;
    }

    public float degToRad(float f) {
        return f * 0.0174532888888889f;
    }

    protected void getAllGroups(final List<Outline> list, Outline outline) {
        outline.childrenOutline.forEach((nextOutline) -> {
            list.add(nextOutline);
            getAllGroups(list, nextOutline);
        });
    }

    protected List<Outline> getAllGroups() {
        List<Outline> list = new ArrayList<>();
        getAllGroups(list, this.outline);
        return list;
    }

    protected static final Vector3f HEAD_OFFSET = new Vector3f(0.0f, 26.5f, 0.0f);
    protected static final Vector3f TORSO_OFFSET = new Vector3f(0.0f, 25.5f, 0.0f);
    protected static final Vector3f RIGHT_ARM_OFFSET = new Vector3f(5.0f, 24.5f, 0.0f);
    protected static final Vector3f LEFT_ARM_OFFSET = new Vector3f(-5.0f, 24.5f, 0.0f);
    protected static final Vector3f NO_OFFSET = new Vector3f(0.0f, 0.0f, 0.0f);

    public static final BiFunction<Function<UUID, Outline>, Pair<Part, Outline>, Vector3f> HUMANOID_PART_FIXER = (getOutline, pair) -> {
        if (pair.getSecond().name.equals("RightArm")) return RIGHT_ARM_OFFSET;
        if (pair.getSecond().name.equals("LeftArm")) return LEFT_ARM_OFFSET;
        if (pair.getSecond().name.equals("Head")) return HEAD_OFFSET;
        if (pair.getSecond().name.equals("Torso")) return TORSO_OFFSET;
        return NO_OFFSET;
    };

    public static final BiFunction<Function<UUID, Outline>, Outline, Vector3f> HUMANOID_GROUP_FIXER = (getOutline, outline) -> {
        if (outline.parent == null)
            return NO_OFFSET;
        Outline parent = getOutline.apply(outline.parent.uuid);
        if (parent == null)
            return NO_OFFSET;

        if (parent.name.equals("RightArm")) return RIGHT_ARM_OFFSET;
        if (parent.name.equals("LeftArm")) return LEFT_ARM_OFFSET;
        if (parent.name.equals("Head")) return HEAD_OFFSET;
        if (parent.name.equals("Torso")) return TORSO_OFFSET;
        return NO_OFFSET;
    };

    public static final BiFunction<Function<UUID, Outline>, Pair<Part, Outline>, Vector3f> PART_NO_FIX = (getOutline, pair) -> NO_OFFSET;

    public static final BiFunction<Function<UUID, Outline>, Outline, Vector3f> GROUP_NO_FIX = (getOutline, outline) -> NO_OFFSET;

    public LayerDefinition createBodyLayer(BiFunction<Function<UUID, Outline>, Pair<Part, Outline>, Vector3f> partFixer,
                                           BiFunction<Function<UUID, Outline>, Outline, Vector3f> groupFixer) {
        return createBodyLayer(partFixer, groupFixer, 0.0f);
    }
    public LayerDefinition createBodyLayer(BiFunction<Function<UUID, Outline>, Pair<Part, Outline>, Vector3f> partFixer,
                                           BiFunction<Function<UUID, Outline>, Outline, Vector3f> groupFixer, float deformation) {
        List<Outline> allGroups = getAllGroups();
        if (outline.childrenParts.size() > 0)
            allGroups.add(new Outline(null, "bb_main", UUID.randomUUID(), new Vector3f(0, 0, 0),
                new Vector3f(0, 0, 0), outline.childrenParts, Set.of(), false));

        List.copyOf(allGroups).forEach((group) -> {
            List<Outline> subgroups = new ArrayList<>();
            AtomicInteger groupIndex = new AtomicInteger(allGroups.indexOf(group));
            group.childrenParts.forEach((partUUID) -> {
                Part cube = parts.get(partUUID);
                if (significantRotation(cube.rotation)) {
                    Outline sub = CollectionUtil.find(subgroups, (s) -> {
                        if (!s.rotation.equals(cube.rotation)) return false;
                        if (significantRotation(s.rotation))
                            return s.origin.equals(cube.origin);
                        else {
                            if (s.rotation.x() == 0 && s.origin.x() != cube.origin.x()) return false;
                            if (s.rotation.y() == 0 && s.origin.y() != cube.origin.y()) return false;
                            if (s.rotation.z() == 0 && s.origin.z() != cube.origin.z()) return false;
                            return true;
                        }
                    });
                    if (sub == null) {
                        sub = new Outline(group.asParent(), cube.name + "_" + cube.uuid, UUID.randomUUID(), cube.origin, cube.rotation,
                                CollectionUtil.of(HashSet::new, cube.uuid), Set.of(), true);
                        subgroups.add(sub);
                        groupIndex.getAndIncrement();
                        CollectionUtil.insert(allGroups, groupIndex.get(), sub);
                    }
                    sub.childrenParts.add(cube.uuid);
                }
            });
        });

        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        Map<UUID, PartDefinition> mappedParts = new HashMap<>();

        Function<UUID, Outline> getOutline = (uuid) -> {
            for (Outline group : allGroups)
                if (group.uuid.equals(uuid))
                    return group;
            return null;
        };

        allGroups.forEach((group) -> {
            float rx = degToRad(-group.rotation.x());
            float ry = degToRad(-group.rotation.y());
            float rz = degToRad(group.rotation.z());

            Vector3f origin = group.origin.copy();
            if (group.parent != null)
                origin.sub(group.parent.origin);
            origin.mul(-1.0f, -1.0f, 1.0f);
            if (group.parent == null)
                origin.add(0.0f, 24.0f, 0.0f);

            Vector3f groupFix = groupFixer.apply(getOutline, group);
            float ox = origin.x() + groupFix.x();
            float oy = origin.y() + groupFix.y();
            float oz = origin.z() + groupFix.z();

            CubeListBuilder cubeListBuilder = CubeListBuilder.create();
            CollectionUtil.forEachReverse(group.childrenParts, (uuid) -> {
                Part cube = parts.get(uuid);
                if (significantRotation(cube.rotation) && !group.rotationSubgroup) return;

                Vector3f partFix = partFixer.apply(getOutline, Pair.of(cube, group));

                cubeListBuilder.texOffs(getTexX(cube), getTexY(cube));
                if (cube.mirrored)
                    cubeListBuilder.mirror();
                cubeListBuilder.addBox(group.origin.x() - cube.to.x() + partFix.x(),
                                -cube.to.y() + group.origin.y() + partFix.y(),
                                cube.from.z() - group.origin.z() + partFix.z(),

                                cube.to.x() - cube.from.x(),
                                cube.to.y() - cube.from.y(),
                                cube.to.z() - cube.from.z(),
                                new CubeDeformation(cube.inflate + deformation));
                if (cube.mirrored)
                    cubeListBuilder.mirror(false);
            });

            PartDefinition parentPart = group.parent != null ? mappedParts.get(group.parent.uuid) : partdefinition;

            mappedParts.put(group.uuid, parentPart.addOrReplaceChild(group.name, cubeListBuilder,
                    !significantRotation(group.rotation)
                        ? PartPose.offset(ox, oy, oz)
                        : PartPose.offsetAndRotation(ox, oy, oz, rx, ry, rz)));
        });

        return LayerDefinition.create(meshdefinition, textureWidth, textureHeight);
    }
}