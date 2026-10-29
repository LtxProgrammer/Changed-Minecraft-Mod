package net.ltxprogrammer.changed.init;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.PoseExtension;
import net.ltxprogrammer.changed.entity.latex.LatexSwimMover;
import net.minecraft.world.entity.Pose;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ChangedPoseExtensions {
    public static final DeferredRegister<PoseExtension> REGISTRY = ChangedRegistry.POSE_EXTENSION.createDeferred(Changed.MODID);

    /// Equivalent to swimming, but slightly different
    public static final RegistryObject<PoseExtension> SLITHERING = REGISTRY.register("slithering", () -> PoseExtension.passthrough(Pose.SWIMMING));
    /// The entity is dissolved into latex, and has little to no form
    public static final RegistryObject<PoseExtension> DISSOLVED = REGISTRY.register("dissolved", () -> PoseExtension.fixed(Pose.SWIMMING, LatexSwimMover.SIZE_RADIUS * 2f, LatexSwimMover.SIZE_HEIGHT));
    /// The entity is deformed into a shapeless puddle
    public static final RegistryObject<PoseExtension> DEFORMED = REGISTRY.register("deformed", () -> PoseExtension.computedFromCore(Pose.STANDING, 1.6667f, 0.0f, 0.41176f, 0.0f));
    /// The taur is standing on its hind legs
    public static final RegistryObject<PoseExtension> REARING = REGISTRY.register("rearing", () -> PoseExtension.computedFromCore(Pose.STANDING, 1.0f, 0.0f, 1.0f, 0.675f));
    /// The entity is diving into water
    public static final RegistryObject<PoseExtension> DIVING = REGISTRY.register("diving", () -> PoseExtension.passthrough(Pose.SWIMMING));
    /// The entity is climbing along a surface
    public static final RegistryObject<PoseExtension> SURFACE_CLIMBING = REGISTRY.register("surface_climbing", () -> PoseExtension.passthrough(Pose.SWIMMING));
}
