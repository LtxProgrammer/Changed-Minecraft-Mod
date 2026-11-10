package net.ltxprogrammer.changed.effect.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.ltxprogrammer.changed.entity.Emote;
import net.ltxprogrammer.changed.util.Color3;
import net.ltxprogrammer.changed.util.UniversalDist;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.registries.ForgeRegistries;

public class ColoredEntityParticleOption implements ParticleOptions {
    private static Entity entityOrException(int id) {
        return UniversalDist.getLevel().getEntity(id);
    }

    public static final Deserializer<ColoredEntityParticleOption> DESERIALIZER = new Deserializer<>() {
        public ColoredEntityParticleOption fromCommand(ParticleType<ColoredEntityParticleOption> type, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            Color3 color = Color3.fromInt(reader.readInt());
            reader.expect(' ');
            return new ColoredEntityParticleOption(type, color, entityOrException(reader.readInt()));
        }

        public ColoredEntityParticleOption fromNetwork(ParticleType<ColoredEntityParticleOption> type, FriendlyByteBuf buffer) {
            return new ColoredEntityParticleOption(type, Color3.fromInt(buffer.readInt()), entityOrException(buffer.readInt()));
        }
    };
    public static Codec<ColoredEntityParticleOption> codec(ParticleType<ColoredEntityParticleOption> type) {
        return RecordCodecBuilder.create(builder -> {
            return builder.group(
                    Color3.CODEC.fieldOf("color").forGetter(option -> option.color),
                    Codec.INT.fieldOf("entity").forGetter(option -> option.entity.getId())
            ).apply(builder, (emote, id) -> {
                return new ColoredEntityParticleOption(type, emote, entityOrException(id));
            });
        });
    }
    private final ParticleType<ColoredEntityParticleOption> type;
    private final Color3 color;
    private final Entity entity;

    public ColoredEntityParticleOption(ParticleType<ColoredEntityParticleOption> type, Color3 color, Entity entity) {
        this.type = type;
        this.color = color;
        this.entity = entity;
    }

    @Override
    public ParticleType<?> getType() {
        return type;
    }

    public Color3 getColor() {
        return color;
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buffer) {
        buffer.writeInt(color.toInt());
        buffer.writeInt(entity.getId());
    }

    @Override
    public String writeToString() {
        return ForgeRegistries.PARTICLE_TYPES.getKey(this.getType()) + " " + color.toInt() + " " + entity.getId();
    }

    public Entity getEntity() {
        return entity;
    }
}
