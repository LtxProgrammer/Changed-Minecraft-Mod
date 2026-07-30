package net.ltxprogrammer.changed.computers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.ltxprogrammer.changed.ability.tree.NodeDisplayInfo;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.StringRepresentable;

import java.util.Arrays;

public class File {
    public enum Type implements StringRepresentable {
        APP("app", "app", 16, 0),
        DATA("data", "dat", 32, 0),
        TEXT("text", "txt", 48, 0),
        PICTURE("picture", "png", 0, 16),
        RECIPE("recipe", "rcp", 16, 16);

        public final String serialName;
        public final String extension;
        public static Codec<Type> CODEC = Codec.STRING.comapFlatMap(Type::fromSerial, Type::getSerializedName);

        Type(String serialName, String extension, int xTexture, int yTexture) {
            this.serialName = serialName;
            this.extension = extension;
            this.xTexture = xTexture;
            this.yTexture = yTexture;
        }

        public static DataResult<Type> fromSerial(String name) {
            return Arrays.stream(values()).filter(type -> type.serialName.equals(name))
                    .findFirst().map(DataResult::success).orElseGet(() -> DataResult.error(() -> name + " is not a valid File.Type"));
        }

        @Override
        public String getSerializedName() {
            return serialName;
        }

        public final int xTexture;
        public final int yTexture;
    }

    public enum Error {
        FILESYSTEM_NOT_FOUND,
        FILE_NOT_FOUND,
        FILE_ALREADY_EXISTS,
        NO_READ_PERMISSION,
        NO_WRITE_PERMISSION,
        INVALID_PATH,
        INVALID_TYPE
    }

    protected final Runnable markModified;
    public final Type type;
    private String content;

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
        this.markModified();
    }
}
