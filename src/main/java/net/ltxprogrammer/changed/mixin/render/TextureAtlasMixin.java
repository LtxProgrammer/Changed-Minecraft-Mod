package net.ltxprogrammer.changed.mixin.render;

import net.ltxprogrammer.changed.client.LatexCoveredBlocks;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.Set;

@Mixin(TextureAtlas.class)
public abstract class TextureAtlasMixin {
    @Inject(method = "getBasicSpriteInfos", at = @At("HEAD"), cancellable = true)
    private void getBasicSpriteInfos(ResourceManager resources, Set<ResourceLocation> sprites, CallbackInfoReturnable<Collection<TextureAtlasSprite.Info>> callback) {
        if (((Object)this) instanceof LatexCoveredBlocks.LatexAtlas atlas) {
            callback.setReturnValue(atlas.getBasicSpriteInfos(resources, sprites));
        }
    }

    @Inject(method = "load(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/client/renderer/texture/TextureAtlasSprite$Info;IIIII)Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;", at = @At("HEAD"), cancellable = true)
    private void load(ResourceManager rm, TextureAtlasSprite.Info info, int atlasWidth, int atlasHeight, int mipLevels, int x, int y,
                      CallbackInfoReturnable<TextureAtlasSprite> callback) {
        if (((Object)this) instanceof LatexCoveredBlocks.LatexAtlas atlas) {
            callback.setReturnValue(atlas.load(rm, info, atlasWidth, atlasHeight, mipLevels, x, y));
        }
    }
}
