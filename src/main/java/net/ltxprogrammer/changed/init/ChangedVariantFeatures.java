package net.ltxprogrammer.changed.init;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.variant.VariantFeature;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ChangedVariantFeatures {
    public static final DeferredRegister<VariantFeature> REGISTRY = ChangedRegistry.TRANSFUR_VARIANT_FEATURES.createDeferred(Changed.MODID);

    public static final RegistryObject<VariantFeature> SCARE_VILLAGERS = REGISTRY.register("scare_villagers", VariantFeature::binary);

    public static final RegistryObject<VariantFeature> AFTERTASTE = REGISTRY.register("transfur/aftertaste", VariantFeature::maxPresent);

    public static final RegistryObject<VariantFeature> ABSORPTION = REGISTRY.register("grab/absorption", VariantFeature::binary);
    public static final RegistryObject<VariantFeature> OXYGEN_SYMBIOSIS = REGISTRY.register("grab/oxygen_symbiosis", VariantFeature::binary);
    public static final RegistryObject<VariantFeature> FRIENDLY_TRANSFUR = REGISTRY.register("grab/friendly_transfur", VariantFeature::binary);

    public static final RegistryObject<VariantFeature> DEPTH_COMPRESSION = REGISTRY.register("aquatic/depth_compression", VariantFeature::maxPresent);
    public static final RegistryObject<VariantFeature> PREVENT_SINKING = REGISTRY.register("aquatic/prevent_sinking", VariantFeature::binary);

    public static final RegistryObject<VariantFeature> BREATHE_DENY_AIR = REGISTRY.register("breathe/deny_air", VariantFeature::binary);
    public static final RegistryObject<VariantFeature> BREATHE_ACCEPT_WATER = REGISTRY.register("breathe/accept_water", VariantFeature::maxPresent);

    public static final RegistryObject<VariantFeature> SHARKS_BONUS_SHARKS = REGISTRY.register("summon_sharks/bonus_sharks", VariantFeature::sumPresent);
    public static final RegistryObject<VariantFeature> SHARKS_CALL_TO_ARMS = REGISTRY.register("summon_sharks/call_to_arms", VariantFeature::maxPresent);
    public static final RegistryObject<VariantFeature> SHARKS_FOLLOW_MY_LEAD = REGISTRY.register("summon_sharks/follow_my_lead", VariantFeature::maxPresent);

    public static final RegistryObject<VariantFeature> SWITCH_HANDS_BONUS_HANDS = REGISTRY.register("switch_hands/bonus_hands", VariantFeature::sumPresent);

    public static final RegistryObject<VariantFeature> HYPNOSIS_ALLURE = REGISTRY.register("hypnosis/allure", VariantFeature::maxPresent);
    public static final RegistryObject<VariantFeature> HYPNOSIS_POTENCY = REGISTRY.register("hypnosis/potency", VariantFeature::maxPresent);
    public static final RegistryObject<VariantFeature> HYPNOSIS_CONTROL = REGISTRY.register("hypnosis/control", VariantFeature::binary);

    public static final RegistryObject<VariantFeature> FLIGHT = REGISTRY.register("flight", VariantFeature::binary);
    public static final RegistryObject<VariantFeature> FLIGHT_UNLIMITED_STAMINA = REGISTRY.register("flight/unlimited_stamina", VariantFeature::binary);
    public static final RegistryObject<VariantFeature> FLIGHT_PASSIVE_GLIDE = REGISTRY.register("flight/passive_glide", VariantFeature::binary);
    public static final RegistryObject<VariantFeature> FLIGHT_STAMINA_EFFICIENCY = REGISTRY.register("flight/stamina_efficiency/flight", VariantFeature::maxPresent);
    public static final RegistryObject<VariantFeature> GLIDE_STAMINA_EFFICIENCY = REGISTRY.register("flight/stamina_efficiency/glide", VariantFeature::maxPresent);
    public static final RegistryObject<VariantFeature> WING_FLAP_BONUS_HORIZONTAL = REGISTRY.register("wing_flap/bonus_horizontal_speed", VariantFeature::sumPresent);
    public static final RegistryObject<VariantFeature> AUTONOMOUS_LANDING = REGISTRY.register("wing_flap/autonomous_landing", VariantFeature::binary);
    public static final RegistryObject<VariantFeature> WING_FLAP_BONUS_CHARGES = REGISTRY.register("wing_flap/bonus_charges", VariantFeature::sumPresent);
    public static final RegistryObject<VariantFeature> WINDS_PUSH_STRENGTH = REGISTRY.register("gale_force_winds/bonus_push_strength", VariantFeature::sumPresent);

    public static final RegistryObject<VariantFeature> CLIMB_COBWEB = REGISTRY.register("climb/cobweb", VariantFeature::binary);
}
