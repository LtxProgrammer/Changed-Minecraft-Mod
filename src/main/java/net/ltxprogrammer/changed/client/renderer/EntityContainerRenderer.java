package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.block.entity.CardboardBoxBlockEntity;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class EntityContainerRenderer extends EntityRenderer<CardboardBoxBlockEntity.EntityContainer> {
    public EntityContainerRenderer(EntityRendererProvider.Context p_174008_) {
        super(p_174008_);
    }

    @Override
    public ResourceLocation getTextureLocation(CardboardBoxBlockEntity.EntityContainer p_114482_) {
        return null;
    }
}
