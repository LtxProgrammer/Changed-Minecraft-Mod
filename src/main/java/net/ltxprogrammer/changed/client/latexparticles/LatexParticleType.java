package net.ltxprogrammer.changed.client.latexparticles;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.RegistryObject;

public class LatexParticleType<T extends LatexParticle> extends ForgeRegistryEntry<LatexParticleType<?>> {
    public static final DeferredRegister<LatexParticleType<?>> REGISTRY = ChangedRegistry.LATEX_PARTICLE_TYPE.createDeferred(Changed.MODID);

    public LatexParticleType() {

    }

    public static final RegistryObject<LatexParticleType<LatexDripParticle>> LATEX_DRIP_PARTICLE = REGISTRY.register("dripping_latex", LatexParticleType::new);
}
