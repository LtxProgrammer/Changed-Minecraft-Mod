package net.ltxprogrammer.changed.effect.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import net.ltxprogrammer.changed.entity.Emote;
import net.ltxprogrammer.changed.init.ChangedParticles;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;

public class EmoteParticleOption implements ParticleOptions {
    public static final Deserializer<EmoteParticleOption> DESERIALIZER = new Deserializer<>() {
        public EmoteParticleOption fromCommand(ParticleType<EmoteParticleOption> type, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            return new EmoteParticleOption(type, Emote.values()[reader.readInt()]);
        }

        public EmoteParticleOption fromNetwork(ParticleType<EmoteParticleOption> type, FriendlyByteBuf buffer) {
            return new EmoteParticleOption(type, Emote.values()[buffer.readInt()]);
        }
    };
    public static Codec<EmoteParticleOption> codec(ParticleType<EmoteParticleOption> type) {
        return Emote.CODEC.xmap((emote) -> new EmoteParticleOption(type, emote), (option) -> option.emote);
    }
    private final ParticleType<EmoteParticleOption> type;
    private final Emote emote;

    public EmoteParticleOption(ParticleType<EmoteParticleOption> type, Emote emote) {
        this.type = type;
        this.emote = emote;
    }

    @Override
    public ParticleType<?> getType() {
        return type;
    }

    public Emote getEmote() {
        return emote;
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buffer) {
        buffer.writeInt(emote.ordinal());
    }

    @Override
    public String writeToString() {
        return Registry.PARTICLE_TYPE.getKey(this.getType()) + " " + emote.ordinal();
    }
}
