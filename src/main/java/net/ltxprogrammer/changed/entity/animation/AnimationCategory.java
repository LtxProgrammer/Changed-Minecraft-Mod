package net.ltxprogrammer.changed.entity.animation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.ltxprogrammer.changed.entity.VisionType;
import net.minecraft.util.StringRepresentable;

import java.util.Arrays;

public enum AnimationCategory implements StringRepresentable {
    /**
     * Entity is transfurring
     */
    TRANSFUR("transfur"),
    /**
     * Entity is idle
     */
    IDLE("idle"),
    /**
     * Entity is used in other entity's animation
     */
    PROP("prop");

    private final String serialName;

    public static final Codec<AnimationCategory> CODEC = Codec.STRING.comapFlatMap(AnimationCategory::fromSerial, AnimationCategory::getSerializedName);

    private AnimationCategory(String serialName) {
        this.serialName = serialName;
    }

    public static DataResult<AnimationCategory> fromSerial(String name) {
        return Arrays.stream(values()).filter(type -> type.serialName.equals(name))
                .findFirst().map(DataResult::success).orElseGet(() -> DataResult.error(name + " is not a known AnimationCategory"));
    }

    @Override
    public String getSerializedName() {
        return serialName;
    }
}
