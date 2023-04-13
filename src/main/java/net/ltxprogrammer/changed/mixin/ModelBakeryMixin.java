package net.ltxprogrammer.changed.mixin;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Either;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.data.MixedTexture;
import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.init.ChangedTextures;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.MultiVariant;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.client.renderer.block.model.multipart.MultiPart;
import net.minecraft.client.renderer.block.model.multipart.Selector;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.apache.commons.compress.utils.Lists;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static net.ltxprogrammer.changed.block.AbstractLatexBlock.COVERED;

@Mixin(ModelBakery.class)
public abstract class ModelBakeryMixin {
    @Shadow @Final private static Logger LOGGER;
    private static final ImmutableMap<LatexType, MixedTexture.OverlayBlock> TYPE_OVERLAY = ImmutableMap.of(
            LatexType.DARK_LATEX, new MixedTexture.OverlayBlock(
                    Changed.modResource("blocks/dark_latex_block_top"),
                    Changed.modResource("blocks/dark_latex_block_side"),
                    Changed.modResource("blocks/dark_latex_block_bottom")),
            LatexType.WHITE_LATEX, new MixedTexture.OverlayBlock(
                    Changed.modResource("blocks/white_latex_block"),
                    Changed.modResource("blocks/white_latex_block"),
                    Changed.modResource("blocks/white_latex_block")));

    @Unique
    private MultiPart overWriteMultiPart(MultiPart multiPart, ModelResourceLocation model) {
        LatexType type = LatexType.valueOf(model.getVariant().split(COVERED.getName() + "=")[1].split(",")[0].toUpperCase());
        MixedTexture.OverlayBlock overlay = TYPE_OVERLAY.get(type);
        ModelBakery self = (ModelBakery)(Object)this;
        List<Selector> toAddSelector = Lists.newArrayList();

        multiPart.getSelectors().forEach(selector -> {
            List<Variant> toAdd = Lists.newArrayList();
            selector.getVariant().getVariants().forEach(variant -> {
                ResourceLocation modelLocation = variant.getModelLocation();
                BlockModel blockModel = (BlockModel)self.unbakedCache.get(modelLocation);

                String nameAppend = "/" + type.getSerializedName();
                ResourceLocation newName = new ResourceLocation(modelLocation.getNamespace(), modelLocation.getPath() + nameAppend);
                self.unbakedCache.computeIfAbsent(newName, (ignored) -> {
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
                    injected.name = blockModel.name + "/" + type.getSerializedName();
                    return injected;
                });

                Variant injected = new Variant(newName, variant.getRotation(), variant.isUvLocked(), variant.getWeight());
                toAdd.add(injected);
            });

            toAddSelector.add(new Selector(selector::getPredicate, new MultiVariant(toAdd)));
        });

        MultiPart injected = new MultiPart(((Supplier<StateDefinition<Block, BlockState>>)multiPart).get(), toAddSelector);
        self.unbakedCache.put(model, injected);
        self.topLevelModels.put(model, injected);
        return injected;
    }

    @Unique
    private MultiVariant overWriteMultiVariant(MultiVariant multiVariant, ModelResourceLocation model) {
        LatexType type = LatexType.valueOf(model.getVariant().split(COVERED.getName() + "=")[1].split(",")[0].toUpperCase());
        MixedTexture.OverlayBlock overlay = TYPE_OVERLAY.get(type);
        ModelBakery self = (ModelBakery)(Object)this;
        List<Variant> toAdd = Lists.newArrayList();

        multiVariant.getVariants().forEach(variant -> {
            ResourceLocation modelLocation = variant.getModelLocation();
            BlockModel blockModel = (BlockModel)self.unbakedCache.get(modelLocation);

            String nameAppend = "/" + type.getSerializedName();
            ResourceLocation newName = new ResourceLocation(modelLocation.getNamespace(), modelLocation.getPath() + nameAppend);
            self.unbakedCache.computeIfAbsent(newName, (ignored) -> {
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
                injected.name = blockModel.name + "/" + type.getSerializedName();
                return injected;
            });

            Variant injected = new Variant(newName, variant.getRotation(), variant.isUvLocked(), variant.getWeight());
            toAdd.add(injected);
        });

        MultiVariant injected = new MultiVariant(toAdd);
        self.unbakedCache.put(model, injected);
        self.topLevelModels.put(model, injected);
        return injected;
    }

    @Inject(method = "loadTopLevel", at = @At("RETURN"))
    private void loadTopLevel(ModelResourceLocation model, CallbackInfo callbackInfo) {
        if (!model.getVariant().contains(COVERED.getName()))
            return;
        LatexType type = LatexType.valueOf(model.getVariant().split(COVERED.getName() + "=")[1].split(",")[0].toUpperCase());
        if (type == LatexType.NEUTRAL)
            return;

        ModelBakery self = (ModelBakery)(Object)this;
        UnbakedModel unbaked = self.unbakedCache.get(model);

        if (unbaked instanceof MultiPart multiPart) {
            overWriteMultiPart(multiPart, model);
        }

        if (unbaked instanceof MultiVariant multiVariant) {
            overWriteMultiVariant(multiVariant, model);
        }
    }
}
