package net.ltxprogrammer.changed.init;

import com.mojang.serialization.Codec;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.effect.particle.*;
import net.ltxprogrammer.changed.entity.Emote;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ChangedParticles {
    private static final Map<ResourceLocation, ParticleType<?>> REGISTRY = new HashMap<>();

    public static final ParticleType<ColoredParticleOption> DRIPPING_LATEX = register(Changed.modResource("dripping_latex"),
            ColoredParticleOption.DESERIALIZER, ColoredParticleOption::codec);
    public static final ParticleType<ColoredParticleOption> GAS = register(Changed.modResource("gas"),
            ColoredParticleOption.DESERIALIZER, ColoredParticleOption::codec);
    public static final ParticleType<EmoteParticleOption> EMOTE = register(Changed.modResource("emote"),
            EmoteParticleOption.DESERIALIZER, EmoteParticleOption::codec);
    public static final SimpleParticleType TSC_SWEEP_ATTACK = (SimpleParticleType)register(Changed.modResource("tsc_sweep_attack"),
            new SimpleParticleType(false));

    public static ColoredParticleOption drippingLatex(Color3 color) {
        return new ColoredParticleOption(DRIPPING_LATEX, color);
    }

    public static ColoredParticleOption gas(Color3 color) {
        return new ColoredParticleOption(GAS, color);
    }

    public static EmoteParticleOption emote(Entity entity, Emote emote) {
        return new EmoteParticleOption(EMOTE, emote, entity);
    }

    private static <T extends ParticleOptions> ParticleType<T> register(ResourceLocation name, ParticleType<T> type) {
        type.setRegistryName(name);
        REGISTRY.put(name, type);
        return type;
    }

    private static <T extends ParticleOptions> ParticleType<T> register(ResourceLocation name, ParticleOptions.Deserializer<T> dec, final Function<ParticleType<T>, Codec<T>> fn) {
        var type = new ParticleType<T>(false, dec) {
            public Codec<T> codec() {
                return fn.apply(this);
            }
        };

        return register(name, type);
    }

    @SubscribeEvent
    public static void registerParticleTypes(RegistryEvent.Register<ParticleType<?>> event) {
        REGISTRY.forEach((name, entry) -> {
            event.getRegistry().register(entry);
        });
    }

    @SubscribeEvent
    public static void registerParticles(ParticleFactoryRegisterEvent event) {
        var engine = Minecraft.getInstance().particleEngine;
        engine.register(DRIPPING_LATEX, LatexDripParticle.Provider::new);
        engine.register(GAS, GasParticle.Provider::new);
        engine.register(EMOTE, EmoteParticle.Provider::new);
        engine.register(TSC_SWEEP_ATTACK, TscSweepParticle.Provider::new);
    }
}
