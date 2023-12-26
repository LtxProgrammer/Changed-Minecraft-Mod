package net.ltxprogrammer.changed.client;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.PngInfo;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.data.MixedTexture;
import net.ltxprogrammer.changed.entity.LatexType;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.block.model.MultiVariant;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.client.renderer.block.model.multipart.MultiPart;
import net.minecraft.client.renderer.block.model.multipart.Selector;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.InactiveProfiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import static net.ltxprogrammer.changed.block.AbstractLatexBlock.COVERED;

public abstract class LatexCoveredBlocks {
    public static final Logger LOGGER = LogUtils.getLogger();
    private static final ImmutableMap<LatexType, MixedTexture.OverlayBlock> TYPE_OVERLAY = ImmutableMap.of(
            LatexType.DARK_LATEX, new MixedTexture.OverlayBlock(
                    Changed.modResource("blocks/dark_latex_block_top"),
                    Changed.modResource("blocks/dark_latex_block_side"),
                    Changed.modResource("blocks/dark_latex_block_bottom")),
            LatexType.WHITE_LATEX, new MixedTexture.OverlayBlock(
                    Changed.modResource("blocks/white_latex_block"),
                    Changed.modResource("blocks/white_latex_block"),
                    Changed.modResource("blocks/white_latex_block")));
    public static final ResourceLocation LATEX_COVER_ATLAS = Changed.modResource("textures/atlas/latex_blocks.png");

