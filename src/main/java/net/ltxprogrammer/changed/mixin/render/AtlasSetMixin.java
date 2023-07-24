package net.ltxprogrammer.changed.mixin.render;

import net.ltxprogrammer.changed.client.LatexCoveredBlocks;
import net.minecraft.client.renderer.texture.AtlasSet;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.Map;

@Mixin(AtlasSet.class)
public abstract class AtlasSetMixin implements AutoCloseable {
    @Shadow @Final private Map<ResourceLocation, TextureAtlas> atlases;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void init(Collection<TextureAtlas> textureAtlases, CallbackInfo callback) {
        if (atlases.containsKey(TextureAtlas.LOCATION_BLOCKS))
            atlases.put(LatexCoveredBlocks.LATEX_COVER_ATLAS, LatexCoveredBlocks.getUploader().getTextureAtlas());
    }
}
