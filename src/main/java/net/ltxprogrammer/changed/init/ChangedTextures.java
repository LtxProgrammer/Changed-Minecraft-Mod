package net.ltxprogrammer.changed.init;

import com.mojang.blaze3d.pipeline.RenderCall;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public class ChangedTextures {
    public static final Map<ResourceLocation, AbstractTexture> REGISTRY = new HashMap<>();

    private static void doOnRenderThread(RenderCall call) {
        if (!RenderSystem.isOnRenderThread())
            RenderSystem.recordRenderCall(call);
        else
            call.execute();
    }

    public static ResourceLocation register(ResourceLocation location, Supplier<AbstractTexture> texture) {
        REGISTRY.computeIfAbsent(location, ignored -> texture.get());
        return location;
    }

    public static void lateRegisterTextureNoSave(ResourceLocation location, Supplier<AbstractTexture> texture) {
        doOnRenderThread(() -> Minecraft.getInstance().getTextureManager().register(location, texture.get()));
    }

    public static void lateRegisterTexture(ResourceLocation location, Supplier<AbstractTexture> texture) {
        doOnRenderThread(() -> Minecraft.getInstance().getTextureManager().register(location, REGISTRY.get(register(location, texture))));
    }

    private static void registerAllOnRenderThread() {
        RenderSystem.assertOnRenderThreadOrInit();

        var textureManager = Minecraft.getInstance().getTextureManager();
        REGISTRY.forEach(textureManager::register);
    }

    public static void registerAll(FMLClientSetupEvent event) {
        if (!RenderSystem.isOnRenderThread())
            RenderSystem.recordRenderCall(ChangedTextures::registerAllOnRenderThread);
        else
            registerAllOnRenderThread();
    }
}
