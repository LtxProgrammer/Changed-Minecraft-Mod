package net.ltxprogrammer.changed.init;

import com.mojang.serialization.Codec;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.effect.particle.*;
import net.ltxprogrammer.changed.entity.Emote;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ChangedParticles {
    public record Color3(float red, float green, float blue) {
        public static final Codec<Color3> CODEC = Codec.INT.xmap(Color3::fromInt, Color3::toInt);
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
