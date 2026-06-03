package net.ltxprogrammer.changed.init;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantFeature;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ChangedTransfurVariantFeatures {
    public static final DeferredRegister<TransfurVariantFeature> REGISTRY = ChangedRegistry.TRANSFUR_VARIANT_FEATURES.createDeferred(Changed.MODID);

    public static final RegistryObject<TransfurVariantFeature> ABSORPTION = REGISTRY.register("transfur/absorption", TransfurVariantFeature::maxPresent);
    public static final RegistryObject<TransfurVariantFeature> OXYGEN_SYMBIOSIS = REGISTRY.register("grab/oxygen_symbiosis", TransfurVariantFeature::maxPresent);
    public static final RegistryObject<TransfurVariantFeature> DEPTH_COMPRESSION = REGISTRY.register("aquatic/depth_compression", TransfurVariantFeature::maxPresent);
    public static final RegistryObject<TransfurVariantFeature> SHARKS_BONUS_SHARKS = REGISTRY.register("summon_sharks/bonus_sharks", TransfurVariantFeature::sumPresent);
    public static final RegistryObject<TransfurVariantFeature> SHARKS_CALL_TO_ARMS = REGISTRY.register("summon_sharks/call_to_arms", TransfurVariantFeature::maxPresent);
    public static final RegistryObject<TransfurVariantFeature> SHARKS_FOLLOW_MY_LEAD = REGISTRY.register("summon_sharks/follow_my_lead", TransfurVariantFeature::maxPresent);
}