    protected static final RenderStateShard.TextureStateShard LATEX_SHEET_MIPPED = new RenderStateShard.TextureStateShard(LATEX_COVER_ATLAS, false, true);
    protected static final RenderStateShard.TextureStateShard LATEX_SHEET = new RenderStateShard.TextureStateShard(LATEX_COVER_ATLAS, false, false);
    protected static final RenderStateShard.LightmapStateShard LIGHTMAP = new RenderStateShard.LightmapStateShard(true);
    protected static final RenderStateShard.ShaderStateShard RENDERTYPE_SOLID_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeSolidShader);
    protected static final RenderStateShard.ShaderStateShard RENDERTYPE_CUTOUT_MIPPED_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeCutoutMippedShader);
    protected static final RenderStateShard.ShaderStateShard RENDERTYPE_CUTOUT_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeCutoutShader);
    private static final RenderType LATEX_SOLID = RenderType.create("changed:latex_solid", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 2097152, true, false, RenderType.CompositeState.builder().setLightmapState(LIGHTMAP).setShaderState(RENDERTYPE_SOLID_SHADER).setTextureState(LATEX_SHEET_MIPPED).createCompositeState(true));
    private static final RenderType LATEX_CUTOUT_MIPPED = RenderType.create("changed:latex_cutout_mipped", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 131072, true, false, RenderType.CompositeState.builder().setLightmapState(LIGHTMAP).setShaderState(RENDERTYPE_CUTOUT_MIPPED_SHADER).setTextureState(LATEX_SHEET_MIPPED).createCompositeState(true));
    private static final RenderType LATEX_CUTOUT = RenderType.create("changed:latex_cutout", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 131072, true, false, RenderType.CompositeState.builder().setLightmapState(LIGHTMAP).setShaderState(RENDERTYPE_CUTOUT_SHADER).setTextureState(LATEX_SHEET).createCompositeState(true));

    public static boolean isRenderingChangedBlockLayer = false;

    public static RenderType latexSolid() { return LATEX_SOLID; }
    public static RenderType latexCutoutMipped() { return LATEX_CUTOUT_MIPPED; }
    public static RenderType latexCutout() { return LATEX_CUTOUT; }

    public static class LatexAtlas extends TextureAtlas {
        private final Function<ResourceLocation, MixedTexture> textureFunction;

        public LatexAtlas(ResourceLocation name, Function<ResourceLocation, MixedTexture> textureFunction) {
            super(name);
            this.textureFunction = textureFunction;
        }

        protected void getBasicSpriteInfo(ResourceLocation name, ResourceManager manager, Queue<TextureAtlasSprite.Info> queue) {
            ResourceLocation pathName = MixedTexture.getResourceLocation(textureFunction.apply(name).getBaseLocation());

            TextureAtlasSprite.Info info;
            try {
                Resource resource = manager.getResource(pathName);

                try {
                    PngInfo pnginfo = new PngInfo(resource.toString(), resource.getInputStream());
                    AnimationMetadataSection animationmetadatasection = resource.getMetadata(AnimationMetadataSection.SERIALIZER);
                    if (animationmetadatasection == null)
                        animationmetadatasection = AnimationMetadataSection.EMPTY;

                    Pair<Integer, Integer> pair = animationmetadatasection.getFrameSize(pnginfo.width, pnginfo.height);
                    info = new TextureAtlasSprite.Info(name, pair.getFirst(), pair.getSecond(), animationmetadatasection);
                } catch (Throwable throwable1) {
                    if (resource != null) {
                        try {
                            resource.close();
                        } catch (Throwable throwable) {
                            throwable1.addSuppressed(throwable);
                        }
                    }

                    throw throwable1;
                }

                if (resource != null)
                    resource.close();
            } catch (RuntimeException runtimeexception) {
                LOGGER.error("Unable to parse metadata from {} : {}", pathName, runtimeexception);
                return;
            } catch (IOException ioexception) {
                LOGGER.error("Using missing texture, unable to load {} : {}", pathName, ioexception);
                return;
            }

            queue.add(info);
        }

        // This method is called by an @inject mixin in TextureAtlasMixin.java, effectively override the private function
        public Collection<TextureAtlasSprite.Info> getBasicSpriteInfos(ResourceManager manager, Set<ResourceLocation> textures) {
            List<CompletableFuture<?>> list = Lists.newArrayList();
            Queue<TextureAtlasSprite.Info> queue = new ConcurrentLinkedQueue<>();

            for(ResourceLocation resourcelocation : textures)
                if (!MissingTextureAtlasSprite.getLocation().equals(resourcelocation))
                    list.add(CompletableFuture.runAsync(() -> this.getBasicSpriteInfo(resourcelocation, manager, queue), Util.backgroundExecutor()));

            CompletableFuture.allOf(list.toArray(new CompletableFuture[0])).join();
            return queue;
        }

        @Nullable
        public TextureAtlasSprite load(ResourceManager resources, TextureAtlasSprite.Info info, int atlasWidth, int atlasHeight, int mipLevels, int x, int y) {
            ResourceLocation fullPath = MixedTexture.getResourceLocation(info.name());
            NativeImage image = MixedTexture.findCachedTexture(fullPath).orElse(null);
            if (image == null)
                image = textureFunction.apply(info.name()).load(resources);
            if (image != null)
                return new TextureAtlasSprite(this, info, mipLevels, atlasWidth, atlasHeight, x, y, image);
            LOGGER.error("Using missing texture, unable to load {}", fullPath);
            return null;
        }

        @Override
        public @NotNull TextureAtlasSprite getSprite(@NotNull ResourceLocation name) {
            LOGGER.trace("Accessing latex sprite " + name);
            return super.getSprite(name);
        }
    }

    // Not really much of a preparable reload listener
    public static class LatexBlockUploader implements AutoCloseable {
        private final Map<ResourceLocation, MixedTexture> registeredSprites = new HashMap<>();
        private @Nullable TextureAtlas.Preparations preparations = null;
        private final LatexAtlas textureAtlas = new LatexAtlas(LATEX_COVER_ATLAS, this::getUnderlyingTexture);

        public LatexAtlas getTextureAtlas() {
            return textureAtlas;
        }

        private MixedTexture getUnderlyingTexture(ResourceLocation name) {
            return registeredSprites.getOrDefault(name, MixedTexture.MISSING);
        }

        public void attachToManager(TextureManager manager) {
            manager.register(this.textureAtlas.location(), this.textureAtlas);
        }

        public void registerSprite(ResourceLocation name, MixedTexture texture) {
            registeredSprites.put(name, texture);
        }

        public void stitchIfPossible(ResourceManager resources, ProfilerFiller profiler) {
            if (preparations == null)
                preparations = this.textureAtlas.prepareToStitch(resources, registeredSprites.keySet().stream(), profiler, 4);
        }

        public void upload(ResourceManager resources, ProfilerFiller profiler) {
            profiler.startTick();
            profiler.push("upload");
            this.stitchIfPossible(resources, profiler);
            if (preparations == null)
                throw new IllegalStateException("Expected preparations");
            this.textureAtlas.reload(preparations);
            preparations = null;
            MixedTexture.clearMemoryCache();
            profiler.pop();
            profiler.endTick();
        }

        @Override
        public void close() {
            this.textureAtlas.clearTextureData();
        }
    }

    protected static class Registrar {
        private final ModelBakeEvent event;
        private final Map<ResourceLocation, UnbakedModel> models;
        private final Map<ResourceLocation, UnbakedModel> earlyBakeModels;
        private final Map<ResourceLocation, ModelResourceLocation> referencePreBaked;
        private final LatexBlockUploader uploader;
        public final boolean mixTextures;

        public Registrar(ModelBakeEvent event,
                         Map<ResourceLocation, UnbakedModel> models,
                         Map<ResourceLocation, UnbakedModel> earlyBakeModels,
                         Map<ResourceLocation, ModelResourceLocation> referencePreBaked,
                         LatexBlockUploader uploader, boolean mixTextures) {
            this.event = event;
            this.models = models;
            if (!mixTextures) {
                this.earlyBakeModels = earlyBakeModels;
                this.referencePreBaked = referencePreBaked;
            } else {
                this.earlyBakeModels = Map.of();
                this.referencePreBaked = Map.of();
            }
            this.uploader = uploader;
            this.mixTextures = mixTextures;
        }

        public void register(ModelResourceLocation name, UnbakedModel model) {
            models.put(name, model);
        }

        public void registerEarlyBake(ModelResourceLocation name, UnbakedModel model) {
            try {
                earlyBakeModels.put(name, model);
            } catch (UnsupportedOperationException ex) {
                LOGGER.warn("Attempted to register early bake model when it should not of ({})", name);
            }
        }

        public void registerPreBaked(ResourceLocation name, ModelResourceLocation prebaked) {
            try {
                referencePreBaked.put(name, prebaked);
            } catch (UnsupportedOperationException ex) {
                LOGGER.warn("Attempted to register reference bake model when it should not of ({})", name);
            }
        }

        public void registerIfAbsent(ResourceLocation name, Function<ResourceLocation, UnbakedModel> fn) {
            models.computeIfAbsent(name, fn);
        }

        public void registerTexture(ResourceLocation saveLocation, MixedTexture mixedTexture) {
            uploader.registerSprite(saveLocation, mixedTexture);
        }

        public UnbakedModel getModel(ResourceLocation name) {
            if (models.containsKey(name))
                return models.get(name);
            return event.getModelLoader().getModelOrMissing(name);
        }

        private TextureAtlasSprite getSprite(Material material) {
            return event.getModelManager().getAtlas(material.atlasLocation()).getSprite(material.texture());
        }

        public void bakeAll() {
            LOGGER.info("The chef has started baking the models, with {} early bake, {} reference bake, {} normal bake",
                    earlyBakeModels.size(), referencePreBaked.size(), models.size());
            ((BakeryExtender)(Object)event.getModelLoader()).removeFromCacheIf(
                    triple -> models.containsKey(triple.getLeft())
            );

            LOGGER.info("Removed already baked models from ModelBakery");
            AtomicInteger index = new AtomicInteger(0);
            final int modelCount = earlyBakeModels.size() + models.size();
            for (var entry : earlyBakeModels.entrySet()) {
                var name = entry.getKey();
                var model = entry.getValue();

                try {
                    // Force model parent chain to generate, so materials resolve correctly
                    model.getMaterials(this::getModel, new HashSet<>());
                } catch (Exception ignored) {}

                event.getModelRegistry().put(entry.getKey(), model.bake(event.getModelLoader(), this::getSprite, BlockModelRotation.X0_Y0, name));

                int currentIndex = index.incrementAndGet();
                if (currentIndex % 50000 == 0) {
                    LOGGER.info("Hit {}/{} baked models", currentIndex, modelCount);
                }
            }

            for (var entry : referencePreBaked.entrySet()) {
                event.getModelRegistry().put(entry.getKey(), event.getModelRegistry().get(entry.getValue()));
            }

            for (var it = models.entrySet().iterator(); it.hasNext();) {
                var entry = it.next();
                var name = entry.getKey();
                var model = entry.getValue();

                try {
                    // Force model parent chain to generate, so materials resolve correctly
                    model.getMaterials(this::getModel, new HashSet<>());
                } catch (Exception ignored) {}

                event.getModelRegistry().put(name, model.bake(event.getModelLoader(), this::getSprite, BlockModelRotation.X0_Y0, name));

                int currentIndex = index.incrementAndGet();
                if (currentIndex % 50000 == 0) {
                    LOGGER.info("Hit {}/{} baked models", currentIndex, modelCount);
                }

                //it.remove();
            }
        }
    }

    private static Material getLatexedMaterial(ResourceLocation name) {
        return new Material(LATEX_COVER_ATLAS, name);
    }

    private static ResourceLocation getDefaultLatexCover(LatexType type) {
        return Changed.modResource("builtin/" + type.getSerializedName());
    }

    private static final Function<LatexType, ResourceLocation> getDefaultLatexCoverCached = Util.memoize(LatexCoveredBlocks::getDefaultLatexCover);

    private static ModelResourceLocation getDefaultLatexModel(LatexType type) {
        return new ModelResourceLocation(getDefaultLatexCoverCached.apply(type), "block");
    }

    private static final Function<LatexType, ModelResourceLocation> getDefaultLatexModelCached = Util.memoize(LatexCoveredBlocks::getDefaultLatexModel);

    private static boolean namespaceQualifiesForCheap(String sourceNamespace) { // Maybe allow configuring preserved namespaces
        if (sourceNamespace.equals("minecraft"))
            return false;
        if (sourceNamespace.equals("changed"))
            return false;

        return true;
    }

    private static boolean qualifiesForCheap(Registrar registrar, String sourceNamespace, BlockModel blockModel) {
        if (!namespaceQualifiesForCheap(sourceNamespace))
            return false;

        if (Changed.config.client.fastAndCheapLatexBlocks.get())
            return true;

        var parentName = blockModel.getParentLocation();
        var elements = blockModel.getElements();

        if (parentName == null) {
            if (elements.size() == 1) { // Model has one element
                var elem = elements.get(0);
                if (elem.from.x() != 0.0f || elem.from.y() != 0.0f || elem.from.z() != 0.0f)
                    return false;
                if (Math.abs(elem.to.x() - 16.0f) < 0.000001f ||
                        Math.abs(elem.to.y() - 16.0f) < 0.000001f ||
                        Math.abs(elem.to.z() - 16.0f) < 0.000001f)
                    return false;
                return true;
            }

            return false;
        }
        if (parentName.equals(new ResourceLocation("block/cube")))
            return true;
        if (parentName.equals(new ResourceLocation("block/cube_mirrored")))
            return true;
        if (parentName.equals(new ResourceLocation("block/cube_all")))
            return true;
        if (parentName.equals(new ResourceLocation("block/cube_column")))
            return true;
        if (parentName.equals(new ResourceLocation("block/block")))
            return true;
        var parentModel = registrar.getModel(blockModel.getParentLocation());
        if (!(parentModel instanceof BlockModel parentBlockModel))
            return false;
        return qualifiesForCheap(registrar, sourceNamespace, parentBlockModel);
    }

    private static Function<ResourceLocation, UnbakedModel> createLatexModel(Registrar registrar, BlockModel blockModel, MixedTexture.OverlayBlock overlay, LatexType type, String nameAppend) {
        return name -> {
            Map<String, Either<Material, String>> injectedTextures = new HashMap<>();

            blockModel.textureMap.forEach((refName, either) -> {
                if (refName.equals("particle"))
                    return;
                either.ifLeft(material -> {
                    var newMaterial = getLatexedMaterial(new ResourceLocation(material.texture() + nameAppend));
                    injectedTextures.put(refName, Either.left(newMaterial));
                    registrar.registerTexture(newMaterial.texture(), new MixedTexture(
                            material.texture(), overlay.guessSide(refName), newMaterial.texture()
                    ));
                }).ifRight(string -> {
                    var newMaterial = getLatexedMaterial(new ResourceLocation(string + nameAppend));
                    injectedTextures.put(refName, Either.left(newMaterial));
                    registrar.registerTexture(newMaterial.texture(), new MixedTexture(
                            new ResourceLocation(string), overlay.guessSide(refName), newMaterial.texture()
                    ));
                });
            });

            injectedTextures.put("particle", Either.left(overlay.particleMaterial));

            BlockModel injected = new BlockModel(
                    blockModel.getParentLocation(),
                    blockModel.getElements(),
                    injectedTextures,
                    blockModel.hasAmbientOcclusion,
                    blockModel.getGuiLight(),
                    blockModel.getTransforms(),
                    blockModel.getOverrides());
            injected.name = blockModel.name + nameAppend;
            return injected;
        };
    }

    private static Function<Variant, Variant> createLatexVariant(Registrar registrar, LatexType type, AtomicBoolean allGeneric) {
        return variant -> {
            MixedTexture.OverlayBlock overlay = TYPE_OVERLAY.get(type);

            ResourceLocation modelLocation = variant.getModelLocation();
            UnbakedModel unbakedModel = registrar.getModel(modelLocation);
            if (!(unbakedModel instanceof BlockModel blockModel)) {
                throw new IllegalStateException("Expected block model, got " + unbakedModel);
            }

            else if (blockModel == registrar.getModel(ModelBakery.MISSING_MODEL_LOCATION)) {
                LOGGER.warn("Missing block model {}", modelLocation);
            }

            String nameAppend = "/" + type.getSerializedName();
            ResourceLocation newName = new ResourceLocation(modelLocation.getNamespace(), modelLocation.getPath() + nameAppend);

            if (registrar.mixTextures || !qualifiesForCheap(registrar, modelLocation.getNamespace(), blockModel)) {
                allGeneric.set(false);
                registrar.registerIfAbsent(newName, createLatexModel(registrar, blockModel, overlay, type, nameAppend));
            }

            else {
                registrar.registerPreBaked(newName, getDefaultLatexModelCached.apply(type));
            }

            return new Variant(newName, variant.getRotation(), variant.isUvLocked(), variant.getWeight());
        };
    }

    @Nullable
    private static UnbakedModel overWriteMultiPart(Registrar registrar, MultiPart multiPart, ModelResourceLocation model, BlockState state, LatexType type) {
        AtomicBoolean allGeneric = new AtomicBoolean(true);
        var newSelectors = multiPart.getSelectors().stream().map(selector -> {
            var newVariants = selector.getVariant().getVariants().stream().map(createLatexVariant(registrar, type, allGeneric)).collect(Collectors.toList());
            var newMultiVariant = new MultiVariant(newVariants);

            return new Selector(selector::getPredicate, newMultiVariant);
        }).toList();

        return allGeneric.get() ? null : new MultiPart(state.getBlock().getStateDefinition(), newSelectors);
    }

    @Nullable
    private static UnbakedModel overWriteMultiVariant(Registrar registrar, MultiVariant multiVariant, ModelResourceLocation model, BlockState state, LatexType type) {
        AtomicBoolean allGeneric = new AtomicBoolean(true);
        var newVariants = multiVariant.getVariants().stream().map(createLatexVariant(registrar, type, allGeneric)).toList();

        return allGeneric.get() ? null : new MultiVariant(newVariants);
    }

    protected static void coverBlock(Registrar registrar, BlockState state, LatexType type) {
        var coveredModelName = BlockModelShaper.stateToModelLocation(state);

        if (Changed.config.client.fastAndCheapLatexBlocks.get() && namespaceQualifiesForCheap(coveredModelName.getNamespace())) {
            registrar.registerPreBaked(coveredModelName, getDefaultLatexModelCached.apply(type)); // Register a generic 1x1x1 block reference
            return;
        }

        var baseModelName = BlockModelShaper.stateToModelLocation(state.setValue(COVERED, LatexType.NEUTRAL));
        var baseModel = registrar.getModel(baseModelName);
        UnbakedModel newModel = null;

        if (baseModel instanceof MultiPart multiPart)
            newModel = overWriteMultiPart(registrar, multiPart, coveredModelName, state, type);
        else if (baseModel instanceof MultiVariant multiVariant)
            newModel = overWriteMultiVariant(registrar, multiVariant, coveredModelName, state, type);

        if (newModel != null)
            registrar.register(coveredModelName, newModel);
        else if (!registrar.mixTextures)
            registrar.registerPreBaked(coveredModelName, getDefaultLatexModelCached.apply(type)); // Register a generic 1x1x1 block reference
        else {
            LOGGER.error("Could not generate covered variant of {} : {}", baseModelName, baseModel);
        }
    }

    private static final Map<ResourceLocation, UnbakedModel> MODEL_CACHE = new HashMap<>();
    private static final Map<ResourceLocation, UnbakedModel> EARLY_CACHE = new HashMap<>();
    private static final Map<ResourceLocation, ModelResourceLocation> MODEL_REF_CACHE = new HashMap<>();
    public static @Nullable UnbakedModel getCachedModel(ResourceLocation name) {
        if (MODEL_REF_CACHE.containsKey(name))
            return EARLY_CACHE.get(MODEL_REF_CACHE.get(name));
        return MODEL_CACHE.get(name);
    }

    private static @Nullable LatexBlockUploader uploader = new LatexBlockUploader();
    public static @NotNull LatexBlockUploader getUploader() {
        if (uploader == null) {
            throw new IllegalStateException("Uploader not created, a dependency for another mod may be missing.");
        }

        return uploader;
    }

    public static class ShouldBeCovered extends Event {
        private final Block block;

        public ShouldBeCovered(Block block) {
            this.block = block;
        }

        public Block getBlock() {
            return block;
        }

        @Override
        public boolean isCancelable() {
            return true;
        }
    }

    @Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEvents {
        @SubscribeEvent
        public static void onRegisterReloadListenerEvent(RegisterClientReloadListenersEvent event) {
            getUploader().attachToManager(Minecraft.getInstance().textureManager);
        }

        @SubscribeEvent
        public static void onModelBake(ModelBakeEvent event) {
            LatexBlockUploader uploader = getUploader();

            MODEL_CACHE.clear();
            final Registrar registrar = new Registrar(event, MODEL_CACHE, EARLY_CACHE, MODEL_REF_CACHE, uploader, Changed.config.client.generateUniqueTexturesForAllBlocks.get());
            LOGGER.info("Gathering blocks to cover");
            List<Block> toCover = Registry.BLOCK.stream().filter(block -> block.getStateDefinition().getProperties().contains(COVERED)).toList();
            LOGGER.info("Starting latex cover generation for {} blocks", toCover.size());
            Stopwatch timerFull = Stopwatch.createStarted();
            Stopwatch timerPart = Stopwatch.createStarted();

            if (!registrar.mixTextures) { // Register default textures
                if (Changed.config.client.fastAndCheapLatexBlocks.get()) {
                    LOGGER.info("Fast and cheap block generation selected!");
                }

                Arrays.stream(LatexType.values()).forEach(type -> {
                    if (type == LatexType.NEUTRAL)
                        return;

                    var name = getDefaultLatexCoverCached.apply(type);
                    var overlay = TYPE_OVERLAY.get(type);

                    registrar.registerTexture(name, new MixedTexture(Changed.modResource("blocks/dark_latex_block_top"), overlay.top, name));
                    registrar.registerEarlyBake(getDefaultLatexModelCached.apply(type), new BlockModel(
                            new ResourceLocation("minecraft", "block/cube_all"),
                            List.of(),
                            Map.of("all", Either.left(getLatexedMaterial(name)),
                                    "particle", Either.left(overlay.particleMaterial)),
                            true,
                            BlockModel.GuiLight.SIDE,
                            ItemTransforms.NO_TRANSFORMS,
                            List.of()
                    ));
                });
            }

            AtomicInteger index = new AtomicInteger(0);
            toCover.forEach(block -> {
                block.getStateDefinition().getPossibleStates().forEach((state) -> {
                    if (state.getValue(COVERED) != LatexType.NEUTRAL)
                        coverBlock(registrar, state, state.getValue(COVERED));
                });
                int currentIndex = index.incrementAndGet();
                if (currentIndex % 500 == 0) {
                    LOGGER.info("Hit {}/{} generated blocks ({} sprites)", currentIndex, toCover.size(), uploader.registeredSprites.size());
                }
            });

            timerPart.stop();
            LOGGER.info("Finished model generation of {} latex models in {}", MODEL_CACHE.size(), timerPart);
            timerPart.reset().start();

            uploader.upload(Minecraft.getInstance().getResourceManager(), InactiveProfiler.INSTANCE);
            uploader.registeredSprites.clear();

            timerPart.stop();
            LOGGER.info("Uploaded generated textures in {}", timerPart);
            timerPart.reset().start();

            registrar.bakeAll();

            timerFull.stop();
            LOGGER.info("Finished baking of {} latex models in {}", MODEL_CACHE.size() + registrar.earlyBakeModels.size(), timerFull);

            EARLY_CACHE.clear();
            MODEL_CACHE.clear();
            MODEL_REF_CACHE.clear();
        }
    }
}
