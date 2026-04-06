package net.ltxprogrammer.changed.client.renderer.layers;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.client.*;
import net.ltxprogrammer.changed.client.latexparticles.LatexDripParticle;
import net.ltxprogrammer.changed.client.latexparticles.SetupContext;
import net.ltxprogrammer.changed.client.latexparticles.SurfacePoint;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.data.MixedTexture;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Vector3f;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class LatexParticlesLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> implements FirstPersonLayer<T> {
    private final RenderLayerParent<T, M> parent;
    private final Minecraft minecraft;
    private final Predicate<ModelPart> canPartDrip;

    private final Map<EntityModel<T>, Function<T, ResourceLocation>> models = new HashMap<>();

    private static final NativeImage MISSING_TEXTURE = new NativeImage(1, 1, false);
    private static final Map<ResourceLocation, NativeImage> cachedTextures = new HashMap<>();

    private static boolean always(ModelPart part) {
        return true;
    }

    /**
     * Creates an empty particle layer
     * @param parent renderer that owns the layer
     */
    public LatexParticlesLayer(RenderLayerParent<T, M> parent) {
        super(parent);
        this.parent = parent;
        this.minecraft = Minecraft.getInstance();
        this.canPartDrip = LatexParticlesLayer::always;
    }

    /**
     * Creates an empty particle layer
     * @param parent renderer that owns the layer
     * @param canPartDrip predicate for filtering which parts can have particles
     */
    public LatexParticlesLayer(RenderLayerParent<T, M> parent, Predicate<ModelPart> canPartDrip) {
        super(parent);
        this.parent = parent;
        this.minecraft = Minecraft.getInstance();
        this.canPartDrip = canPartDrip;
    }

    /**
     * Creates a particle layer for the base entity model
     * @param parent renderer that owns the layer
     * @param model base entity model
     */
    public LatexParticlesLayer(RenderLayerParent<T, M> parent, M model) {
        super(parent);
        this.parent = parent;
        this.minecraft = Minecraft.getInstance();
        this.canPartDrip = LatexParticlesLayer::always;

        models.put(model, parent::getTextureLocation);
    }

    /**
     * Creates a particle layer for the base entity model
     * @param parent renderer that owns the layer
     * @param model base entity model
     * @param canPartDrip predicate for filtering which parts can have particles
     */
    public LatexParticlesLayer(RenderLayerParent<T, M> parent, M model, Predicate<ModelPart> canPartDrip) {
        super(parent);
        this.parent = parent;
        this.minecraft = Minecraft.getInstance();
        this.canPartDrip = canPartDrip;

        models.put(model, parent::getTextureLocation);
    }

    public LatexParticlesLayer<T, M> addModel(EntityModel<T> model) {
        models.put(model, parent::getTextureLocation);
        return this;
    }

    public LatexParticlesLayer<T, M> addModel(EntityModel<T> model, Function<T, ResourceLocation> textureFetcher) {
        models.put(model, textureFetcher);
        return this;
    }

    public static void purgeTextureCache() {
        cachedTextures.values().forEach(NativeImage::close);
        cachedTextures.clear();
    }

    public NativeImage getImage(ResourceLocation name) {
        return cachedTextures.computeIfAbsent(name, resourceLocation ->
                tryFromDisk(resourceLocation).or(() -> trySkinDirectory(resourceLocation)).orElse(MISSING_TEXTURE));
    }

    public Optional<NativeImage> tryFromDisk(ResourceLocation name) {
        return minecraft.getResourceManager().getResource(name)
                .flatMap(resource -> {
                    try (var inputStream = resource.open()) {
                        return Optional.of(NativeImage.read(inputStream));
                    } catch (IOException ignored) {}

                    return Optional.empty();
                });
    }

    public Optional<NativeImage> trySkinDirectory(ResourceLocation name) {
        return ((SkinManagerExtender)Minecraft.getInstance().getSkinManager()).getSkinImage(name);
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

    public static SurfacePoint findRandomSurface(ModelPart part, RandomSource random) {
        var cube = ((ModelPartExtender)(Object)part).getRandomCubeWeighted(random);
        var polygon = ((CubeExtender)cube).getRandomPolygonWeighted(random);

        float lerpX = random.nextFloat();
        float lerpY = random.nextFloat();
        var vertex = lerpVertex(
                lerpVertex(polygon.vertices[0], polygon.vertices[1], lerpX),
                lerpVertex(polygon.vertices[3], polygon.vertices[2], lerpX),
                lerpY
        );

        Vector3f scaledPos = new Vector3f();
        return new SurfacePoint(polygon.normal, vertex.pos.mul(1.0f / 16.0f, scaledPos), new UVPair(vertex.u, vertex.v));
    }

    private Optional<EntityModel<T>> getRandomModel(RandomSource random) {
        if (this.models.isEmpty())
            return Optional.empty();
        int indexToGet = random.nextInt(this.models.size());
        for (var key : this.models.keySet()) {
            if (indexToGet-- == 0)
                return Optional.of(key);
        }
        return Optional.empty();
    }

    public void createNewDripParticle(LivingEntity entity) {
        var optionalModel = getRandomModel(entity.getRandom());
        if (optionalModel.isEmpty())
            return;
        var model = optionalModel.get();
        List<ModelPartStem> partsWithCubes;
        if (model instanceof AdvancedHumanoidModel<?> advancedHumanoidModel)
            partsWithCubes = advancedHumanoidModel.getAllParts().filter(part -> !part.getLeaf().cubes.isEmpty()).filter(part -> canPartDrip.test(part.getLeaf())).toList();
        else if (model instanceof AgeableListModel<?> ageableListModel) {
            partsWithCubes = Stream.concat(
                    StreamSupport.stream(ageableListModel.headParts().spliterator(), false),
                    StreamSupport.stream(ageableListModel.bodyParts().spliterator(), false)
            ).flatMap(ModelPartStem::streamFromRoot).filter(part -> !part.getLeaf().cubes.isEmpty()).filter(part -> canPartDrip.test(part.getLeaf())).toList();
        } else {
            partsWithCubes = List.of();
        }
        if (partsWithCubes.isEmpty())
            return;

        var partToAttach = partsWithCubes.get(entity.level().random.nextInt(partsWithCubes.size()));
        var surface = findRandomSurface(partToAttach.getLeaf(), entity.level().random);

        Color3 color;
        float alpha;

        var image = getImage(models.get(model).apply((T)entity));

        if (image != MISSING_TEXTURE) {
            var rgba = MixedTexture.sampleNearest(image, null, surface.uv().u(), surface.uv().v());
            color = new Color3(rgba.r(), rgba.g(), rgba.b());
            alpha = rgba.a();
            if (alpha < 0.00001f)
                return;
        } else
            return;

        ChangedClient.particleSystem.getOrThrow()
                .addParticle(LatexDripParticle.of(entity, model, partToAttach, surface, color, alpha, 100));
    }

    private static Predicate<ModelPart> capturingPose = null;
    private static BiConsumer<ModelPart, PoseStack.Pose> handlePose = null;
    public static boolean isCapturing() {
        return capturingPose != null;
    }

    public static boolean capture(ModelPart part, PoseStack pose) {
        if (capturingPose == null)
            return false;

        handlePose.accept(part, ((PoseStackExtender)pose).copyLast());

        return true;
    }

    @Override
    public void render(PoseStack pose, MultiBufferSource bufferSource, int packedLight, T entity, float f1, float f2, float partialTicks, float bobAmount, float f3, float f4) {
        final var particles = ChangedClient.particleSystem.getOrThrow().getAllParticlesForEntity(entity);
        if (particles.isEmpty())
            return;

        capturingPose = part -> particles.stream().anyMatch(particle -> particle.wantsPartInfo(part));
        handlePose = (part, modelPose) -> particles.stream().filter(particle -> particle.wantsPartInfo(part)).forEach(particle -> {
            particle.handlePartPosition(part, modelPose);
        });

        parent.getModel().renderToBuffer(pose, bufferSource.getBuffer(parent.getModel().renderType(parent.getTextureLocation(entity))),
                packedLight, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f);

        handlePose = null;
        capturingPose = null;

        particles.forEach(particle -> {
            particle.setupForRender(pose, partialTicks, SetupContext.THIRD_PERSON);
            particle.renderFromLayer(bufferSource, partialTicks);
        });
    }

    /*@Override
    public void renderFirstPersonOnArms(PoseStack pose, MultiBufferSource bufferSource, int packedLight, T entity, HumanoidArm arm, PoseStack stackCorrector, float partialTick) {
        final var model = parent.getModel();
        model.setAllLimbsVisible(entity, false);
        model.getArm(arm).visible = true;

        ChangedClient.particleSystem.getAllParticlesForEntity(entity).forEach(particle -> {
            particle.setupForRender(pose, partialTick, SetupContext.FIRST_PERSON);
        });

        model.setAllLimbsVisible(entity, true);
    }*/
}
