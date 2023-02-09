package net.ltxprogrammer.changed.init;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.effect.particle.LatexDripParticle;
import net.ltxprogrammer.changed.effect.particle.TscSweepParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ChangedParticles {
    public record Color3(float red, float green, float blue) {
        public static final Map<String, Color3> NAMED_COLORS = new HashMap<>();

        public int toInt() {
            return (int)(red * 255f) << 16 |
                    (int)(green * 255f) << 8 |
                    (int)(blue * 255f) << 0;
        }

        public static Color3 named(String name, float red, float green, float blue) {
            Color3 color3 = new Color3(red, green, blue);
            NAMED_COLORS.put(name, color3);
            return color3;
        }

        public static Color3 fromInt(int num) {
            return new Color3(
                    ((num & 0xff0000) >> 16) / 255f,
                    ((num & 0x00ff00) >> 8) / 255f,
                    ((num & 0x0000ff) >> 0) / 255f
            );
        }

        @Nullable
        private static Color3 parseHex(String tag) {
            if (tag.length() > 0) {
                if (tag.charAt(0) == '#')
                    tag = tag.substring(1);

                try {
                    return fromInt(Integer.parseInt(tag, 16));
                } catch (Exception ignored) {}
            }

            return null;
        }

        public static Color3 getColor(String color) {
            return NAMED_COLORS.computeIfAbsent(color.toLowerCase(), Color3::parseHex);
        }

        public static final Color3 WHITE = named("white", 1.0f, 1.0f, 1.0f);
        public static final Color3 BLACK = named("black", 0.0f, 0.0f, 0.0f);
        public static final Color3 GRAY = named("gray", 0.588f, 0.588f, 0.588f);
        public static final Color3 DARK = named("dark", 0.224f, 0.224f, 0.224f);
        public static final Color3 BROWN = named("brown", 0.365f, 0.278f, 0.263f);
        public static final Color3 BLUE = named("blue", 0.318f, 0.396f, 0.616f);
        public static final Color3 SILVER = named("silver", 0.584f, 0.612f, 0.647f);
        public static final Color3 YELLOW = named("yellow", 1.0f, 0.824f, 0.004f);
        public static final Color3 GREEN = named("green", 0.749f, 0.949f, 0.596f);

        public static final Color3 TSC_BLUE = named("tsc_blue", 0.31f, 0.76f, 1.0f);
    }

    private static final Map<ParticleType<?>, Function<SpriteSet, ParticleProvider<SimpleParticleType>>> REGISTRY = new HashMap<>();

    public static final SimpleParticleType DRIPPING_LATEX = register(new SimpleParticleType(false)
            .setRegistryName("dripping_latex"), LatexDripParticle.Provider::new);
    public static final SimpleParticleType TSC_SWEEP_ATTACK = register(new SimpleParticleType(false)
            .setRegistryName("tsc_sweep_attack"), TscSweepParticle.Provider::new);

    private static SimpleParticleType register(ParticleType<?> particle, Function<SpriteSet, ParticleProvider<SimpleParticleType>> provider) {
        REGISTRY.put(particle, provider);
        return (SimpleParticleType) particle;
    }

    @SubscribeEvent
    public static void registerParticleTypes(RegistryEvent.Register<ParticleType<?>> event) {
        event.getRegistry().registerAll(REGISTRY.keySet().toArray(new ParticleType[0]));
    }

    @SubscribeEvent
    public static void registerParticles(ParticleFactoryRegisterEvent event) {
        REGISTRY.forEach((particle, provider) -> Minecraft.getInstance().particleEngine.register((SimpleParticleType) particle,
                provider::apply));
    }
}
