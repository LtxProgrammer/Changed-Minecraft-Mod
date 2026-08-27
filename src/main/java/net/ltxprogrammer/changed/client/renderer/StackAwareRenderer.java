package net.ltxprogrammer.changed.client.renderer;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;

/// Allows renderer overrides to be aware of the renderers it is overriding
public interface StackAwareRenderer<T extends Entity> {
    /**
     * Gets called when resolving a renderer for a certain entity.
     * @param renderer The renderer this renderer is overriding. It may be the base renderer or another override
     */
    void setShadowedRenderer(EntityRenderer<? super T> renderer);

    EntityRenderer<? super T> getShadowedRenderer();
}
