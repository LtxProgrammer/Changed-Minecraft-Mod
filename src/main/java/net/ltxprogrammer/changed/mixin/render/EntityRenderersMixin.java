package net.ltxprogrammer.changed.mixin.render;

import net.ltxprogrammer.changed.init.ChangedEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(EntityRenderers.class)
public abstract class EntityRenderersMixin {
    @Inject(method = "createEntityRenderers", at = @At("RETURN"))
    private static void createChangedEntityRenderers(EntityRendererProvider.Context context, CallbackInfoReturnable<Map<EntityType<?>, EntityRenderer<?>>> callback) {
        ChangedEntityRenderers.registerComplexRenderers(context);
    }
}
