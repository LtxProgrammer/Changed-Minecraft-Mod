package net.ltxprogrammer.changed.init;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantFeature;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ChangedTransfurVariantFeatures {
    public static final DeferredRegister<TransfurVariantFeature> REGISTRY = ChangedRegistry.TRANSFUR_VARIANT_FEATURES.createDeferred(Changed.MODID);

    public static final RegistryObject<TransfurVariantFeature> ABSORPTION = REGISTRY.register("transfur/absorption", TransfurVariantFeature::new);
    public static final RegistryObject<TransfurVariantFeature> OXYGEN_SYMBIOSIS = REGISTRY.register("grab/oxygen_symbiosis", TransfurVariantFeature::new);
}
