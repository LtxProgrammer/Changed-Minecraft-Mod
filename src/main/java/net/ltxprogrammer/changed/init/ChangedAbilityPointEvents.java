package net.ltxprogrammer.changed.init;

import com.mojang.serialization.Codec;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.ability.tree.events.*;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ChangedAbilityPointEvents {
    public static final DeferredRegister<Codec<? extends AbstractPointEvent<?>>> REGISTRY = ChangedRegistry.POINT_EVENTS.createDeferred(Changed.MODID);

    public static final RegistryObject<Codec<OnTransfurOther>> ON_TRANSFUR_OTHER = REGISTRY.register("on_transfur_other", () -> OnTransfurOther.CODEC);
    public static final RegistryObject<Codec<DistanceWalked>> DISTANCE_WALKED = REGISTRY.register("distance_walked", () -> DistanceWalked.CODEC);
    public static final RegistryObject<Codec<DistanceSprinted>> DISTANCE_SPRINTED = REGISTRY.register("distance_sprinted", () -> DistanceSprinted.CODEC);
    public static final RegistryObject<Codec<DistanceCrouched>> DISTANCE_CROUCHED = REGISTRY.register("distance_crouched", () -> DistanceCrouched.CODEC);
    public static final RegistryObject<Codec<DistanceSwam>> DISTANCE_SWAM = REGISTRY.register("distance_swam", () -> DistanceSwam.CODEC);
    public static final RegistryObject<Codec<OnJump>> ON_JUMP = REGISTRY.register("on_jump", () -> OnJump.CODEC);
    public static final RegistryObject<Codec<TimeAsVariant>> TIME_AS_VARIANT = REGISTRY.register("time_as_variant", () -> TimeAsVariant.CODEC);
    public static final RegistryObject<Codec<TimeInFluid>> TIME_IN_FLUID = REGISTRY.register("time_in_fluid", () -> TimeInFluid.CODEC);
    public static final RegistryObject<Codec<TimeBreathingFluid>> TIME_BREATHING_FLUID = REGISTRY.register("time_breathing_fluid", () -> TimeBreathingFluid.CODEC);
}
