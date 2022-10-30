package net.ltxprogrammer.changed.mixin;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.PngInfo;
import com.mojang.datafixers.util.Pair;
import net.ltxprogrammer.changed.data.MixedTexture;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;

@Mixin(TextureAtlas.class)
public abstract class TextureAtlasMixin {
    private static final Logger LOGGER = LogManager.getLogger(TextureAtlasMixin.class);

    private ResourceLocation getResourceLocation(ResourceLocation p_118325_) {
        return new ResourceLocation(p_118325_.getNamespace(), String.format("textures/%s%s", p_118325_.getPath(), ".png"));
    }

    /**
     * @author LtxProgrammer
     * @reason Injecting would be impossible
     */
    @Overwrite
    private Collection<TextureAtlasSprite.Info> getBasicSpriteInfos(ResourceManager p_118305_, Set<ResourceLocation> p_118306_) {
        List<CompletableFuture<?>> list = Lists.newArrayList();
        Queue<TextureAtlasSprite.Info> queue = new ConcurrentLinkedQueue<>();

        for(ResourceLocation resourcelocation : p_118306_) {
            if (!MissingTextureAtlasSprite.getLocation().equals(resourcelocation)) {
                list.add(CompletableFuture.runAsync(() -> {
                    ResourceLocation resourcelocation1 = this.getResourceLocation(resourcelocation);

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
                        LOGGER.error("Unable to parse metadata from {} : {}", resourcelocation1, runtimeexception);
                        return;
                    } catch (IOException ioexception) {
                        // Injected code here
                        var found = MixedTexture.findTexture(resourcelocation1);
                        if (found == null) {
                            LOGGER.error("Using missing texture, unable to load {} : {}", resourcelocation1, ioexception);
                            return;
                        }

                        else {
                            textureatlassprite$info = new TextureAtlasSprite.Info(resourcelocation, found.getWidth(), found.getHeight(), AnimationMetadataSection.EMPTY);
                        }
                    }

                    queue.add(textureatlassprite$info);
                }, Util.backgroundExecutor()));
            }
        }

        CompletableFuture.allOf(list.toArray(new CompletableFuture[0])).join();
        return queue;
    }

    @Inject(method = "load(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/client/renderer/texture/TextureAtlasSprite$Info;IIIII)Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;", at = @At("HEAD"), cancellable = true)
    private void load(ResourceManager p_118288_, TextureAtlasSprite.Info p_118289_, int p_118290_, int p_118291_, int p_118292_, int p_118293_, int p_118294_,
        CallbackInfoReturnable<TextureAtlasSprite> callbackInfo) {
        ResourceLocation resourcelocation = this.getResourceLocation(p_118289_.name());
        if (p_118288_.hasResource(resourcelocation))
            return;

        else {
            var texture = MixedTexture.findTexture(resourcelocation);
            if (texture == null)
                return;

            else {
                try {
                    callbackInfo.setReturnValue(new TextureAtlasSprite((TextureAtlas)(Object)this, p_118289_, p_118292_, p_118290_, p_118291_, p_118293_, p_118294_, texture));
                } catch (Exception exception) {
                    LOGGER.error("Failed to load mixed texture {}", p_118289_.name(), exception);
                    return;
                }
            }
        }
    }
}
