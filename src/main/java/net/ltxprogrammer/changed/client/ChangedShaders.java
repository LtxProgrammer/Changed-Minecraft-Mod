package net.ltxprogrammer.changed.client;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.IOException;
import java.util.function.Consumer;

public class ChangedShaders {
    public static void reloadShaders(ResourceManager resourceManager, Consumer<Pair<ShaderInstance, Consumer<ShaderInstance>>> loader) throws IOException {
        // No shaders as of now
        /*loader.accept(Pair.of(new ShaderInstance(resourceManager, Changed.modResource("rendertype_color_sample_3d_particle"), DefaultVertexFormat.NEW_ENTITY), (instance) -> {
            rendertypeColorSample3DParticleShader = instance;
        }));*/
    }
}
