package net.ltxprogrammer.changed.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;

import java.util.Arrays;
import java.util.function.Predicate;

public enum VisionType implements StringRepresentable, Predicate<MobEffect> {
    NORMAL("normal", effect -> false),
    NIGHT_VISION("night_vision", MobEffects.NIGHT_VISION::equals),
    BLIND("blind", MobEffects.BLINDNESS::equals);

    public static Codec<VisionType> CODEC = Codec.STRING.comapFlatMap(VisionType::fromSerial, VisionType::getSerializedName).orElse(NORMAL);

    public final String serialName;
    public final Predicate<MobEffect> shouldHaveEffect;

    VisionType(String serialName, Predicate<MobEffect> shouldHaveEffect) {
        this.serialName = serialName;
        this.shouldHaveEffect = shouldHaveEffect;
    }

    @Override
    public String getSerializedName() {
        return serialName;
    }

    public static DataResult<VisionType> fromSerial(String name) {
        return Arrays.stream(values()).filter(type -> type.serialName.equals(name))
                .findFirst().map(DataResult::success).orElseGet(() -> DataResult.error(name + " is not a valid VisionType"));
    }

    @Override
    public boolean test(MobEffect effect) {
        return shouldHaveEffect.test(effect);
    }
}
