package net.ltxprogrammer.changed.entity.ai;

import com.mojang.serialization.DataResult;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.beast.AbstractDarkLatexEntity;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.LivingEntity;

import java.util.Arrays;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public enum TamedEntityAttackType implements BiPredicate<ChangedEntity, LivingEntity>, StringRepresentable {
    ALWAYS_KILL("always_kill", (self, target) -> false),
    TRY_TRANSFUR("try_transfur", (self, target) -> {
        return target.getType().is(ChangedTags.EntityTypes.HUMANOIDS) || target instanceof ChangedEntity;
    });

    private final String serializedName;
    private final BiPredicate<ChangedEntity, LivingEntity> predicate;

    TamedEntityAttackType(String serializedName, BiPredicate<ChangedEntity, LivingEntity> predicate) {
        this.serializedName = serializedName;
        this.predicate = predicate;
    }

    @Override
    public boolean test(ChangedEntity self, LivingEntity possibleTarget) {
        return predicate.test(self, possibleTarget);
    }

    public Predicate<LivingEntity> forEntity(ChangedEntity self) {
        return target -> this.test(self, target);
    }

    @Override
    public String getSerializedName() {
        return serializedName;
    }

    public static DataResult<TamedEntityAttackType> fromSerial(String serializedName) {
        return Arrays.stream(values()).filter(value -> value.serializedName.equals(serializedName))
                .findAny().map(DataResult::success).orElse(DataResult.error(
                        () -> "Invalid attack type " + serializedName
                ));
    }


    public TamedEntityAttackType cycle() {
        if (this.ordinal() + 1 == values().length)
            return values()[0];
        else
            return values()[this.ordinal() + 1];
    }

    public Component getDisplayText() {
        return Component.translatable("changed.tamed_entity.attacking." + serializedName);
    }
}
