package net.ltxprogrammer.changed.util;

import com.google.common.collect.ImmutableMap;
import net.ltxprogrammer.changed.data.DeferredModelLayerLocation;
import net.ltxprogrammer.changed.data.OnlineTexture;
import net.ltxprogrammer.changed.init.ChangedTextures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.core.Registry;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.fml.DistExecutor;

import java.util.function.Supplier;

import static net.ltxprogrammer.changed.util.Dynamic.replaceLocked;

public class DynamicClient {
    @OnlyIn(Dist.CLIENT)
    private static class Internal {
        public static <T extends Entity> void lateRegisterEntityRenderer(EntityType<? extends T> entityType, EntityRendererProvider<T> entityRendererProvider) {
            EntityRenderers.register(entityType, entityRendererProvider);

            Minecraft minecraft = Minecraft.getInstance();
            EntityRenderDispatcher dispatcher = minecraft.getEntityRenderDispatcher();
            EntityRendererProvider.Context context = new EntityRendererProvider.Context(dispatcher, dispatcher.itemRenderer,
                    minecraft.getResourceManager(), dispatcher.entityModels, dispatcher.font);
            ImmutableMap.Builder<EntityType<?>, EntityRenderer<?>> builder = ImmutableMap.builder();
            dispatcher.renderers.forEach((type, renderer) -> {
                if (!type.equals(entityType))
                    builder.put(type, renderer);
            });
            builder.put(entityType, entityRendererProvider.create(context));
            dispatcher.renderers = builder.build();
        }

        // TODO dynamically update patreon forms
        public static void lateRegisterLayerDefinition(ModelLayerLocation layerLocation, Supplier<LayerDefinition> supplier) {
            ForgeHooksClient.registerLayerDefinition(layerLocation, supplier);

            Minecraft minecraft = Minecraft.getInstance();
            EntityRenderDispatcher dispatcher = minecraft.getEntityRenderDispatcher();
            ImmutableMap.Builder<ModelLayerLocation, LayerDefinition> builder = ImmutableMap.builder();
            dispatcher.entityModels.roots.forEach((location, definition) -> {
                if (!location.equals(layerLocation))
                    builder.put(location, definition);
            });
            builder.put(layerLocation, supplier.get());
            dispatcher.entityModels.roots = builder.build();
        }
    }

    public static <T extends Entity> void lateRegisterEntityRenderer(EntityType<? extends T> entityType, EntityRendererProvider<T> entityRendererProvider) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> Internal.lateRegisterEntityRenderer(entityType, entityRendererProvider));
    }

    public static void lateRegisterLayerDefinition(DeferredModelLayerLocation layerLocation, Supplier<LayerDefinition> supplier) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> Internal.lateRegisterLayerDefinition(layerLocation.get(), supplier));
    }

    public static void lateRegisterOnlineTexture(Resource resource) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ChangedTextures.lateRegisterTexture(resource.getLocation(), () -> OnlineTexture.of(resource)));
    }
}
