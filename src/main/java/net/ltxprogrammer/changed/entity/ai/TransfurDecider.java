package net.ltxprogrammer.changed.entity.ai;

import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.ability.ILatexAssimilatedEntity;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.TransfurCause;
import net.ltxprogrammer.changed.entity.TransfurContext;
import net.ltxprogrammer.changed.entity.variant.GenderedPair;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.BiConsumer;
import java.util.function.Function;

public interface TransfurDecider<T extends LivingEntity> {
    LatexAssimilationDecision<?> apply(T assimilatedMob, LivingEntity target);

    default Function<LivingEntity, LatexAssimilationDecision<?>> withAssimilatedMob(T assimilatedMob) {
        return target -> this.apply(assimilatedMob, target);
    }

    static <T extends LivingEntity, V extends ChangedEntity> TransfurDecider<T> simpleMobDecider(TransfurVariant<V> variant, float damage) {
        return simpleMobDecider(variant, damage, (entity, transfurredEntity) -> {});
    }

    static <T extends LivingEntity, V extends ChangedEntity> TransfurDecider<T> simpleMobDecider(RegistryObject<TransfurVariant<V>> variant, float damage) {
        return simpleMobDecider(variant, damage, (entity, transfurredEntity) -> {});
    }

    static <T extends LivingEntity, VM extends ChangedEntity, VF extends ChangedEntity> TransfurDecider<T> simpleMobDecider(GenderedPair<VM, VF> variant, float damage) {
        return simpleMobDecider(variant, damage, (entity, transfurredEntity) -> {});
    }

    static <T extends LivingEntity, V extends ChangedEntity> TransfurDecider<T> simpleMobDecider(TransfurVariant<V> variant, float damage, BiConsumer<T, IAbstractChangedEntity> postTransfurListener) {
        return (assimilatedMob, target) -> {
            ILatexAssimilatedEntity self = ILatexAssimilatedEntity.forEntity(assimilatedMob);

            return LatexAssimilationDecision.weakAbsorption(variant,
                    self.absorb(), damage,
                    transfurredEntity -> postTransfurListener.accept(assimilatedMob, transfurredEntity));
        };
    }

    static <T extends LivingEntity, V extends ChangedEntity> TransfurDecider<T> simpleMobDecider(RegistryObject<TransfurVariant<V>> variant, float damage, BiConsumer<T, IAbstractChangedEntity> postTransfurListener) {
        return (assimilatedMob, target) -> {
            ILatexAssimilatedEntity self = ILatexAssimilatedEntity.forEntity(assimilatedMob);

            return LatexAssimilationDecision.weakAbsorption(variant.get(),
                    self.absorb(), damage,
                    transfurredEntity -> postTransfurListener.accept(assimilatedMob, transfurredEntity));
        };
    }

    static <T extends LivingEntity, VM extends ChangedEntity, VF extends ChangedEntity> TransfurDecider<T> simpleMobDecider(GenderedPair<VM, VF> variant, float damage, BiConsumer<T, IAbstractChangedEntity> postTransfurListener) {
        return (assimilatedMob, target) -> {
            ILatexAssimilatedEntity self = ILatexAssimilatedEntity.forEntity(assimilatedMob);

            return LatexAssimilationDecision.weakAbsorption(variant.getRandomVariant(assimilatedMob.getRandom()),
                    self.absorb(), damage,
                    transfurredEntity -> postTransfurListener.accept(assimilatedMob, transfurredEntity));
        };
    }
}
