package net.ltxprogrammer.changed.client;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;

public interface ConditionalEntityRendererProvider<T extends Entity> extends EntityRendererProvider<T> {
    boolean shouldOverride(T entity);
}
