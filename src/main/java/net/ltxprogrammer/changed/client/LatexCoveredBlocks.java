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
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.block.model.multipart.MultiPart;
import net.minecraft.client.renderer.block.model.multipart.Selector;
import net.minecraft.client.renderer.texture.*;
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
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Function;
import java.util.stream.Collectors;

import static net.ltxprogrammer.changed.block.AbstractLatexBlock.COVERED;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public abstract class LatexCoveredBlocks {
    private static final Logger LOGGER = LogUtils.getLogger();
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
            if (!MixedTexture.cacheExists(fullPath))
                textureFunction.apply(info.name()).load(resources);

            Optional<NativeImage> cached = MixedTexture.findCachedTexture(fullPath);
            if (cached.isPresent())
                return new TextureAtlasSprite(this, info, mipLevels, atlasWidth, atlasHeight, x, y, cached.get());
            LOGGER.error("Using missing texture, unable to find {} in cache", fullPath);
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

        public LatexBlockUploader(TextureManager manager) {
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
        private final LatexBlockUploader uploader;

        public Registrar(ModelBakeEvent event, Map<ResourceLocation, UnbakedModel> models, LatexBlockUploader uploader) {
            this.event = event;
            this.models = models;
            this.uploader = uploader;
        }

        public void register(ModelResourceLocation name, UnbakedModel model) {
            models.put(name, model);
        }

        public void registerIfAbsent(ResourceLocation name, Function<ResourceLocation, UnbakedModel> fn) {
            models.computeIfAbsent(name, fn);
        }

        public void registerTexture(ResourceLocation saveLocation, MixedTexture mixedTexture) {
            uploader.registerSprite(saveLocation, mixedTexture);
        }

        public UnbakedModel getModel(ResourceLocation name) {
            var model = event.getModelLoader().getModel(name);
            return model != event.getModelLoader().getModel(ModelBakery.MISSING_MODEL_LOCATION) ? model : models.get(name);
        }

        private TextureAtlasSprite getSprite(Material material) {
            return event.getModelManager().getAtlas(material.atlasLocation()).getSprite(material.texture());
        }

        public void bakeAll() {
            ((BakeryExtender)(Object)event.getModelLoader()).removeFromCacheIf(
                    triple -> models.containsKey(triple.getLeft())
            );
            models.forEach((name, model) -> {
                // Force model parent chain to generate, so materials resolve correctly
                model.getMaterials(this::getModel, new HashSet<>());
                event.getModelRegistry().put(name, model.bake(event.getModelLoader(), this::getSprite, BlockModelRotation.X0_Y0, name));
            });
        }
    }

    private static Material getLatexedMaterial(ResourceLocation name) {
        return new Material(LATEX_COVER_ATLAS, name);
    }

    private static Function<ResourceLocation, UnbakedModel> createLatexModel(Registrar registrar, BlockModel blockModel, MixedTexture.OverlayBlock overlay, String nameAppend) {
        return name -> {
            Map<String, Either<Material, String>> injectedTextures = new HashMap<>();

            blockModel.textureMap.forEach((refName, either) -> {
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

    private static Function<Variant, Variant> createLatexVariant(Registrar registrar, LatexType type) {
        return variant -> {
            MixedTexture.OverlayBlock overlay = TYPE_OVERLAY.get(type);

            ResourceLocation modelLocation = variant.getModelLocation();
            BlockModel blockModel = (BlockModel)registrar.getModel(modelLocation);

            String nameAppend = "/" + type.getSerializedName();
            ResourceLocation newName = new ResourceLocation(modelLocation.getNamespace(), modelLocation.getPath() + nameAppend);
            registrar.registerIfAbsent(newName, createLatexModel(registrar, blockModel, overlay, nameAppend));

            return new Variant(newName, variant.getRotation(), variant.isUvLocked(), variant.getWeight());
        };
    }

    private static UnbakedModel overWriteMultiPart(Registrar registrar, MultiPart multiPart, ModelResourceLocation model, BlockState state, LatexType type) {
        return new MultiPart(state.getBlock().getStateDefinition(),
                multiPart.getSelectors().stream().map(selector ->
                        new Selector(selector::getPredicate, new MultiVariant(
                                selector.getVariant().getVariants().stream().map(createLatexVariant(registrar, type)).collect(Collectors.toList())
                        ))
                ).collect(Collectors.toList()));
    }

    private static UnbakedModel overWriteMultiVariant(Registrar registrar, MultiVariant multiVariant, ModelResourceLocation model, BlockState state, LatexType type) {
        return new MultiVariant(
                multiVariant.getVariants().stream().map(createLatexVariant(registrar, type)).collect(Collectors.toList())
        );
    }

    protected static void coverBlock(Registrar registrar, BlockState state, LatexType type) {
        var coveredModelName = BlockModelShaper.stateToModelLocation(state);
        var baseModelName = BlockModelShaper.stateToModelLocation(state.setValue(COVERED, LatexType.NEUTRAL));

        var baseModel = registrar.getModel(baseModelName);
        if (baseModel instanceof MultiPart multiPart)
            registrar.register(coveredModelName, overWriteMultiPart(registrar, multiPart, coveredModelName, state, type));
        else if (baseModel instanceof MultiVariant multiVariant)
            registrar.register(coveredModelName, overWriteMultiVariant(registrar, multiVariant, coveredModelName, state, type));
    }

    private static final Map<ResourceLocation, UnbakedModel> MODEL_CACHE = new HashMap<>();
    public static @Nullable UnbakedModel getCachedModel(ResourceLocation name) {
        return MODEL_CACHE.get(name);
    }

    private static @Nullable LatexBlockUploader uploader = null;
    public static LatexBlockUploader getUploader() {
        return uploader;
    }

    @SubscribeEvent
    public static void onRegisterReloadListenerEvent(RegisterClientReloadListenersEvent event) {
        Minecraft minecraft = Minecraft.getInstance();
        uploader = new LatexBlockUploader(minecraft.textureManager);
        //event.registerReloadListener(uploader);
    }

    @SubscribeEvent
    public static void onModelBake(ModelBakeEvent event) {
        if (uploader == null)
            throw new IllegalStateException("Uploader not created!");

        MODEL_CACHE.clear();
        final Registrar registrar = new Registrar(event, MODEL_CACHE, uploader);
        List<Block> toCover = Registry.BLOCK.stream().filter(block -> block.getStateDefinition().getProperties().contains(COVERED)).toList();
        LOGGER.info("Starting latex cover generation for {} blocks", toCover.size());
        Stopwatch timer = Stopwatch.createStarted();

        toCover.forEach(block -> {
            LOGGER.trace("Processing block {}", block);
            block.getStateDefinition().getPossibleStates().forEach((state) -> {
                if (state.getValue(COVERED) != LatexType.NEUTRAL)
                    coverBlock(registrar, state, state.getValue(COVERED));
            });
        });

        uploader.upload(Minecraft.getInstance().getResourceManager(), InactiveProfiler.INSTANCE);
        registrar.bakeAll();

        timer.stop();
        LOGGER.info("Finished model generation of {} models in {}", MODEL_CACHE.size(), timer);
    }
}
