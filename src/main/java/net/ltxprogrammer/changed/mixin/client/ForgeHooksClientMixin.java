package net.ltxprogrammer.changed.mixin.client;

import net.ltxprogrammer.changed.init.ChangedTextures;
import net.minecraft.client.resources.model.Material;
import net.minecraftforge.client.ForgeHooksClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(ForgeHooksClient.class)
public abstract class ForgeHooksClientMixin {
    @Inject(method = "gatherFluidTextures", at = @At("HEAD"), remap = false)
    private static void gatherFluidTextures(Set<Material> textures, CallbackInfo callback) {
        textures.addAll(ChangedTextures.MATERIAL_MAP.values());
        // Bruh the things I gotta do for this
    }
}
