package net.ltxprogrammer.changed.effect.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.ltxprogrammer.changed.entity.Emote;
import net.ltxprogrammer.changed.util.UniversalDist;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;

public class EmoteParticleOption implements ParticleOptions {
    private static Entity entityOrException(int id) {
        return UniversalDist.getLevel().getEntity(id);
    }

    public static final Deserializer<EmoteParticleOption> DESERIALIZER = new Deserializer<>() {
        public EmoteParticleOption fromCommand(ParticleType<EmoteParticleOption> type, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            Emote emote = Emote.values()[reader.readInt()];
            reader.expect(' ');
            return new EmoteParticleOption(type, emote, entityOrException(reader.readInt()));
        }

        public EmoteParticleOption fromNetwork(ParticleType<EmoteParticleOption> type, FriendlyByteBuf buffer) {
            return new EmoteParticleOption(type, Emote.values()[buffer.readInt()], entityOrException(buffer.readInt()));
        }
    };
    public static Codec<EmoteParticleOption> codec(ParticleType<EmoteParticleOption> type) {
        return RecordCodecBuilder.create(builder -> {
            return builder.group(
                    Emote.CODEC.fieldOf("emote").forGetter(option -> option.emote),
                    Codec.INT.fieldOf("entity").forGetter(option -> option.entity.getId())
            ).apply(builder, (emote, id) -> {
                return new EmoteParticleOption(type, emote, entityOrException(id));
            });
        });
    }
    private final ParticleType<EmoteParticleOption> type;
    private final Emote emote;
    private final Entity entity;

    public EmoteParticleOption(ParticleType<EmoteParticleOption> type, Emote emote, Entity entity) {
        this.type = type;
        this.emote = emote;
        this.entity = entity;
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

    public Entity getEntity() {
        return entity;
    }
}
