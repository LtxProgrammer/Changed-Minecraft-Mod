package net.ltxprogrammer.changed.client.renderer.layers;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class EmissiveBodyLayer<M extends EntityModel<T>, T extends LivingEntity> extends EyesLayer<T, M> {
    private final RenderType renderType;
    private final ResourceLocation emissiveTexture;

    public EmissiveBodyLayer(RenderLayerParent<T, M> p_116964_, ResourceLocation emissiveTexture) {
        super(p_116964_);
        this.renderType = RenderType.eyes(emissiveTexture);
        this.emissiveTexture = emissiveTexture;
    }

    public ResourceLocation getEmissiveTexture() {
        return emissiveTexture;
    }

    public RenderType renderType() {
        return renderType;
    }
}