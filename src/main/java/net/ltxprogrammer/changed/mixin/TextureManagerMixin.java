package net.ltxprogrammer.changed.mixin;

import net.ltxprogrammer.changed.init.ChangedTextures;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.Tickable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TextureManager.class)
public abstract class TextureManagerMixin implements PreparableReloadListener, Tickable, AutoCloseable {
    @Inject(method = "getTexture(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/renderer/texture/AbstractTexture;", at = @At("HEAD"), cancellable = true)
    public void getTexture(ResourceLocation p_118507_, CallbackInfoReturnable<AbstractTexture> ci) {
        TextureManager self = (TextureManager)(Object)this;

        AbstractTexture abstracttexture = self.byPath.get(p_118507_);
        if (abstracttexture == null) {
            abstracttexture = ChangedTextures.REGISTRY.get(p_118507_);

            if (abstracttexture != null) {
                self.register(p_118507_, abstracttexture);
                ci.setReturnValue(abstracttexture);
                ci.cancel();
            }
        }
    }
}
