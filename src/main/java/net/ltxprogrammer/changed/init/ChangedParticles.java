package net.ltxprogrammer.changed.init;

import com.mojang.serialization.Codec;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.effect.particle.ColoredParticleOption;
import net.ltxprogrammer.changed.effect.particle.LatexDripParticle;
import net.ltxprogrammer.changed.effect.particle.TscSweepParticle;
import net.ltxprogrammer.changed.network.packet.ChangedLevelParticlesPacket;
import net.ltxprogrammer.changed.network.packet.ChangedPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
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

    public static <T extends ParticleOptions> int sendParticles(ServerLevel server, T particle, double x, double y, double z, int p_8772_, double p_8773_, double p_8774_, double p_8775_, double p_8776_) {
        ChangedLevelParticlesPacket packet = new ChangedLevelParticlesPacket(particle, false, x, y, z, (float)p_8773_, (float)p_8774_, (float)p_8775_, (float)p_8776_, p_8772_);
        int i = 0;

        for(int j = 0; j < server.players().size(); ++j) {
            ServerPlayer serverplayer = server.players().get(j);
            if (sendParticles(server, serverplayer, false, x, y, z, packet)) {
                ++i;
            }
        }

        return i;
    }

    private static boolean sendParticles(ServerLevel server, ServerPlayer player, boolean overrideLimiter, double x, double y, double z, ChangedPacket packet) {
        if (player.getLevel() != server) {
            return false;
        } else {
            BlockPos blockpos = player.blockPosition();
            if (blockpos.closerToCenterThan(new Vec3(x, y, z), overrideLimiter ? 512.0D : 32.0D)) {
                Changed.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> player), packet);
                return true;
            } else {
                return false;
            }
        }
    }
    public static final ParticleType<ColoredParticleOption> DRIPPING_LATEX = register(Changed.modResource("dripping_latex"),
            ColoredParticleOption.DESERIALIZER, ColoredParticleOption::codec);
    public static final SimpleParticleType TSC_SWEEP_ATTACK = (SimpleParticleType)register(Changed.modResource("tsc_sweep_attack"),
            new SimpleParticleType(false));

    public static ColoredParticleOption drippingLatex(Color3 color) {
        return new ColoredParticleOption(DRIPPING_LATEX, color);
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
        engine.register(TSC_SWEEP_ATTACK, TscSweepParticle.Provider::new);
    }
}
