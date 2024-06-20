package net.ltxprogrammer.changed.data;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import net.ltxprogrammer.changed.Changed;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.resources.metadata.texture.TextureMetadataSection;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.io.IOException;

@OnlyIn(Dist.CLIENT)
public class OnlineTexture extends AbstractTexture {
    private final Resource resource;

    public OnlineTexture(Resource resource) {
        this.resource = resource;
    }

    public static OnlineTexture of(Resource resource) {
        return new OnlineTexture(resource);
    }

    @Override
    public void load(ResourceManager p_117955_) throws IOException {
        SimpleTexture.TextureImage simpletexture$textureimage = this.getTextureImage();
        simpletexture$textureimage.throwIfError();
        TextureMetadataSection texturemetadatasection = simpletexture$textureimage.getTextureMetadata();
        boolean flag;
        boolean flag1;
        if (texturemetadatasection != null) {
            flag = texturemetadatasection.isBlur();
            flag1 = texturemetadatasection.isClamp();
        } else {
            flag = false;
            flag1 = false;
        }

        NativeImage nativeimage = simpletexture$textureimage.getImage();
        if (!RenderSystem.isOnRenderThreadOrInit()) {
            RenderSystem.recordRenderCall(() -> {
                this.doLoad(nativeimage, flag, flag1);
            });
        } else {
            this.doLoad(nativeimage, flag, flag1);
        }
    }

    private void doLoad(NativeImage p_118137_, boolean p_118138_, boolean p_118139_) {
        TextureUtil.prepareImage(this.getId(), 0, p_118137_.getWidth(), p_118137_.getHeight());
        p_118137_.upload(0, 0, 0, 0, 0, p_118137_.getWidth(), p_118137_.getHeight(), p_118138_, p_118139_, false, true);
    }

    protected SimpleTexture.TextureImage getTextureImage() throws IOException {
        SimpleTexture.TextureImage $$5;
        try {
            NativeImage nativeimage = NativeImage.read(resource.getInputStream());
            TextureMetadataSection texturemetadatasection = null;

            try {
                texturemetadatasection = resource.getMetadata(TextureMetadataSection.SERIALIZER);
            } catch (RuntimeException runtimeexception) {
                Changed.LOGGER.warn("Failed reading metadata of: {}", resource.getLocation(), runtimeexception);
            }

            $$5 = new SimpleTexture.TextureImage(texturemetadatasection, nativeimage);
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

        return $$5;
    }
}
