package net.ltxprogrammer.changed.init;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.variant.VariantFeature;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ChangedVariantFeatures {
    public static final DeferredRegister<VariantFeature> REGISTRY = ChangedRegistry.TRANSFUR_VARIANT_FEATURES.createDeferred(Changed.MODID);

    public static final RegistryObject<VariantFeature> ABSORPTION = REGISTRY.register("grab/absorption", VariantFeature::binary);
    public static final RegistryObject<VariantFeature> OXYGEN_SYMBIOSIS = REGISTRY.register("grab/oxygen_symbiosis", VariantFeature::binary);
    public static final RegistryObject<VariantFeature> DEPTH_COMPRESSION = REGISTRY.register("aquatic/depth_compression", VariantFeature::maxPresent);
    public static final RegistryObject<VariantFeature> SHARKS_BONUS_SHARKS = REGISTRY.register("summon_sharks/bonus_sharks", VariantFeature::sumPresent);
    public static final RegistryObject<VariantFeature> SHARKS_CALL_TO_ARMS = REGISTRY.register("summon_sharks/call_to_arms", VariantFeature::maxPresent);
    public static final RegistryObject<VariantFeature> SHARKS_FOLLOW_MY_LEAD = REGISTRY.register("summon_sharks/follow_my_lead", VariantFeature::maxPresent);

    public static final RegistryObject<VariantFeature> BREATHE_DENY_AIR = REGISTRY.register("breathe/deny_air", VariantFeature::binary);
    public static final RegistryObject<VariantFeature> BREATHE_ACCEPT_WATER = REGISTRY.register("breathe/accept_water", VariantFeature::maxPresent);
}
