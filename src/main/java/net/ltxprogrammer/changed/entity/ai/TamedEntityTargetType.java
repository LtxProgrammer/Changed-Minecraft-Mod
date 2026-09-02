package net.ltxprogrammer.changed.entity.ai;

import com.mojang.serialization.DataResult;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.TransfurCause;
import net.ltxprogrammer.changed.entity.beast.AbstractDarkLatexEntity;
import net.ltxprogrammer.changed.entity.latex.LatexType;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;

import java.util.Arrays;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public enum TamedEntityTargetType implements BiPredicate<ChangedEntity, LivingEntity>, StringRepresentable {
    TRANSFURABLE_ENTITIES("transfurable_entities", (self, target) -> {
        if (target == self.getOwner())
            return false;

        var targetLatexType = LatexType.getEntityLatexType(target);
        if (targetLatexType != null && self.getLatexType().isHostileTo(targetLatexType))
            return true;

        LatexAssimilationDecision<?> decision = switch (self.getTransfurMode()) {
            case REPLICATION -> self.makeLatexAssimilationDecision(TransfurCause.GRAB_REPLICATE, target);
            case ABSORPTION -> self.makeLatexAssimilationDecision(TransfurCause.GRAB_ABSORB, target);
            default -> null;
        };

        AssimilationBehavior behavior = ProcessTransfur.computeAssimilationBehavior(target, decision);
        return behavior != null;
    }),
    MONSTERS("monsters", (self, target) -> {
        if (target == self.getOwner())
            return false;
        var targetLatexType = LatexType.getEntityLatexType(target);
        if (targetLatexType != null && self.getLatexType().isHostileTo(LatexType.getEntityLatexType(target)))
            return true;
        return target.getType().getCategory() == MobCategory.MONSTER;
    }),
    HOSTILE_TO_OWNER("hostile_to_owner", (self, target) -> {
        if (target == self.getOwner() || self.getOwner() == null)
            return false;
        if (target instanceof Mob mob)
            return mob.getTarget() == self.getOwner();
        return self.getOwner().getLastAttacker() == target;
    });

    private final String serializedName;
    private final BiPredicate<ChangedEntity, LivingEntity> predicate;

    TamedEntityTargetType(String serializedName, BiPredicate<ChangedEntity, LivingEntity> predicate) {
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

    public static DataResult<TamedEntityTargetType> fromSerial(String serializedName) {
        return Arrays.stream(values()).filter(value -> value.serializedName.equals(serializedName))
                .findAny().map(DataResult::success).orElse(DataResult.error(
                        () -> "Invalid target type " + serializedName
                ));
    }

    public Component getDisplayText() {
        return Component.translatable("changed.tamed_entity.targeting." + serializedName);
    }

    public TamedEntityTargetType cycle() {
        if (this.ordinal() + 1 == values().length)
            return values()[0];
        else
            return values()[this.ordinal() + 1];
    }
}
