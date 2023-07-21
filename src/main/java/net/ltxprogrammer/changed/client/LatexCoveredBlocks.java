package net.ltxprogrammer.changed.client;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.data.MixedTexture;
import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.init.ChangedTextures;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.block.model.multipart.MultiPart;
import net.minecraft.client.renderer.block.model.multipart.Selector;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    protected static class Registrar {
        private final ModelBakeEvent event;
        private final Map<ResourceLocation, UnbakedModel> models;

        public Registrar(ModelBakeEvent event) {
            this.event = event;
            this.models = new HashMap<>();
        }

        public Registrar(ModelBakeEvent event, Map<ResourceLocation, UnbakedModel> models) {
            this.event = event;
            this.models = models;
        }

        public void register(ModelResourceLocation name, UnbakedModel model) {
            models.put(name, model);
        }

        public void registerIfAbsent(ResourceLocation name, Function<ResourceLocation, UnbakedModel> fn) {
            models.computeIfAbsent(name, fn);
        }

        public UnbakedModel getModel(ResourceLocation name) {
            var model = event.getModelLoader().getModel(name);
            return model != event.getModelLoader().getModel(ModelBakery.MISSING_MODEL_LOCATION) ? model : models.get(name);
        }
    }

    private static Function<ResourceLocation, UnbakedModel> createLatexModel(BlockModel blockModel, MixedTexture.OverlayBlock overlay, String nameAppend) {
        return name -> {
            Map<String, Either<Material, String>> injectedTextures = new HashMap<>();

            blockModel.textureMap.forEach((refName, either) -> {
                either.ifLeft(material -> {
                    injectedTextures.put(refName, Either.left(new Material(TextureAtlas.LOCATION_BLOCKS,
                            new ResourceLocation(material.texture() + nameAppend))));
                    var saveLocation = MixedTexture.getResourceLocation(
                            new ResourceLocation(material.texture() + nameAppend)
                    );
                    ChangedTextures.lateRegisterTextureNoSave(saveLocation, () -> new MixedTexture(
                            material.texture(), overlay.guessSide(refName), saveLocation
                    ));
                }).ifRight(string -> {
                    injectedTextures.put(refName, Either.right(string + nameAppend));
                    var saveLocation = MixedTexture.getResourceLocation(
                            new ResourceLocation(string + nameAppend)
                    );
                    ChangedTextures.lateRegisterTextureNoSave(saveLocation, () -> new MixedTexture(
                            new ResourceLocation(string), overlay.guessSide(refName), saveLocation
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
            registrar.registerIfAbsent(newName, createLatexModel(blockModel, overlay, nameAppend));

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

    @SubscribeEvent
    public static void onModelBake(ModelBakeEvent event) {
        MODEL_CACHE.clear();
        final Registrar registrar = new Registrar(event, MODEL_CACHE);
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

        timer.stop();
        LOGGER.info("Finished model generation of {} models in {}", MODEL_CACHE.size(), timer);
    }
}
