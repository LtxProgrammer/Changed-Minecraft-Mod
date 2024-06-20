package net.ltxprogrammer.changed.client.renderer.model.armor;

import net.ltxprogrammer.changed.data.DeferredModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public record ArmorModelLayerLocation(DeferredModelLayerLocation inner, DeferredModelLayerLocation outer) {
    public static final Map<UUID, ArmorModelLayerLocation> ARMOR_LAYER_LOCATIONS = new HashMap<>();

    public static DeferredModelLayerLocation createInnerArmorLocation(ResourceLocation location) {
        return new DeferredModelLayerLocation(location, "inner_armor");
    }
    public static DeferredModelLayerLocation createOuterArmorLocation(ResourceLocation location) {
        return new DeferredModelLayerLocation(location, "outer_armor");
    }
}
