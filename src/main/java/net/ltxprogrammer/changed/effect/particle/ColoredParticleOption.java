package net.ltxprogrammer.changed.effect.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import net.ltxprogrammer.changed.init.ChangedParticles;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;

public class ColoredParticleOption implements ParticleOptions {
    public static final ParticleOptions.Deserializer<ColoredParticleOption> DESERIALIZER = new ParticleOptions.Deserializer<>() {
        public ColoredParticleOption fromCommand(ParticleType<ColoredParticleOption> type, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            return new ColoredParticleOption(type, ChangedParticles.Color3.fromInt(reader.readInt()));
        }

        public ColoredParticleOption fromNetwork(ParticleType<ColoredParticleOption> type, FriendlyByteBuf buffer) {
            return new ColoredParticleOption(type, ChangedParticles.Color3.fromInt(buffer.readInt()));
        }
    };
    public static Codec<ColoredParticleOption> codec(ParticleType<ColoredParticleOption> type) {
        return ChangedParticles.Color3.CODEC.xmap((color) -> new ColoredParticleOption(type, color), (dripOption) -> dripOption.color);
    }
    private final ParticleType<ColoredParticleOption> type;
    private final ChangedParticles.Color3 color;

    public ColoredParticleOption(ParticleType<ColoredParticleOption> type, ChangedParticles.Color3 color) {
        this.type = type;
        this.color = color;
    }

    @Override
    public ParticleType<?> getType() {
        return type;
    }

    public ChangedParticles.Color3 getColor() {
        return color;
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buffer) {
        buffer.writeInt(color.toInt());
    }

    @Override
    public String writeToString() {
        return Registry.PARTICLE_TYPE.getKey(this.getType()) + " " + color.toInt();
    }
}
