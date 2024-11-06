package net.ltxprogrammer.changed.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;

import java.util.Arrays;
import java.util.function.Predicate;

public enum MiningStrength implements StringRepresentable, Predicate<MobEffect> {
    STRONG("strong", MobEffects.DIG_SPEED::equals),
    NORMAL("normal", effect -> false),
    WEAK("weak", MobEffects.DIG_SLOWDOWN::equals);

    public static Codec<MiningStrength> CODEC = Codec.STRING.comapFlatMap(MiningStrength::fromSerial, MiningStrength::getSerializedName).orElse(NORMAL);

    public final String serialName;
    public final Predicate<MobEffect> shouldHaveEffect;

    MiningStrength(String serialName, Predicate<MobEffect> shouldHaveEffect) {
        this.serialName = serialName;
        this.shouldHaveEffect = shouldHaveEffect;
    }

    @Override
    public String getSerializedName() {
        return serialName;
    }

    public static DataResult<MiningStrength> fromSerial(String name) {
        return Arrays.stream(values()).filter(type -> type.serialName.equals(name))
                .findFirst().map(DataResult::success).orElseGet(() -> DataResult.error(name + " is not a valid mining strength"));
    }

    @Override
    public boolean test(MobEffect effect) {
        return shouldHaveEffect.test(effect);
    }
}
