package net.ltxprogrammer.changed.client;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.math.Transformation;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.latex.IClientLatexTypeExtensions;
import net.ltxprogrammer.changed.entity.latex.LatexType;
import net.ltxprogrammer.changed.entity.latex.SpreadingLatexType;
import net.ltxprogrammer.changed.init.ChangedLatexTypes;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.world.LatexCoverGetter;
import net.ltxprogrammer.changed.world.LatexCoverState;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.NamedRenderTypeManager;
import net.minecraftforge.client.model.IModelBuilder;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.geometry.UnbakedGeometryHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.slf4j.Logger;

import java.io.Reader;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LatexCoveredBlocksRenderer implements PreparableReloadListener {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ResourceLocation BLOCK_ATLAS = InventoryMenu.BLOCK_ATLAS;
    public static final FileToIdConverter MODEL_LISTER = FileToIdConverter.json("latex_cover_models");
    public static final FileToIdConverter STATE_LISTER = FileToIdConverter.json("latex_cover_model_blockstates");
    private static final ResourceLocation RENDERTYPE_SOLID = ResourceLocation.fromNamespaceAndPath(ResourceLocation.DEFAULT_NAMESPACE, "solid");

    private static final LatexModelDefinition.Context LATEX_MODEL_DEFINITION_CONTEXT = new LatexModelDefinition.Context();

    private static final ResourceLocation DEFAULT_TOP = Changed.modResource("default_top");
    private static final ResourceLocation DEFAULT_BOTTOM = Changed.modResource("default_bottom");
    private static final ResourceLocation DEFAULT_NORTH = Changed.modResource("default_north");
    private static final ResourceLocation DEFAULT_SOUTH = Changed.modResource("default_south");
    private static final ResourceLocation DEFAULT_EAST = Changed.modResource("default_east");
    private static final ResourceLocation DEFAULT_WEST = Changed.modResource("default_west");
    private static final ResourceLocation DEFAULT_EXTRA = Changed.modResource("default_extra");

    private static final MultiVariantFaces DEFAULT_MODEL = new MultiVariantFaces(
            Map.of(Direction.UP, new Variant(DEFAULT_TOP, BlockModelRotation.X0_Y0.getRotation(), false, 0),
                    Direction.DOWN, new Variant(DEFAULT_BOTTOM, BlockModelRotation.X0_Y0.getRotation(), false, 0),
                    Direction.NORTH, new Variant(DEFAULT_NORTH, BlockModelRotation.X0_Y0.getRotation(), false, 0),
                    Direction.SOUTH, new Variant(DEFAULT_SOUTH, BlockModelRotation.X0_Y0.getRotation(), false, 0),
                    Direction.EAST, new Variant(DEFAULT_EAST, BlockModelRotation.X0_Y0.getRotation(), false, 0),
                    Direction.WEST, new Variant(DEFAULT_WEST, BlockModelRotation.X0_Y0.getRotation(), false, 0)),
            new Variant(DEFAULT_EXTRA, BlockModelRotation.X0_Y0.getRotation(), false, 0)
    );

    public interface TypedModelResolver {
        CompletableFuture<BakedModel> resolve(ResourceLocation modelLocation, ModelState modelState);
    }

    public interface ModelResolver {
        CompletableFuture<BakedModel> resolve(LatexType type, ResourceLocation modelLocation, ModelState modelState);

        default TypedModelResolver withType(LatexType type) {
            return (modelLocation, modelState) -> resolve(type, modelLocation, modelState);
        }

        static ModelResolver forUnbakedMapAndAtlas(Map<ResourceLocation, BlockModel> models, Function<ResourceLocation, TextureAtlasSprite> getSprite, Executor executor) {
            return (latexType, modelLocation, modelState) -> {
                return bakeModelAsync(models.get(modelLocation), IClientLatexTypeExtensions.of(latexType), getSprite, modelState, modelLocation, executor);
            };
        }
    }

    public static class MultiVariantFaces {
        private final Map<Direction, Variant> faces;
        private final Variant extra;

        public MultiVariantFaces(Map<Direction, Variant> faces, Variant extra) {
            this.faces = faces;
            this.extra = extra;
        }

        @OnlyIn(Dist.CLIENT)
        public static class Deserializer implements JsonDeserializer<MultiVariantFaces> {
            private Variant deserializeOrNull(JsonElement element, JsonDeserializationContext context) {
                if (element.isJsonNull())
                    return null;
                return context.deserialize(element, Variant.class);
            }

            public MultiVariantFaces deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
                Map<Direction, Variant> map = Maps.newHashMap();
                Variant extraModel;
                if (!element.isJsonObject())
                    throw new JsonParseException("Element is an object");

                var object = element.getAsJsonObject();
                if (object.has("top"))
                    map.put(Direction.UP, deserializeOrNull(object.get("top"), context));
                else
                    map.put(Direction.UP, new Variant(DEFAULT_TOP, Transformation.identity(), false, 1));

                if (object.has("bottom"))
                    map.put(Direction.DOWN, deserializeOrNull(object.get("bottom"), context));
                else
                    map.put(Direction.DOWN, new Variant(DEFAULT_BOTTOM, Transformation.identity(), false, 1));

                if (object.has("north"))
                    map.put(Direction.NORTH, deserializeOrNull(object.get("north"), context));
                else
                    map.put(Direction.NORTH, new Variant(DEFAULT_NORTH, Transformation.identity(), false, 1));

                if (object.has("south"))
                    map.put(Direction.SOUTH, deserializeOrNull(object.get("south"), context));
                else
                    map.put(Direction.SOUTH, new Variant(DEFAULT_SOUTH, Transformation.identity(), false, 1));

                if (object.has("east"))
                    map.put(Direction.EAST, deserializeOrNull(object.get("east"), context));
                else
                    map.put(Direction.EAST, new Variant(DEFAULT_EAST, Transformation.identity(), false, 1));

                if (object.has("west"))
                    map.put(Direction.WEST, deserializeOrNull(object.get("west"), context));
                else
                    map.put(Direction.WEST, new Variant(DEFAULT_WEST, Transformation.identity(), false, 1));

                if (object.has("extra"))
                    extraModel = deserializeOrNull(object.get("extra"), context);
                else
                    extraModel = new Variant(DEFAULT_EXTRA, Transformation.identity(), false, 1);

                return new MultiVariantFaces(map, extraModel);
            }
        }
    }

    public static class LatexModelDefinition {
        private final Map<String, MultiVariantFaces> variants = Maps.newLinkedHashMap();

        public static LatexModelDefinition fromStream(LatexModelDefinition.Context context, Reader p_111542_) {
            return GsonHelper.fromJson(context.gson, p_111542_, LatexModelDefinition.class);
        }

        public static LatexModelDefinition fromJsonElement(LatexModelDefinition.Context context, JsonElement p_250730_) {
            return context.gson.fromJson(p_250730_, LatexModelDefinition.class);
        }

        public LatexModelDefinition(Map<String, MultiVariantFaces> variants) {
            this.variants.putAll(variants);
        }

        public LatexModelDefinition(List<LatexModelDefinition> definitions) {
            for (LatexModelDefinition definition : definitions) {
                this.variants.putAll(definition.variants);
            }
        }

        @VisibleForTesting
        public boolean hasVariant(String variantName) {
            return this.variants.get(variantName) != null;
        }

        @VisibleForTesting
        public MultiVariantFaces getVariant(String variantName) {
            MultiVariantFaces multivariant = this.variants.get(variantName);
            if (multivariant == null) {
                throw new LatexModelDefinition.MissingVariantException();
            } else {
                return multivariant;
            }
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            } else {
                if (object instanceof LatexModelDefinition latexModelDefinition) {
                    return this.variants.equals(latexModelDefinition.variants);
                }

                return false;
            }
        }

        public int hashCode() {
            return 31 * this.variants.hashCode();
        }

        public Map<String, MultiVariantFaces> getVariants() {
            return this.variants;
        }

        @VisibleForTesting
        public Set<MultiVariantFaces> getMultiVariants() {
            return Sets.newHashSet(this.variants.values());
        }

        @OnlyIn(Dist.CLIENT)
        public static final class Context {
            protected final Gson gson = (new GsonBuilder())
                    .registerTypeAdapter(LatexModelDefinition.class, new LatexModelDefinition.Deserializer())
                    .registerTypeAdapter(Variant.class, new Variant.Deserializer())
                    .registerTypeAdapter(MultiVariant.class, new MultiVariant.Deserializer())
                    .registerTypeAdapter(MultiVariantFaces.class, new MultiVariantFaces.Deserializer()).create();
            private StateDefinition<Block, BlockState> definition;

            public StateDefinition<Block, BlockState> getDefinition() {
                return this.definition;
            }

            public void setDefinition(StateDefinition<Block, BlockState> p_111553_) {
                this.definition = p_111553_;
            }
        }

        @OnlyIn(Dist.CLIENT)
        public static class Deserializer implements JsonDeserializer<LatexModelDefinition> {
            public LatexModelDefinition deserialize(JsonElement p_111559_, Type p_111560_, JsonDeserializationContext p_111561_) throws JsonParseException {
                JsonObject jsonobject = p_111559_.getAsJsonObject();
                Map<String, MultiVariantFaces> map = this.getVariants(p_111561_, jsonobject);
                if (!map.isEmpty()) {
                    return new LatexModelDefinition(map);
                } else {
                    throw new JsonParseException("'variants' not found");
                }
            }

            protected Map<String, MultiVariantFaces> getVariants(JsonDeserializationContext p_111556_, JsonObject p_111557_) {
                Map<String, MultiVariantFaces> map = Maps.newHashMap();
                if (p_111557_.has("variants")) {
                    JsonObject jsonobject = GsonHelper.getAsJsonObject(p_111557_, "variants");

                    for (Map.Entry<String, JsonElement> entry : jsonobject.entrySet()) {
                        map.put(entry.getKey(), p_111556_.deserialize(entry.getValue(), MultiVariantFaces.class));
                    }
                }

                return map;
            }
        }

        @OnlyIn(Dist.CLIENT)
        protected class MissingVariantException extends RuntimeException {
        }
    }

    public static class ModelSet {
        @Nullable
        private final BakedModel surfaceTop;
        @Nullable
        private final BakedModel surfaceBottom;
        @Nullable
        private final BakedModel surfaceNorth;
        @Nullable
        private final BakedModel surfaceSouth;
        @Nullable
        private final BakedModel surfaceEast;
        @Nullable
        private final BakedModel surfaceWest;
        @Nullable
        private final BakedModel extra;

        public ModelSet(@Nullable BakedModel surfaceTop,
                        @Nullable BakedModel surfaceBottom,
                        @Nullable BakedModel surfaceNorth,
                        @Nullable BakedModel surfaceSouth,
                        @Nullable BakedModel surfaceEast,
                        @Nullable BakedModel surfaceWest,
                        @Nullable BakedModel extra) {
            this.surfaceTop = surfaceTop;
            this.surfaceBottom = surfaceBottom;
            this.surfaceNorth = surfaceNorth;
            this.surfaceSouth = surfaceSouth;
            this.surfaceEast = surfaceEast;
            this.surfaceWest = surfaceWest;
            this.extra = extra;
        }

        private static CompletableFuture<BakedModel> getOrNull(@Nullable Variant variant, TypedModelResolver resolver) {
            if (variant == null)
                return CompletableFuture.completedFuture(null);
            return resolver.resolve(variant.getModelLocation(), variant).exceptionally(e -> {
                var modelLocation = variant.getModelLocation();
                if (modelLocation.equals(DEFAULT_BOTTOM) ||
                        modelLocation.equals(DEFAULT_TOP) ||
                        modelLocation.equals(DEFAULT_NORTH) ||
                        modelLocation.equals(DEFAULT_SOUTH) ||
                        modelLocation.equals(DEFAULT_EAST) ||
                        modelLocation.equals(DEFAULT_WEST) ||
                        modelLocation.equals(DEFAULT_EXTRA))
                    return null;

                LOGGER.error("Failed to resolve model {} : {}", variant.getModelLocation(), e);
                return null;
            });
        }

        public static CompletableFuture<Map<LatexType, ModelSet>> resolveModels(MultiVariantFaces multiVariantFaces, ModelResolver resolver) {
            final var modelSetByType = getCoverTypes().map(latexType -> {
                var typedResolver = resolver.withType(latexType);
                return Util.sequence(List.of(getOrNull(multiVariantFaces.faces.get(Direction.UP), typedResolver),
                        getOrNull(multiVariantFaces.faces.get(Direction.DOWN), typedResolver),
                        getOrNull(multiVariantFaces.faces.get(Direction.NORTH), typedResolver),
                        getOrNull(multiVariantFaces.faces.get(Direction.SOUTH), typedResolver),
                        getOrNull(multiVariantFaces.faces.get(Direction.EAST), typedResolver),
                        getOrNull(multiVariantFaces.faces.get(Direction.WEST), typedResolver),
                        getOrNull(multiVariantFaces.extra, typedResolver))).thenApply(bakedModels -> {
                            return Pair.of(latexType, new ModelSet(
                                    bakedModels.get(0),
                                    bakedModels.get(1),
                                    bakedModels.get(2),
                                    bakedModels.get(3),
                                    bakedModels.get(4),
                                    bakedModels.get(5),
                                    bakedModels.get(6)
                            ));
                });
            }).toList();

            return Util.sequence(modelSetByType).thenApply((result) -> {
                return result.stream().filter(Objects::nonNull).collect(Collectors.toUnmodifiableMap(Pair::getFirst, Pair::getSecond));
            });
        }

        @Nullable
        public BakedModel getModel(Direction surface) {
            return switch (surface) {
                case UP -> surfaceTop;
                case DOWN -> surfaceBottom;
                case NORTH -> surfaceNorth;
                case SOUTH -> surfaceSouth;
                case EAST -> surfaceEast;
                case WEST -> surfaceWest;
            };
        }

        @Nullable
        public TextureAtlasSprite getParticleIcon() {
            return surfaceTop == null ? null : surfaceTop.getParticleIcon(ModelData.EMPTY);
        }

        @Nullable
        public BakedModel getExtraModel() {
            return extra;
        }
    }

    private final Minecraft minecraft;
    private final BlockRenderDispatcher dispatcher;
    private final ModelBlockRenderer modelRenderer;
    private Map<LatexType, ModelSet> defaultModelSets;
    private Map<BlockState, Map<LatexType, ModelSet>> specialModelSets = new HashMap<>();

    public LatexCoveredBlocksRenderer(Minecraft minecraft) {
        this.minecraft = minecraft;
        this.dispatcher = minecraft.getBlockRenderer();
        this.modelRenderer = dispatcher.getModelRenderer();
    }

    private ModelSet getModelSet(BlockState blockState, LatexCoverState coverState) {
        return specialModelSets.getOrDefault(blockState, defaultModelSets).get(coverState.getType());
    }

    public RenderType getRenderType(LatexCoverState coverState) {
        // Maybe use a tag
        if (ChangedClient.shouldBeRenderingWaveVision() && ChangedLatexTypes.DARK_LATEX.get().isFriendlyTo(coverState.getType()))
            return ChangedShaders.waveVisionResonantSolid(WaveVisionRenderer.LATEX_RESONANCE_NEUTRAL);
        return RenderType.solid();
    }

    private static final ThreadLocal<LatexCoverGetter> threadLocal = ThreadLocal.withInitial(() -> null);

    public static Optional<LatexCoverGetter> getLatexCoverStateGetter() {
        return Optional.ofNullable(threadLocal.get());
    }

    private boolean wrappedTesselate(
            BlockAndTintGetter level, LatexCoverGetter latexCoverGetter,
            BlockPos blockPos, PoseStack poseStack, VertexConsumer bufferBuilder,
            BlockState blockState, LatexCoverState coverState,
            RandomSource random) {
        final ModelSet modelSet = getModelSet(blockState, coverState);

        if (SpreadingLatexType.isBlockFull(level, blockPos, blockState))
            return false;

        int lightColor = this.getLightColor(level, blockPos);

        boolean surfaceTop = coverState.getProperties().contains(SpreadingLatexType.UP) && coverState.getValue(SpreadingLatexType.UP);
        boolean surfaceBottom = coverState.getProperties().contains(SpreadingLatexType.DOWN) && coverState.getValue(SpreadingLatexType.DOWN);
        boolean surfaceNorth = coverState.getProperties().contains(SpreadingLatexType.NORTH) && coverState.getValue(SpreadingLatexType.NORTH);
        boolean surfaceSouth = coverState.getProperties().contains(SpreadingLatexType.SOUTH) && coverState.getValue(SpreadingLatexType.SOUTH);
        boolean surfaceEast = coverState.getProperties().contains(SpreadingLatexType.EAST) && coverState.getValue(SpreadingLatexType.EAST);
        boolean surfaceWest = coverState.getProperties().contains(SpreadingLatexType.WEST) && coverState.getValue(SpreadingLatexType.WEST);

        long seed = coverState.getSeed(blockPos);

        final RenderType renderType = this.getRenderType(coverState);

        threadLocal.set(latexCoverGetter);

        if (surfaceTop && modelSet.surfaceTop != null) {
            modelRenderer.tesselateWithAO(level, modelSet.surfaceTop, blockState, blockPos, poseStack, bufferBuilder, true, random, seed, lightColor,
                    ModelData.EMPTY, renderType);
        }

        if (surfaceBottom && modelSet.surfaceBottom != null) {
            modelRenderer.tesselateWithAO(level, modelSet.surfaceBottom, blockState, blockPos, poseStack, bufferBuilder, true, random, seed, lightColor,
                    ModelData.EMPTY, renderType);
        }

        if (surfaceNorth && modelSet.surfaceNorth != null) {
            modelRenderer.tesselateWithAO(level, modelSet.surfaceNorth, blockState, blockPos, poseStack, bufferBuilder, true, random, seed, lightColor,
                    ModelData.EMPTY, renderType);
        }

        if (surfaceSouth && modelSet.surfaceSouth != null) {
            modelRenderer.tesselateWithAO(level, modelSet.surfaceSouth, blockState, blockPos, poseStack, bufferBuilder, true, random, seed, lightColor,
                    ModelData.EMPTY, renderType);
        }

        if (surfaceEast && modelSet.surfaceEast != null) {
            modelRenderer.tesselateWithAO(level, modelSet.surfaceEast, blockState, blockPos, poseStack, bufferBuilder, true, random, seed, lightColor,
                    ModelData.EMPTY, renderType);
        }

        if (surfaceWest && modelSet.surfaceWest != null) {
            modelRenderer.tesselateWithAO(level, modelSet.surfaceWest, blockState, blockPos, poseStack, bufferBuilder, true, random, seed, lightColor,
                    ModelData.EMPTY, renderType);
        }

        if (modelSet.extra != null) {
            modelRenderer.tesselateWithAO(level, modelSet.extra, blockState, blockPos, poseStack, bufferBuilder, true, random, seed, lightColor,
                    ModelData.EMPTY, renderType);
        }

        threadLocal.remove();

        return true;
    }

    private int getLightColor(BlockAndTintGetter level, BlockPos blockPos) {
        return LevelRenderer.getLightColor(level, blockPos);
        /*int lightColor = LevelRenderer.getLightColor(level, blockPos);
        int lightColorAbove = LevelRenderer.getLightColor(level, blockPos.above());
        int k = lightColor & 255;
        int l = lightColorAbove & 255;
        int i1 = lightColor >> 16 & 255;
        int j1 = lightColorAbove >> 16 & 255;
        return (Math.max(k, l)) | (Math.max(i1, j1)) << 16;*/
    }

    private static void vertex(VertexConsumer consumer, double x, double y, double z, float red, float green, float blue, float alpha, float texCoordU, float texCoordV, int packedLight) {
        consumer.vertex(x, y, z).color(red, green, blue, alpha).uv(texCoordU, texCoordV).uv2(packedLight).normal(0.0F, 1.0F, 0.0F).endVertex();
    }

    public boolean tesselate(
            BlockAndTintGetter level, LatexCoverGetter latexCoverGetter,
            BlockPos blockPos, PoseStack poseStack, VertexConsumer bufferBuilder,
            BlockState blockState, LatexCoverState coverState,
            RandomSource random) {
        try {
            return this.wrappedTesselate(level, latexCoverGetter, blockPos, poseStack, bufferBuilder, blockState, coverState, random);
        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.forThrowable(throwable, "Tesselating latex cover in world");
            CrashReportCategory crashreportcategory = crashreport.addCategory("Block being tesselated");
            CrashReportCategory.populateBlockDetails(crashreportcategory, level, blockPos, (BlockState)null);
            throw new ReportedException(crashreport);
        }
    }

    public void renderBreakingTexture(BlockState blockState, LatexCoverState coverState, BlockPos blockPos, ClientLevel level, PoseStack poseStack, VertexConsumer bufferBuilder, RandomSource random) {
        if (!coverState.isPresent())
            return;

        LatexCoverGetter coverGetter = LatexCoverGetter.wrap(level);
        this.tesselate(level, coverGetter, blockPos, poseStack, bufferBuilder, blockState, coverState, random);
    }

    private static IModelBuilder<?> modelBuilderFor(TextureAtlasSprite particle, ResourceLocation namedRenderType) {
        return IModelBuilder.of(true, true, true,
                ItemTransforms.NO_TRANSFORMS, ItemOverrides.EMPTY,
                particle,
                NamedRenderTypeManager.get(namedRenderType));
    }

    private static CompletableFuture<Map<Block, LatexModelDefinition>> loadBlockStates(ResourceManager resources, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            return STATE_LISTER.listMatchingResources(resources);
        }, executor).thenCompose((namedResources) -> {
            List<CompletableFuture<Pair<Block, LatexModelDefinition>>> list = new ArrayList<>(namedResources.size());

            for (Map.Entry<ResourceLocation, Resource> entry : namedResources.entrySet()) {
                list.add(CompletableFuture.supplyAsync(() -> {
                    ResourceLocation blockId = STATE_LISTER.fileToId(entry.getKey());
                    if (!ForgeRegistries.BLOCKS.containsKey(blockId)) {
                        LOGGER.error("Skipping {} as it does not map to a block", blockId);
                        return null;
                    }

                    Block block = ForgeRegistries.BLOCKS.getValue(blockId);
                    try (Reader reader = entry.getValue().openAsReader()) {
                        return Pair.of(block, LatexModelDefinition.fromStream(LATEX_MODEL_DEFINITION_CONTEXT, reader));
                    } catch (Exception exception) {
                        LOGGER.error("Failed to load model definition {}", entry.getKey(), exception);
                        return null;
                    }
                }, executor));
            }

            return Util.sequence(list).thenApply((result) -> {
                return result.stream().filter(Objects::nonNull).collect(Collectors.toUnmodifiableMap(Pair::getFirst, Pair::getSecond));
            });
        });
    }

    private static final Splitter COMMA_SPLITTER = Splitter.on(',');
    private static final Splitter EQUAL_SPLITTER = Splitter.on('=').limit(2);

    @Nullable
    static <T extends Comparable<T>> T getValueHelper(Property<T> property, String p_119278_) {
        return property.getValue(p_119278_).orElse((T) null);
    }

    private static Predicate<BlockState> predicate(StateDefinition<Block, BlockState> stateDefinition, String text) {
        Map<Property<?>, Comparable<?>> map = Maps.newHashMap();

        for (String s : COMMA_SPLITTER.split(text)) {
            Iterator<String> iterator = EQUAL_SPLITTER.split(s).iterator();
            if (iterator.hasNext()) {
                String s1 = iterator.next();
                Property<?> property = stateDefinition.getProperty(s1);
                if (property != null && iterator.hasNext()) {
                    String s2 = iterator.next();
                    Comparable<?> comparable = getValueHelper(property, s2);
                    if (comparable == null) {
                        throw new RuntimeException("Unknown value: '" + s2 + "' for blockstate property: '" + s1 + "' " + property.getPossibleValues());
                    }

                    map.put(property, comparable);
                } else if (!s1.isEmpty()) {
                    throw new RuntimeException("Unknown blockstate property: '" + s1 + "'");
                }
            }
        }

        Block block = stateDefinition.getOwner();
        return (p_119262_) -> {
            if (p_119262_ != null && p_119262_.is(block)) {
                for (Map.Entry<Property<?>, Comparable<?>> entry : map.entrySet()) {
                    if (!Objects.equals(p_119262_.getValue(entry.getKey()), entry.getValue())) {
                        return false;
                    }
                }

                return true;
            } else {
                return false;
            }
        };
    }

    private static CompletableFuture<Map<BlockState, Map<LatexType, ModelSet>>> bakeBlockStateModels(ModelResolver modelResolver,
                                                                                                     Map<Block, LatexModelDefinition> definitions) {
        var futures = definitions.entrySet().stream().map(definitionEntry -> {
            List<CompletableFuture<Pair<String, Map<LatexType, ModelSet>>>> list = new ArrayList<>(definitionEntry.getValue().getVariants().size());

            definitionEntry.getValue().getVariants().forEach((text, faces) -> {
                list.add(ModelSet.resolveModels(faces, modelResolver).thenApply(modelSets -> {
                    return Pair.of(text, modelSets);
                }));
            });

            return Util.sequence(list).thenApply((result) -> {
                return result.stream().filter(Objects::nonNull).collect(Collectors.toUnmodifiableMap(Pair::getFirst, Pair::getSecond));
            }).thenApply(blockStateToModelSet -> {
                Map<BlockState, Map<LatexType, ModelSet>> blockStateMap = Maps.newHashMap();
                StateDefinition<Block, BlockState> stateDefinition = definitionEntry.getKey().getStateDefinition();
                blockStateToModelSet.forEach((text, modelSet) -> {
                    stateDefinition.getPossibleStates().stream().filter(predicate(stateDefinition, text)).forEach((blockState -> {
                        if (blockStateMap.containsKey(blockState))
                            throw new RuntimeException("Overlapping definition for " + blockState);
                        blockStateMap.put(blockState, modelSet);
                    }));
                });
                return blockStateMap;
            });
        }).toList();

        return Util.sequence(futures).thenApply(list -> {
            return list.stream().flatMap(map -> map.entrySet().stream())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        });
    }

    private static CompletableFuture<Map<ResourceLocation, BlockModel>> loadBlockModels(ResourceManager resources, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            return MODEL_LISTER.listMatchingResources(resources);
        }, executor).thenCompose((namedResources) -> {
            List<CompletableFuture<Pair<ResourceLocation, BlockModel>>> list = new ArrayList<>(namedResources.size());

            for (Map.Entry<ResourceLocation, Resource> entry : namedResources.entrySet()) {
                list.add(CompletableFuture.supplyAsync(() -> {
                    try (Reader reader = entry.getValue().openAsReader()) {
                        return Pair.of(MODEL_LISTER.fileToId(entry.getKey()), BlockModel.fromStream(reader));
                    } catch (Exception exception) {
                        LOGGER.error("Failed to load model {}", entry.getKey(), exception);
                        return null;
                    }
                }, executor));
            }

            return Util.sequence(list).thenApply((result) -> {
                return result.stream().filter(Objects::nonNull).collect(Collectors.toUnmodifiableMap(Pair::getFirst, Pair::getSecond));
            });
        });
    }

    private static Stream<LatexType> getCoverTypes() {
        return ChangedRegistry.LATEX_TYPE.get().getValues().stream()
                .filter(type -> type != ChangedLatexTypes.NONE.get());
    }

    private static void bakeElements(IModelBuilder<?> builder,
                                     List<BlockElement> elements,
                                     IClientLatexTypeExtensions properties,
                                     Function<ResourceLocation, TextureAtlasSprite> getSprite,
                                     ModelState modelState,
                                     ResourceLocation modelLocation) {
        final var normalMatrix = modelState.getRotation().getNormalMatrix();

        elements.forEach(blockElement -> {
            blockElement.faces.forEach((side, face) -> {
                final var normal = new Vector3f(side.getStepX(), side.getStepY(), side.getStepZ());
                normal.mul(normalMatrix);

                final var apparentDirection = Direction.getNearest(normal.x, normal.y, normal.z);

                var sprite = getSprite.apply(properties.getTextureForFace(apparentDirection));
                var quad = UnbakedGeometryHelper.bakeElementFace(blockElement, face, sprite, side, modelState, modelLocation);
                if (face.cullForDirection == null)
                    builder.addUnculledFace(quad);
                else
                    builder.addCulledFace(face.cullForDirection, quad);
            });
        });
    }

    private static CompletableFuture<BakedModel> bakeModelAsync(BlockModel unbakedModel,
                                                                IClientLatexTypeExtensions properties,
                                                                Function<ResourceLocation, TextureAtlasSprite> getSprite,
                                                                ModelState modelState,
                                                                ResourceLocation modelLocation,
                                                                Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            final var builder = modelBuilderFor(getSprite.apply(properties.getTextureForParticle()), properties.getNamedRenderType());
            bakeElements(builder, unbakedModel.getElements(), properties, getSprite, modelState, modelLocation);
            return builder.build();
        }, executor);
    }

    @Override
    public CompletableFuture<Void> reload(PreparationBarrier barrier, ResourceManager resources, ProfilerFiller profilerA, ProfilerFiller profilerB, Executor backgroundExecutor, Executor minecraftExecutor) {
        return loadBlockModels(resources, backgroundExecutor)
                .thenApply(unbakedModels -> ModelResolver.forUnbakedMapAndAtlas(unbakedModels, minecraft.getTextureAtlas(BLOCK_ATLAS), backgroundExecutor))
                .thenCompose(barrier::wait)
                .thenCompose(resolver -> {
                    return ModelSet.resolveModels(DEFAULT_MODEL, resolver)
                            .thenApplyAsync(defaultModelSets -> {
                                this.defaultModelSets = defaultModelSets;
                                return resolver;
                            }, minecraftExecutor);
                })
                .thenCombine(loadBlockStates(resources, minecraftExecutor), LatexCoveredBlocksRenderer::bakeBlockStateModels)
                .thenCompose(Function.identity())
                .thenAcceptAsync(bakedModels -> {
                    this.specialModelSets = bakedModels;
                }, minecraftExecutor);
    }
}
