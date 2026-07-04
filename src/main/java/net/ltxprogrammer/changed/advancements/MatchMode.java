package net.ltxprogrammer.changed.advancements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.StringRepresentable;

import java.util.Arrays;
import java.util.function.Predicate;
import java.util.stream.Stream;

public enum MatchMode implements StringRepresentable {
    /**
     * Returns whether all elements of this stream match the provided predicate.
     * May not evaluate the predicate on all elements if not necessary for
     * determining the result.  If the stream is empty then {@code true} is
     * returned and the predicate is not evaluated.
     **/
    ALL_OF("all_of", "text.changed.all_of"),
    /**
     * Returns whether any elements of this stream match the provided
     * predicate.  May not evaluate the predicate on all elements if not
     * necessary for determining the result.  If the stream is empty then
     * {@code false} is returned and the predicate is not evaluated.
     **/
    ANY_OF("any_of", "text.changed.any_of"),
    /**
     * Returns whether no elements of this stream match the provided predicate.
     * May not evaluate the predicate on all elements if not necessary for
     * determining the result.  If the stream is empty then {@code true} is
     * returned and the predicate is not evaluated.
     **/
    NONE_OF("none_of", "text.changed.none_of");

    private final String serializedName;
    private final String langId;

    MatchMode(String serializedName, String langId) {
        this.serializedName = serializedName;
        this.langId = langId;
    }

    @Override
    public String getSerializedName() {
        return serializedName;
    }

    public MutableComponent getDisplayText() {
        return Component.translatable(langId);
    }

    public static DataResult<MatchMode> fromSerial(String serializedName) {
        return Arrays.stream(values()).filter(value -> value.serializedName.equals(serializedName))
                .findAny().map(DataResult::success).orElse(DataResult.error(
                        () -> "Invalid match mode " + serializedName
                ));
    }

    public static final Codec<MatchMode> CODEC = Codec.STRING.comapFlatMap(MatchMode::fromSerial, MatchMode::getSerializedName);

    public <T> boolean apply(Stream<T> stream, Predicate<T> predicate) {
        return switch (this) {
            case ALL_OF -> stream.allMatch(predicate);
            case ANY_OF -> stream.anyMatch(predicate);
            case NONE_OF -> stream.noneMatch(predicate);
        };
    }
}
