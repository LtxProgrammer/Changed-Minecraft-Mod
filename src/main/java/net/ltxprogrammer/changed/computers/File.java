package net.ltxprogrammer.changed.computers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;

public class File {
    public static final Codec<File> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
                Type.CODEC.fieldOf("t").forGetter(f -> f.type),
                Codec.STRING.fieldOf("c").forGetter(f -> f.content)
        ).apply(instance, File::new)
    );

    public enum Type {
        DATA("dat"),
        TEXT("txt"),
        PICTURE("png"),
        RECIPE("rcp");

        public final String extension;
        public static Codec<Type> CODEC = Codec.STRING.xmap(Type::valueOf, Type::name);

        Type(String extension) {
            this.extension = extension;
        }
    }

    public final Type type;
    public final String content;

    public File(Type type, String content) {
        this.type = type;
        this.content = content;
    }

    public File(CompoundTag tag) {
        this.type = Type.valueOf(tag.getString("t"));
        this.content = tag.getString("c");
    }

    public void saveTo(CompoundTag tag) {
        tag.putString("t", type.name());
        tag.putString("c", content);
    }

    public CompoundTag save() {
        var t = new CompoundTag();
        saveTo(t);
        return t;
    }
}
