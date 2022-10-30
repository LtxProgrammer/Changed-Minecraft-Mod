package net.ltxprogrammer.changed.init;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ChangedParticles {
    public record Color3(float red, float green, float blue) {
        public static final Map<String, Color3> NAMED_COLORS = new HashMap<>();

        public static Color3 named(String name, float red, float green, float blue) {
            Color3 color3 = new Color3(red, green, blue);
            NAMED_COLORS.put(name, color3);
            return color3;
        }

        @Nullable
        private static Color3 parseHex(String tag) {
            if (tag.length() > 0) {
                if (tag.charAt(0) == '#')
                    tag = tag.substring(1);

                try {
                    int num = Integer.parseInt(tag, 16);

                    return new Color3(
                            ((num & 0xff0000) >> 16) / 255f,
                            ((num & 0x00ff00) >> 8) / 255f,
                            ((num & 0x0000ff) >> 0) / 255f
                    );
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
    }

    public static class LatexDripParticle extends TextureSheetParticle {

        private final SpriteSet sprite;
        private boolean lastOnGround = false;

        protected LatexDripParticle(ClientLevel p_108328_, double x, double y, double z, double vx, double vy, double vz, SpriteSet sprite, Color3 color) {
            super(p_108328_, x, y, z, vx, vy, vz);
            this.sprite = sprite;

            this.setSize(0.07f, 0.07f);
            this.quadSize *= 0.28f;

            this.lifetime = 100;

            this.gravity = 0.65f;
            this.hasPhysics = true;

            this.xd = vx * 1;
            this.yd = vy * 1;
            this.zd = vz * 1;

            this.setColor(color.red, color.green, color.blue);

            this.pickSprite(sprite);
        }

        @Override
        public void tick() {
            super.tick();
            if (!lastOnGround && this.onGround) {
                level.playLocalSound(x, y, z, new SoundEvent(new ResourceLocation("minecraft:entity.slime.squish_small")),
                        SoundSource.HOSTILE, 0.05f, 1.0f, true);
                lastOnGround = this.onGround;
            }
        }

        @Override
        public @NotNull ParticleRenderType getRenderType() {
            return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
        }

        public static class LatexDripProvider implements ParticleProvider<SimpleParticleType> {
            protected final SpriteSet sprite;
            protected static Color3 nextColor = null;

            public LatexDripProvider(SpriteSet p_106394_) {
                this.sprite = p_106394_;
            }

            @Nullable
            @Override
            public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z,
                                           double xSpeed, double ySpeed, double zSpeed) {
                var ret = new LatexDripParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, sprite, nextColor);
                nextColor = null;
                return ret;
            }

            public static void setNextColor(Color3 next) {
                nextColor = next;
            }

            public static void setNextColor(String next) {
                nextColor = Color3.getColor(next);
            }
        }

        public static void setNextColor(Color3 next) {
            LatexDripProvider.nextColor = next;
        }

        public static void setNextColor(String next) {
            LatexDripProvider.nextColor = Color3.getColor(next);
        }
    }
    private static final Map<ParticleType<?>, Function<SpriteSet, ParticleProvider<SimpleParticleType>>> REGISTRY = new HashMap<>();
    private static final Map<String, SimpleParticleType> DRIP_PARTICLES = new HashMap<>();

    public static final SimpleParticleType DRIPPING_LATEX = register(new SimpleParticleType(false).setRegistryName("dripping_latex"), LatexDripParticle.LatexDripProvider::new);

    private static <T extends ParticleType<?>> SimpleParticleType register(ParticleType<?> particle, Function<SpriteSet, ParticleProvider<SimpleParticleType>> provider) {
        REGISTRY.put(particle, provider);
        return (SimpleParticleType) particle;
    }

    private static SimpleParticleType registerDrip(String color, ParticleType<?> particle, Function<SpriteSet, ParticleProvider<SimpleParticleType>> provider) {
        SimpleParticleType tmp = register(particle.setRegistryName("dripping_latex_" + color), provider);
        DRIP_PARTICLES.put(color, tmp);
        return tmp;
    }

    public static SimpleParticleType getParticleFromColor(String color) {
        return DRIP_PARTICLES.getOrDefault(color, null);
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
