package net.ltxprogrammer.changed.data;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public class DeferredModelLayerLocation implements Supplier<ModelLayerLocation> {
    public final ResourceLocation model;
    public final String layer;

    public DeferredModelLayerLocation(ResourceLocation p_171121_, String p_171122_) {
        this.model = p_171121_;
        this.layer = p_171122_;
    }

    public ResourceLocation getModel() {
        return this.model;
    }

    public String getLayer() {
        return this.layer;
    }

    public boolean equals(Object p_171126_) {
        if (this == p_171126_) {
            return true;
        } else if (!(p_171126_ instanceof DeferredModelLayerLocation)) {
            return false;
        } else {
            DeferredModelLayerLocation modellayerlocation = (DeferredModelLayerLocation)p_171126_;
            return this.model.equals(modellayerlocation.model) && this.layer.equals(modellayerlocation.layer);
        }
    }

    public int hashCode() {
        int i = this.model.hashCode();
        return 31 * i + this.layer.hashCode();
    }

    public String toString() {
        return this.model + "#" + this.layer;
    }

    public ModelLayerLocation get() {
        return new ModelLayerLocation(model, layer);
    }
}
