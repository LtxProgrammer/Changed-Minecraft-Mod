package net.ltxprogrammer.changed.entity.ai;

import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.ability.ILatexAssimilatedEntity;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.TransfurCause;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.init.ChangedDamageSources;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * The assimilation decided by a non-latex transfurring source (entity, block, item).
 * @param transfurVariant
 * @param postTransfurListener called when the target successfully transfurs into transfurVariant
 */
public record NonLatexAssimilationDecision<T extends ChangedEntity>(TransfurVariant<T> transfurVariant,
                                                                    TransfurCause cause,
                                                                    @Nullable IAbstractChangedEntity source,
                                                                    float transfurProgress,
                                                                    float extraDamage,
                                                                    Consumer<IAbstractChangedEntity> postTransfurListener) {

    public static <T extends ChangedEntity> NonLatexAssimilationDecision<T> of(TransfurVariant<T> transfurVariant, TransfurCause cause,
                                                                               @Nullable IAbstractChangedEntity source,
                                                                               float transfurProgress,
                                                                               float extraDamage) {
        return of(transfurVariant, cause, source, transfurProgress, extraDamage, entity -> {});
    }

    public static <T extends ChangedEntity> NonLatexAssimilationDecision<T> of(TransfurVariant<T> transfurVariant, TransfurCause cause,
                                                                               @Nullable IAbstractChangedEntity source,
                                                                               float transfurProgress,
                                                                               float extraDamage, Consumer<IAbstractChangedEntity> postTransfurListener) {
        return new NonLatexAssimilationDecision<>(transfurVariant, cause, source, transfurProgress, extraDamage, postTransfurListener);
    }

    public static <T extends ChangedEntity> NonLatexAssimilationDecision<T> fromBlockOrItem(TransfurVariant<T> transfurVariant, TransfurCause cause,
                                                                               float transfurProgress,
                                                                               float extraDamage) {
        return of(transfurVariant, cause, null, transfurProgress, extraDamage, entity -> {});
    }

    public static <T extends ChangedEntity> NonLatexAssimilationDecision<T> fromBlockOrItem(TransfurVariant<T> transfurVariant, TransfurCause cause,
                                                                               float transfurProgress,
                                                                               float extraDamage, Consumer<IAbstractChangedEntity> postTransfurListener) {
        return of(transfurVariant, cause, null, transfurProgress, extraDamage, postTransfurListener);
    }

    public DamageSource getDamageSource(RegistryAccess registryAccess) {
        final var sourceEntity = source != null ? source.getEntity() : null;
        return ChangedDamageSources.entityTransfur(registryAccess, sourceEntity);
    }

    public AssimilationBehavior assimilateVictimBehavior(LivingEntity target) {
        final var sourceEntity = source != null ? source.getEntity() : null;
        return AssimilationBehavior.progressThenTransfur(target,
                this.getDamageSource(target.level().registryAccess()),
                transfurProgress,
                () -> {
                    var newEntity = transfurVariant.replaceEntity(target, sourceEntity);
                    ChangedSounds.broadcastSound(newEntity.getEntity(), transfurVariant.sound, 1.0f, 1.0f);
                    postTransfurListener.accept(newEntity);
                    return newEntity;
                });
    }

    public NonLatexAssimilationDecision<?> withTransfurVariant(TransfurVariant<?> newTransfurVariant) {
        return new NonLatexAssimilationDecision<>(newTransfurVariant, cause, source, transfurProgress, extraDamage, postTransfurListener);
    }

    public NonLatexAssimilationDecision<T> withTransfurProgress(float newProgress) {
        return new NonLatexAssimilationDecision<>(transfurVariant, cause, source, newProgress, extraDamage, postTransfurListener);
    }

    public NonLatexAssimilationDecision<T> appendTransfurListener(Consumer<IAbstractChangedEntity> listener) {
        return new NonLatexAssimilationDecision<>(transfurVariant, cause, source, transfurProgress, extraDamage, postTransfurListener.andThen(listener));
    }
}
