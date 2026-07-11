package net.ltxprogrammer.changed.computers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;

public class File {
    public enum Type {
        APP("app", 16, 0),
        DATA("dat", 32, 0),
        TEXT("txt", 48, 0),
        PICTURE("png", 0, 16),
        RECIPE("rcp", 16, 16);

        public final String extension;
        public static Codec<Type> CODEC = Codec.STRING.xmap(Type::valueOf, Type::name);

        Type(String extension, int xTexture, int yTexture) {
            this.extension = extension;
            this.xTexture = xTexture;
            this.yTexture = yTexture;
        }

        public final int xTexture;
        public final int yTexture;
    }

    protected final Runnable markModified;
    public final Type type;
    public final String content;

    public File(Type type, String content, Runnable markModified) {
        this.type = type;
        this.content = content;
        this.markModified = markModified;
    }

    public File(CompoundTag tag, Runnable markModified) {
        this.type = Type.valueOf(tag.getString("t"));
        this.content = tag.getString("c");
        this.markModified = markModified;
    }

    public void markModified() {
        this.markModified.run();
    }

    public void saveTo(CompoundTag tag) {
        tag.putString("t", type.name());
        tag.putString("c", content);
    }

    public CompoundTag serialize() {
        var t = new CompoundTag();
        saveTo(t);
        return t;
    }
}
