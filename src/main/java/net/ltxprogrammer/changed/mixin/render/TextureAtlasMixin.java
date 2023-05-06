package net.ltxprogrammer.changed.mixin.render;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.PngInfo;
import com.mojang.datafixers.util.Pair;
import net.ltxprogrammer.changed.data.MixedTexture;
import net.ltxprogrammer.changed.entity.LatexType;
import net.minecraft.Util;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.Stitcher;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;

@Mixin(TextureAtlas.class)
public abstract class TextureAtlasMixin {
    @Unique
    private static final Logger LOGGER = LogManager.getLogger(TextureAtlasMixin.class);

    @Unique
    private ResourceLocation getResourceLocation(ResourceLocation p_118325_) {
        return new ResourceLocation(p_118325_.getNamespace(), String.format("textures/%s%s", p_118325_.getPath(), ".png"));
    }

    @Unique
    private ResourceLocation getUnderlyingLocation(ResourceLocation location) {
        boolean flag = false;
        for (LatexType value : LatexType.values()) {
            if (value == LatexType.NEUTRAL) continue;

            if (location.getPath().endsWith("/" + value.toString().toLowerCase())) {
                flag = true;
                break;
            }
        }

        if (!flag)
            return location;
        else {
            return new ResourceLocation(location.getNamespace(), location.getPath().substring(0, location.getPath().lastIndexOf('/')));
        }
    }

    /**
     * @author LtxProgrammer
     * @reason Injecting would be impossible
     */
    @Overwrite
    private Collection<TextureAtlasSprite.Info> getBasicSpriteInfos(ResourceManager p_118305_, Set<ResourceLocation> p_118306_) {
        List<CompletableFuture<?>> list = Lists.newArrayList();
        Queue<TextureAtlasSprite.Info> queue = new ConcurrentLinkedQueue<>();

        for(final ResourceLocation resourcelocation : p_118306_) {
            if (!MissingTextureAtlasSprite.getLocation().equals(resourcelocation)) {
                list.add(CompletableFuture.runAsync(() -> {
                    ResourceLocation updatedLocation = getUnderlyingLocation(resourcelocation);
                    ResourceLocation resourcelocation1 = this.getResourceLocation(updatedLocation);

                    TextureAtlasSprite.Info textureatlassprite$info;
                    try {
                        Resource resource = p_118305_.getResource(resourcelocation1);

                        try {
                            PngInfo pnginfo = new PngInfo(resource.toString(), resource.getInputStream());
                            AnimationMetadataSection animationmetadatasection = resource.getMetadata(AnimationMetadataSection.SERIALIZER);
                            if (animationmetadatasection == null) {
                                animationmetadatasection = AnimationMetadataSection.EMPTY;
                            }

                            Pair<Integer, Integer> pair = animationmetadatasection.getFrameSize(pnginfo.width, pnginfo.height);
                            textureatlassprite$info = new TextureAtlasSprite.Info(resourcelocation, pair.getFirst(), pair.getSecond(), animationmetadatasection);
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

                        if (resource != null) {
                            resource.close();
                        }
                    } catch (RuntimeException runtimeexception) {
                        LOGGER.error("Unable to parse metadata from {} : {}", updatedLocation, runtimeexception);
                        return;
                    } catch (IOException ioexception) {
                        LOGGER.error("Using missing texture, unable to load {} : {}", updatedLocation, ioexception);
                        return;
                    }

                    queue.add(textureatlassprite$info);
                }, Util.backgroundExecutor()));
            }
        }

        CompletableFuture.allOf(list.toArray(new CompletableFuture[0])).join();
        return queue;
    }

    // Replacing mixin `load` >1000, with 1 `getLoadedSprites`
    @Inject(method = "getLoadedSprites", at = @At("RETURN"))
    private void getLoadedSprites(ResourceManager resourceManager, Stitcher stitcher, int mipLevels, CallbackInfoReturnable<List<TextureAtlasSprite>> callback) {
        var list = callback.getReturnValue(); // Append covered blocks
        stitcher.gatherSprites((info, atlasWidth, atlasHeight, x, y) -> {
            ResourceLocation resourcelocation = this.getResourceLocation(info.name());
            if (resourceManager.hasResource(resourcelocation) || info == MissingTextureAtlasSprite.info())
                return;

            var texture = MixedTexture.findMixedTexture(resourcelocation);
            if (texture == null) {
                int attempts = 60; // Corrupt textures may be from a race condition
                while (attempts > 0 && texture == null) {
                    try {
                        Thread.sleep(10);
                    } catch (Exception e) {
                        LOGGER.error("Failed to sleep", e);
                    }
                    texture = MixedTexture.findMixedTexture(resourcelocation);
                    attempts--;
                }
                if (texture == null)
                    LOGGER.error("Missing generated texture for {}", info.name());
            }

            if (texture != null) {
                try {
                    list.add(new TextureAtlasSprite((TextureAtlas)(Object)this, info, mipLevels, atlasWidth, atlasHeight, x, y, texture));
                } catch (Exception exception) {
                    LOGGER.error("Failed to load mixed texture {}", info.name(), exception);
                }
            }
        });
    }

    @Inject(method = "load(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/client/renderer/texture/TextureAtlasSprite$Info;IIIII)Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;", at = @At("HEAD"), cancellable = true)
    private void load(ResourceManager rm, TextureAtlasSprite.Info info, int atlasWidth, int atlasHeight, int mipLevels, int x, int y,
        CallbackInfoReturnable<TextureAtlasSprite> callbackInfo) {
        ResourceLocation resourcelocation = this.getResourceLocation(info.name());
        if (rm.hasResource(resourcelocation) || info == MissingTextureAtlasSprite.info())
            return;

        for (var type : Arrays.stream(LatexType.values()).filter(type -> type != LatexType.NEUTRAL).toList()) {
            if (resourcelocation.getPath().endsWith("/" + type.getSerializedName() + ".png")) {
                callbackInfo.setReturnValue(null);
                return;
            }
        }
    }
}
